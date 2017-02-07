/**
 * Created by Zortrox on 9/13/2016.
 */

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.util.concurrent.*;

public class NetServer extends NetObject {

	NetServer(int port, String IP) {
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

	//listens on the main thread and creates a new thread to read from each connection
	private void TCPConnection() throws Exception{
		//start listening
		System.out.println("<server>: Listening for connections.");
		ServerSocket serverSocket = new ServerSocket(mPort);

		//process requests (read/send data)
		while (true)
		{
			//wait for new connection
			Socket clientSocket = serverSocket.accept();
			System.out.println("<server>: New connection.");

			//create new thread to listen
			Thread thrConn = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String recMsg = "";

						//keep reading data until user "exits"
						while (!recMsg.toLowerCase().equals("exit")) {
							//receive the data
							Message msg = new Message();
							receiveTCPData(clientSocket, msg);

							//whether the initial "hello" or user text
							switch (msg.mType) {
								case MSG_INIT:
									//send back "hello"
									String strInitial = new String(msg.mData);
									msg.mData = ("Message received, " +
											strInitial.substring(strInitial.lastIndexOf(' ') + 1)).getBytes();
									sendTCPData(clientSocket, msg);
									break;
								case MSG_TEXT:
									//send back capitalized text
									recMsg = new String(msg.mData);
									if (recMsg.toLowerCase().equals("exit")) {
										System.out.println("[client disconnecting]");
										msg.mData = "Goodbye".getBytes();
									} else {
										System.out.println("<client>: " + recMsg);
										msg.mData = recMsg.toUpperCase().getBytes();
									}
									sendTCPData(clientSocket, msg);
									break;
							}
						}
					}
					catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			});
			thrConn.start();	//start the thread
		}
	}
}
