/**
 * Created by Zortrox on 9/13/2016.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

class Message {
	byte mType = 0;
	byte[] mData;
	InetAddress mIP;
	int mPort;
}

public class NetObject extends Thread {
	int mType;
	int mPort;
	String mIP;

	//packet size in bytes
	static final int PACKET_SIZE = 1024;

	//message types
	static final byte MSG_INIT 		= 0; //init connection
	static final byte MSG_TEXT 		= 1; //sending text

	public void run() {	}

	void receiveTCPData(Socket socket, Message msg) throws Exception{
		DataInputStream inData = new DataInputStream(socket.getInputStream());

		//get size of receiving data
		byte[] byteSize = new byte[4];
		inData.readFully(byteSize);
		ByteBuffer bufSize = ByteBuffer.wrap(byteSize);
		int dataSize = bufSize.getInt();

		//get message type
		msg.mType = inData.readByte();

		//receive data
		msg.mData = new byte[dataSize];
		inData.readFully(msg.mData);
	}

	void sendTCPData(Socket socket, Message msg) throws Exception{
		DataOutputStream outData = new DataOutputStream(socket.getOutputStream());

		//send size of data
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(msg.mData.length);
		byte[] dataSize = b.array();
		outData.write(dataSize);

		//send message type
		outData.writeByte(msg.mType);

		//send data
		outData.write(msg.mData);
		outData.flush();
	}
}
