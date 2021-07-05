package daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;


import io.ConnectionToDB;
import model.user.User;


public class UserDAO {

	static Connection connection;

	private UserDAO() {
	}


	/**
	 * Get all of the usernames on the server.
	 * @return
	 */
	public static ArrayList<String> selectAll() {

		String query = "select `id` from Users";

		ArrayList<String> result = new ArrayList<String>();

		try {
			connection = ConnectionToDB.startConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query); //execute only works for read-only queries
			while(resultSet.next()) {
				//one record for each iteration of the loop. getString(n) starts counting from n = 1 onwards!!
				result.add(resultSet.getString(1));
			}

		}catch(SQLException e) {
			e.printStackTrace();
			ConnectionToDB.closeConnection();
			return result;
		}


		return result;

	}
	
	

	/**
	 * Creates a new user by adding their entry to the 
	 * id-password table, and creating a new 'mailbox'
	 * table just for that new user. 
	 * 
	 * @param user
	 */
	public static void createUser(String id, String password) {


		try {
			//get a connection
			connection = ConnectionToDB.startConnection();

			//add username-password entry in users' table
			String sql ="INSERT INTO Users (`id`, `password`)\n"
					+ "VALUES (?, ?);\n";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, id);
			preparedStatement.setString(2, password);
			preparedStatement.executeUpdate();


			//create the user's 'message-box' table
			sql = "CREATE TABLE {0} ( \n"
					+ "`timestamp` BIGINT,\n"
					+ "`senderId` VARCHAR(30),\n"
					+ "`message` VARCHAR(5000),\n"
					+ "`messageHash` VARCHAR(1000),\n"
					+ "CONSTRAINT primaryKey PRIMARY KEY (`timestamp`, `senderId`)\n"
					+ ")";
			Statement statement = connection.createStatement();
			statement.execute(MessageFormat.format(sql, id));



		}catch(SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Deletes a user account from the server.	
	 * @param userId
	 * @return
	 */
	public static boolean deleteUser(String userId) {
		
		//prevent the main table from getting deleted
		if(userId.equals("Users")) {
			return false;
		}
		
		try {
			String sql = "DELETE FROM `Users` WHERE id = ?";
			connection = ConnectionToDB.startConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userId);
			preparedStatement.executeUpdate();
			
			sql = "DROP TABLE IF EXISTS "+userId;
			Statement statement  = connection.createStatement();
			statement.executeUpdate(sql);
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	public static boolean userExists(String userId) {
		
		String sql = "SELECT * FROM `Users` WHERE id = ?";
		
		try {
			connection = ConnectionToDB.startConnection();
			PreparedStatement preparedStatement;
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userId);
			ResultSet results = preparedStatement.executeQuery();
			return results.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
		
	

	/**
	 * Check if a password entered by the user is the same as the one stored on the server.
	 * @param user
	 * @param passwordAttempt
	 * @return
	 */
	public static boolean authenticate(User user, String passwordAttempt) {

		//get the encrypted password from the server
		String password = getPassword(user);

		//if the password attempt is successful, return true
		if(passwordAttempt.equals(password)) {
			return true;
		}

		return false;
	}


	/**
	 * Register a User's new public key on the server.
	 * @param user
	 */
	public static void registerNewPublicKey(User user) {

		connection = ConnectionToDB.startConnection();

		try {
			
			String sql = " UPDATE Users\n"
					+ "SET publicKey = ?\n"
					+ "WHERE id = ?";
			
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, user.getPublicKey());
			preparedStatement.setString(2, user.getId());
			preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	
	/**
	 * Get a User's public key from the server.
	 * @param userId
	 * @return
	 */
	public static String getPublicKey(String userId) {
		connection = ConnectionToDB.startConnection();

		try {
			
			String sql = "select publicKey from Users where id = ?";
			
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userId);		
			ResultSet resultSet = preparedStatement.executeQuery();
			
			
			String publicKey;
			while(resultSet.next()) {
				publicKey = resultSet.getString(1);
				return publicKey;
			}


		} catch (SQLException e) {
			e.printStackTrace();
		}


		return null;
	}
	
	

	/**
	 * Modify a user's password on the server.
	 * @param user
	 * @param newPassword
	 */
	public static void modifyPassword(User user, String newPassword) {

		//get the connection
		connection = ConnectionToDB.startConnection();

		try {
			
			//standardized update-query
			String sql = " UPDATE `Users`\n"
					+ "SET `password` = ?\n"
					+ "WHERE `id` = ?";
			
			//make a prepared statement
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, newPassword.trim());
			preparedStatement.setString(2, user.getId());			
			preparedStatement.executeUpdate();
		
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	/**
	 * Get a User's (ecnrypted) password from the server.
	 * @param user
	 * @return
	 */

	public static String getPassword(User user) {

		try {
			connection = ConnectionToDB.startConnection();

			String sql = "select `password` from `Users` where id=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, user.getId());
			ResultSet resultSet = preparedStatement.executeQuery();

			String password = null;
			while(resultSet.next()) {
				password = resultSet.getString(1);
			}

			return password;

		}catch(SQLException e) {

		}

		return null;
	}



	/**
	 * Create the Users table. To be called in case it doesn't exist on the server yet.
	 */
	public static void createUsersTable() {
		
		Connection connection = ConnectionToDB.startConnection();
		String sql = "CREATE TABLE IF NOT EXISTS `Users` (\n" + 
				"  `id` varchar(30) NOT NULL,\n" + 
				"  `password` varchar(1200) DEFAULT NULL,\n" + 
				"  `publicKey` varchar(1200) DEFAULT NULL,\n" + 
				"   PRIMARY KEY (`id`))\n";
		
		try {
			Statement statement  = connection.createStatement();
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	





}
