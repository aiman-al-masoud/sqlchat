package main;

import model.conversations.Conversation;
import model.conversations.ConversationManager;
import model.conversations.Message;

public class ConversationsTester {

	public static void main(String[] args) {
		
		Conversation conv = ConversationManager.getInstance().getConversation("amico");
		
		for(Message msg : conv.getMessages()) {
			System.out.println(msg.prettyToString());
		}
		
	}

}
