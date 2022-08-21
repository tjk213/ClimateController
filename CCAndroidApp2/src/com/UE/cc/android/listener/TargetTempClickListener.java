package com.UE.cc.android.listener;

import com.UE.cc.android.ui.UpDownIntActivity;
import com.UE.cc.common.CCConstants;

public class TargetTempClickListener extends UpDownIntClickListener implements CCConstants 
{
	public TargetTempClickListener(UpDownIntActivity act)
	{
		super(act);
	}
	
	@Override
	protected String getTitle() {
		return "Set Target Temperature";
	}

	@Override
	protected String getErrorMsg() {
		return "Target Temperature must be an integer between " + getIntMin() + " and " + getIntMax();
	}

	@Override
	protected int getIntMin() {
		return TARGET_TEMP_MIN;
	}

	@Override
	protected int getIntMax() {
		return TARGET_TEMP_MAX;
	}

}
