package edu.baylor.cs.hoochat;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Packet {

	long SID;
	int type;
	int length;
	List<String> data;

	public Packet (long SID, int type) {
		setSID (SID);
		setType (type);
		data = new ArrayList<> ();
	}
	public Packet (long SID, int type, String [] data) {
		this (SID, type);
		this. data = Arrays. asList (data);
	}
	private Packet (long SID, int type, int length) {
		this (SID, type);
		setLength (length);
	}

	public long getSID() {
		return SID;
	}
	public void setSID(long SID) {
		this.SID = SID;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getLength() {
		return length;
	}
	private void setLength(int length) {
		this.length = length;
	}
	public List<String> getData() {
		return data;
	}
	public void setData(List<String> data) {
		this.data = data;
	}
	
	// Initializes packet based on its 16-byte header data
	static public Packet initialize (byte[] header) {
		return header. length >= 16 ? new Packet (header [0] | (((long) 0 | header [1]) << 8) | (((long) 0 | header [2]) << 16) | (((long) 0 | header [3]) << 24) | (((long) 0 | header [4]) << 32) | (((long) 0 | header [5]) << 40) | (((long) 0 | header [6]) << 48) | (((long) 0 | header [7]) << 56), (int) (((long) 0 | header [8]) | (((long) 0 | header [9]) << 8) | (((long) 0 | header [10]) << 16) | (((long) 0 | header [11]) << 24)), (int) (((long) 0 | (header [12] >= 0 ? header [12] : header [12] + 256)) | (((long) 0 | (header [13] >= 0 ? header [13] : header [13] + 256)) << 8) | (((long) 0 | (header [14] >= 0 ? header [14] : header [14] + 256)) << 16) | (((long) 0 | (header [15] >= 0 ? header [15] : header [15] + 256)) << 24))) : null;
	}
	
	// Reads and decodes packet data
	public boolean parseData (byte [] data) {
		return parseData (data, 0, data. length);
	}
	public boolean parseData(byte[] data, int offset, int length) {
		this. data = new ArrayList<> ();
		byte [] bytes = new byte [length];
		int bytes_pos = 0;
		byte last = 0;
		boolean first = true;
		for (int i = offset; i != offset + length; ++i) {
			if (data [i] >= 65 && data [i] <= 80) {
				if (first = !first) {
					bytes [bytes_pos++] = (byte) ((last - 65) | ((data [i] - 65) << 4));
				} else {					
					last = data [i];
				}
			} else if (data [i] == 88) {
				if (bytes_pos != 0) {
					try {
						this. data. add (new String (bytes, 0, bytes_pos, "UTF-8"));
						bytes_pos = 0;
					} catch (UnsupportedEncodingException e) {
						return false;
					}
				}
			} else {
				return false;
			}
		}
		return true;
	}	
	
	// Gets byte representation of the packet
	public byte[] serialize () { 
		byte[][] bytes = new byte[data.size()][];
		int message_length = 16;
		for (int i = 0; i != data. size (); ++i) {
			try {
				message_length += (bytes [i] = data. get (i). getBytes ("UTF-8")). length * 2 + 1; 
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}
		byte[] message = new byte[message_length];
		message [0] = (byte) (0xff & (SID >> 0)); 
		message [1] = (byte) (0xff & (SID >> 8));
		message [2] = (byte) (0xff & (SID >> 16));
		message [3] = (byte) (0xff & (SID >> 24));
		message [4] = (byte) (0xff & (SID >> 32));
		message [5] = (byte) (0xff & (SID >> 40));
		message [6] = (byte) (0xff & (SID >> 48));
		message [7] = (byte) (0xff & (SID >> 56));
		message [8] = (byte) (type >> 0);
		message [9] = (byte) (type >> 8);
		message [10] = (byte) (type >> 16);
		message [11] = (byte) (type >> 24);
		message [12] = (byte) ((message_length - 16) >> 0);
		message [13] = (byte) ((message_length - 16) >> 8);
		message [14] = (byte) ((message_length - 16) >> 16);
		message [15] = (byte) ((message_length - 16) >> 24);
		int message_pos = 16;
		for (byte[] byteline : bytes) {
			for (byte b : byteline) {
				message[message_pos++] = (byte) ((b & 0xf) + 65);
				message[message_pos++] = (byte) (((b >> 4) & 0xf) + 65);
			}
			message[message_pos++] = 88;
		}
		return message;
	}
	
}
