///#include "PracticalSocket.h"    // For Socket, ServerSocket, and SocketException

#include <iostream>             // For cout, cerr
#include <cstdlib>              // For atoi, exit

#include <sys/socket.h>
#include <resolv.h>
#include <openssl/ssl.h>
#include <openssl/ssl3.h>
#include <openssl/err.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include "Packet.h"
#include "PacketType.h"
#include <map>
#include "DB.h"
#include <random>

using namespace std;
typedef unsigned char byte;


const int PORT = 2015;
const int INCOMING_STATIC_BUFFER = 10240;

const unsigned int RCVBUFSIZE = 32;     // Size of receiver buffer

DB db;
map<uint64_t, int> sids;


/* ssl_server.c
 *
 * Copyright (c) 2000 Sean Walton and Macmillan Publishers.  Use may be in
 * whole or in part in accordance to the General Public License (GPL).
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
*/

/*****************************************************************************/
/*** ssl_server.c                                                          ***/
/***                                                                       ***/
/*** Demonstrate an SSL server.                                            ***/
/*****************************************************************************/


#define FAIL    -1

/*---------------------------------------------------------------------*/
/*--- OpenListener - create server socket                           ---*/
/*---------------------------------------------------------------------*/
int OpenListener(int port)
{   int sd;
    struct sockaddr_in addr;

    sd = socket(PF_INET, SOCK_STREAM, 0);
    bzero(&addr, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    addr.sin_addr.s_addr = INADDR_ANY;
    if ( bind(sd, (const sockaddr*) &addr, sizeof(addr)) != 0 )
    {
        perror("can't bind port");
        abort();
    }
    if ( listen(sd, 10) != 0 )
    {
        perror("Can't configure listening port");
        abort();
    }
    return sd;
}

/*---------------------------------------------------------------------*/
/*--- InitServerCTX - initialize SSL server  and create context     ---*/
/*---------------------------------------------------------------------*/
SSL_CTX* InitServerCTX(void)
{
    SSL_CTX *ctx;

    //OpenSSL_add_all_algorithms();		/* load & register all cryptos, etc. */
    SSL_load_error_strings();			/* load all error messages */
	SSL_library_init ();
	const SSL_METHOD* method = TLSv1_server_method();		/* create new server-method instance */
    ctx = SSL_CTX_new(method);			/* create new context from method */
    if ( ctx == NULL )
    {
        ERR_print_errors_fp(stderr);
        abort();
    }
    return ctx;
}

/*---------------------------------------------------------------------*/
/*--- LoadCertificates - load from files.                           ---*/
/*---------------------------------------------------------------------*/
void LoadCertificates(SSL_CTX* ctx, const char* CertFile)
{
	/* set the local certificate from CertFile */
    if ( SSL_CTX_use_certificate_file(ctx, CertFile, SSL_FILETYPE_PEM) <= 0 )
    {
        ERR_print_errors_fp(stderr);
        abort();
    }
    /* set the private key from KeyFile (may be the same as CertFile) */
    if ( SSL_CTX_use_PrivateKey_file(ctx, CertFile, SSL_FILETYPE_PEM) <= 0 )
    {
        ERR_print_errors_fp(stderr);
        abort();
    }
    /* verify private key */
    if ( !SSL_CTX_check_private_key(ctx) )
    {
        fprintf(stderr, "Private key does not match the public certificate\n");
        abort();
    }
}

byte* readData (SSL* ssl, byte* buffer, int length) {
	int read_total = 0, read_now = 0;
	while (read_total != length) {
		if ((read_now = SSL_read(ssl, buffer + read_total, length - read_total)) > 0) {
			read_total += read_now;
		} else {
			ERR_print_errors_fp(stderr);
			return 0;
		}
	}
	return buffer;
}

byte* readHeader (SSL* ssl, byte* buffer) {
	return readData (ssl, buffer, 16);
}

bool HandlePacket (Packet& request, Packet& response) {
	int uid, mid;
	if (request.getType() == PacketType::LOGIN_REQUEST) {
		response.setType(PacketType::LOGIN_RESPONSE);
		if (request.getData().size() == 2 && (uid = db.logIn(request.getData()[0], request.getData()[1])) != -1) {
			__uint64_t sid = (((__uint64_t) rand () << 32) | rand ());
			sids [sid] = uid;
			response.setSID(sid);
			response.getData().push_back("1");
		} else {
			response.getData().push_back("0");
		}
		return true;
	}
	auto it = sids.find(request.getSID());
	if (it == sids.end ()) {
		response.setType(request.getType() + 1);
		return true;
	}
	uid = it-> second;
	switch (request.getType()) {
	case PacketType::LOGOUT_REQUEST:
		response.setType(PacketType::LOGOUT_RESPONSE);
		response.setSID(request.getSID());
		sids.erase(request.getSID());
		response.getData().push_back("1");
		return true;
	case PacketType::PULL_REQUEST:
		response.setType(PacketType::PULL_RESPONSE);
		response.setSID(response.getSID());
		if (request.getData().size() == 1) {
			try {
				mid = stoi (request.getData()[0]);
				response.getData().push_back(db.readMessage(uid, mid));
			} catch (exception& ex) { }
		}
		return true;
	case PacketType::REMOVE_REQUEST:
		response.setType(PacketType::REMOVE_RESPONSE);
		response.setSID(request.getSID());
		if (request.getData().size() == 1) {
			try {
				mid = stoi (request.getData()[0]);
				if (db.removeMessage(uid, mid)) {
					response.getData().push_back("1");
					return true;
				}
			} catch (exception& ex) { }
		}
		response.getData().push_back("0");
		return true;
	case PacketType::SEND_REQUEST:
		response.setType(PacketType::SEND_RESPONSE);
		response.setSID(request.getSID());
		if (request.getData().size() == 2 && db.sendMessage(uid, request.getData()[1], request.getData()[2])) {
			response.getData().push_back("1");
			return true;
		}
		response.getData().push_back("0");
		return true;
	case PacketType::NEWMESSAGES_REQUEST:
		response.setType(PacketType::NEWMESSAGES_RESPONSE);
		response.setSID(request.getSID());
		for (int id : db.listUnreadMessages(uid)) {
			response.getData().push_back(to_string(id));
		}
		return true;
	default:
		return false;
	}
}

/*---------------------------------------------------------------------*/
/*--- Servlet - SSL servlet (contexts can be shared)                ---*/
/*---------------------------------------------------------------------*/
void Servlet(SSL* ssl)	/* Serve the connection -- threadable */
{
	byte header [16];
	byte data [INCOMING_STATIC_BUFFER];

    if ( SSL_accept(ssl) == FAIL )					/* do SSL-protocol accept */
        ERR_print_errors_fp(stderr);
    else
    {							/* get any certificates */
    	Packet request (readHeader (ssl, header));
    	if (request.getLength () <= INCOMING_STATIC_BUFFER) {
        	request. parseData (readData (ssl, data, request. getLength ()), 0, request. getLength ());
    	} else {
    		byte* buffer = new byte [request. getLength ()];
    		request. parseData (readData (ssl, data, request. getLength ()), 0, request. getLength ());
    		delete [] buffer;
    	}

    	Packet response (0, 0);
    	if (HandlePacket (request, response)) {
    		byte* response_data = response. serialize ();
    		SSL_write (ssl, response_data, response. getLength ());
    		delete [] response_data;
    	} else {
            ERR_print_errors_fp(stderr);
    	}
    }
    int sd = SSL_get_fd(ssl);							/* get socket connection */
    SSL_free(ssl);									/* release SSL state */
    close(sd);										/* close connection */
}

/*---------------------------------------------------------------------*/
/*--- main - create SSL socket server.                              ---*/
/*---------------------------------------------------------------------*/
int main(int count, char *strings[])
{   SSL_CTX *ctx;
    int server;
    char *portnum;

    portnum = strings[1];
    ctx = InitServerCTX();								/* initialize SSL */
    LoadCertificates(ctx, "/var/www/certificate.pem");	/* load certs */
    server = OpenListener (PORT);				/* create server socket */

    while (1)
    {   struct sockaddr_in addr;
        int len = sizeof(addr);
        SSL *ssl;

        int client = accept(server, (struct sockaddr*) &addr, (unsigned int*) &len);		/* accept connection as usual */
        ssl = SSL_new(ctx);         					/* get new SSL state with context */
        SSL_set_fd(ssl, client);						/* set connection socket to SSL state */
        Servlet(ssl);									/* service connection */
    }
    close(server);										/* close server socket */
    SSL_CTX_free(ctx);									/* release context */
}
