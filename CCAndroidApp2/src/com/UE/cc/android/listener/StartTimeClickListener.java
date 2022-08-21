package com.UE.cc.android.listener;

import com.UE.cc.android.ui.CreateDayActivity;
import com.UE.cc.util.Time;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TimePicker;

public class StartTimeClickListener implements OnClickListener
{
	private CreateDayActivity activity;
	private int periodNum;
	private TimePicker tp;
	
	public StartTimeClickListener(CreateDayActivity act, int p)
	{
		this.activity = act;
		this.periodNum = p;
	}
	
	@Override
	public void onClick(View v) 
	{
		AlertDialog.Builder startTimeSelectBuilder = new AlertDialog.Builder(activity.getImplementingClass());
		startTimeSelectBuilder.setTitle("Set the Start Time for Period #" + (periodNum+1));
		tp = new TimePicker(activity.getImplementingClass());
		Time currentStartTime = activity.getStartTime(periodNum);
		tp.setCurrentHour(currentStartTime.getHour());
		tp.setCurrentMinute(currentStartTime.getMin());
		startTimeSelectBuilder.setView(tp);
		startTimeSelectBuilder.setNegativeButton("Cancel",null);
		startTimeSelectBuilder.setPositiveButton("Set", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				activity.updateStartTime(periodNum,new Time(tp.getCurrentHour(),tp.getCurrentMinute()));
			}
		});
		startTimeSelectBuilder.show();
	}
}
