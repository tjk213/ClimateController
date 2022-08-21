package com.UE.cc.android.ui;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.UE.cc.domain.CCManager;
import com.UE.cc.domain.DaySchedule;
import com.UE.cc.domain.Schedule;
import com.UE.cc.common.CCConstants;
import com.UE.cc.util.CCCommand;
import com.UE.cc.util.DayScheduleComparator;
import com.UE.cc.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

public abstract class CCActivity extends Activity implements CCConstants
{
	static protected CCManager manager;
	static private ReceiveCommandTask receiver;
	static
	{
		try {
			manager = new CCManager();
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	private int layoutID;
	private int menuID;
	protected MenuItem refresh;

	protected CCActivity(int layoutID, int menuID)
	{		
		this.layoutID = layoutID;
		this.menuID = menuID;
	}

	@Override
    final protected void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
		setContentView(this.layoutID);
		enableHomeButton();
		initializeViews();
    }
	
	@Override
	final protected void onResume()
	{
		super.onResume();
		if(receiver != null) receiver.cancel(true);
		receiver = new ReceiveCommandTask();
		receiver.execute();
	}
	
	@Override final protected void onRestart()
	{
		super.onRestart();
		updateViews();
	}
	
	protected void enableHomeButton() {
		getActionBar().setHomeButtonEnabled(true);
	}

	/**
	 * Called from CCActivity.onCreate()
	 * 
	 * Typically, all views will be initialized with findViewById() 
	 * and all click listeners will be set within this method
	 */
	protected abstract void initializeViews();
	
	/**
	 * Called in 2 situations:
	 * 	1. from CCActivity.onRestart() to ensure that every activity
	 * 		running in background is synchronized with underlying
	 * 		domain layer before it is brought to the foreground
	 * 	2. By ReceiveCommandTask.onPostExecute() after a CCCommand
	 * 		is received via TCP Socket
	 * 
	 * Typically, this method will refresh data in any of the Activity's
	 * views with the data currently stored in the domain layer
	 * via this.manager
	 */
	protected abstract void updateViews();
	
	/**
	 * Called in 2 situations:
	 * 	1. from CCActivity.onPrepareOptionsMenu() [called after onResume()]
	 * 	2. from CCActivity.onOptionsItemSelected() when user presses refresh button
	 * 
	 * Typically, this method will create a new SendCommandTask via CCActivity.sendCommand()
	 * to ensure that all objects within the domain layer that the activity references have
	 * been updated with the most recent information from the server
	 * @return true
	 */
    protected abstract void onRefreshActivity();
	
	/**
	 * Control clickability of all input views on Status Activity
	 * 
	 * This method is called while a SendCommandTask is running in
	 * background to avoid any possible race conditions and to
	 * prevent the user from making changes based on false or
	 * outdated data
	 * @param enabled - true = enabled; false = disabled
	 */
	protected abstract void setInputEnabled(boolean enabled);
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(menuID, menu);
        super.onCreateOptionsMenu(menu);
        refresh = menu.findItem(R.id.menu_refresh);
    	onRefreshActivity(); //called here because this must occur after onCreate() has returned
    	return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		switch(item.getItemId())
		{
		case android.R.id.home:
            return startNewActivity(this, StatusActivity.class); //TODO: should this kill instance of edit day activity?
		case R.id.menu_refresh:	
            onRefreshActivity();
            return true;
		case R.id.menu_view_schedule:
			return startNewActivity(this,ScheduleActivity.class);
		case R.id.menu_about:
			return startNewActivity(this, AboutActivity.class);	
		case R.id.menu_new_day:
			return startNewActivity(this, CreateDayActivity.class);
		case R.id.menu_status:
			return startNewActivity(this, StatusActivity.class);
		case R.id.menu_reset:
			resetSchedule();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void resetSchedule() 
	{
		DaySchedule weekend = manager.getDaySchedule(WEEKEND);
		DaySchedule weekday = manager.getDaySchedule(WEEKDAY);
		DayScheduleComparator c = new DayScheduleComparator();
		if((weekend != null && (c.compare(weekend,DaySchedule.getDefaultWeekend())) != 0) || (weekday != null) && (c.compare(weekday,DaySchedule.getDefaultWeekday())) != 0) 
		{
			AlertDialog.Builder warningDialogBuilder = new AlertDialog.Builder(this);
			warningDialogBuilder.setTitle("Are You Sure?");
			warningDialogBuilder.setMessage("Resetting the schedule will overwrite your current DaySchedules saved as 'Weekend' and 'Weekday'");
			warningDialogBuilder.setNegativeButton("Cancel",null);
			warningDialogBuilder.setPositiveButton("Reset Schedule",new DialogInterface.OnClickListener() 
			{	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					sendCommand(TCP_CMD_SET_SCHEDULE,Schedule.getDefaultSchedule());
				}
			});
			warningDialogBuilder.show();
		}
		else {
			sendCommand(TCP_CMD_SET_SCHEDULE,Schedule.getDefaultSchedule());
		}
	}

	/**
	 * Method to start new activity
	 * @param currentActivity - CCActivity currently displayed
	 * @param newActivity - new <CCActivity>.class object to display
	 * @return true
	 */
	protected boolean startNewActivity(CCActivity currentActivity, Class<?> newActivity)
	{
		Intent ccIntent = new Intent(currentActivity.getApplicationContext(), newActivity);
		currentActivity.startActivity(ccIntent);
		return true;
	}
	
	protected void showErrorDialog(String msg) 
	{
		AlertDialog.Builder errorDialogBuilder = new AlertDialog.Builder(this);
		errorDialogBuilder.setTitle("Error:");
		errorDialogBuilder.setMessage(msg);
		errorDialogBuilder.setPositiveButton("Okay",null);
		errorDialogBuilder.show();
	}
	
	protected void sendCommand(String cmd) {
		new SendCommandTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new CCCommand<Void>(cmd));
	}
	
