package com.UE.cc.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import com.UE.cc.domain.DaySchedule;
import com.UE.cc.domain.HvacSystem;
import com.UE.cc.domain.Schedule;
import com.UE.cc.util.CCCommand;
import com.UE.cc.util.DayOfWeek;

public class AndroidProtocol extends ClientProtocol
{
	private static int objectCount = 0;
	
	public AndroidProtocol(Socket s, CCServerManager ccsm) 
	{
		super("Android-"+objectCount++,s,ccsm);
		androidProtos.add(this);
	}
	
	@Override
	protected void setup() throws IOException
	{
		this.in = new ObjectInputStream(clientSocket.getInputStream());
		this.out = new ObjectOutputStream(clientSocket.getOutputStream());
	}
	
	@Override
	protected void loop() throws IOException, ClassNotFoundException
	{
		CCCommand<?> c = (CCCommand<?>) ((ObjectInputStream) in).readObject();
		CCServerManager.printToStandardOut("CMD Read: " + c.getCommand());
		processCCCommand(c);
	}
	
	@Override
	public void sendCommand(Object outbound) throws IOException
	{
		if(!(outbound instanceof CCCommand<?>)) throw new IOException("Invalid Type sent to AndroidProtocol.sendCommand()");
		if(out != null)
		{
			((ObjectOutputStream) out).reset();
			((ObjectOutputStream) out).writeObject(outbound);
		}
	}

	public void processCCCommand(CCCommand<?> c) throws IOException,ClassNotFoundException
	{
		if(c.getCommand().equals(TCP_CMD_SET_SET_POINT))
		{
			Integer newSetPoint = (Integer)c.getParam(0);
			manager.getHvac().setSetPoint(newSetPoint);
			ArduinoProtocol.forwardToArduino(TCP_CMD_SET_SET_POINT + "\n" + newSetPoint.toString());
			forwardToAndroids(c);
		}
		else if(c.getCommand().equals(TCP_CMD_SET_OP_MODE))
		{
			Integer newOpMode = (Integer)c.getParam(0);
			manager.getHvac().setOpMode(newOpMode);
			ArduinoProtocol.forwardToArduino(TCP_CMD_SET_OP_MODE + "\n" + newOpMode.toString());
			forwardToAndroids(c);
		}
		else if(c.getCommand().equals(TCP_CMD_SET_FAN_MODE))
		{
			Integer newFanMode = (Integer)c.getParam(0);
			manager.getHvac().setFanMode(newFanMode);
			ArduinoProtocol.forwardToArduino(TCP_CMD_SET_FAN_MODE + ARDUINO_PARAM_DELIMETER + newFanMode.toString());
			forwardToAndroids(c);
		}
		else if(c.getCommand().equals(TCP_CMD_SET_ZONE_TITLE))
		{
			manager.getHvac().setZoneTitle((String)c.getParam(0));
			forwardToAndroids(c);
		}
		else if(c.getCommand().equals(TCP_CMD_GET_WEEK_SCHEDULE))
			sendCommand(new CCCommand<Schedule>(c.getCommand(),manager.getSchedule()));
		else if(c.getCommand().equals(TCP_CMD_UPDATE_WEEKLY_SCHEDULE))
		{
			manager.getSchedule().setSchedule((DayOfWeek)c.getParam(0),(DaySchedule)c.getParam(1));
			forwardToAndroids(c);
		}
		else if(c.getCommand().equals(TCP_CMD_GET_DAY_SCHEDULES))
			sendCommand(new CCCommand<ArrayList<DaySchedule>>(c.getCommand(),manager.getDaySchedules()));
		else if(c.getCommand().equals(TCP_CMD_GET_HVAC))
			sendCommand(new CCCommand<HvacSystem>(TCP_CMD_GET_HVAC,manager.getHvac()));
		else if(c.getCommand().equals(TCP_CMD_SAVE_DAY))
		{
			DaySchedule d = (DaySchedule) c.getParam(0);
			if(manager.saveDay(d))
				forwardToAndroids(new CCCommand<ArrayList<DaySchedule>>(c.getCommand(),manager.getDaySchedules()));
			else
				sendCommand(null);
		}
		else if(c.getCommand().equals(TCP_CMD_SET_HOLD))
		{
			manager.getHvac().setOnHold((Boolean)c.getParam(0));
			Integer newHold;
			if(manager.getHvac().isOnHold())
				newHold = ON;
			else
				newHold = OFF;
			ArduinoProtocol.forwardToArduino(TCP_CMD_SET_HOLD + ARDUINO_PARAM_DELIMETER + newHold.toString());
			forwardToAndroids(c);
		}
		else if(c.getCommand().equals(TCP_CMD_SET_SCHEDULE))
		{
			manager.setSchedule((Schedule) c.getParam(0));
			manager.saveDaysInSchedule(manager.getSchedule());
			forwardToAndroids(new CCCommand<Object>(TCP_CMD_SET_SCHEDULE,new Object[]{manager.getSchedule(),manager.getDaySchedules()}));
			
		}
		else if(c.getCommand().equals(TCP_CMD_DELETE_DAY))
		{
			DaySchedule toBeRemoved = (DaySchedule) c.getParam(0);
			manager.remove(toBeRemoved);
			forwardToAndroids(new CCCommand<Object>(TCP_CMD_DELETE_DAY,new Object[]{manager.getSchedule(),manager.getDaySchedules()}));
		}
		else if(c.getCommand().equals(TCP_CMD_REFRESH_SCHEDULE_ACTIVITY))
			sendCommand(new CCCommand<Object>(TCP_CMD_REFRESH_SCHEDULE_ACTIVITY,new Object[]{manager.getSchedule(),manager.getDaySchedules()}));
		else
		{
			CCServerManager.printToStandardErr("AndroidProtocol.processCCCommand() - ERROR: Unknown Command:" + c.getCommand());
			sendCommand(new CCCommand<Void>("Unknown Command"));
		}
	}
	
	public static void forwardToAndroids(CCCommand<?> cmd) throws IOException
	{
		Iterator<AndroidProtocol> iter = androidProtos.iterator();
		while(iter.hasNext())
		{
			AndroidProtocol a = iter.next();
			if(a != null)
			{
				CCServerManager.printToStandardOut("Sending CMD to " + a.getName() + ": " + cmd.toString());
				a.sendCommand(cmd);
			}
		}
	}
}
