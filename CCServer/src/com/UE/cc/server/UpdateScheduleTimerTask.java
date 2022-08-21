package com.UE.cc.server;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimerTask;

import com.UE.cc.domain.DaySchedule;
import com.UE.cc.domain.ProgrammablePeriod;
import com.UE.cc.domain.HvacSystem;
import com.UE.cc.util.CCCommand;
import com.UE.cc.util.DayOfWeek;
import com.UE.cc.util.Time;
import com.UE.cc.common.CCConstants;

public class UpdateScheduleTimerTask extends TimerTask implements CCConstants
{
	private CCServerManager manager;
	private ProgrammablePeriod currentPeriod;
	
	public UpdateScheduleTimerTask(CCServerManager ccsm) {
		this.manager = ccsm;
	}
	
	@Override
	public void run() 
	{
		if(updateHvacFromSchedule())
		{
			CCServerManager.printToStandardOut("Update From Schedule: " + currentPeriod.toString());
			
			try 
			{
				AndroidProtocol.forwardToAndroids(new CCCommand<HvacSystem>(TCP_CMD_GET_HVAC,manager.getHvac()));
				ArduinoProtocol.updateArduino();
			} 
			catch (IOException e) 
			{
				CCServerManager.printToStandardErr(e.toString());
				e.printStackTrace();
			}
		}
	}
	
	public boolean updateHvacFromSchedule()
	{
		DayOfWeek currentDay = getCurrentDay();
		DaySchedule currentDaySchedule = manager.getSchedule().getDaySchedule(currentDay);
		Time now = getCurrentTime();
		currentPeriod = currentDaySchedule.getPeriod(now);
		return manager.getHvac().updateProgrammablePeriod(currentPeriod);
	}

	private DayOfWeek getCurrentDay() 
	{
		Calendar c = Calendar.getInstance();
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		return DayOfWeek.get(dayOfWeek); //Note: ints assigned to each day in Calendar class must match values assigned in DayOfWeek enum
	}

	private Time getCurrentTime() 
	{
		Calendar c = Calendar.getInstance();
		int hr = c.get(Calendar.HOUR_OF_DAY);
		int min = c.get(Calendar.MINUTE);
		return new Time(hr,min);
	}
}
