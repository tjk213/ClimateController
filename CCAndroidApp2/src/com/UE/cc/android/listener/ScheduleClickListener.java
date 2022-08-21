package com.UE.cc.android.listener;

import com.UE.cc.R;
import com.UE.cc.android.ui.ScheduleActivity;
import com.UE.cc.util.DayOfWeek;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScheduleClickListener implements OnClickListener 
{
	private ScheduleActivity activity;
	private DayOfWeek day;
	private AlertDialog dayScheduleSelect;
	private AlertDialog.Builder daySelectBuilder;
	private LinearLayout customTitle;

	public ScheduleClickListener(ScheduleActivity act, DayOfWeek day)
	{
		this.activity = act;
		this.day = day;
		this.daySelectBuilder = new AlertDialog.Builder(activity);
	}
	
	@Override
	public void onClick(View v) 
	{
		customTitle = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.schedule_dialog_title,null);
		TextView tv = (TextView)customTitle.getChildAt(0);
		tv.setText("Select Schedule for " + day);
		daySelectBuilder.setCustomTitle(customTitle);
		daySelectBuilder.setNegativeButton("Cancel", null);
		daySelectBuilder.setNeutralButton("Create New", new DialogInterface.OnClickListener() 
		{	
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				activity.startNewCreateDayActivity();
			}
		});
		daySelectBuilder.setSingleChoiceItems(activity.getDayScheduleNames(),activity.getDayScheduleIndex(day), new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				activity.updateWeeklySchedule(day,which);
				dayScheduleSelect.dismiss();
			}
		});
		dayScheduleSelect = daySelectBuilder.create();
		dayScheduleSelect.setOnShowListener(new OnShowListener() 
		{
			@Override
			public void onShow(DialogInterface dialog) 
			{
				dayScheduleSelect.getListView().setOnItemLongClickListener(new OnItemLongClickListener() 
				{
					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) 
					{
						activity.editDay(position);
						return true;
					}
				});
			}
		});
		dayScheduleSelect.show();
	}
}
