package model.users;

import java.util.ArrayList;
import java.util.logging.Logger;

import daos.MessageDAO;
import daos.UserDAO;
import model.conversations.Conversation;
import model.conversations.ConversationManager;
import model.conversations.Message;
import model.encryption.EncrypterBuilder;
import model.encryption.EncrypterIF;

/**
 * A User is the main actor in this whole business of IM.
 * Don't store the password field in the User object
 * for obvious reasons...
 */

public class User {



	/**
	 * This user's id
	 */
	private String id;

	/**
	 * Is this user logged in?
	 */
	private boolean loggedIn = false;


	/**
	 * This user's listers
	 */
	ArrayList<UserListener> listeners;


	/**
	 * The current conversation that the user is in
	 */
	Conversation currentConversation;


	/**
	 * This User's current encrypter
	 */
	EncrypterIF encrypter;



	Logger logger;

	
	



	public User(String id) {
		this.id = id;
		listeners = new ArrayList<UserListener>();
		logger = Logger.getLogger("UserLogger");
	}




	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return id;
	}



	/**
	 * Log the user in after they input the right password.
	 * @param passwordAttempt
	 */

	public boolean logIn(String passwordAttempt) {
		
		//make a new encrypter
		encrypter = EncrypterBuilder.getInstance().getDefaultEncrypter();

		//authenticate this user, and write their public key to the DB
		if(UserDAO.authenticate(this, passwordAttempt)) {
			loggedIn = true;
			
			//if login was successful, register a new public key for this user.
			UserDAO.registerNewPublicKey(this);
			
			return true;
		}
		
		//login operation unsuccessful
		return false;
	}
	
	
	
	
	


	/**
	 * Sends a message to another user.
	 * Only works if this user is logged in.
	 * @param recipientId
	 * @param message
	 */
	public void sendMessage(String message) {
		if(loggedIn) {
			
			
			Message toBeSent = new Message(System.currentTimeMillis(), this.id, message);

			
			currentConversation.appendMessage(toBeSent);

			
			ArrayList<Message> messages = new ArrayList<Message>();
			messages.add(toBeSent);
			//notify listeners!
			for(UserListener listener : listeners) {
				listener.update(messages);
			}


			//get the end-user's public key
			String encryptionKey = UserDAO.getPublicKey(currentConversation.toString());
			//set it as the encryption key
			if(encryptionKey!=null) {
				encrypter.setEncryptionKey(encryptionKey.split("\\s+"));
				//encrypt the message
				message = encrypter.encrypt(message);
			}
			
			
			//send the message to the recipient
			MessageDAO.messageUser(currentConversation.toString(), this, message);
			
			
		}
	}
	
	

	/**
	 * Pulls this user's pending messages from the server.
	 * Only works if this user is logged in.
	 */
	public void pullMessages(){
		if(loggedIn) {
			ArrayList<Message> incomingMessages = MessageDAO.pullMessages(this);
			
			if(encrypter!=null) {
				for(Message message : incomingMessages) {
					String plaintext = encrypter.decipher(message.getMessage());
					message.setMessage(plaintext);
				}
			}

			ConversationManager.getInstance().archiveMessages(incomingMessages);

			//notify listeners!
			for(UserListener listener : listeners) {
				listener.update(incomingMessages);
			}

		}

	}


	



	/**
	 * Tell the server to memorize a new user.
	 * @param password
	 */
	public void createUser(String password) {
		UserDAO.createUser(id, password);
	}



	public void addListener(UserListener listener) {
		listeners.add(listener);
	}
	public void removeListener(UserListener listener) {
		listeners.remove(listener);
	}
	public interface UserListener{
		public void update(ArrayList<Message> messages);
	}



	
	public void enterConversation(Conversation conversation) {
		this.currentConversation = conversation;
	}
	
	public void exitConversation() {
		this.currentConversation  =null;
	}
	
	public String getPublicKey() {
		return encrypter.getPublicKey()[0]+" "+encrypter.getPublicKey()[1];
	}
	
	
	
	
	


}
