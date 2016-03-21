#ifndef __DB_h
#define __DB_h


#include <mysql_connection.h>
#include <mysql_driver.h>
#include <cppconn/driver.h>
#include <cppconn/exception.h>
#include <cppconn/resultset.h>
#include <cppconn/prepared_statement.h>

using namespace std;


class DB {
	const string LOCATION = "tcp://127.0.0.1:3306";
	const string USERNAME = "root";
	const string PASSWORD = "root";
	const string DATABASE = "dbo";

    sql::mysql::MySQL_Driver* driver;
    sql::Connection* connection;

public:
    DB () {
        driver = sql::mysql::get_mysql_driver_instance ();
        connection = driver-> connect (LOCATION, USERNAME, PASSWORD);
        connection-> setSchema (DATABASE);
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
		bool uid = -1;
		sql::PreparedStatement* statement = connection-> prepareStatement ("XXX (?, ?)");
		statement-> setString (1, username);
		statement-> setString (2, password);
		sql::ResultSet* results = statement-> executeQuery ();
		if (results-> next ()) {
			uid = results-> getInt ("ID");
		}
		delete results;
		delete statement;
		return uid;
	}

	vector<int> listUnreadMessages (int uid) {
		vector<int> mids;
		sql::PreparedStatement* statement = connection-> prepareStatement ("XXX (?)");
		statement-> setInt (1, uid);
		sql::ResultSet* results = statement-> executeQuery ();
		if (results-> next ()) {
			mids. push_back (results-> getInt ("ID"));
		}
		delete results;
		delete statement;
		return mids;
	}

	string readMessage (int uid, int mid) {
		string message;
		sql::PreparedStatement* statement = connection-> prepareStatement ("XXX (?, ?)");
		statement-> setInt (1, uid);
		statement-> setInt (2, mid);
		sql::ResultSet* results = statement-> executeQuery ();
		if (results-> next ()) {
			message = results-> getString ("message");
			if (!markMessageAsRead (uid, mid)) {
				message. clear ();
			}
		}
		delete results;
		delete statement;
		return message;
	}

	// Hides the message from recipient, doesn't remove it completely so that sender can still see it
	bool removeMessage (int uid, int mid) {
		bool result;
		sql::PreparedStatement* statement = connection-> prepareStatement ("XXX (?, ?)");
		statement-> setInt (1, uid);
		statement-> setInt (2, mid);
		result = statement-> executeUpdate ();
		delete statement;
		return result;
	}

	bool sendMessage (int uid, string recipient, string message) {
		bool result;
		sql::PreparedStatement* statement = connection-> prepareStatement ("XXX (?, ?, ?)");
		statement-> setInt (1, uid);
		statement-> setString (2, recipient);
		statement-> setString (3, message);
		result = statement-> execute ();
		delete statement;
		return result;
	}

private:
	bool markMessageAsRead (int uid, int mid) {
		bool result;
		sql::PreparedStatement* statement = connection-> prepareStatement ("XXX (?, ?)");
		statement-> setInt (1, uid);
		statement-> setInt (2, mid);
		result = statement-> executeUpdate ();
		delete statement;
		return result;
	}

};


#endif  // __DB_h
