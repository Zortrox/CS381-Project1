/**
 * Created by Zortrox on 9/13/2016.
 */

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.util.concurrent.*;

public class NetServer extends NetObject {
	private BlockingQueue<Socket> qSockets = new LinkedBlockingQueue<>();
	private Thread tSockets;

	NetServer(int port, String IP) {
		mPort = port;
		mIP = IP;
	}

	public void run() {
		try {
			TCPConnection();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void TCPConnection() throws Exception{
		//queue up new requests
		tSockets = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("<server>: Listening for connections.");

					ServerSocket serverSocket = new ServerSocket(mPort);

					while (true) {
						Socket newSocket = serverSocket.accept();
						qSockets.put(newSocket);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		tSockets.start();

		//process requests
		while (true)
		{
			Socket clientSocket = qSockets.take();
			System.out.println("<server>: New connection.");

			String recMsg = "";

			while (!recMsg.toLowerCase().equals("exit")) {
				Message msg = new Message();
				receiveTCPData(clientSocket, msg);

				switch (msg.mType) {
					case MSG_INIT:
						String strInitial = new String(msg.mData);
						msg.mData = ("Message received, " +
								strInitial.substring(strInitial.lastIndexOf(' ') + 1)).getBytes();
						sendTCPData(clientSocket, msg);
						break;
					case MSG_TEXT:
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
	}
}
