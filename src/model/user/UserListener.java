package model.user;

import java.util.ArrayList;

import model.conversations.Conversation;
import model.conversations.messages.*;

/**
 * This interface is meant to be implemented by 
 * classes that need to have access to up-to-date info about the User.
 */
public interface UserListener{
	public void onEnteredConversation(Conversation conversation);
	public void onExitedConversation();
	public void onLoggingOut();
	public void onLoggingIn();
	public void onMessages(ArrayList<Message> messages);
}




