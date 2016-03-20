#include "PracticalSocket.h"    // For Socket, ServerSocket, and SocketException

#include <iostream>             // For cout, cerr
#include <cstdlib>              // For atoi, exit

using namespace std;

const unsigned int RCVBUFSIZE = 32;     // Size of receiver buffer

void HandleTCPClient(TCPSocket *sock);  // TCP Client handling function

int main(int argc, char** argv) {
    if (argc != 2) {
        cerr << "Usage: " << argv[0] << " <Server Port>" << endl;
        exit(1);
    }

    // TODO error check this, don't use atoi
    unsigned short servPort = atoi(argv[1]);    // First arg: local port

    try {   // TODO may want to restructure this
        TCPServerSocket servSock(servPort);     // Server Socket object

        while (true) {  // Run forever
            HandleTCPClient(servSock.accept()); // Wait for client to connect
        }
    } catch (SocketException &e) {
        cerr << e.what() << endl;
        exit(1);
    }
    
    // This place is never reached
    return 0;
}

// TCP Client handling function
void HandleTCPClient(TCPSocket *sock) {
    cout << "Handling client ";
    try {
        cout << sock->getForeignAddress() << ":";
    } catch (SocketException &e) {
        cerr << "Unable to get foreign address" << endl;
    }

    try {
        cout << sock->getForeignPort();
    } catch (SocketException &e) {
        cerr << "Unable to get foreign port" << endl;
    }
    cout << endl;

    // ECHO
    char echoBuffer[RCVBUFSIZE];
    int recvMsgSize;
    while ((recvMsgSize = sock->recv(echoBuffer, RCVBUFSIZE)) > 0) {
        sock->send(echoBuffer, recvMsgSize);
    }
    delete sock;
}
