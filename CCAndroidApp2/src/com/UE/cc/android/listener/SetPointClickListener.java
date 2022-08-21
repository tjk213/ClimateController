package com.UE.cc.android.listener;

import com.UE.cc.android.ui.CreateDayActivity;
import com.UE.cc.common.CCConstants;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.NumberPicker;

public class SetPointClickListener implements OnClickListener,CCConstants 
{
	private CreateDayActivity activity;
	private NumberPicker np;
	private int periodNum;
	private AlertDialog setPointSelect;

	public SetPointClickListener(CreateDayActivity act, int periodNum)
	{
		this.activity = act;
		this.periodNum = periodNum;
	}
	
	@Override
	public void onClick(View v) 
	{
		AlertDialog.Builder setPointSelectBuilder = new AlertDialog.Builder(activity.getImplementingClass());
		setPointSelectBuilder.setTitle(String.format("Set the Set Point for Period #%d",periodNum+1));
		np = new NumberPicker(activity.getImplementingClass());
		np.setMaxValue(TARGET_TEMP_MAX);
		np.setMinValue(TARGET_TEMP_MIN);
		np.setValue(activity.getSetPoint(periodNum));
		np.setWrapSelectorWheel(false);
		setPointSelectBuilder.setView(np);
		setPointSelectBuilder.setNegativeButton("Cancel",null);
		setPointSelectBuilder.setPositiveButton("Set", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				activity.updateSetPoint(periodNum,np.getValue());
				setPointSelect.dismiss();
			}
		});
		setPointSelect = setPointSelectBuilder.show();
	}
}
