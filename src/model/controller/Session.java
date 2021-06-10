package model.controller;

import java.util.ArrayList;
import model.conversations.Conversation;
import model.conversations.ConversationManager;
import model.conversations.Message;
import model.users.User;
import model.users.User.UserListener;
import model.users.UserManager;
import model.users.UserStatus;


/**
 * A Session listens to the current User, and is 
 * listened to by the User Interface.
 * 
 * It handles the conditionals/logic, and defines the 
 * text-based commands that can be used to interact 
 * with the system.
 * 
 */

public class Session implements UserListener{

	/**
	 * The listeners that obeys this Session.
	 * (Probably a User Interface Objects)
	 */
	SessionListener userInterface;


	/**
	 * To keep track of the user.
	 */
	User localUser;


	/**
	 * Has to be implemented by any User interface.
	 */
	public interface SessionListener{

		public String getUsernameProcedure();
		public void displayHelp();
		public void listConversations(ArrayList<Conversation> conversations);
		public void exitConversation(ArrayList<Conversation> conversations);
		public void printMessages(ArrayList<Message> messages);
		public void mainLoop();
		public void conversationLoop(Conversation conversation);
		public void welcomeUser(String userId);
		public String getPasswordAttempt();

	}


	/**
	 * Adds a new listener.
	 * @param listener
	 */
	public void addListener(SessionListener listener) {
		this.userInterface = listener;
	}

	/**
	 * Removes a new listener.
	 * @param listener
	 */
	public void removeListener(SessionListener listener) {
		this.userInterface = null;
	}


	public Session() {

	}






	public void startSession(){
		
		//get the current user 
		localUser = UserManager.getInstance().getLocalUser();

		//if no user is currently saved, ask for a userId		
		if(localUser==null) {
			setLocalUser();
		}
		
		//add this Session to the User's listeners 
		localUser.addListener(this);

		//ask the user for their password 
		passwordLoop();

		//welcome the user if login was successful
		userInterface.welcomeUser(localUser.getId());

		//start the thread that polls the remote server for incoming messages
		localUser.startPullingMessages();

		//give user some guidance
		userInterface.displayHelp();

		//start the main program loop
		userInterface.mainLoop();
	}


	/**
	 * Accepts a string-command and tells localUser what to do.
	 * @param command
	 */

	public void runCommand(String command) {

		//if command is to exit, then terminate the program
		if(command.toUpperCase().trim().equals("EXIT")) {
			//exit the program with no error code.
			System.exit(0);
		}

		//if the command is to logout...
		if(command.toUpperCase().trim().equals("LOGOUT")) {
			localUser.logout();
		}

		//if the command is an "ls", then list the available conversations
		if(command.toUpperCase().trim().equals("LS")) {
			userInterface.listConversations(ConversationManager.getInstance().getConversations());
		}

		//if the command is "help", display help
		if(command.toUpperCase().trim().equals("HELP")) {
			userInterface.displayHelp();
		}

		//if the command is to open a conversation...
		if(command.split("\\s+")[0].toUpperCase().trim().equals("OPEN")) {
			Conversation conversation = ConversationManager.getInstance().getConversation(command.split("\\s+")[1].trim());
			localUser.enterConversation(conversation);			
		}
	}



	public void conversationCommand(String command) {

		//if command is "end", end the conversation
		if(command.toUpperCase().trim().equals("END")) {
			localUser.exitConversation();
			return;
		}

		//else, send a message
		localUser.sendMessage(command);
	}
	

	public boolean isInConversation() {
		return localUser.isInConversation();
	}


	public void setLocalUser() {
		String userId = userInterface.getUsernameProcedure();
		localUser = new User(userId);
		UserManager.getInstance().saveLocalUser(localUser);
	}
	
	
	public void passwordLoop() {
		String passwordAttempt;
		do {
			
			passwordAttempt = userInterface.getPasswordAttempt();	

			//////extremely ugly part/////
			if(passwordAttempt.toUpperCase().equals("LOGOUT")) {
				UserManager.getInstance().deleteLocalUser();
				System.exit(0);
			}
			////////////////////
			
		}while(!localUser.logIn(passwordAttempt));
	}
	

	@Override
	public void update(ArrayList<Message> messages) {
		userInterface.printMessages(messages);
	}

	@Override
	public void updateStatus(UserStatus status, Object[] objects) {

		switch(status) {

		case ENTERING_CONVERSATION:
			Conversation currentConv = (Conversation)objects[0];
			userInterface.conversationLoop(currentConv);
			break;
		case EXITING_CONVERSATION:
			userInterface.exitConversation(ConversationManager.getInstance().getConversations());
			break;
		case LOGGING_OUT:
			setLocalUser();
			passwordLoop();
			break;

		}
	}













}
