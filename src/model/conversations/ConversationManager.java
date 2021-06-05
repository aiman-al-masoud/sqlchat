package model.conversations;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import model.users.User;

public class ConversationManager {
	
	HashMap<String, Conversation> conversationsMap;
	
	
	private static ConversationManager instance;
	
	private ConversationManager() {
		conversationsMap = new HashMap<String, Conversation>();
		for(File file : Conversation.conversationsDir.listFiles()) {
			conversationsMap.put(file.getName(), new Conversation(new User(file.getName())));
		}
	}
	
	public static ConversationManager getInstance() {
		if(instance==null) {
			instance = new ConversationManager();
		}
		return instance;
	}
	
	
	public Conversation getConversation(String otherUsersName) {
		
		Conversation conv = conversationsMap.get(otherUsersName);
		if(conv==null) {
			conv = new Conversation(new User(otherUsersName));
		}
		return conv;
	}
	
	
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
	
	
	
	
	

}
