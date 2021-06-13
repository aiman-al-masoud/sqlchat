package model.conversations;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class ConversationManager {
	
	HashMap<String, Conversation> conversationsMap;
	
	
	private static ConversationManager instance;
	
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
	
	
	public Conversation getConversation(String conversationId) {
		
		Conversation conv = conversationsMap.get(conversationId);
		if(conv==null) {
			conv = new Conversation(conversationId);
			conversationsMap.put(conversationId, conv);
		}
		return conv;
	}
	
	
	
	public void removeConversation(String conversationId) {
		
		try {
			Conversation toBeRemoved = getConversation(conversationId);
			conversationsMap.remove(conversationId);
			toBeRemoved.delete();
		}catch(Exception e) {	
		}
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
