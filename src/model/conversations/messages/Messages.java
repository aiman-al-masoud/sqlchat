package model.conversations.messages;


/**
 * A class containing a bunch of static methods having to do with the Message IF.
 * 
 * @author aiman
 *
 */
public class Messages {
	
	/**
	 * Loads a message from a string stored in a file.
	 * @param pickleString
	 * @return
	 */
	public static Message loadMessage(String pickleString) {
		
		try {
			String[] parts = pickleString.split(AbstractMessage.SEPARATOR);
			
			
			long timestamp = Long.parseLong(parts[0]);
			String senderId = parts[1];
			String message = parts[2];
			String recipientId = parts[3];
			String messageHash = parts[4];
			String type = parts[5];
			
			
			switch(MessageTypes.valueOf(type.trim())) {
			
			case RECEIVED:
				String verified = parts[6];
				return new ReceivedMessage(timestamp, senderId, message, messageHash, verified.equals("true")? true: false );
			case SENT:	
				return new SentMessage(timestamp, recipientId, message);	
			}
			
		}catch(Exception e) {
			
		}

		return null;
	}
	
	
	
	

}
