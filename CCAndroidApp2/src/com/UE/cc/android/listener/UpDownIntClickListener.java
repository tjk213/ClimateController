package com.UE.cc.android.listener;

import com.UE.cc.android.ui.UpDownIntActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public abstract class UpDownIntClickListener implements OnClickListener 
{
	private UpDownIntActivity activity;
	private EditText intEditor;
	private AlertDialog setIntDialog;
	private String intAsString;
	private int upDownInt;
	private final AlertDialog.Builder intDialogBuilder;
	
	public UpDownIntClickListener(UpDownIntActivity act)
	{
		this.activity = act;
		intDialogBuilder = new AlertDialog.Builder(activity.getImplementingClass());
		intDialogBuilder.setTitle(getTitle());
		/*
		 * Set button click listener is set to null here because it is
		 * overridden in onClick() below. Creating the clickListener here
		 * will not provide the ability to parse the input for errors
		 * before dismissing the dialog
		 */
		intDialogBuilder.setPositiveButton("Set", null);
		/*
		 * Default clickListener simply dismisses the dialog, so no
		 * explicit clickListener is defined here
		 */
		intDialogBuilder.setNegativeButton("Cancel", null);
	}
	
	@Override
	public void onClick(View v) 
	{
		setIntDialog = buildIntDialog();
		/*
		 * Message must be non-null prior to showing dialog in
		 * order to display error message later. This is viewed
		 * as a bug and may be unnecessary in later OS 
		 * (current = 4.2.1)
		 */
		setIntDialog.setMessage("");
		setIntDialog.setOnShowListener(new DialogInterface.OnShowListener() 
		{	
			@Override
			public void onShow(DialogInterface dialog) 
			{
				Button pos = setIntDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				pos.setOnClickListener(new View.OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						intAsString = intEditor.getText().toString();
						try 
						{
							upDownInt = Integer.parseInt(intAsString);
							if(upDownInt < getIntMin() || upDownInt > getIntMax())
								displayError();
							else
							{
								activity.updateInt(upDownInt);
								setIntDialog.dismiss();
							}
						}
						catch(NumberFormatException ex) 
						{
							displayError();
						}
					}

					private void displayError() 
					{
						intEditor.setText("");
						setIntDialog.setMessage("Error: " + getErrorMsg());
					}
				});
			}
		});
		setIntDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		setIntDialog.show();
	}
	
	private AlertDialog buildIntDialog()
	{
		intEditor = new EditText(activity.getImplementingClass());
		intEditor.setInputType(InputType.TYPE_CLASS_NUMBER);
		intDialogBuilder.setView(intEditor); 
		return intDialogBuilder.create();
	}
	
	protected abstract String getTitle();
	protected abstract String getErrorMsg();
	protected abstract int getIntMin();
	protected abstract int getIntMax();
}
