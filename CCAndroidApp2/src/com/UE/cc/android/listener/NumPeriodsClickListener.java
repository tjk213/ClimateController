package com.UE.cc.android.listener;

import com.UE.cc.android.ui.UpDownIntActivity;
import com.UE.cc.common.CCConstants;

public class NumPeriodsClickListener extends UpDownIntClickListener implements CCConstants 
{
	
	public NumPeriodsClickListener(UpDownIntActivity act)
	{
		super(act);
	}
	@Override
	protected String getTitle() {
		return "Set Number of Programmable Periods";
	}

	@Override
	protected String getErrorMsg() {
		return "Number must be an integer between " + getIntMin() + " and " + getIntMax();
	}

	@Override
	protected int getIntMin() {
		return NUM_PERIODS_MIN;
	}

	@Override
	protected int getIntMax() {
		return NUM_PERIODS_MAX;
	}

}
