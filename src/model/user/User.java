package model.user;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import daos.MessageDAO;
import daos.UserDAO;
import model.conversations.Conversation;
import model.conversations.ConversationManager;
import model.conversations.Message;
import model.encryption.EncrypterBuilder;
import model.encryption.EncrypterIF;
import sha256.SHA256;

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

	/**
	 * the hasher is used to safely store passwords on the server. 
	 */
	SHA256 hasher;


	/**
	 * Periodically pulls messages from the server.
	 */
	Timer pullTaskTimer;


	public User(String id) {
		//set this User's id.
		this.id = id;
		//create a list for this User's listeners
		listeners = new ArrayList<UserListener>();
		//create a logger
		logger = Logger.getLogger("UserLogger");
		//create a SHA256 object to encrypt passwords
		hasher = new SHA256();
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

		//get the default encrypter.
		encrypter = EncrypterBuilder.getInstance().getDefaultEncrypter();

		//hash the password attempt, to compare it to the hashed password on the server
		String encryptedPasswordAttempt = hasher.encrypt(passwordAttempt);

		//authenticate this user and set the loggedIn flag
		return loggedIn = UserDAO.authenticate(this, encryptedPasswordAttempt);
	}


	/**
	 * logout disables all of the connectivity-related features of this object
	 */
	public void logout() {
		
		//set loggedIn back to false
		loggedIn = false;

		//stop polling the server for new messages
		this.stopPullingMessages();

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

			//create a message object
			Message toBeSent = new Message(System.currentTimeMillis(), this.id, message);

			//notify listeners!
			ArrayList<Message> messages = new ArrayList<Message>();
			messages.add(toBeSent);
			for(UserListener listener : listeners) {
				listener.onMessages(messages);
			}

			//send the message over to the server
			this.currentConversation.sendMessage(message);

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
				listener.onMessages(incomingMessages);
			}
		}

	}
	
	

	/**
	 * Tell the server to store a new user.
	 * @param password
	 */
	public void createUser(String password) {
		encrypter = EncrypterBuilder.getInstance().getDefaultEncrypter();
		String encryptedPassword = hasher.encrypt(password);
		UserDAO.createUser(id, encryptedPassword);
		UserDAO.registerNewPublicKey(this);
	}

	/**
	 * Delete this user from the server
	 * @param password
	 */
	public boolean deleteUser(String password) {

		boolean success = false;
		if(UserDAO.authenticate(this, hasher.encrypt(password))) {
			this.stopPullingMessages();
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
	 * Set a new conversation as the current conversation.
	 * @param conversation
	 */
	public void enterConversation(Conversation conversation) {
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
	 * Modify the password stored on the server.
	 * @param currentPassword
	 * @param newPassword
	 */
	public void modifyPassword(String newPassword) {
		if(loggedIn) {
			String encryptedPassword = hasher.encrypt(newPassword);
			UserDAO.modifyPassword(this, encryptedPassword);
		}	
	}


	/**
	 * Change the current encrypter, modifying the relative info stored publicly on the DB.
	 */
	public void changeEncrypter() {

		if(loggedIn) {
			//get a new encrypter 
			encrypter = EncrypterBuilder.getInstance().getNewEncrypter();
			//change the public key on the DB.
			UserDAO.registerNewPublicKey(this);
		}

	}


	/**
	 * Start polling the server every x seconds for new messages.
	 */
	public void startPullingMessages() {

		//make a new timertask that pulls messages for this User.
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				pullMessages();
			}

		};

		//start the timer, poll the server every 1 second for new messages.
		pullTaskTimer = new Timer();
		long millisecs = 1000;
		pullTaskTimer.schedule(task, 0, millisecs);

	}


	/**
	 * Stops pulling messages 
	 */
	public void stopPullingMessages() {
		if(pullTaskTimer!=null ) {
			pullTaskTimer.cancel();

		}
	}




}
