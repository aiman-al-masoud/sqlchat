package model.conversations;

import java.util.Date;

import model.user.LocalUser;

public class Message {

	long timeSent; //in unix time
	String senderId;
	String message;


	public Message(long timeSent, String senderId, String message) {
		this.timeSent = timeSent;
		this.senderId = senderId;
		this.message = message;
	}

	public static Message buildMessage(String messageString) {
		try {
			String[] parts = messageString.split(";");
			long timeSent = Long.parseLong(parts[0].trim());
			String senderId = parts[1].trim();
			String message = parts.length>2? parts[2].trim() : ""; //in case of an empty message
			return new Message(timeSent, senderId, message);
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
		return timeSent+";"+senderId+";"+message;
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
	


}
