package model.conversations;

import java.util.Date;

import daos.MessageDAO;
import daos.UserDAO;
import model.encryption.EncrypterBuilder;
import model.encryption.EncrypterIF;
import model.user.LocalUser;

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




	public static Message loadMessage(String messageString) {
		try {
			String[] parts = messageString.split(";");
			long timeSent = Long.parseLong(parts[0].trim());
			String senderId = parts[1].trim();
			String message = parts.length>2? parts[2].trim() : ""; //in case of an empty message

			String recipientId = parts[3].trim();

			return new Message(timeSent, senderId, message, recipientId);
		}catch(NumberFormatException e) {

		}
		return null;
	}



	public Date getTimeSent() {
		return new Date(timeSent);
	}

	public String getMessage() {
		return message;
	}

	public String getSender() {
		return senderId;
	}

	@Override
	public String toString() {
		return timeSent+";"+senderId+";"+message+";"+recipientId;
	}




	public String prettyToString() {

		String prettyMessage = getTimeSent()+" ";


		if(senderId.equals(LocalUser.getInstance().getLocalUser().getId())) {
			prettyMessage+="[you]: ";
		}else {
			prettyMessage+="["+senderId+"]: ";
		}

		return prettyMessage+message;
	}


	public void setMessage(String message) {
		this.message = message;
	}





	public void sendMe() {
		String publicKey = UserDAO.getPublicKey(recipientId);			
		EncrypterIF encr = EncrypterBuilder.getInstance().getDefaultEncrypter();
		encr.setEncryptionKey(new String[] {publicKey.split("\\s+")[0].trim(),  publicKey.split("\\s+")[1].trim()});
		String encryptedMessage =  encr.encrypt(message);
		MessageDAO.messageUser(recipientId, senderId, encryptedMessage);
	}







}
