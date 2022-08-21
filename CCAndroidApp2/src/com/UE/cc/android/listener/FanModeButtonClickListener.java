package com.UE.cc.android.listener;

import com.UE.cc.android.ui.ModeButtonActivity;
import com.UE.cc.common.CCConstants;

public class FanModeButtonClickListener extends ModeButtonClickListener implements CCConstants
{
	
	public FanModeButtonClickListener(ModeButtonActivity act, int rowID)
	{
		this.activity = act;
		this.rowID = rowID;
	}
	
	@Override
	protected String getTitle() {
		return FAN_MODE_SELECT_TITLE;
	}

	@Override
	protected String[] getModes() {
		return FAN_MODES;
	}

	@Override
	protected int getCurrentMode() {
		return activity.getFanMode(rowID);
	}

	@Override
	protected void updateMode(int newMode) {
		activity.updateFanMode(rowID,newMode);
	}
}
