package com.UE.cc.android.listener;

import com.UE.cc.android.ui.ModeButtonActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class ModeButtonClickListener implements OnClickListener
{
	private AlertDialog modeSelect;
	protected ModeButtonActivity activity;
	protected int rowID;
	
	@Override
	public void onClick(View v) 
	{
		AlertDialog.Builder modeSelectBuilder = new AlertDialog.Builder(activity.getImplementingClass());
		modeSelectBuilder.setTitle(getTitle());
		modeSelectBuilder.setSingleChoiceItems(getModes(), getCurrentMode(), new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				updateMode(which);
				modeSelect.dismiss();
			}
		});
		modeSelect = modeSelectBuilder.show();
	}

	protected abstract String getTitle();
	protected abstract String[] getModes();
	protected abstract int getCurrentMode();
	protected abstract void updateMode(int newMode);
}
