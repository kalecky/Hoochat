CC=g++
CFLAGS=-Wall
LDFLAGS=-lmysqlcppconn

cppserver: cppserver.o PracticalSocket.o
	$(CC) cppserver.o PracticalSocket.o -o cppserver $(LDFLAGS)

cppserver.o: cppserver.cpp

PracticalSocket.o: PracticalSocket.cpp PracticalSocket.h

clean:
	rm *.o cppserver
