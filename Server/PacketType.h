#ifndef __PacketType_h
#define __PacketType_h


static class PacketType {
public:

	static const int
		LOGIN_REQUEST = 1,
		LOGIN_RESPONSE = 2,
		LOGOUT_REQUEST = 3,
		LOGOUT_RESPONSE = 4,
		SEND_REQUEST = 5,
		SEND_RESPONSE = 6,
		PULL_REQUEST = 7,
		PULL_RESPONSE = 8,
		REMOVE_REQUEST = 9,
		REMOVE_RESPONSE = 10,
		NEWMESSAGE_PUSH = 11
	;
	
};


#endif  // __PacketType_h
