package com.UE.cc.android.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.UE.cc.R;
import com.UE.cc.android.listener.ButtonClickListener;
import com.UE.cc.android.listener.CreateDayClickListener;
import com.UE.cc.android.listener.FanModeButtonClickListener;
import com.UE.cc.android.listener.NumPeriodsClickListener;
import com.UE.cc.android.listener.OpModeButtonClickListener;
import com.UE.cc.android.listener.SetPointClickListener;
import com.UE.cc.android.listener.StartTimeClickListener;
import com.UE.cc.domain.DaySchedule;
import com.UE.cc.domain.ProgrammablePeriod;
import com.UE.cc.util.Time;

/**
 * Activity to view/edit/create a 'DaySchedule' object. This activity
 * differs fundamentally from Status and Schedule because changes are not
 * uploaded to server instantly, user must make all desired changes and 
 * press 'done'. All of the information is then sent as one object to the
 * server. 
 * 
 * This is because making multiple changes to the schedule may 
 * mandate that it be temporarily invalid. At this point, syncing the 
 * object with the server would be futile  
 * @author TK
 */
public class CreateDayActivity extends CCActivity implements UpDownIntActivity,ModeButtonActivity
{
	private EditText dayNameET;
	private Button upButton,downButton,doneButton;
	private TextView numPeriodsTV;
	private TableLayout tableLayout;
	private int numRows;
	private DaySchedule daySchedule;	
	private AlertDialog dayScheduleSelect;
	private boolean daySelected = false;
	private boolean done = false;
	
	public CreateDayActivity()
	{
		super(R.layout.create_day,R.menu.create_day_menu);
		this.numRows=0;
	}
	
	@Override
	protected void initializeViews() 
	{
		dayNameET = (EditText) findViewById(R.id.nameEditText);
		upButton = (Button) findViewById(R.id.upButton);
		upButton.setOnClickListener(new ButtonClickListener(this,1));
		downButton = (Button) findViewById(R.id.downButton);
		downButton.setOnClickListener(new ButtonClickListener(this,-1));
		numPeriodsTV = (TextView) findViewById(R.id.numPeriods);
		numPeriodsTV.setOnClickListener(new NumPeriodsClickListener(this));
		doneButton = (Button) findViewById(R.id.doneButton);
		doneButton.setOnClickListener(new CreateDayClickListener(this));
		tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		daySelected = getIntent().getBooleanExtra(HAS_DAYSCHEDULE,false);
		if(daySelected)
		{
			daySchedule = (DaySchedule) getIntent().getSerializableExtra(DAYSCHEDULE);
			dayNameET.setText(daySchedule.toString());
		}
		else
			daySchedule = new DaySchedule(null, 2);
	}

