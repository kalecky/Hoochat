CC=g++
CFLAGS=-Wall -std=c++11
LDFLAGS=-L/usr/local/lib -L/home/ubuntu/Hoochat/openssl/lib -lssl -lcrypto

x: x.o
	$(CC) x.o -o x $(LDFLAGS)
	
x.o: x.cpp
	$(CC) -c x.cpp ${CFLAGS}
