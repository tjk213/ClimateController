package com.UE.cc.server;

import com.UE.cc.common.CCConstants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public abstract class ClientProtocol implements Runnable,CCConstants
{
	protected static Set<AndroidProtocol> androidProtos = new HashSet<AndroidProtocol>();
	protected static Set<ArduinoProtocol> arduinoProtos = new HashSet<ArduinoProtocol>();
	
	private String name;
	private Thread clientThread;
	protected CCServerManager manager;
	protected Socket clientSocket;
	protected OutputStream out;
	protected InputStream in;
	

	public ClientProtocol(String name,Socket s, CCServerManager m)
	{
		this.name = name;
		this.clientSocket = s;
		this.manager = m;
	}
	
	public void start()
	{
		clientThread = new Thread(this,name);
		clientThread.start();
	}
	
	@Override
	public void run()
	{
		try
		{
			setup();
			while(clientSocket.isConnected())
				loop();
		}
		catch(IOException e) 
		{
			CCServerManager.printToStandardErr(e.toString());
			e.printStackTrace();
		}
		catch(ClassNotFoundException e) 
		{
			CCServerManager.printToStandardErr(e.toString());
			e.printStackTrace();
		}
		androidProtos.remove(this);
		arduinoProtos.remove(this);
	}
	
	protected abstract void setup() throws IOException;
	protected abstract void loop() throws IOException,ClassNotFoundException;
	protected abstract void sendCommand(Object o) throws IOException;

	public String getName() {
		return name;
	}
}
