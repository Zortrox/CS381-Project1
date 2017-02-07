/**
 * Created by Zortrox on 9/13/2016.
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class NetClient extends NetObject{

	NetClient(int port, String IP) {
		mPort = port;
		mIP = IP;
	}

	//start the main thread
	public void run() {
		try {
			TCPConnection();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void TCPConnection() throws Exception {
		Socket socket = null;
		boolean bServerFound = false;

		//keep trying to connect to server
		while(!bServerFound)
		{
			try
			{
				socket = new Socket(mIP, mPort);
				bServerFound = true;
			}
			catch(ConnectException e)
			{
				System.out.println("Server refused, retrying...");

				try
				{
					Thread.sleep(2000); //2 seconds
				}
				catch(InterruptedException ex){
					ex.printStackTrace();
				}
			}
		}

		//attach reader to console
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		//initialize message
		String msgSend = "Hello, this is client.";
		System.out.println(msgSend);

		//wrap data, send it, then receive response
		Message msg = new Message();
		msg.mData = msgSend.getBytes();
		msg.mType = MSG_INIT;
		sendTCPData(socket, msg);
		receiveTCPData(socket, msg);
		String msgReceive = new String(msg.mData);
		System.out.println("<server>: " + msgReceive);

		//keep getting user input
		//send data, receive server data
		while (!msgSend.toLowerCase().equals("exit"))	{
			System.out.print("Text to Send: ");
			msgSend = reader.readLine();
			msg.mData = msgSend.getBytes();
			msg.mType = MSG_TEXT;
			sendTCPData(socket, msg);
			receiveTCPData(socket, msg);
			msgReceive = new String(msg.mData);
			System.out.println("<server>: " + msgReceive);
		}

		//close socket if user "exits"
		socket.close();
	}
}
