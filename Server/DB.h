#ifndef __DB_h
#define __DB_h


#include <mysql_connection.h>
#include <mysql_driver.h>
#include <cppconn/driver.h>
#include <cppconn/exception.h>
#include <cppconn/resultset.h>
#include <cppconn/prepared_statement.h>
#include <ctime>
#include <stdio.h>

using namespace std;


class DB {
	const string LOCATION = "tcp://127.0.0.1:3306";
	const string USERNAME = "root";
	const string PASSWORD = "uplnebajecnyheslo";
	const string DATABASE = "dbo";

    sql::mysql::MySQL_Driver* driver;
    sql::Connection* connection;

public:
    DB () {
        driver = sql::mysql::get_mysql_driver_instance ();
        connection = driver-> connect (LOCATION, USERNAME, PASSWORD);
        connection-> setSchema (DATABASE);
        connection-> setAutoCommit (true);
        //connection-> setClientOption("CLIENT_MULTI_RESULTS", ) CLIENT_MULTI_RESULTS
    }
    ~DB () {
    	if (connection) {
    		if (!connection-> isClosed ()) {
    			connection-> close ();
    		}
    		delete connection;
    	}
    }

	// Returns user id if credentials are valid, otherwise -1
	int logIn (const string& username, const string& password) {
		int uid = -1;
		sql::PreparedStatement* statement = connection-> prepareStatement ("SELECT `userID` FROM `dbo`.`Users` WHERE email = ? and pw like SHA2(CONCAT(?, `salt`), 256) limit 1");
		statement-> setString (1, username);
		statement-> setString (2, password);
		//sql::ResultSet* results = statement-> executeQuery ("CALL `logIn`('" + username + "', '" + password + "')");
		sql::ResultSet* results = statement-> executeQuery ();
		if (results-> next ()) {
			uid = results-> getInt (1);
			while (results-> next ()) ;
		}
		results-> close ();
		statement-> close ();
		delete results;
		delete statement;
		return uid;
	}


	string latestCheck (int uid) {
		string latest;
		sql::PreparedStatement* statement = connection-> prepareStatement ("SELECT max(messageID) FROM Recipients WHERE seen=0 and recipientID=? and not deleted");
		statement-> setInt (1, uid);
		sql::ResultSet* results = statement-> executeQuery ();
		if (results-> next ()) {
			latest = to_string (results-> getInt (1));
			while (results-> next ()) ;
		}
		results-> close ();
		statement-> close ();
		delete results;
		delete statement;
		return latest;
	}

	vector<string> listUnreadMessages (int uid) {
		vector<string> mids;
		sql::PreparedStatement* statement = connection-> prepareStatement ("SELECT concat ('[', msgID, '] ', Message.created, ' ', email, ': ', if (length (message) <= 10, message, concat (substr(message, 1, 10), '...'))) FROM `Message` join Recipients on Recipients.messageID = `Message`.msgID join Users on Users.userId = src_userID WHERE seen=0 and recipientID=? and not Recipients.deleted order by Message.created desc");
		statement-> setInt (1, uid);
		sql::ResultSet* results = statement-> executeQuery ();
		while (results-> next ()) {
			mids. push_back (results-> getString (1));
		}
		results-> close ();
		statement-> close ();
		delete results;
		delete statement;
		return mids;
	}

	vector<string> listAllMessages (int uid) {
		vector<string> mids;
		sql::PreparedStatement* statement = connection-> prepareStatement ("SELECT concat ('[', msgID, '] ', Message.created, ' ', email, ': ', if (length (message) <= 10, message, concat (substr(message, 1, 10), '...'))) FROM `Message` join Recipients on Recipients.messageID = `Message`.msgID join Users on Users.userId = src_userID WHERE recipientID=? and not Recipients.deleted order by Message.created desc");
		statement-> setInt (1, uid);
		sql::ResultSet* results = statement-> executeQuery ();
		while (results-> next ()) {
			mids. push_back (results-> getString (1));
		}
		results-> close ();
		statement-> close ();
		delete results;
		delete statement;
		return mids;
	}

	vector<string> listSentMessages (int uid) {
		vector<string> mids;
		sql::PreparedStatement* statement = connection-> prepareStatement ("SELECT concat ('[', msgID, '] ', created, ' ', if (length (message) <= 10, message, concat (substr(message, 1, 10), '...'))) FROM `Message` WHERE src_userID=? and not deleted order by created desc");
		statement-> setInt (1, uid);
		sql::ResultSet* results = statement-> executeQuery ();
		while (results-> next ()) {
			mids. push_back (results-> getString (1));
		}
		results-> close ();
		statement-> close ();
		delete results;
		delete statement;
		return mids;
	}

