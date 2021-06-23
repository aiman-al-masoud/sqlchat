package model.user;

import java.util.ArrayList;

import model.conversations.Conversation;
import model.conversations.Message;

/**
 * This interface is meant to be implemented by UI
 * classes that have to display up-to-date info about the User.
 */
public interface UserListener{
	public void onEnteredConversation(Conversation conversation);
	public void onExitedConversation();
	public void onLoggingOut();
	public void onLoggingIn();
	public void onMessages(ArrayList<Message> messages);
}




