package com.UE.cc.android.ui;

import android.content.Context;

public interface ModeButtonActivity 
{
	public void updateOpMode(int rowID, int newOpMode);
	
	public void updateFanMode(int rowID, int newFanMode);
	
	public int getOpMode(int rowID);
	
	public int getFanMode(int rowID);
	
	public Context getImplementingClass();
}
