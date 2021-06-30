package controller;

import java.util.ArrayList;

import model.conversations.Conversation;
import model.conversations.Message;

/**
 * Has to be implemented by any User interface.
 */
public interface SessionListener{

	public void displayHelp();
	public void listConversations(ArrayList<Conversation> conversations);
	public void exitConversation(ArrayList<Conversation> conversations);
	public void printMessages(ArrayList<Message> messages);
	public void mainLoop();
	public void welcomeUser(String userId);
	public void displayRegisteredUsers(ArrayList<String> allRegisteredUsers);
	public void userMessage(String message);
	public void startPrompt(UserPrompt userPrompt);
	public void goHome();	
	public void displayConversation(Conversation conversation);
	
	

}
