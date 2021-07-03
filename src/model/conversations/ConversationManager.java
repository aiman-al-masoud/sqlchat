package model.conversations;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import daos.MessageDAO;
import model.user.LocalUser;


public class ConversationManager {

	
	private static ConversationManager instance;

	
	HashMap<String, Conversation> conversationsMap;

	Timer pullTaskTimer;
	

	private ConversationManager() {
		conversationsMap = new HashMap<String, Conversation>();
		for(File file : Conversation.conversationsDir.listFiles()) {
			conversationsMap.put(file.getName(), new Conversation(file.getName()));
		}
	}

	public static ConversationManager getInstance() {

		//create the conversations dir if it doesn't exist yet
		if(!Conversation.conversationsDir.exists()) {
			Conversation.conversationsDir.mkdir();
		}

		if(instance==null) {
			instance = new ConversationManager();
		}
		return instance;
	}


	/**
	 * Get a conversation by its conversation id. Creates a new empty one if no such conversation was there.
	 * @param conversationId
	 * @return
	 */
	public Conversation getConversation(String conversationId) {

		Conversation conv = conversationsMap.get(conversationId);
		if(conv==null) {
			conv = new Conversation(conversationId);
			conversationsMap.put(conversationId, conv);
		}
		return conv;
	}



	/**
	 * Deletes a conversation by its id.
	 * @param conversationId
	 */
	public void removeConversation(String conversationId) {

		try {
			Conversation toBeRemoved = getConversation(conversationId);
			conversationsMap.remove(conversationId);
			toBeRemoved.delete();
		}catch(Exception e) {	
		}
	}


	/**
	 * Get all of the saved conversations.
	 * @return
	 */
	public ArrayList<Conversation> getConversations(){
		return new ArrayList<>(conversationsMap.values());
	}



	/**
	 * Sorts and adds each message to its appropriate conversation.
	 * @param messages
	 */

	public void archiveMessages(ArrayList<Message> messages) {
		for(Message message : messages) {
			getConversation(message.getSender()).appendMessage(message);
		}
	}


	/**
	 * Pulls the users's pending messages from the server.
	 */
	public void pullMessages(){


		//pull this user's raw incoming messages, and removes them from the server.
		ArrayList<Message> incomingMessages = MessageDAO.pullMessages(LocalUser.getInstance().getLocalUser());

		//decipher the messages with the local User's private key.
		for(Message message : incomingMessages) {
			message.decipherForMe();
		}

		//store the received messages in their respective conversations
		archiveMessages(incomingMessages);

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
