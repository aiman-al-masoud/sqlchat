package model.conversations;

import java.util.ArrayList;

public interface ConversationListener {
	
	public void onMessages(ArrayList<Message> messages);
	
}
