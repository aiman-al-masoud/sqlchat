package model.conversations;

import java.util.ArrayList;

/**
 * A conversation listener gets updated whenever a conversation gets new messages
 * from "within" or from "without".
 * @author aiman
 *
 */
public interface ConversationListener {
	
	public void onMessages(ArrayList<Message> messages);
	
}
