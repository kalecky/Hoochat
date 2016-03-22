#ifndef __PacketType_h
#define __PacketType_h


namespace PacketType {

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
		NEWMESSAGES_REQUEST = 11,
		NEWMESSAGES_RESPONSE = 12
	;
	
};


#endif  // __PacketType_h
