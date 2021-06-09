package model.users;

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

		//hash the password attempt to comapre it to the hashed, stored password on the server
		String encryptedPasswordAttempt = hasher.encrypt(passwordAttempt);

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

			//change my public key, so that the next time I get sent messages, those new messages are encrypted with a different key.
			if(incomingMessages.size()!=0) {
				changeEncrypter();
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
		if(loggedIn) {
			String encryptedPassword = hasher.encrypt(newPassword);
			UserDAO.modifyPassword(this, encryptedPassword);
		}	
	}


	/**
	 * Change the current encrypter, modifying the relative info stored publicly on the DB.
	 */
	public void changeEncrypter() {
		
		//get a new encrypter 
		encrypter = EncrypterBuilder.getInstance().getNewEncrypter();
		//change the public key on the DB.
		UserDAO.registerNewPublicKey(this);
	}



	/**
	 * Start polling the server every x seconds for new messages.
	 */
	public void startPullingMessages() {

		//reference to this User.
		User myself = this;

		//make a new timertask that pulls messages for this User.
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				myself.pullMessages();
			}

		};

		//start the timer, poll the server every 1 second for new messages.
		Timer timer = new Timer();
		timer.schedule(task, 0, 1000);

	}




}