	private void promptForDay() 
	{
		AlertDialog.Builder daySelectBuilder = new AlertDialog.Builder(this);
		daySelectBuilder.setTitle("Select Day to View/Edit");
		daySelectBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				CreateDayActivity.this.finish(); //Upon cancel, kill entire activity rather than just dialog
			}
		});
		daySelectBuilder.setNeutralButton("Create New", null); //Simply dismiss dialog, default DaySchedule is already displayed in background
		daySelectBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() 
		{	
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) 
			{
				if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled())
				{
					CreateDayActivity.this.finish(); //Upon back, kill entire activity rather than just dialog
					return true;
				}
				return false;
			}
		});
		daySelectBuilder.setSingleChoiceItems(manager.getDayScheduleNames(),-1, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				daySchedule = manager.getDaySchedule(which);
				dayNameET.setText(daySchedule.toString());
				updateViews();
				dayScheduleSelect.dismiss();  //Not sure if this is necessary
			}
		});
		dayScheduleSelect = daySelectBuilder.create();
		dayScheduleSelect.show();		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		daySelected = true;
	}

	@Override
	protected void updateViews() 
	{
		if(!daySelected)
		{
			this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			promptForDay();
		}
		if(done)
		{
			startNewActivity(this, ScheduleActivity.class);
			this.finish();
		}
		numPeriodsTV.setText(Integer.toString(daySchedule.getNumPeriods()));
		if(numRows > 0)
		{
			tableLayout.removeViews(1,numRows);
			numRows = 0;
		}
			
		for(int i=0; i<daySchedule.getNumPeriods(); i++)
		{
			TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.table_row,null);
			TextView numTV = (TextView) row.findViewById(R.id.periodNum);
			numTV.setText(Integer.toString(i+1));
			Button startTimeButton = (Button) row.findViewById(R.id.startTime);
			startTimeButton.setText(getStartTime(i).toString());
			if(i == 0)
				startTimeButton.setOnClickListener(new OnClickListener() 
				{	
					@Override
					public void onClick(View v) {
						Toast.makeText(CreateDayActivity.this,"Cannot Change Period #1 Start Time", Toast.LENGTH_SHORT).show();
					}
				});
			else
				startTimeButton.setOnClickListener(new StartTimeClickListener(this,i));
			
			Button setPointButton = (Button) row.findViewById(R.id.setPoint);
			setPointButton.setText(Integer.toString(getSetPoint(i)));
			setPointButton.setOnClickListener(new SetPointClickListener(this,i));
			Button opModeButton = (Button) row.findViewById(R.id.opMode);
			opModeButton.setText(OPERATION_MODES[getOpMode(i)]);
			opModeButton.setOnClickListener(new OpModeButtonClickListener(this,i));
			Button fanModeButton = (Button) row.findViewById(R.id.fanMode);
			fanModeButton.setText(FAN_MODES[getFanMode(i)]);
			fanModeButton.setOnClickListener(new FanModeButtonClickListener(this,i));
			tableLayout.addView(row);
			numRows++;
		}
		tableLayout.requestLayout(); //Not sure if this is necessary
	}
	
	@Override
	public void stepInt(int delta)
	{
		daySchedule.stepNumPeriods(delta);
		updateViews();
	}
	
	public void createDay()
	{
		String name = dayNameET.getText().toString();
		if(name == null || name.equals("")) {
			dayNameET.setError("The new day must be given a name");
		}
		else
		{
			daySchedule.setName(name);
			if(daySchedule.scheduleIsValid())
			{
				done = true;
				sendCommand(TCP_CMD_SAVE_DAY,daySchedule);
			}
			else {
				showErrorDialog("This day cannot be saved due to an invalid schedule");
			}
		}
	}
	
	@Override
	public final boolean onOptionsItemSelected(final MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.menu_delete:
			deleteDay();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void deleteDay()
	{
		if(manager.getDaySchedules().size() <= 1)
			showErrorDialog("Cannot Delete Only Remaining Day");
		else
		{
			String name = dayNameET.getText().toString();
			if(name == null || name.equals(""))
			{
				startNewActivity(this, ScheduleActivity.class);
				this.finish();
			}
			else
			{
				daySchedule.setName(name);
				done = true;
				sendCommand(TCP_CMD_DELETE_DAY,daySchedule);
			}
		}
	}

	@Override
	public void updateInt(int upDownInt) 
	{
		daySchedule.updateNumPeriods(upDownInt);
		updateViews();
	}

	@Override
	public Context getImplementingClass() {
		return this;
	}

	@Override
	public void updateOpMode(int rowID, int newOpMode) 
	{
		ArrayList<ProgrammablePeriod> periods = daySchedule.getPeriods();
		ProgrammablePeriod pp = periods.get(rowID);
		pp.setOpMode(newOpMode);
		updateViews();
	}

	@Override
	public void updateFanMode(int rowID, int newFanMode) {
		daySchedule.getPeriods().get(rowID).setFanMode(newFanMode);
		updateViews();
	}
	
	public void updateSetPoint(int rowID, int newSetPoint)
	{
		daySchedule.getPeriods().get(rowID).setSetPoint(newSetPoint);
		updateViews();
	}
	
	public void updateStartTime(int rowID, Time newTime)
	{
		daySchedule.getPeriods().get(rowID).setStartTime(newTime);
		updateViews();
	}

	@Override
	public int getOpMode(int rowID) {
		return daySchedule.getPeriods().get(rowID).getOpMode();
	}

	@Override
	public int getFanMode(int rowID) {
		return daySchedule.getPeriods().get(rowID).getFanMode();
	}
	
	public int getSetPoint(int rowID) {
		return daySchedule.getPeriods().get(rowID).getSetPoint();
	}
	
	public Time getStartTime(int rowID) {
		return daySchedule.getPeriods().get(rowID).getStartTime();
	}
	
	@Override
	protected void setInputEnabled(boolean enabled)
	{
		refresh.setVisible(!enabled);
		doneButton.setClickable(enabled);
	}

	@Override
	protected void onRefreshActivity() {
		sendCommand(TCP_CMD_GET_DAY_SCHEDULES);
	}
}
