package daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;

import io.ConnectionToDB;
import model.conversations.Message;
import model.users.User;

public class MessageDAO {


	static Connection connection;

	/**
	 * Send a text-message to a user.
	 * 
	 * @param recipient
	 * @param sender
	 * @param message
	 */
	public static void messageUser(String recipient, User sender, String message) {

		

		String senderId = sender.getId();
		long unixTime = System.currentTimeMillis(); 
		String recipientId = recipient;


		//if the recipientId contains spaces, stop everything as it could be a sql injection
		if(recipientId.split("\\s+").length>1) {
			return;
		}


		try {
			connection = ConnectionToDB.startConnection();
			String sql ="INSERT INTO "+recipientId+" (`timestamp`, `senderId`, `message`)\n"
					+ "VALUES (?, ?, ?);\n";

			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, unixTime);
			preparedStatement.setString(2, senderId);
			preparedStatement.setString(3, message);

			preparedStatement.execute();

		}catch(SQLException e) {
			e.printStackTrace();
		}
	}




	/**
	 * Pull all of the messages of a given user.
	 * @param recipient
	 * @return
	 */
	public static ArrayList<Message> pullMessages(User recipient) {
	

		ArrayList<Message> result = new ArrayList<Message>();

		String recipientId = recipient.getId();

		try {

			connection = ConnectionToDB.startConnection();
			String sql = "select * from {0}";

			Statement statement = connection.createStatement();

			ResultSet resultSet = statement.executeQuery(MessageFormat.format(sql, recipientId)); 
			
			while(resultSet.next()) {
				//one record for each iteration of the loop
				//starts counting from 1 onwards!!
				result.add(new Message(Long.parseLong(resultSet.getString(1)), resultSet.getString(2), resultSet.getString(3) ));
			}

			//delete all messages from the mail-box table
			statement = connection.createStatement();
			sql = "DELETE FROM {0}";
			statement.executeUpdate(MessageFormat.format(sql, recipientId));


		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		
		return result;
	}






}
