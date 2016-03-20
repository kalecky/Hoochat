#include "mysql_connection.h"
#include "mysql_driver.h"

#include <iostream>
#include <cppconn/driver.h>
#include <cppconn/exception.h>
#include <cppconn/resultset.h>
#include <cppconn/statement.h>

using namespace std;

const string LOCATION = "tcp://127.0.0.1:3306";
const string USERNAME = "root";
const string PASSWORD = "root";
const string DATABASE = "dbo";
const string QUERY = "CALL allUsers()";

int main() {

    // Sample database connection code
    try {
        sql::mysql::MySQL_Driver *driver;
        sql::Connection *con;
        sql::Statement *stmt;
        sql::ResultSet *res;

        /* Establish connection */
        driver = sql::mysql::get_mysql_driver_instance(); // Driver is automatically freed
        con = driver->connect(LOCATION, USERNAME, PASSWORD); // Must free later

        /* Connect to database */
        con->setSchema(DATABASE);
        
        /* Prepare a statement */
        stmt = con->createStatement();
        res = stmt->executeQuery(QUERY);
        while (res->next()) {
            cout << res->getString("fname") << endl;            
        }

        /* Free result set, statement, and connection */
        delete res;
        delete stmt;
        delete con;

    } catch (sql::SQLException e) {
        cout << "Exception thrown" << endl;
    }

    // Server code

}
