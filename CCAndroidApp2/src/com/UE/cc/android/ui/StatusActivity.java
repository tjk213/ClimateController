package com.UE.cc.android.ui;

import java.util.Timer;

import android.content.Context;
import android.text.Html;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import com.UE.cc.R;
import com.UE.cc.android.listener.ButtonClickListener;
import com.UE.cc.android.listener.FanModeButtonClickListener;
import com.UE.cc.android.listener.HoldChangeListener;
import com.UE.cc.android.listener.OpModeButtonClickListener;
import com.UE.cc.android.listener.TargetTempClickListener;
import com.UE.cc.android.listener.ZoneTitleClickListener;
import com.UE.cc.android.util.SetPointTimerTask;

public class StatusActivity extends CCActivity implements ModeButtonActivity,UpDownIntActivity,ZoneTitleActivity
{
	private TextView zoneTitleTV,currentTempTV,hvacStatusTV,fanStatusTV,targetTempTV;
	private Button upButton,downButton,opModeButton,fanModeButton;
	private Switch holdSwitch;
	private Timer timer;
	private SetPointTimerTask timerTask;
	
	private OnCheckedChangeListener holdListener;  //TODO: Extend Switch to include this code -- doesn't really belong here
	
	public StatusActivity()
	{
		super(R.layout.status,R.menu.status_menu);
		this.timer = new Timer();
		this.holdListener = new HoldChangeListener(this); //doesn't belong here
	}
	
	protected void initializeViews()
	{
		this.setTitle(R.string.status_activity_label);
		zoneTitleTV = (TextView) findViewById(R.id.zoneTitle);
		zoneTitleTV.setOnClickListener(new ZoneTitleClickListener(this));
		currentTempTV = (TextView) findViewById(R.id.currentTemp);
		targetTempTV = (TextView) findViewById(R.id.targetTemp);
		targetTempTV.setOnClickListener(new TargetTempClickListener(this));
		hvacStatusTV = (TextView) findViewById(R.id.HvacStatus);
		fanStatusTV = (TextView) findViewById(R.id.FanStatus);
		opModeButton = (Button) findViewById(R.id.opModeButton);
		opModeButton.setOnClickListener(new OpModeButtonClickListener(this,-1)); //TODO: Eliminate need for ambiguous '-1'
		fanModeButton = (Button) findViewById(R.id.fanModeButton);
		fanModeButton.setOnClickListener(new FanModeButtonClickListener(this,-1));
		upButton = (Button) findViewById(R.id.upButton);
		downButton = (Button) findViewById(R.id.downButton);
		upButton.setOnClickListener(new ButtonClickListener(this,1));
		downButton.setOnClickListener(new ButtonClickListener(this,-1));
		holdSwitch = (Switch) findViewById(R.id.holdSwitch);
		holdSwitch.setOnCheckedChangeListener(holdListener);
	}
	
	@Override
	protected void updateViews()
	{
		zoneTitleTV.setText(Html.fromHtml("<u>" + manager.getHvac().getZoneTitle() + "</u>"));
		currentTempTV.setText(Integer.toString(manager.getHvac().getCurrentTemp()) + "°F");
		targetTempTV.setText(Integer.toString(manager.getHvac().getTargetTemp()) + "°F");
		hvacStatusTV.setText("HVAC: " + HVAC_STATUSES[manager.getHvac().getHvacStatus()]);
		fanStatusTV.setText("Fan: " + FAN_STATUSES[manager.getHvac().getFanStatus()]);
		opModeButton.setText(OPERATION_MODES[manager.getHvac().getOpMode()]);
		fanModeButton.setText(FAN_MODES[manager.getHvac().getFanMode()]);
		
		
		holdSwitch.setOnCheckedChangeListener(null); //doesn't belong here
		holdSwitch.setChecked(manager.getHvac().isOnHold());
		holdSwitch.setOnCheckedChangeListener(holdListener); //doesn't belong here
		
	}
	
	@Override
	public void updateZoneTitle(String zt) 
	{
		sendCommand(TCP_CMD_SET_ZONE_TITLE,zt);
		zoneTitleTV.setText(Html.fromHtml("<u>" + zt + "</u>"));
	}
	
