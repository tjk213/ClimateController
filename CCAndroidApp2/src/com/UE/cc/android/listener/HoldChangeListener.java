package com.UE.cc.android.listener;

import com.UE.cc.android.ui.StatusActivity;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class HoldChangeListener implements OnCheckedChangeListener
{
	private StatusActivity activity;
	
	public HoldChangeListener(StatusActivity act)
	{
		this.activity = act;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{
		activity.updateHold();
	}
	

}
