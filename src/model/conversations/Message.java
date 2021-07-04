package model.conversations;

import java.security.PublicKey;
import java.util.Date;

import daos.MessageDAO;
import daos.UserDAO;
import model.encryption.Encryption;
import model.user.LocalUser;

/**
 * A Message can either be sent or deciphered. It can also be stored
 * away in a file as a string, and "loaded" back later when it has to be
 * displayed to the user. 
 * 
 * @author aiman
 */
public class Message {

	long timeSent; //in unix time
	String senderId;
	String message;
	String recipientId;



	public Message(long timeSent, String senderId, String message, String recipientId) {
		this.timeSent = timeSent;
		this.senderId = senderId;
		this.message = message;
		this.recipientId = recipientId;
	}


	/**
	 * Re-creates a message from a string that can be stored in a file.
	 * @param messageString
	 * @return
	 */
	public static Message loadMessage(String messageString) {
		String[] parts = messageString.split(";");
		long timeSent = Long.parseLong(parts[0].trim());
		String senderId = parts[1].trim();
		String message = parts[2];
		String recipientId = parts[3].trim();

		return new Message(timeSent, senderId, message, recipientId);
	}



	/**
	 * Sends this message to its "recipientId"
	 */
	public void sendMe() {
		String publicKeyString = UserDAO.getPublicKey(recipientId);			
		PublicKey publicKey = Encryption.getInstance().buildPublicKey(publicKeyString.split("\\s+")[0], publicKeyString.split("\\s+")[1]);
		String encryptedMessage =  Encryption.getInstance().cipher(publicKey, message);
		MessageDAO.messageUser(recipientId, senderId, encryptedMessage);
	}



	/**
	 * Decipher the contents of the message using the user's current local private key
	 */
	public void decipherForMe() {
		message = Encryption.getInstance().decipher(message);
	}


	/**
	 * Gets the Date this message was sent at.
	 * @return
	 */
	public Date getTimeSent() {
		return new Date(timeSent);
	}

	/**
	 * Gets the contents of the message
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Gets the sender's id.
	 * @return
	 */
	public String getSender() {
		return senderId;
	}

	/**
	 * Converts the message to the string to be stored in a file.
	 */
	@Override
	public String toString() {
		return timeSent+";"+senderId+";"+message+";"+recipientId;
	}


	/**
	 * Used to display the message in a polished form.
	 * @return
	 */
	public String prettyToString() {

		String prettyMessage = getTimeSent()+" ";

		if(senderId.equals(LocalUser.getInstance().getLocalUser().getId())) {
			prettyMessage+="[you]: ";
		}else {
			prettyMessage+="["+senderId+"]: ";
		}
		return prettyMessage+message;
	}



}
