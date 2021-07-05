package model.conversations.messages;

import java.security.PublicKey;

import daos.MessageDAO;
import daos.UserDAO;
import model.encryption.Encryption;
import model.user.LocalUser;

/**
 * A SentMessage gets sent by the local user.
 * 
 * Can be sent.
 * 
 * @author aiman
 *
 */
public class SentMessage extends AbstractMessage {
	
	
	String YOU = "you";
	
	public SentMessage(String recipientId, String message) {
		this.recipientId = recipientId;
		this.message = message;
		this.timestamp = System.currentTimeMillis();

		
		//compute the hash and cipher it with the local user's private key
		messageHash = Encryption.hash(message);
		messageHash = Encryption.getInstance().cipher(Encryption.getInstance().getCurrentPrivateKey(), messageHash);
		
		this.senderId = LocalUser.getInstance().getLocalUser().getId();
		this.type = MessageTypes.SENT;
	}
	
	public SentMessage(long timestamp, String recipientId, String message) {
		this(recipientId, message);
		this.timestamp = timestamp;
	}
	
	
	@Override
	public String getPrettyString() {
		return getDate()+" ["+YOU+"]: "+message;
	}
	
	
	/**
	 * Get the encrypted version of this message.
	 * @return
	 */
	public String getEncryptedMessage() {
		String publicKeyString = UserDAO.getPublicKey(recipientId);			
		PublicKey publicKey = Encryption.getInstance().buildPublicKey(publicKeyString.split("\\s+")[0], publicKeyString.split("\\s+")[1]);
		return Encryption.getInstance().cipher(publicKey, message);
	}
	
	
	/**
	 * Send this message to its recipient.
	 */
	public void sendMe() {
		MessageDAO.sendMessage(this);
	}



}
