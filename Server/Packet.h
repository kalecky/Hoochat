#ifndef __Packet_h
#define __Packet_h


#include <vector>
#include <string>

using namespace std;
typedef unsigned char byte;


class Packet {

	__int64_t SID;
	int type;
	int length;
	vector<string> data;

	Packet (__int64_t SID, int type, int length) {
		setSID (SID);
		setType (type);
		setLength (length);
	}

public:
	Packet (__int64_t SID, int type) : Packet (SID, type, 0) { }
	Packet (byte* header) : Packet (header [0] | (header [1] << 8) | (header [2] << 16) | (header [3] << 24) | (header [4] << 32) | (header [5] << 40) | (header [6] << 48) | (header [7] << 56), (header [8] | (header [9] << 8) | (header [10] << 16) | (header [11] << 24)), header [12] | (header [13] << 8) | (header [14] << 16) | (header [15] << 24)) { }

	__int64_t getSID() {
		return SID;
	}
	void setSID(__int64_t SID) {
		this-> SID = SID;
	}
	int getType() {
		return type;
	}
	void setType(int type) {
		this-> type = type;
	}
	int getLength() {
		return length;
	}

private:
	void setLength(int length) {
		this-> length = length;
	}

public:
	vector<string> getData() {
		return data;
	}
	void setData(vector<string> data) {
		this-> data = data;
	}

	// Reads and decodes packet data
	bool parseData (byte* data, int offset, int length) {
		this-> data. clear ();
		byte* bytes = new byte [length];
		int bytes_pos = 0;
		byte last = 0;
		bool first = true;
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
						this-> data. push_back (string (bytes, 0, bytes_pos));
						bytes_pos = 0;
					} catch (exception* ex) {
						delete [] bytes;
						return false;
					}
				}
			} else {
				delete [] bytes;
				return false;
			}
		}
		delete [] bytes;
		return true;
	}
	
	// Gets byte representation of the packet
	byte* serialize () {
		byte** bytes = new byte [data. size ()][];
		int message_length = 16;
		int i = 0;
		for (auto it = data. begin (); it != data. end (); ++it) {
			try {
				bytes [i++] = it-> c_str ();
				message_length += it-> length () * 2 + 1;
			} catch (exception* ex) {
				return 0;
			}
		}
		byte* message = new byte[message_length];
		message [0] = (byte) (SID >> 0);
		message [1] = (byte) (SID >> 8);
		message [2] = (byte) (SID >> 16);
		message [3] = (byte) (SID >> 24);
		message [4] = (byte) (SID >> 32);
		message [5] = (byte) (SID >> 40);
		message [6] = (byte) (SID >> 48);
		message [7] = (byte) (SID >> 56);
		message [8] = (byte) (type >> 0);
		message [9] = (byte) (type >> 8);
		message [10] = (byte) (type >> 16);
		message [11] = (byte) (type >> 24);
		message [12] = (byte) ((message_length - 16) >> 0);
		message [13] = (byte) ((message_length - 16) >> 8);
		message [14] = (byte) ((message_length - 16) >> 16);
		message [15] = (byte) ((message_length - 16) >> 24);
		int message_pos = 16;
		for (auto* byteline = bytes; byteline != bytes + data. size (); ++byteline) {
			for (auto* b = *byteline; b; ++b) {
				message[message_pos++] = (byte) (((*b) & 0xf) + 65);
				message[message_pos++] = (byte) ((((*b) >> 4) & 0xf) + 65);
			}
			message[message_pos++] = 88;
		}
		delete [] bytes;
		return message;
	}
	
};


#endif  // Packet_h
