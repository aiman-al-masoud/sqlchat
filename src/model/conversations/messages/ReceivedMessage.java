package model.conversations.messages;

import java.security.PublicKey;

import daos.UserDAO;
import model.encryption.Encryption;
import model.user.LocalUser;


/**
 * A ReceivedMessage gets received by the local user.
 * 
 * Can be deciphered and verified.
 * @author aiman
 *
 */
public class ReceivedMessage extends AbstractMessage {

	static String VERIFIED = "verified";
	static String UNVERIFIED = "unverified";
	Boolean verified = null;


	public ReceivedMessage(long timestamp, String senderId, String message, String messageHash) {

		this.timestamp = timestamp;
		this.senderId = senderId;
		this.message = message;
		this.messageHash = messageHash;
		this.type = MessageTypes.RECEIVED;
		this.recipientId = LocalUser.getInstance().getLocalUser().getId();
	}



	/**
	 * Alternate constructor for loading a stored ReceivedMessage back to memory from disk.
	 */
	public ReceivedMessage(long timestamp, String senderId, String message, String messageHash, boolean verified) {
		this(timestamp,senderId, message, messageHash);
		this.verified = verified;
	}


	@Override
	public String getPrettyString() {
		return getDate()+" ["+senderId+"]: "+this.message+" ["+ (verifySender()?VERIFIED:UNVERIFIED) +"]";
	}

	@Override
	public String getPickleString() {
		return super.getPickleString()+super.SEPARATOR+verifySender();
	}



	/**
	 * Deciphers the message with the current user's private key
	 */
	public void decipherMe() {
		this.message = Encryption.getInstance().decipher(message);
	}


	/**
	 * verify the identity of the sender
	 * @return
	 */
	public boolean verifySender() {

		//if verified flag is still null, try verifying the message
		if(verified==null) {
			String calculatedHash = message==null? "-1" : Encryption.hash(message);
			String publicKeyString = UserDAO.getPublicKey(senderId);			
			PublicKey publicKey = Encryption.getInstance().buildPublicKey(publicKeyString.split("\\s+")[0], publicKeyString.split("\\s+")[1]);
			verified = calculatedHash.equals(Encryption.getInstance().decipher(publicKey, messageHash));
		}

		return verified;
	}
















}
