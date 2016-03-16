package edu.baylor.cs.hoochat;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Packet {

	long SID;
	int type;
	int length;
	List<String> data = new ArrayList<String> ();
	
	public Packet (long SID, int type, int size) {
		
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
	public void setLength(int length) {
		this.length = length;
	}
	public List<String> getData() {
		return data;
	}
	public void setData(List<String> data) {
		this.data = data;
	}
	
	public boolean parseData(byte[] data, int offset, int length) {
		this. data. clear();
		byte [] bytes = new byte [length];
		int bytes_pos = 0;
		byte last = 0;
		boolean first = true;
		for (int i = offset; i != offset + length; ++i) {
			if (data [i] >= 65 && data [i] <= 80) {
				if (first = !first) {
					bytes [bytes_pos++] = (byte) ((last - 64) | ((data [i] - 64) << 4));
				} else {					
					last = data [i];
				}
			} else if (data [i] == 10) {
				if (bytes_pos != 0) {
					try {
						this. data. add (new String (bytes, 0, bytes_pos, "UTF-8"));
						bytes_pos = 0;
					} catch (UnsupportedEncodingException e) {
						return false;
					}
				}
			} else if (data [i] != 13) {
				return false;
			}
		}
		if (bytes_pos != 0) {
			try {
				this. data. add (new String (bytes, 0, bytes_pos, "UTF-8"));
				bytes_pos = 0;
			} catch (UnsupportedEncodingException e) {
				return false;
			}
		}
		return true;
	}
	
	// TODO
	public byte[] serialize () {
		byte[][] bytes = new byte[data.size()][];
		for (int i = 0; i != data. size (); ++i) {
			try {
				bytes [i] = data. get (i). getBytes ("UTF-8");
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}
	}
	
	// To be fetched with 16-byte header data
	static public Packet initialize (byte[] header) {
		return header. length == 16 ? new Packet (header [0] | (header [1] << 8) | (header [2] << 16) | (header [3] << 24) | (header [4] << 32) | (header [5] << 40) | (header [6] << 48) | (header [7] << 56), (header [8] | (header [9] << 8) | (header [10] << 16) | (header [11] << 24)), header [12] | (header [13] << 8) | (header [14] << 16) | (header [15] << 24)) : null;
	}
	
}
