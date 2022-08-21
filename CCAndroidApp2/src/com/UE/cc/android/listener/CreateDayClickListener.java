package com.UE.cc.android.listener;

import com.UE.cc.android.ui.CreateDayActivity;

import android.view.View;
import android.view.View.OnClickListener;

public class CreateDayClickListener implements OnClickListener 
{
	private CreateDayActivity activity;
	
	public CreateDayClickListener(CreateDayActivity act)
	{
		this.activity = act;
	}
	
	@Override
	public void onClick(View v) 
	{
		activity.createDay();
	}
}
