package com.UE.cc.android.listener;

import com.UE.cc.android.ui.ZoneTitleActivity;
import com.UE.cc.common.CCConstants;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class ZoneTitleClickListener implements OnClickListener,CCConstants 
{
	private ZoneTitleActivity activity;
	private AlertDialog.Builder builder;
	private AlertDialog alertDialog;
	private EditText et;
	
	public ZoneTitleClickListener(ZoneTitleActivity act)
	{
		this.activity = act;
		this.builder = new AlertDialog.Builder(activity.getImplementingClass());
		builder.setTitle("Rename Zone");
		/*
		 * Set button click listener is set to null here because it is
		 * overridden in onClick() below. Creating the clickListener here
		 * will not provide the ability to parse the input for errors
		 * before dismissing the dialog
		 */
		builder.setPositiveButton("Set", null);
		/*
		 * Default clickListener simply dismisses the dialog, so no
		 * explicit clickListener is defined here
		 */
		builder.setNegativeButton("Cancel", null);
	}
	
	@Override
	public void onClick(View v) 
	{
		et = new EditText(activity.getImplementingClass());
		et.setText(activity.getZoneTitle());
		et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		builder.setView(et);
		alertDialog = builder.create();
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() 
		{	
			@Override
			public void onShow(DialogInterface dialog) 
			{
				Button pos = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				pos.setOnClickListener(new View.OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						String newTitle = et.getText().toString();
						Log.d("ZTCL.onClick()","Title: " + newTitle);
						if(newTitle == null) et.setError("Title Must Be Created");
						else if(newTitle.isEmpty()) et.setError("Title Must Be Greater than 0 Characters");
						else if(newTitle.length()>ZONE_TITLE_MAX_LENGTH) et.setError("Title must be less than " + ZONE_TITLE_MAX_LENGTH + "Characters");
						else if(newTitle.contains("-") || newTitle.contains("\n")) et.setError("Title cannot contain '-' or '\\n'");
						else
						{
							activity.updateZoneTitle(newTitle);
							alertDialog.dismiss();
						}
					}
				});
			}
		});
		alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		alertDialog.show();
	}
}
