package com.UE.cc.android.listener;

import com.UE.cc.android.ui.ModeButtonActivity;
import com.UE.cc.common.CCConstants;

public class OpModeButtonClickListener extends ModeButtonClickListener implements CCConstants 
{

	public OpModeButtonClickListener(ModeButtonActivity act,int rowID)
	{
		this.activity = act;
		this.rowID = rowID;
	}
	
	@Override
	protected String getTitle() 
	{
		return OP_MODE_SELECT_TITLE;
	}

	@Override
	protected String[] getModes() {
		return OPERATION_MODES;
	}

	@Override
	protected int getCurrentMode() {
		return activity.getOpMode(rowID); 
	}

	@Override
	protected void updateMode(int newMode) {
		activity.updateOpMode(rowID,newMode);
	}
	
}
