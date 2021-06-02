package io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * This class is sort of like a singleton.
 *
 */
public class ConnectionToDB {
	
	/**
	 * This project makes use of a single DB, with a single
	 * user and a single schema, to keep things simple
	 * and for economic reasons. 
	 */
	
	//connection, and connection-related parameters
	static private Connection connection;
	static private String domain = "sql11.freemysqlhosting.net";
	static private int port = 3306;
	static private String username = "sql11415583";
	static private String password = "xlswAUuwlk";
	static private String schema = "sql11415583";
	static private String DBURL = "jdbc:mysql://"+domain+":"+port+"/"+schema;


	//checks if a connection is already open
	private  static boolean isOpen() {
		if(connection == null) {
			return false;
		}else {
			return true;
		}
	}


	//closes a connection if it's open.
	public static void closeConnection() {
		if(isOpen() == true) {
			try {
				connection.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}	
		}

	}



	/**
	 * Tries to return an active connection to the DB.
	 * @return
	 */
	public static Connection startConnection() {
		
		//if connection is not null, don't touch it
		if(isOpen()) {
			return connection;
		}
		
		//else, if connection still has to be opened, do it
		try {
			connection = DriverManager.getConnection(DBURL, username, password);
		}catch(Exception e ) {
			e.printStackTrace();
			return null;
		}
		return connection;
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	



}

