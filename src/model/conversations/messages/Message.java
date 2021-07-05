package model.conversations.messages;

import java.util.Date;

/**
 * This interface is implemented by different kinds of messages that
 * need to be treated differently.
 * 
 * This interface is used in lieu of the actual implementations 
 * in the controller and UI modules that don't need to know wheather a message
 * was sent or received.
 * 
 * @author aiman
 *
 */
public interface Message {
	
	
	/**
	 * returns a string for the purpose of storage on a file.
	 * @return
	 */
	public String getPickleString();
	
	/**
	 * returns a "polished" string for the purpose of being displayed in text-based user-interfaces.
	 * @return
	 */
	public String getPrettyString();
	
	
	/**
	 * Get the date this message has been sent
	 * @return
	 */
	public Date getDate();
	
	
	/**
	 * Get the content of the message
	 * @return
	 */
	public String getMessage();
	
	
	/**
	 * Get the sender's ID
	 * @return
	 */
	public String getSenderId();
	
	
	/**
	 * Get the recipient's ID
	 * @return
	 */
	public String getRecipientId();
	
	
	/**
	 * Get the hash of this message.
	 * @return
	 */
	public String getMessageHash();
	
	
	/**
	 * Get the timestamp of this message.
	 * @return
	 */
	public long getTimestamp();
	
	
	
}
