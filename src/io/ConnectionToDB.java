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
	static private String domain;
	static private int port;
	static private String username;
	static private String password;
	static private String schema;
	static private String DBURL;

	
	static final private String CONFIGFILE_PATH = "res/settings/netConfig";
	static private ConfigFile connConfig = new ConfigFile(CONFIGFILE_PATH);
	
	
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
	public synchronized static Connection startConnection() {
		
		//if connection is not null, don't touch it
		if(isOpen()) {
			return connection;
		}
		
		//else, retreive config settings from config file
		domain = connConfig.get("domain");
		port = Integer.parseInt(connConfig.get("port"));
		username = connConfig.get("username");
		password = connConfig.get("password");
		schema = connConfig.get("schema");
		DBURL = "jdbc:mysql://"+domain+":"+port+"/"+schema+"?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

		
		//connection still has to be opened, do it
		try {

			connection = DriverManager.getConnection(DBURL, username, password);
		}catch(Exception e ) {
			e.printStackTrace();
			return null;
		}
		return connection;
	}
	
	
	/**
	 * The IP address or equivalent DNS of the sql-server. 
	 * @param domain
	 */
	public static void setDomain(String domain) {
		connConfig.put("domain", domain);
	}
	
	
	/**
	 * The port at which the server makes this service available.
	 * @param port
	 */
	public static void setPort(int port) {
		connConfig.put("port", port+"");
	}
	
	/**
	 * The username of the user accessing the DB.
	 * @param username
	 */
	public static void setUsername(String username) {
		connConfig.put("username", username);
	}
	
	/**
	 * The user's password.
	 * @param password
	 */
	public static void setPassword(String password) {
		connConfig.put("password", password);
	}
	
	
	/**
	 * The schema that contains the necessary tables.
	 * @param schema
	 */
	public static void setSchema(String schema) {
		connConfig.put("schema",schema);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	



}

