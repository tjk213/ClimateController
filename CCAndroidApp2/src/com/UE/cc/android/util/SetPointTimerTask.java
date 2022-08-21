package com.UE.cc.android.util;

import java.util.TimerTask;

import com.UE.cc.android.ui.StatusActivity;

/**
 * TimerTask used to delay processing of incrementing/decrementing
 * an UpDownInt. This allows the user to tap up/down buttons multiple
 * times without sending multiple sockets, causing the UI to be unresponsive,
 * or creating shared variable problems
 * @author TK
 */
public class SetPointTimerTask extends TimerTask
{
	private StatusActivity activity;
	private int newInt;
	
	public SetPointTimerTask(StatusActivity act, int newInt)
	{
		this.activity = act;
		this.newInt = newInt;
	}
	
	@Override
	public void run() {
		activity.executeStepSetPoint(newInt);
	}
}
