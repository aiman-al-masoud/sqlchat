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
 * The User accesses his/her conversations, sending or receiving 
 * messages therefrom.
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
	private ArrayList<UserListener> listeners;


	/**
	 * The current conversation that the user is in
	 */
	Conversation currentConversation;


	/**
	 * This User's current encrypter
	 */
	EncrypterIF encrypter;



	/**
	 * A logger for any logs produced by this user.
	 */
	Logger logger;






	public User(String id) {
		//set this User's id.
		this.id = id;
		//create a list for this User's listeners
		listeners = new ArrayList<UserListener>();
		//create a logger
		logger = Logger.getLogger("UserLogger");
	}


	/**
	 * Get this User's id.
	 * @return
	 */
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return getId();
	}



	/**
	 * Log the user in after they input the right password.
	 * @param passwordAttempt
	 */

	public boolean logIn(String passwordAttempt) {

		//build the default encrypter.
		encrypter = EncrypterBuilder.getInstance().getDefaultEncrypter();

		//encrypt passwordAttempt, 'cuz the one on the server is encrypted
		encrypter.setEncryptionKey(new String[]{encrypter.getPublicKey()[0], encrypter.getPublicKey()[1]});
		String encryptedPasswordAttempt = encrypter.encrypt(passwordAttempt);
		
		//authenticate this user, and write their public key to the DB
		if(UserDAO.authenticate(this, encryptedPasswordAttempt )) {

			//if login was successful, set the login variable to true.
			loggedIn = true;

			//return success
			return true;
		}

		//login operation unsuccessful
		return false;
	}




	/**
	 * Sends a message to the current conversation.
	 * Only works if this user is logged in.
	 * @param recipientId
	 * @param message
	 */
	public void sendMessage(String message) {

		//check if this User is logged in, and has chosen a conversation.
		if(loggedIn && currentConversation!=null) {

			//create a message object
			Message toBeSent = new Message(System.currentTimeMillis(), this.id, message);

			//append the message to the current conversation
			currentConversation.appendMessage(toBeSent);

			//notify listeners!
			ArrayList<Message> messages = new ArrayList<Message>();
			messages.add(toBeSent);
			for(UserListener listener : listeners) {
				listener.update(messages);
			}


			//get the recipient's public key
			String encryptionKey = UserDAO.getPublicKey(currentConversation.getId());
			//if the recipient does have an encryption key:
			if(encryptionKey!=null) {
				//set it as the encryption key
				encrypter.setEncryptionKey(encryptionKey.split("\\s+"));
				//encrypt the message
				message = encrypter.encrypt(message);
			}


			//send the message to the recipient
			MessageDAO.messageUser(currentConversation.getId(), this, message);
			
			//change the ecnrypter 
			changeEncrypter();

		}
	}



	/**
	 * Pulls this user's pending messages from the server.
	 * Only works if this user is logged in.
	 */
	public void pullMessages(){

		//check if this user is logged in 
		if(loggedIn) {

			//pull this user's raw incoming messages, and removes them from the server.
			ArrayList<Message> incomingMessages = MessageDAO.pullMessages(this);

			//decipher the messages with this User's private key.
			for(Message message : incomingMessages) {
				String plaintext = encrypter.decipher(message.getMessage());
				message.setMessage(plaintext);
			}

			//store the received messages in their respective conversations
			ConversationManager.getInstance().archiveMessages(incomingMessages);

			//notify listeners!
			for(UserListener listener : listeners) {
				listener.update(incomingMessages);
			}

		}

	}



	/**
	 * Tell the server to store a new user.
	 * @param password
	 */
	public void createUser(String password) {
		
		encrypter = EncrypterBuilder.getInstance().getDefaultEncrypter();
		
		encrypter.setEncryptionKey(new String[]{encrypter.getPublicKey()[0], encrypter.getPublicKey()[1]});
		String encryptedPassword = encrypter.encrypt(password);
		
		UserDAO.createUser(id, encryptedPassword);
		
		UserDAO.registerNewPublicKey(this);
	}



	/**
	 * Set a new conversation as the current conversation.
	 * @param conversation
	 */
	public void enterConversation(Conversation conversation) {
		this.currentConversation = conversation;
	}

	/**
	 * Set the current conversation to null.
	 */
	public void exitConversation() {
		this.currentConversation  =null;
	}

	/**
	 * get this user's public key.
	 * @return
	 */
	public String getPublicKey() {
		return encrypter.getPublicKey()[0]+" "+encrypter.getPublicKey()[1];
	}



	/**
	 * Add a user-listener to this User.
	 * @param listener
	 */
	public void addListener(UserListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a user-listener from this user.
	 * @param listener
	 */
	public void removeListener(UserListener listener) {
		listeners.remove(listener);
	}

	/**
	 * This interface is meant to be implemented by UI
	 * classes that have to display updated info about the User.
	 */
	public interface UserListener{
		public void update(ArrayList<Message> messages);
	}

	/**
	 * Modify the password stored on the server.
	 * @param currentPassword
	 * @param newPassword
	 */
	public void modifyPassword(String newPassword) {
		encrypter = EncrypterBuilder.getInstance().getDefaultEncrypter();
		encrypter.setEncryptionKey(new String[]{encrypter.getPublicKey()[0], encrypter.getPublicKey()[1]});
		String encryptedPassword = encrypter.encrypt(newPassword);
		UserDAO.modifyPassword(this, encryptedPassword);
	}

	
	public void changeEncrypter() {
		
		//get the password 
		String password = UserDAO.getPassword(this);
		
		//decipher it
		String plainPassword = encrypter.decipher(password);
		
		//get a new encrypter 
		encrypter = EncrypterBuilder.getInstance().getNewEncrypter();
		
		//encrypt the password with the new public key
		encrypter.setEncryptionKey(new String[]{encrypter.getPublicKey()[0], encrypter.getPublicKey()[1]});
		String encryptedPassword = encrypter.encrypt(plainPassword);
		
		//re-insert the newly-encrypted password on the DB.
		UserDAO.modifyPassword(this, encryptedPassword);
		
		//change the public key on the DB.
		UserDAO.registerNewPublicKey(this);
	}




}