	protected void sendCommand(String cmd, String param) {
		new SendCommandTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new CCCommand<String>(cmd,param));
	}
	
	protected void sendCommand(String cmd, int param) {
		new SendCommandTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new CCCommand<Integer>(cmd,param));
	}
	
	protected void sendCommand(String cmd, boolean param) {
		new SendCommandTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new CCCommand<Boolean>(cmd,param));
	}
	
	protected void sendCommand(String cmd, Integer[] param) {
		new SendCommandTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new CCCommand<Integer>(cmd,param));
	}
	
	protected void sendCommand(String cmd, DaySchedule param) {
		new SendCommandTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new CCCommand<DaySchedule>(cmd,param));
	}
	
	protected void sendCommand(String cmd, Schedule param) {
		new SendCommandTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new CCCommand<Schedule>(cmd,param));
	}
	
	protected void sendCommand(String cmd, Object[] param) {
		new SendCommandTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,new CCCommand<Object>(cmd,param));
	}
	
	private void setLoading()
	{
		ProgressBar pb = new ProgressBar(CCActivity.this);
		pb.setIndeterminate(true);
		refresh.setActionView(pb);
		setInputEnabled(false);
	}
	
	private void clearLoading()
	{
		setInputEnabled(true);
		refresh.setActionView(null);
	}
	
	private abstract class CCCommandTask<E1,E2,E3> extends AsyncTask<E1,E2,E3>
	{
		private Exception error = null;
		
		protected void cancel(Exception e)
		{
			this.error = e;
			this.cancel(true);
		}
		
		@Override
		public void onCancelled(Object o)
		{
			if(error != null)
			{
				showErrorDialog("CCCommandTask.onCancelled() - ERROR: Cannot Communicate with Server. Please Try Again Later.");
				error.printStackTrace();
			}
		}
	}
	
	private class SendCommandTask extends CCCommandTask<CCCommand<?>,Void, Void>
	{
		@Override
		protected void onPreExecute() 
		{
			ProgressBar pb = new ProgressBar(CCActivity.this);
			pb.setIndeterminate(true);
			refresh.setActionView(pb);
			setInputEnabled(false);
		}
	
		@Override
		protected Void doInBackground(CCCommand<?>... ccCommand) 
		{
			try { manager.sendCommand(ccCommand[0]); } 
			catch (IOException e) { cancel(e); }
			return null;
		}
	}
	
	protected class ReceiveCommandTask extends CCCommandTask<Void,Void,Void>
	{
		@Override
		protected Void doInBackground(Void... params) 
		{
			try
			{
				while(!ReceiveCommandTask.this.isCancelled())
				{
					try 
					{
						manager.receiveCommand(); 
						break;
					} 
					catch (SocketTimeoutException e) {}
				}
			}
			catch(ClassNotFoundException e) { cancel(e); }
			catch(IOException e) { cancel(e); }
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v)
		{
			/* 
			 * Re-set loading in case loading was not already set.
			 * This occurs, for instance, when the server sends a 
			 * command spawned by the programmed schedule or the 
			 * arduino, rather than a response to an outgoing
			 * command from this device
			 */
			setLoading();
			updateViews();
			clearLoading();
			restartReceiverTask();
		}
		
		private void restartReceiverTask()
		{
			receiver = new ReceiveCommandTask();
			receiver.execute();
		}
	}
}
