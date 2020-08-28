# Mechanic Shop Database Management System

The system will be used track information about customers, cars, mechanics, car ownership, service request and billing information. It is implemented using PostgreSQL to hold the database. The system is also built using Java and the Java Database Connector (jdbc) to create a program that connects to the PostgreSQL database and provides a user-friendly interface to insert data and run complex queries into the database.

**PLEASE NOTE: PostgreSQL needs to be installed and running on the machine to run the Java program**

## Quick Start Manual (instructions for a UCR lab machine with PostgreSQL installed, possbily works on your local machine)

Instructions on running the Java application:
1. Create the neccessary directories by running these commands:
```
mkdir /tmp/$LOGNAME
cd /tmp/$LOGNAME
mkdir test
mkdir test/data
mkdir sockets
export PGPORT=5432
export PGDATA=/tmp/$LOGNAME/test/data
initdb
```
2. Start the server by running the script:
```
./start_server.sh
```
and check the status if the server has started by running the script:
```
./check_status.sh
```
3. Download the github repository (.zip) in /tmp/$LOGNAME and extract it.
4. Set the extracted folder as your current directory.
5. Run these commands into the terminal (make sure a PostgreSQL server is running before this instruction):

Creating a database, $DB_NAME can be replaced in **ALL** instructions to whatever name you want the database to be.
```
createdb -h /tmp/$LOGNAME/sockets $DB_NAME
```

Pipelining a SQL script (to execute SQL commands in bulk, and copies information from .csv files in directory /csv_data)
```
psql -h /tmp/$LOGNAME/sockets $DB_NAME < create.sql
```

6. Now that the database has been created with information inside, we can run the Java program:
```
cd cs-166_phase-3_code/code/java
```

7. You can compile the code located in the /src folder by running the script:
```
./compile.sh
```
8. Finally, to start up the Java program you can run the run.sh script with arguments:
```
./run.sh <$DB_NAME> <PORT> <USER>
```
Example (using UCR lab machines, with PostgreSQL server running on port 5432)
```
./run.sh test_DB 5432 jvo033
```
You should be able to see the interactive menu at this point.

9. Once you are done running the Java program, set the current directory back to /tmp/$LOGNAME:
```
cd /tmp/$LOGNAME
```
and stop the server by running the script
```
./flush.sh
```

## Contributers
* Johnny Vo (jvo033@ucr.edu)
* Nish Patel (npate145@ucr.edu)
