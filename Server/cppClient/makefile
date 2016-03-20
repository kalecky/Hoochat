CC=g++
CFLAGS=-Wall

cppclient: cppclient.o PracticalSocket.o
	$(CC) cppclient.o PracticalSocket.o -o cppclient

cppclient.o: cppclient.cpp

PracticalSocket.o: PracticalSocket.cpp PracticalSocket.h

clean:
	rm *.o cppclient
