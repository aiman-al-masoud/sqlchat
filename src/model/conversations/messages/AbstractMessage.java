package model.conversations.messages;

import java.util.Date;

/**
 * A class that gets extended by all implementations of Message IF
 * owing to its implementation of some common behaviors and attributes.
 * @author aiman
 *
 */
public abstract class AbstractMessage implements Message {
	
	
	
	protected long timestamp;
	protected String senderId;
	protected String message;
	protected String messageHash;
	protected String recipientId;
	protected MessageTypes type;
	public static String SEPARATOR = ";";
	
	
	@Override
	public String getPickleString() {
		return timestamp+SEPARATOR+senderId+SEPARATOR+message+SEPARATOR+recipientId+SEPARATOR+messageHash+SEPARATOR+type;
	}

	
	@Override
	public Date getDate() {
		return new Date(timestamp);
	}
	
	/**
	 * Gets the contents of the message
	 * @return
	 */
	@Override
	public String getMessage() {
		return message;
	}
	
	
	@Override
	public String getSenderId() {
		return senderId;
	}
	
	@Override
	public String getRecipientId() {
		return recipientId;
	}
	
	
	@Override
	public String getMessageHash() {
		return messageHash;
	}
	
	@Override
	public long getTimestamp() {
		return timestamp;
	}
	
	
	
}
