package com.UE.cc.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Timer;

import com.UE.cc.common.CCConstants;

public class CCServer implements CCConstants
{
	static private ServerSocket serverSocket = null;
	static private CCServerManager manager; 
	static private byte[] headerBuffer = new byte[HEADER_SIZE];
	static private InputStream in;
	static private Socket clientSocket;
	static private ArduinoProtocol arduinoProto;
	static private AndroidProtocol androidProto;
	static private UpdateScheduleTimerTask scheduleUpdater;
	
	public static void main(String[] args)
	{
		manager = new CCServerManager();
		startScheduleUpdater();
		
		try {
			serverSocket = new ServerSocket(TCP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(true)
		{
			try
			{
				clientSocket = waitForIncomingSocket();
				String clientType = readHeader();
				createClientThread(clientType);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}

	private static void startScheduleUpdater() 
	{
		Timer timer = new Timer();
		scheduleUpdater = new UpdateScheduleTimerTask(manager);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE)+1);
		cal.set(Calendar.SECOND, 0);
		timer.scheduleAtFixedRate(scheduleUpdater, cal.getTime(), 60*1000);
	}

	private static void createClientThread(String clientType) 
	{
		if(clientType.equals(ANDROID_HEADER))
		{
			androidProto = new AndroidProtocol(clientSocket,manager);
			androidProto.start();
		}
		else if(clientType.equals(ARDUINO_HEADER))
		{
			arduinoProto = new ArduinoProtocol(clientSocket,manager);
			arduinoProto.start();
		}
	}

	private static String readHeader() throws IOException 
	{
		in = clientSocket.getInputStream();
		in.read(headerBuffer,0,HEADER_SIZE);
		String h = new String(headerBuffer);
		CCServerManager.printToStandardOut("Header: " + h);
		return h;
	}

	private static Socket waitForIncomingSocket() throws IOException {
		return serverSocket.accept();
	}
}
