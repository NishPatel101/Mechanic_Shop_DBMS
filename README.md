# Mechanic Shop Database Management System

The system will be used track information about customers, cars, mechanics, car ownership, service request and billing information. It is implemented using PostgreSQL to hold the database. The system is also built using Java and the Java Database Connector (jdbc) to create a program that connects to the PostgreSQL database and provides a user-friendly interface to insert data and run complex queries into the database.

**PLEASE NOTE: PostgreSQL needs to be installed and running on the machine to run the Java program**

## Quick Start Manual (instructions for a UCR lab machine, possbily works on your local machine)

Instructions on running the Java application:
1. Download the github repository (.zip) and extract it.
2. Set the extracted folder as your current directory.
3. Run these commands into the terminal (make sure a PostgreSQL server is running before this instruction):

Creating a database, $DB_NAME can be replaced in **ALL** instructions to whatever name you want the database to be.
```
createdb -h /tmp/$LOGNAME/sockets $DB_NAME
```

Pipelining a SQL script (to execute SQL commands in bulk, and copies information from .csv files in directory /csv_data)
```
psql -h /tmp/$LOGNAME/sockets $DB_NAME < create.sql
```




## Contributers
* Johnny Vo (jvo033@ucr.edu)
* Nish Patel (npate145@ucr.edu)
