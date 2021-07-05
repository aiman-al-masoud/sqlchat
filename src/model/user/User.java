package model.user;

import java.util.ArrayList;

import daos.UserDAO;
import model.conversations.Conversation;
import model.conversations.ConversationListener;
import model.encryption.Encryption;
import model.conversations.messages.Message;


/**
 * 
 * A User listens to the current conversation he is in.
 */

public class User implements ConversationListener{

	/**
	 * This user's id
	 */
	private String id;

	/**
	 * Is this user logged in?
	 */
	private boolean loggedIn = false;

	/**
	 * This user's listeners
	 */
	private ArrayList<UserListener> listeners;

	/**
	 * The current conversation that the user is in
	 */
	Conversation currentConversation;


	public User(String id) {
		//set this User's id.
		this.id = id;
		//create a list for this User's listeners
		listeners = new ArrayList<UserListener>();
	}


	/**
	 * Get this User's id.
	 * @return
	 */
	public String getId() {
		return id;
	}


	/**
	 * Is the user currently logged in?
	 * @return
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}


	/**
	 * Log the user in after they input the right password.
	 * @param passwordAttempt
	 */

	public boolean logIn(String passwordAttempt) {

		//hash the password attempt, to compare it to the hashed password on the server
		String encryptedPasswordAttempt =  Encryption.hash(passwordAttempt);

		//authenticate this user and set the loggedIn flag
		return loggedIn = UserDAO.authenticate(this, encryptedPasswordAttempt);
	}


	/**
	 * logout disables all of the connectivity-related features of this object
	 */
	public void logout() {

		//set loggedIn back to false
		loggedIn = false;

		//notify listeners
		for(UserListener listener : listeners) {
			listener.onLoggingOut();
		}
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

			//send the message over to the server
			this.currentConversation.sendMessage(message);

		}
	}


	/**
	 * Set a new conversation as the current conversation.
	 * @param conversation
	 */
	public void enterConversation(Conversation conversation) {

		if(!loggedIn) {
			return;
		}

		this.currentConversation = conversation;

		//notify listeners!
		for(UserListener listener : listeners) {
			listener.onEnteredConversation(conversation);
		}

	}

	/**
	 * Set the current conversation to null.
	 */
	public void exitConversation() {
		this.currentConversation  =null;

		//notify listeners!
		for(UserListener listener : listeners) {
			listener.onExitedConversation();
		}
	}

	/**
	 * Is the User currently involved in a conversation?
	 * @return
	 */
	public boolean isInConversation() {
		return currentConversation==null? false : true;
	}


	/**
	 * Tell the server to store a new user.
	 * @param password
	 */
	public void createUser(String password) {
		String encryptedPassword = Encryption.hash(password);
		UserDAO.createUser(id, encryptedPassword);
		UserDAO.registerNewPublicKey(this);
	}

	/**
	 * Delete this user from the server
	 * @param password
	 */
	public boolean deleteUser(String password) {

		boolean success = false;
		if(UserDAO.authenticate(this, Encryption.hash(password))) {
			this.loggedIn = false;
			success = UserDAO.deleteUser(id);
		}

		return success;
	}


	/**
	 * Checks if this user exists on the server.
	 */
	public boolean exists() {
		return UserDAO.userExists(this.id);	
	}


	/**
	 * get this user's public key.
	 * @return
	 */
	public String getPublicKey() {
		return Encryption.getInstance().getPublicKeyString();
	}

	/**
	 * Add a user-listener to this User.
	 * @param listener
	 */
	public void addListener(UserListener listener) {
		listeners.add(listener);
	}
	

	/**
	 * Modify the password stored on the server.
	 * @param currentPassword
	 * @param newPassword
	 */
	public void modifyPassword(String newPassword) {
		if(loggedIn) {
			String encryptedPassword = Encryption.hash(newPassword);
			UserDAO.modifyPassword(this, encryptedPassword);
		}	
	}


	/**
	 * Change the current encrypter, modifying the relative info stored publicly on the DB.
	 */
	public void changeEncrypter() {

		if(loggedIn) {
			//get a new keypair 
			Encryption.getInstance().renewKeyPair();			
			//change the public key on the DB.
			UserDAO.registerNewPublicKey(this);
		}

	}


	/**
	 * Receives messages from the currentConversation and forwards them to this User's listeners.
	 */
	@Override
	public void onMessages(ArrayList<Message> messages) {
		for(UserListener listener : listeners) {
			listener.onMessages(messages);
		}
	}





}
