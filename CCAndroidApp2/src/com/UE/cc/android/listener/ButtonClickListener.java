package com.UE.cc.android.listener;


import com.UE.cc.android.ui.UpDownIntActivity;

import android.view.View;
import android.view.View.OnClickListener;

public class ButtonClickListener implements OnClickListener
{
	private int delta;
	private UpDownIntActivity activity;
	
	public ButtonClickListener(UpDownIntActivity act, int delta)
	{
		this.delta = delta;
		this.activity = act;
	}
	
	@Override
	public void onClick(View v)
	{
		activity.stepInt(delta);
	}
}