	@Override
	public void updateOpMode(int rowID, int newOpMode)
	{
		//rowID is ignored, parameter is only there to comply
		//with ModeButtonActivity Interface
		sendCommand(TCP_CMD_SET_OP_MODE,newOpMode);
		opModeButton.setText(OPERATION_MODES[newOpMode]);
	}
	
	@Override
	public void updateFanMode(int rowID, int newFanMode)
	{
		//rowID is ignored, parameter is only there to comply
		//with ModeButtonActivity Interface
		sendCommand(TCP_CMD_SET_FAN_MODE,newFanMode);
		fanModeButton.setText(FAN_MODES[newFanMode]);
	}
	
	/**
	 * Preconditions: newTarget is valid integer
	 * Postconditions:	AsyncTask executed to set new temp via TCP command
	 * 					newTarget is set to TV (should be reset by AsyncTask if error is returned)
	 */
	@Override
	public void updateInt(int newTarget)
	{
		sendCommand(TCP_CMD_SET_SET_POINT,newTarget);
		targetTempTV.setText(Integer.toString(newTarget) + "°F");
	}
	
	public void updateHold() {
		sendCommand(TCP_CMD_SET_HOLD,holdSwitch.isChecked());
	}
	
	/**
	 * Preconditions: delta is 1 or -1
	 * Postconditions:	timerTask scheduled to increment/decrement temp after delay 'UP_DOWN_INT_DELAY'
	 * 					new temp is set to TV (should be reset by AsyncTask if error is returned)
	 * 
	 * Delay is added before TCP command is sent in order to allow user to tap up/down
	 * buttons multiple times
	 */
	@Override
	public void stepInt(int delta)
	{
		//disable manual entry until temp is updated from this click
		//view will be re-enabled by AsyncTask
		targetTempTV.setClickable(false);
		//Retrieve current value from UI (manager.hvac may not have
		//been updated yet)
		String targetTempString = (String)targetTempTV.getText();
		int newTarget = Integer.parseInt(targetTempString.substring(0,targetTempString.indexOf("°")))+delta;
		if(newTarget >= TARGET_TEMP_MIN && newTarget <= TARGET_TEMP_MAX)
		{
			//Cancel any previous timerTask within the last 'UP_DOWN_INT_DELAY' milliseconds
			if(timerTask != null) timerTask.cancel();
			timer.purge();
			timerTask = new SetPointTimerTask(this,newTarget);
			timer.schedule(timerTask, UP_DOWN_INT_DELAY);
			targetTempTV.setText(Integer.toString(newTarget) + "°F");
		}
	}
	
	/**
	 * Preconditions: newTarget is a valid temp
	 * Postconditions: AsyncTask to set setPoint via TCP Command is executed
	 * Created to allow TimerTask to call back to Activity Class
	 */
	public void executeStepSetPoint(final int newTarget)
	{
		runOnUiThread(new Runnable() //AsyncTasks must be executed from UI Thread
		{
			@Override
			public void run() {
				sendCommand(TCP_CMD_SET_SET_POINT,newTarget);
			}	
		});
	}

	@Override
	public String getZoneTitle() {
		return manager.getHvac().getZoneTitle();
	}
	
	@Override
	public int getFanMode(int rowID) 
	{
		//rowID is ignored, parameter is only there to comply
		//with ModeButtonActivity Interface
		return manager.getHvac().getFanMode();
	}

	@Override
	public int getOpMode(int rowID) 
	{
		//rowID is ignored, parameter is only there to comply
		//with ModeButtonActivity Interface
		return manager.getHvac().getOpMode();
	}

	@Override
	public Context getImplementingClass() {
		return this;
	}
	
	@Override
	protected void setInputEnabled(boolean enabled)
	{
		upButton.setClickable(enabled);
		downButton.setClickable(enabled);
		targetTempTV.setClickable(enabled);
		opModeButton.setClickable(enabled);
		fanModeButton.setClickable(enabled);
		holdSwitch.setClickable(enabled);
	}

	@Override
	protected void onRefreshActivity() {
		sendCommand(TCP_CMD_GET_HVAC);
	}
}
