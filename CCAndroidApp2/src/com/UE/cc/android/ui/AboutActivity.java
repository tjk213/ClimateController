package com.UE.cc.android.ui;

import com.UE.cc.R;

//TODO:Add layer of indirection or change this activity to a dialog so unimplemented methods are not needed
public class AboutActivity extends CCActivity {

	public AboutActivity() {
		super(R.layout.about,R.menu.about_menu);
	}
	
	@Override
	protected void initializeViews() {
	}

	@Override
	protected void updateViews() {
	}
	
	@Override
	protected void enableHomeButton(){}
	
	@Override
	protected void setInputEnabled(boolean enabled){}

	@Override
	protected void onRefreshActivity() {}
}
