package com.UE.cc.domain;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.UE.cc.common.CCConstants;
import com.UE.cc.util.CCCommand;
import com.UE.cc.util.DayOfWeek;

public class CCManager implements CCConstants
{
	private TCPClient tcp;
	private HvacSystem hvac;
	private Schedule schedule;
	private ArrayList<DaySchedule> daySchedules = new ArrayList<DaySchedule>();
	
	public CCManager() throws UnknownHostException {
		this.tcp = new TCPClient();
	}
	
	public void sendCommand(CCCommand<?> outgoingCmd) throws IOException {
		tcp.sendCommand(outgoingCmd);
	}
	
	public void receiveCommand() throws ClassNotFoundException, IOException
	{
		CCCommand<?> incomingCommand = tcp.waitForCommand();
		String cmd = incomingCommand.getCommand();
		if(cmd.equals(TCP_CMD_GET_HVAC))
			hvac = (HvacSystem) incomingCommand.getParam(0);		
		else if(cmd.equals(TCP_CMD_GET_WEEK_SCHEDULE))
			schedule = (Schedule) incomingCommand.getParam(0);		
		else if(cmd.equals(TCP_CMD_GET_DAY_SCHEDULES) || cmd.equals(TCP_CMD_SAVE_DAY))
			daySchedules = extractArrayList(incomingCommand.getParam(0));
		else if(cmd.equals((TCP_CMD_SET_CURRENT_TEMP)))
			hvac.setCurrentTemp((Integer)incomingCommand.getParam(0));
		else if(cmd.equals((TCP_CMD_SET_HVAC_STATUS)))
			hvac.setHvacStatus((Integer)incomingCommand.getParam(0));
		else if(cmd.equals((TCP_CMD_SET_FAN_STATUS)))
			hvac.setFanStatus((Integer)incomingCommand.getParam(0));
		else if(cmd.equals(TCP_CMD_SET_SET_POINT))
			hvac.setSetPoint((Integer) incomingCommand.getParam(0));
		else if(cmd.equals(TCP_CMD_SET_OP_MODE))
			hvac.setOpMode((Integer) incomingCommand.getParam(0));
		else if(cmd.equals(TCP_CMD_SET_FAN_MODE))
			hvac.setFanMode((Integer) incomingCommand.getParam(0));
		else if(cmd.equals(TCP_CMD_SET_ZONE_TITLE))
			hvac.setZoneTitle((String) incomingCommand.getParam(0));
		else if(cmd.equals(TCP_CMD_UPDATE_WEEKLY_SCHEDULE))
		{
//			int newDay = (Integer) incomingCommand.getParam(0);
//			int newVal = (Integer) incomingCommand.getParam(1);
//			DayOfWeek day = DayOfWeek.get(newDay);
			if(schedule != null)
			schedule.setSchedule((DayOfWeek)incomingCommand.getParam(0),(DaySchedule)incomingCommand.getParam(1));
		}
		else if(cmd.equals(TCP_CMD_SET_HOLD))
			hvac.setOnHold((Boolean)incomingCommand.getParam(0));
		else if(cmd.equals(TCP_CMD_SET_SCHEDULE) || cmd.equals(TCP_CMD_DELETE_DAY) || cmd.equals(TCP_CMD_REFRESH_SCHEDULE_ACTIVITY))
		{
			schedule = (Schedule) incomingCommand.getParam(0);
			daySchedules = extractArrayList(incomingCommand.getParam(1));
		}
	}

	@SuppressWarnings("unchecked")
	private ArrayList<DaySchedule> extractArrayList(Object o) {
		return (ArrayList<DaySchedule>) o;
	}
	
	public String[] getDayScheduleNames() 
	{
		String[] names = new String[daySchedules.size()];
		for(int i=0; i<daySchedules.size(); i++)
			names[i] = (daySchedules.get(i).toString());
		return names;
	}
	
	public DaySchedule getDaySchedule(int index) {
		return daySchedules.get(index);
	}

	public int getDayIndex(DayOfWeek day)
	{
		DaySchedule d = schedule.getDaySchedule(day);
		return daySchedules.indexOf(d);
	}

	public DaySchedule getDaySchedule(String name) 
	{//TODO: eliminate need to create new dayschedule here
		int i = daySchedules.indexOf(new DaySchedule(name,0));
		if(i != -1)
			return daySchedules.get(i);
		return null;
	}
	
	public HvacSystem getHvac() {
		return hvac;
	}
	
	public Schedule getSchedule() {
		return schedule;
	}

	public ArrayList<DaySchedule> getDaySchedules() {
		return daySchedules;
	}
}
