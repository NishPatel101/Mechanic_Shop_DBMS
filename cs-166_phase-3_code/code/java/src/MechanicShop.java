/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class MechanicShop{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public MechanicShop(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + MechanicShop.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		MechanicShop esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new MechanicShop (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. AddCustomer");
				System.out.println("2. AddMechanic");
				System.out.println("3. AddCar");
				System.out.println("4. InsertServiceRequest");
				System.out.println("5. CloseServiceRequest");
				System.out.println("6. ListCustomersWithBillLessThan100");
				System.out.println("7. ListCustomersWithMoreThan20Cars");
				System.out.println("8. ListCarsBefore1995With50000Milles");
				System.out.println("9. ListKCarsWithTheMostServices");
				System.out.println("10. ListCustomersInDescendingOrderOfTheirTotalBill");
				System.out.println("11. < EXIT");
				
				/*
				 * FOLLOW THE SPECIFICATION IN THE PROJECT DESCRIPTION
				 */
				switch (readChoice()){
					case 1: AddCustomer(esql); break;
					case 2: AddMechanic(esql); break;
					case 3: AddCar(esql); break;
					case 4: InsertServiceRequest(esql); break;
					case 5: CloseServiceRequest(esql); break;
					case 6: ListCustomersWithBillLessThan100(esql); break;
					case 7: ListCustomersWithMoreThan20Cars(esql); break;
					case 8: ListCarsBefore1995With50000Milles(esql); break;
					case 9: ListKCarsWithTheMostServices(esql); break;
					case 10: ListCustomersInDescendingOrderOfTheirTotalBill(esql); break;
					case 11: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice
	
	public static void AddCustomer(MechanicShop esql){//1
		try{
			String findIDsize = "SELECT id FROM Customer;";
			int countCID;
			List<List<String>> numOfCID = esql.executeQueryAndReturnResult(findIDsize);
			countCID = numOfCID.size();
			System.out.print("Enter new customer's first name: ");
			String c_fname = in.readLine();
			System.out.print("Enter new customer's last name: ");
			String c_lname = in.readLine();
			System.out.print("Enter new customer's phone number: ");
			String c_phone = in.readLine();
	         	System.out.print("Enter new customer's address: ");
			String c_address = in.readLine();
		
			String query = "INSERT INTO Customer (id, fname, lname, phone, address) ";
				query += "VALUES (\'" + countCID + "\','" + c_fname + "\',\'" + c_lname + "\',\'" + c_phone + "\',\'" + c_address + "\');";
		
			esql.executeUpdate(query);
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}
	
	public static void AddMechanic(MechanicShop esql){//2
		try {
			//Finding new ID, since Mechanic.id is type int4
			String nextID = "SELECT id FROM Mechanic;";
			List<List<String>> usedIDs = esql.executeQueryAndReturnResult(nextID);
			int m_id = usedIDs.size();

			System.out.print("Enter new mechanic's first name: ");
			String fname = in.readLine();
			System.out.print("Enter new mechanic's last name: ");
			String lname = in.readLine();
			System.out.print("Enter new mechanic's years of experience: ");
			String experience = in.readLine();
			
			String addMechanic = "INSERT INTO Mechanic (id, fname, lname, experience) ";
				addMechanic += "VALUES (\'" + m_id + "\',\'" + fname + "\',\'" + lname + "\',\'" + experience + "\');";	
			
			esql.executeUpdate(addMechanic);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void AddCar(MechanicShop esql){//3
		try{
			System.out.print("Enter new car's VIN: ");
			String car_vin = in.readLine();
			System.out.print("Enter new car's make: ");
			String car_make = in.readLine();
			System.out.print("Enter new car's model: ");
			String car_model = in.readLine();
			System.out.print("Enter new car's year: ");
			String car_year_string = in.readLine(); //convert string to int
			int car_year = Integer.parseInt(car_year_string);

			String query = "INSERT INTO Car(vin, make, model, year) ";
				query += "VALUES (\'" + car_vin + "\',\'" + car_make + "\',\'" + car_model + "\',\'" + car_year + "\');";

        		esql.executeUpdate(query);
			
      		}catch(Exception e){
        		 System.err.println (e.getMessage());
      		}
	}
	
public static void InsertServiceRequest(MechanicShop esql){//4
		try {
			//Finding new ID, since Service_Request.rid is type in4
			String nextID = "SELECT rid FROM Service_Request;";
			List<List<String>> usedIDs = esql.executeQueryAndReturnResult(nextID);
			int rid = usedIDs.size() + 1;
			
			System.out.print("Enter last name of customer: ");
			String lastName = in.readLine();
			
			String lookupLastName = "SELECT * FROM Customer WHERE lname = '" + lastName + "';";
			int numRows = esql.executeQueryAndPrintResult(lookupLastName);
			
			//Customer not found
			if (numRows == 0) {
				boolean decide = false;
				do {
					System.out.println("Could not find a customer with that last name. Would you like to add a new customer? Y/N");
					String input = in.readLine();
					if (input.equalsIgnoreCase("Y")) {
						AddCustomer(esql);
						decide = true;	
					}
					else if (input.equalsIgnoreCase("N")) {
						System.out.println("Cancelling initiated service request.");
						decide = true;
					}
				} while (!decide);
			}

			//Customer name found; parse cid
			else {
				System.out.print("Enter customer id: ");
				String inputCID = in.readLine();
				String selectCar = "SELECT ROW_NUMBER() OVER (ORDER BY O.car_vin), C FROM Customer Cust, Owns O, Car C WHERE Cust.id = O.customer_id AND C.vin = O.car_vin AND O.customer_id = '" + inputCID + "';";
				numRows = esql.executeQueryAndPrintResult(selectCar);
				boolean validOption = false;
				String rowChoice = "";
				int row = -1;
				do {
					System.out.print("Input 0 to select a listed car or 1 to add a new car: ");
					rowChoice = in.readLine();
					row = Integer.parseInt(rowChoice);
					if (row == 0 || row == 1) {
						validOption = true;
					}
					else {
						System.out.println("Invalid option selected.");
					}
				} while (!validOption);
				
				//Adding new car for service request
				if (row == 1) {
					//Replace function call with code for brute force way
					//of getting the added car's vin to insert into new SR (unless there is a better solution)
					System.out.print("Enter new car's VIN: ");
            				String car_vin = in.readLine();
            				System.out.print("Enter new car's make: ");
            				String car_make = in.readLine();
            				System.out.print("Enter new car's model: ");
            				String car_model = in.readLine();
            				System.out.print("Enter new car's year: ");
            				String car_year_string = in.readLine(); //convert string to int
            				int car_year = Integer.parseInt(car_year_string);

            				String query = "INSERT INTO Car(vin, make, model, year) ";
                			query += "VALUES ('" + car_vin + "','" + car_make + "','" + car_model + "','" + car_year + "');";

                			esql.executeUpdate(query);


					System.out.print("Enter current odometer reading on the car: ");
					String odometer = in.readLine();
					System.out.print("Enter customer's complaints with the car: ");
			 		String complaint = in.readLine();
					
					String initiateSR = "INSERT INTO Service_Request (rid, customer_id, car_vin, date, odometer, complain) VALUES (" + Integer.toString(rid) + ", " + inputCID + ", '" + car_vin + "', CURRENT_DATE, " + odometer + ", '" + complaint + "');";
				}
				
				//Choosing listed car for service request
				else {
					/*String findCar = "SELECT carToService FROM ";
							findCar += "(SELECT ROW_NUMBER() OVER (ORDER BY O.car_vin) AS rowNum, C.vin, C.make, C.model, C.year ";
							findCar += "FROM Customer Cust, Owns O, Car C ";
							findCar += "WHERE C.vin = O.car_vin ";
							findCar += "AND O.customer_id = Cust.id ";
							findCar += "AND O.customer_id = '" + inputCID + "') AS carToService ";
					       findCar += "WHERE rowNum = " + rowChoice + ";";
					
					List<List<String>> cars = esql.executeQueryAndReturnResult(findCar);
					String selectedCar = cars.get(0).get(0);
					String[] parse = selectedCar.split(",");
					String vin = parse[1];*/
					
					boolean validVin = false;
					String editVin = "";
					Scanner scan = new Scanner(System.in);
					do {
						System.out.print("Choose a vin from the list: ");
						editVin = scan.nextLine();
						//Check vin as valid in list
						String check = "SELECT C.vin FROM Car C, Customer Cust, Owns O WHERE C.vin = '" + editVin + "' AND C.vin = O.car_vin AND O.customer_id = Cust.id;";
						List<List<String>> checkCarSelect = esql.executeQueryAndReturnResult(check);
						if (checkCarSelect.isEmpty()) {
							System.out.print("Vin not found in list.");	
						}
						else {
							validVin = true;
						}
					} while (!validVin);
					

					System.out.print("Enter odometer reading on the car: ");
					String odometer = scan.nextLine();
					System.out.print("Enter customer's complaint(s) about the car: ");
					String complaint = scan.nextLine();

					String addServiceRequest = "INSERT INTO Service_Request (rid, customer_id, car_vin, date, odometer, complain) VALUES (" + Integer.toString(rid) + ", " + inputCID + ", '" + editVin + "', CURRENT_DATE, " + odometer + ", '" + complaint + "');";
					esql.executeUpdate(addServiceRequest);
				}
			}
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	public static void CloseServiceRequest(MechanicShop esql) throws Exception{//5
			String query;
			int count;
		
			//finds size for CID
			String findCRsize = "SELECT wid FROM Closed_Request;"; 
			List<List<String>> CR_size = esql.executeQueryAndReturnResult(querySize);
			int CID = CR_size.size();
			
			//Get request number from user & check if it exists
			System.out.println("Insert service request number: ");
			String requestNum = in.readLine();
			query = "SELECT * FROM Service_Request SR WHERE SR.rid = \'" + requestNum + "\';";			
			count = esql.executeQuery(query);
			if(count == 0) {
				System.out.println("Service request cannot be found.\n");
				return;
			}
			
			//Get mechanic id from user & check existence
			System.out.println("Insert mechanic id: ");
			String mechID = in.readLine();
			query = "SELECT * FROM Mechanic M WHERE M.id = \'" + requestNum + "\';";
			count = esql.executeQuery(query);
			if(count == 0) {
				System.out.println("Mechanic ID cannot be found.\n");
				return;
			}
		
			System.out.println("Insert repair comments: ");
			String comment = in.readLine();
		
			System.out.println("Insert bill charge amount: ");
			String billAmt = in.readLine();
		
			//check if closing date is after the request date
			query = "SELECT * FROM Service_Request SR WHERE SR.rid = '" + requestNum + "' AND SR.date <= CURRENT_DATE;";
			count = esql.executeQuery(query);
			if(count == 0) {
				System.out.println("Closing date is invalid. Date must be after request date.\n");
				return;
			}
		
			query = "INSERT INTO Closed_Request (wid, rid, mid, date, comment, bill) VALUES (\'" + CID + "\',\'" + requestNum + "\',\'" + mechID + "\',\'" + comment + "\','" + billAmt + "\');";
			esql.executeUpdate(query);
		
	}
	
	public static void ListCustomersWithBillLessThan100(MechanicShop esql){//6
		try {
			String less_than_100 = "SELECT Cust.fname, Cust.lname, CR.date, CR.comment, CR.bill ";
			       less_than_100 += "FROM Customer Cust, Service_Request SR, Closed_Request CR ";
			       less_than_100 += "WHERE CR.bill < 100 ";
			       less_than_100 += "AND Cust.id = SR.customer_id ";
			       less_than_100 += "AND SR.rid = CR.rid;";

			int numRows = esql.executeQueryAndPrintResult(less_than_100);
			System.out.println("Total row(s): " + numRows);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void ListCustomersWithMoreThan20Cars(MechanicShop esql){//7
		try{
 		String query = "SELECT numList.fname, numList.lname, numList.numOwnedCars FROM (SELECT C.id, C.fname, C.lname, COUNT(*) AS numOwnedCars FROM Owns O,Customer C WHERE C.id = O.customer_id GROUP BY C.id, C.fname, C.lname) AS numList WHERE numOwnedCars > 20;";
		
 		int rowCount = esql.executeQueryAndPrintResult(query);
 		System.out.println ("total row(s): " + rowCount);
		}catch(Exception e){
 			System.err.println (e.getMessage());
		}
	}
	
	public static void ListCarsBefore1995With50000Milles(MechanicShop esql){//8
		try {
			String query = "SELECT C.make, C.model, C.year ";
			       query += "FROM Car C, Service_Request S ";
			       query += "WHERE S.car_vin = C.vin ";
			       query += "AND S.odometer < 50000 ";
			       query += "AND C.year < 1995;";

			int numRows = esql.executeQueryAndPrintResult(query);
			System.out.println("Total row(s): " + numRows);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void ListKCarsWithTheMostServices(MechanicShop esql){//9
		try{	
			System.out.print("Input how many cars should be listed: ");
			String k_amount = in.readLine();
				
			String query = "SELECT C.make, C.model, topNumServices.numOfServices FROM Car C, (SELECT S.car_vin, COUNT(S.car_vin) as numOfServices FROM Service_Request S WHERE S.rid NOT IN (SELECT C.rid FROM Closed_Request C) GROUP BY S.car_vin ORDER BY numOfServices DESC) topNumServices WHERE C.vin = topNumServices.car_vin ORDER BY topNumServices.numOfServices DESC LIMIT " + k_amount + ";";
			
			esql.executeQueryAndPrintResult(query);
 			//System.out.println ("total row(s): " + rowCount);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void ListCustomersInDescendingOrderOfTheirTotalBill(MechanicShop esql){//10
		try {
			String query = "SELECT C.fname, C.lname, Total ";
			       query += "FROM Customer C, ";
					query += "(SELECT SUM(CR.bill) AS Total, SR.customer_id ";
					query += "FROM Closed_Request CR, Service_Request SR ";
					query += "WHERE CR.rid = SR.rid ";
					query += "GROUP BY SR.customer_id) AS AggrCost ";
			       query += "WHERE C.id = AggrCost.customer_id ";
			       query += "ORDER BY AggrCost.Total DESC;";

			int numRows = esql.executeQueryAndPrintResult(query);
			System.out.println("Total row(s): " + numRows);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
}