	string readMessage (int uid, int mid) {
		string message;
		sql::PreparedStatement* statement = connection-> prepareStatement ("SELECT Message.created, concat(fname, ' ', lname, ' <', Users.email, '>'), message, Recipients.seen FROM `Message` join Recipients on Recipients.messageID = `Message`.msgID join Users on Users.userId = src_userID WHERE recipientID=? and `Message`.msgID=? and not Recipients.deleted");
		statement-> setInt (1, uid);
		statement-> setInt (2, mid);
		sql::ResultSet* results = statement-> executeQuery ();
		if (results-> next ()) {
			message = "Date: ";
			message = message + results-> getString (1) + "\r\nFrom: " + results-> getString (2) + "\r\n----------\r\n" + results-> getString (3);
			if (!results-> getBoolean (4) && !markMessageAsRead (uid, mid)) {
				message. clear ();
			}
			while (results-> next ()) ;
		}
		results-> close ();
		statement-> close ();
		delete results;
		delete statement;
		return message. empty () ? _readSentMessage (uid, mid) : message;
	}

	// Hides the message from recipient, doesn't remove it completely so that sender can still see it
	bool removeMessage (int uid, int mid) {
		bool result;
		sql::PreparedStatement* statement = connection-> prepareStatement ("UPDATE Recipients SET deleted = 1 where recipientID=? and messageID=? and not deleted");
		statement-> setInt (1, uid);
		statement-> setInt (2, mid);
		result = statement-> executeUpdate ();
		statement-> close ();
		delete statement;
		return result ? result : _removeSentMessage (uid, mid);
	}

	bool sendMessage (int uid, string recipient, string message) {
		/*int recipient_id = findUser (recipient);
		if (recipient_id == -1) {
			return false;
		}*/
		bool result;
		sql::PreparedStatement* statement = connection-> prepareStatement ("CALL sendMessage (?, ?, ?, ?)");
		statement-> setInt (1, uid);
		statement-> setString (2, recipient);
		statement-> setString (3, message);
		char buffer [256];
		time_t now = time (NULL);
		tm* tmnow = localtime (&now);
		strftime (buffer, 256, "%F %T", tmnow);
		statement-> setString (4, buffer);
		//printf ("msg: %i %s %s %s", uid, recipient, message, buffer);
		result = statement-> execute ();
		statement-> close ();
		delete statement;
		return result;
	}

private:
	bool markMessageAsRead (int uid, int mid) {
		bool result;
		sql::PreparedStatement* statement = connection-> prepareStatement ("UPDATE Recipients SET seen = 1 where recipientID=? and messageID=? and not deleted");
		statement-> setInt (1, uid);
		statement-> setInt (2, mid); //Ununneeded
		result = statement-> executeUpdate ();
		statement-> close ();
		delete statement;
		return result;
	}

	string _readSentMessage (int uid, int mid) {
		string message;
		sql::PreparedStatement* statement = connection-> prepareStatement ("SELECT message FROM `Message` WHERE src_userID=? and `Message`.msgID=? and not deleted");
		statement-> setInt (1, uid);
		statement-> setInt (2, mid);
		sql::ResultSet* results = statement-> executeQuery ();
		if (results-> next ()) {
			message = "From: you";
			message = message + "\r\n----------\r\n" + results-> getString (1);
			while (results-> next ()) ;
		}
		results-> close ();
		statement-> close ();
		delete results;
		delete statement;
		return message;
	}
	bool _removeSentMessage (int uid, int mid) {
		bool result;
		sql::PreparedStatement* statement = connection-> prepareStatement ("UPDATE Message SET deleted = 1 where src_userID=? and msgID=? and not deleted");
		statement-> setInt (1, uid);
		statement-> setInt (2, mid);
		result = statement-> executeUpdate ();
		statement-> close ();
		delete statement;
		return result;
	}

	/*int findUser (string username) {
		sql::PreparedStatement* statement = connection-> prepareStatement ("SELECT userId from Users where email=?");
		statement-> setString (1, username);
		sql::ResultSet* results = statement-> executeQuery ();
		if (results-> next ()) {
			return results-> getInt (1);
		}
		delete results;
		delete statement;
		return -1;
	}*/

};


#endif  // __DB_h
