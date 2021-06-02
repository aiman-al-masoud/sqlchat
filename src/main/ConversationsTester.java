package main;

import conversations.Conversation;
import conversations.ConversationManager;
import messages.Message;

public class ConversationsTester {

	public static void main(String[] args) {
		
		Conversation conv = ConversationManager.getInstance().getConversation("amico");
		
		for(Message msg : conv.getMessages()) {
			System.out.println(msg.prettyToString());
		}
		
	}

}
