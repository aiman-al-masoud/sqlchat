package daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;

import io.ConnectionToDB;
import model.users.User;


public class UserDAO {

	static Connection connection;

	private UserDAO() {
	}


	public static ArrayList<User> selectAll() {

		String query = "select * from Users";

		ArrayList<User> result = new ArrayList<User>();

		try {
			connection = ConnectionToDB.startConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query); //execute only works for read-only queries
			while(resultSet.next()) {
				//one record for each iteration of the loop
				//starts counting from 1 onwards!!
				result.add(new User(resultSet.getString(1)));
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
					+ "CONSTRAINT primaryKey PRIMARY KEY (`timestamp`, `senderId`)\n"
					+ ")";
			Statement statement = connection.createStatement();
			statement.execute(MessageFormat.format(sql, id));



		}catch(SQLException e) {
			e.printStackTrace();
		}

	}





	public static boolean authenticate(User user, String passwordAttempt) {

		String userId = user.getId();

		try {

			connection = ConnectionToDB.startConnection();

			String sql = "select `password` from `Users` where id=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userId);


			ResultSet resultSet = preparedStatement.executeQuery();


			String password = null;
			while(resultSet.next()) {
				password = resultSet.getString(1);
			}

			if(passwordAttempt.equals(password)) {
				//registerNewPublicKey(user);
				return true;
			}


		}catch(SQLException e) {
			e.printStackTrace();
			ConnectionToDB.closeConnection();
		}

		return false;
	}


	public static void registerNewPublicKey(User user) {

		connection = ConnectionToDB.startConnection();



		try {
			Statement statement  = connection.createStatement();

			statement.executeUpdate(" UPDATE Users\n"
					+ "SET publicKey = \""+user.getPublicKey()+"\"\n"
					+ "WHERE id = \""+user.getId()+"\"");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}



	public static String getPublicKey(String userId) {
		connection = ConnectionToDB.startConnection();

		try {
			Statement statement  = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("select publicKey from Users where id = \""+userId+"\"");

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












}
