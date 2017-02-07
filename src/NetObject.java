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

//TCP data wrapper
class Message {
	byte mType = 0;		//type of data
	byte[] mData;		//actual data
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

	//needed, but overwritten by children
	public void run() {	}

	//handles receiving TCP data and wraps the message in a Message class
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

	//handles sending wrapped data in the Message class over TCP
	void sendTCPData(Socket socket, Message msg) throws Exception{
		DataOutputStream outData = new DataOutputStream(socket.getOutputStream());

		//wrap size of data
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(msg.mData.length);
		byte[] dataSize = b.array();
		outData.write(dataSize);

		//wrap message type
		outData.writeByte(msg.mType);

		//send data
		outData.write(msg.mData);
		outData.flush();
	}
}
