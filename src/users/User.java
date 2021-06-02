package users;

import java.util.ArrayList;

import conversations.ConversationManager;
import messages.Message;
import messages.MessageDAO;

public class User {

	/**
	 * Don't store the password field in the User object
	 * for obvious reasons...
	 */

	private String id;
	private boolean loggedIn = false;
	
	ArrayList<UserListener> listeners;
	

	public User(String id) {
		this.id = id;
		listeners = new ArrayList<UserListener>();
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
		if(UserDAO.authenticate(this, passwordAttempt)) {
			loggedIn = true;
			return true;
		}
		return false;
	}


	/**
	 * Sends a message to another user.
	 * Only works if this user is logged in.
	 * @param recipientId
	 * @param message
	 */
	public void sendMessage(String recipientId, String message) {
		if(loggedIn) {
			Message toBeSent = new Message(System.currentTimeMillis(), this.id, message);
			ConversationManager.getInstance().getConversation(recipientId).appendMessage(toBeSent);
			MessageDAO.messageUser(new User(recipientId), this, message);
			
			ArrayList<Message> messages = new ArrayList<Message>();
			messages.add(toBeSent);
			
			//notify listeners!
			for(UserListener listener : listeners) {
				listener.update(messages);
			}
			
		}
	}

	/**
	 * Pulls this user's pending messages from the server.
	 * Only works if this user is logged in.
	 * @return
	 */
	public ArrayList<Message> pullMessages(){
		if(loggedIn) {
			ArrayList<Message> incomingMessages = MessageDAO.pullMessages(this);
			ConversationManager.getInstance().archiveMessages(incomingMessages);
			
			//notify listeners!
			for(UserListener listener : listeners) {
				listener.update(incomingMessages);
			}
			
			return incomingMessages;
		}
		return null;
	}
	
	
	public boolean equals(User otherUser) {
		if(otherUser.getId().equals(this.id)) {
			return true;
		}
		return false;
	}

	
	
	
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
	





}
