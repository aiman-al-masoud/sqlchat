package daos;

import java.sql.Connection;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;

import io.ConnectionToDB;
import model.conversations.messages.*;
import model.conversations.messages.SentMessage;
import model.user.User;

public class MessageDAO {


	static Connection connection;

	/**
	 * Send a text-message to a user.
	 * 
	 * @param recipient
	 * @param sender
	 * @param message
	 */
	public static void sendMessage(SentMessage message) {

		
		//get the fields from the message object.
		long unixTime = message.getTimestamp();
		String recipientId = message.getRecipientId();
		String senderId = message.getSenderId();
		String messageContent  = message.getEncryptedMessage();
		String messageHash = message.getMessageHash();
		
		

		//if the recipientId contains spaces, stop everything as it could be a sql injection
		if(recipientId.split("\\s+").length>1) {
			return;
		}


		try {
			connection = ConnectionToDB.startConnection();
			String sql ="INSERT INTO "+recipientId+" (`timestamp`, `senderId`, `message`, `messageHash`)\n"
					+ "VALUES (?, ?, ?, ?);\n";

			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, unixTime);
			preparedStatement.setString(2, senderId);
			preparedStatement.setString(3, messageContent);
			preparedStatement.setString(4, messageHash);

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

			//one record for each iteration of the loop
			while(resultSet.next()) {
				
							
				//build a ReceivedMessage and decipher it for the local user 
				ReceivedMessage msg = new ReceivedMessage(Long.parseLong(resultSet.getString(1)), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
				result.add(msg);
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
