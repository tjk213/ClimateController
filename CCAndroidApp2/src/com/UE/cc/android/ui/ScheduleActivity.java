package com.UE.cc.android.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

import com.UE.cc.R;
import com.UE.cc.android.listener.ScheduleClickListener;
import com.UE.cc.android.listener.ZoneTitleClickListener;
import com.UE.cc.domain.DaySchedule;
import com.UE.cc.domain.HvacSystem;
import com.UE.cc.util.DayOfWeek;

public class ScheduleActivity extends CCActivity implements ZoneTitleActivity
{
	private TextView zoneTitleTV;
	private Button[] buttons;
	
	public ScheduleActivity()
	{
		super(R.layout.schedule,R.menu.schedule_menu);
		this.buttons = new Button[DayOfWeek.getSize()];
	}

	@Override
	protected void initializeViews() 
	{
		zoneTitleTV = (TextView) findViewById(R.id.zoneTitle);
		zoneTitleTV.setOnClickListener(new ZoneTitleClickListener(this));
		for(DayOfWeek d: DayOfWeek.values())
		{
			buttons[d.getV()] = (Button) findViewById(getButtonID(d));
			buttons[d.getV()].setOnClickListener(new ScheduleClickListener(this,d));
		}
	}

	@Override
	protected void updateViews() 
	{
		HvacSystem h = manager.getHvac();
		String zt = h.getZoneTitle();
		zoneTitleTV.setText(Html.fromHtml("<u>" + zt + "</u>"));
		for(DayOfWeek d: DayOfWeek.values())
			buttons[d.getV()].setText(manager.getSchedule().getDaySchedule(d).toString());
	}

	public void updateWeeklySchedule(DayOfWeek day, int newSchedule) 
	{
		sendCommand(TCP_CMD_UPDATE_WEEKLY_SCHEDULE,(new Object[]{day,manager.getDaySchedules().get(newSchedule)}));
		buttons[day.getV()].setText(manager.getDayScheduleNames()[newSchedule]);
	}
	
	@Override
	public void updateZoneTitle(String zt) 
	{
		sendCommand(TCP_CMD_SET_ZONE_TITLE,zt);
		zoneTitleTV.setText(Html.fromHtml("<u>" + zt + "</u>"));
	}

	public CharSequence[] getDayScheduleNames() {
		return manager.getDayScheduleNames();
	}

	public int getDayScheduleIndex(DayOfWeek day) {
		return manager.getDayIndex(day);
	}
	
	public void editDay(int position) {
		startNewCreateDayActivity(manager.getDaySchedule(position));
	}
	
	private void startNewCreateDayActivity(DaySchedule dailySchedule) 
	{
		Intent ccIntent = new Intent(this, CreateDayActivity.class);
		ccIntent.putExtra(DAYSCHEDULE,dailySchedule);
		ccIntent.putExtra(HAS_DAYSCHEDULE,true);
		this.startActivity(ccIntent);
	}
	
	public void startNewCreateDayActivity() {
		startNewCreateDayActivity(new DaySchedule(null,2));
	}

	private int getButtonID(DayOfWeek d) 
	{
		switch(d)
		{
			case SUNDAY:
				return R.id.sundayButton;
			case MONDAY:
				return R.id.mondayButton;
			case TUESDAY:
				return R.id.tuesdayButton;
			case WEDNESDAY:
				return R.id.wedsButton;
			case THURSDAY:
				return R.id.thursButton;
			case FRIDAY:
				return R.id.friButton;
			case SATURDAY:
				return R.id.satButton;
			default:
				return -1;			
		}
	}
	
	@Override
	protected void setInputEnabled(boolean enabled) 
	{
		for(DayOfWeek d: DayOfWeek.values())
			buttons[d.getV()].setClickable(enabled);
	}

	@Override
	public String getZoneTitle() {
		return manager.getHvac().getZoneTitle();
	}

	@Override
	public Context getImplementingClass() {
		return this;
	}

	@Override
	protected void onRefreshActivity() {
		sendCommand(TCP_CMD_REFRESH_SCHEDULE_ACTIVITY);
	}
}
