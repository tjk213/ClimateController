package com.UE.cc.domain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.UE.cc.common.CCConstants;
import com.UE.cc.util.CCCommand;

public class TCPClient implements CCConstants
{
	private InetAddress ip;
	private Socket clientSocket;
	private OutputStream out;
	private ObjectOutputStream objectOut;
	private ObjectInputStream objectIn;
	
	public TCPClient() throws UnknownHostException {
//		this.ip = InetAddress.getByName("192.168.1.127");  //464 Server
//		this.ip = InetAddress.getByName("54.244.159.175"); //AWS server
		this.ip = InetAddress.getByName("192.168.1.127");  //Slave Server
	}
	
	public void sendCommand(CCCommand<?> c) throws IOException
	{
		checkConnection();
		objectOut.writeObject(c);
	}
	
	/**
	 * This method checks for an existing objectInputStream (Creates a new one if necessary)
	 * and then blocks on inputStream.readObject(). An instance of CCCommand is expected, any
	 * other objects will throw a ClassNotFoundExceptions or a ClassCastException.
	 * 
	 * This method only blocks on readObject() for a fraction of a second. If no command is
	 * received in this time, a java.net.SocketTimeoutException will be thrown. This method 
	 * should be repeatedly called in order to ensure that incoming data from the server is
	 * received in a timely manner, but implementation of the TimeOut allows a UI to periodically
	 * alter the destination of the incoming command. The timeout also ensure that any abandoned
	 * threads will eventually terminate
	 *  
	 * @return CCCommand<?> read by objectInputStream.readObject(). The corresponding output
	 * 		stream should be newly created or reset before each object that is sent to ensure
	 * 		that every object is properly transmitted.
	 * @throws ClassNotFoundException - Incoming object should be an instance of CCCommand<?>
	 * @throws IOException - the Exception will be an instance of java.net.SocketTimeoutException
	 * 		if no object is read within a fraction of a second. Other IOExceptions will be thrown
	 * 		if the Socket cannot be created or an I/O stream is corrupted
	 * @author TK
	 */
	public CCCommand<?> waitForCommand() throws ClassNotFoundException, IOException
	{
		checkConnection();
		return (CCCommand<?>) objectIn.readObject();
	}
	
	private synchronized void checkConnection() throws IOException
	{
		if(clientSocket == null || !clientSocket.isConnected() || objectIn == null || objectOut == null)
			reconnect();
	}
	
	private synchronized void reconnect() throws IOException 
	{
		clientSocket = new Socket(ip,TCP_PORT);
		clientSocket.setKeepAlive(true);
		clientSocket.setSoTimeout(300);
		out = clientSocket.getOutputStream();
		out.write(ANDROID_HEADER.getBytes());
		objectOut = new ObjectOutputStream(out);
		objectIn = new ObjectInputStream(clientSocket.getInputStream());
	}
}
