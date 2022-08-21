package com.UE.cc.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;

import com.UE.cc.util.CCCommand;

public class ArduinoProtocol extends ClientProtocol
{
	private static int objectCount = 0;
	
	byte[] buffer = new byte[CMD_MAX_LENGTH];
	int length,param,numCmds;
	String rawString,cmd,cmdArgsString,rawRemainder = "",rawNew;
	String[] cmds,cmdArgsArray;
	
	public ArduinoProtocol(Socket s,CCServerManager m) 
	{
		super("Arduino-" + objectCount++,s,m);
		arduinoProtos.add(this);
	}
	
	@Override
	protected void setup() throws IOException
	{
		this.in = clientSocket.getInputStream();
		this.out = clientSocket.getOutputStream();
		initArduino();
	}
	
	@Override
	protected void loop() throws IOException
	{//TODO: after thread is killed by Exception (such as NumberFormatException as described below
		//arduino does not realize it is disconnnected and never reconnects. Fix this bug - ensure
		//arduino knows when its disconnected - reconnecting appears to work fine if it were to realize
		//its necessary
		length = in.read(buffer);
		rawNew = new String(buffer,0,length);
		rawString = rawRemainder + rawNew;
		cmds = rawString.split("~");
		numCmds = cmds.length;
		if(rawString.charAt(rawString.length()-1) != '~')
		{
			rawRemainder = cmds[cmds.length-1];
			numCmds--;
		}
		else
		{
			rawRemainder = "";
		}
		for(int i=0; i <numCmds; i++)
		{
			cmdArgsString = cmds[i];
			//TODO: change this delimeter - when -4 is returned as 
			//currentTemp due to loss of power on the preamp, an empty
			//token is interpreted as param and throws a NumberFormatException
			cmdArgsArray = cmdArgsString.split("-");
			cmd = cmdArgsArray[0];
			param = Integer.parseInt(cmdArgsArray[1]);
			CCServerManager.printToStandardOut("CMD: " + cmd + " Param: " + param);
			processArduinoCommand(cmd,param);
		}
	}
	
	@Override
	public void sendCommand(Object o) throws IOException 
	{
		if(!(o instanceof String)) throw new IOException("Invalid Type sent to ArduinoProtocol.sendCommand()");
		String response = (String) o;
		String outToArduino = response + "\n";
		if(outToArduino.length() > ARDUINO_MAX_RESPONSE) throw new IOException("ERROR: Response Length: " + outToArduino.length() + " Max: " + ARDUINO_MAX_RESPONSE);
		out.write((outToArduino.getBytes()));
	}
	
	public void initArduino() throws IOException
	{
		sendCommand(((Integer)manager.getHvac().getTargetTemp()).toString());
		sendCommand(((Integer)manager.getHvac().getOpMode()).toString());
		sendCommand(((Integer)manager.getHvac().getFanMode()).toString());
	}
	

	public static void updateArduino() throws IOException
	{
		Iterator<ArduinoProtocol> iter = arduinoProtos.iterator();
		while(iter.hasNext())
		{
			ArduinoProtocol a = iter.next();
			if(a != null)
			{
				CCServerManager.printToStandardOut("Updating " + a.getName() + "... ");
				a.sendCommand(TCP_CMD_SET_SET_POINT + ARDUINO_PARAM_DELIMETER + a.manager.getHvac().getTargetTemp());
				a.sendCommand(TCP_CMD_SET_OP_MODE + ARDUINO_PARAM_DELIMETER + a.manager.getHvac().getOpMode());
				a.sendCommand(TCP_CMD_SET_FAN_MODE + ARDUINO_PARAM_DELIMETER + a.manager.getHvac().getFanMode());
			}
		}
	}

	private void processArduinoCommand(String cmd, int param) throws IOException
	{
		CCCommand<?> toAndroid = new CCCommand<Integer>(cmd,param);
		if(cmd.equals(TCP_CMD_SET_SET_POINT))
			manager.getHvac().setSetPoint(param);
		else if(cmd.equals(TCP_CMD_SET_OP_MODE))
			manager.getHvac().setOpMode(param);
		else if(cmd.equals(TCP_CMD_SET_FAN_MODE))
			manager.getHvac().setFanMode(param);
		else if(cmd.equals(TCP_CMD_SET_CURRENT_TEMP))
			manager.getHvac().setCurrentTemp(param);
		else if(cmd.equals(TCP_CMD_SET_HVAC_STATUS))
			manager.getHvac().setHvacStatus(param);
		else if(cmd.equals(TCP_CMD_SET_FAN_STATUS))
			manager.getHvac().setFanStatus(param);
		else if(cmd.equals(TCP_CMD_SET_HOLD))
		{
			boolean hold = false;
			if(param == 1) hold = true;
			manager.getHvac().setOnHold(hold);
			toAndroid = new CCCommand<Boolean>(cmd,hold);
		}
		else
		{
			CCServerManager.printToStandardErr("ArduinoProtocol.processCCCommand() - ERROR: Unknown Command: " + cmd);
			return;
		}
		AndroidProtocol.forwardToAndroids(toAndroid);
	}

	public static void forwardToArduino(String cmd) throws IOException 
	{
		Iterator<ArduinoProtocol> iter = arduinoProtos.iterator();
		while(iter.hasNext())
		{
			ArduinoProtocol a = iter.next();
			if(a != null)
			{
				CCServerManager.printToStandardOut("Sending CMD to " + a.getName() + ": " + cmd.toString());
				a.sendCommand(cmd);
			}
		}
	}
}
