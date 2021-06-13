package model.controller;

import java.io.File;
import java.util.ArrayList;

import daos.UserDAO;
import io.ConnectionToDB;
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

		public void displayHelp();
		public void listConversations(ArrayList<Conversation> conversations);
		public void exitConversation(ArrayList<Conversation> conversations);
		public void printMessages(ArrayList<Message> messages);
		public void mainLoop();
		public void conversationLoop(Conversation conversation);
		public void welcomeUser(String userId);
		public String userPrompt(String message);
		public void userMessage(String message);


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
		
		//if necessary
		createSettingsDir();
		
		//get the current user 
		localUser = UserManager.getInstance().getLocalUser();

		//add this Session to the User's listeners 
		if(localUser!=null) {
			localUser.addListener(this);
		}
	}


	public void startSession(){

		//if no user is currently saved, ask for a userId		
		if(localUser==null) {
			setLocalUser();
		}

		//ask the user for their password 
		passwordLoop();

		//start the thread that polls the remote server for incoming messages
		localUser.startPullingMessages();

		//start the main program loop
		userInterface.mainLoop();
	}


	/**
	 * Accepts a string-command and tells localUser what to do.
	 * @param command
	 */

	public void runCommand(String command) {

		//switch on the first argument of the command
		String firstArgument = command.split("\\s+")[0].toUpperCase().trim();
		switch(firstArgument) {

		case "EXIT":
			//terminate the program with no error code.
			System.exit(0);
		case "LOGOUT":
			//log the current user out
			localUser.logout();
			break;
		case "LOGIN":
			//prompt the user to log in
			if(!localUser.isLoggedIn()) {
				setLocalUser();
				passwordLoop();
			}
			break;
		case "LS":
			//list the conversations
			if(localUser.isLoggedIn()) {
				userInterface.listConversations(ConversationManager.getInstance().getConversations());
			}
			break;
		case "HELP":
			//display some help
			userInterface.displayHelp();
			break;
		case "OPEN":
			//open a conversation
			if(localUser.isLoggedIn()) {
				Conversation conversation = ConversationManager.getInstance().getConversation(command.split("\\s+")[1].trim());
				localUser.enterConversation(conversation);	
			}
			break;
		case "CONFIG":
			//logout, and prompt the user to enter the new connection-settings
			if(localUser!=null) {
				localUser.logout();
			}
			setConnectionParametersProcedure();
			break;
		case "SIGNUP":
			//lets you pick a new user id and password. 
			createNewUserProcedure();
			break;
		case "CHKEY":	
			//changes the public key
			localUser.changeEncrypter();
			this.userInterface.userMessage("NEW PUBLIC KEY: "+localUser.getPublicKey());
			break;
		case "CLS":
			//clears the screen
			userInterface.userMessage("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
			break;
		case "RM":
			//deletes a conversation
			ConversationManager.getInstance().removeConversation(command.split("\\s+")[1].trim());
			break;
		default:
			//displays default message.
			userInterface.userMessage("'"+firstArgument+"' not recognized as a command!\n Please enter 'help' for a list of valid commands.");
			break;

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
		String userId = userInterface.userPrompt("Enter your user id:");
		localUser = new User(userId);
		UserManager.getInstance().saveLocalUser(localUser);
		//add this Session to the User's listeners 
		localUser.addListener(this);
	}


	public void passwordLoop() {
		String passwordAttempt;
		do {

			passwordAttempt = userInterface.userPrompt("Enter your password:");	

			//////extremely ugly part/////
			if(passwordAttempt.toUpperCase().equals("LOGOUT")) {
				UserManager.getInstance().deleteLocalUser();
				System.exit(0);
			}
			////////////////////

		}while(!localUser.logIn(passwordAttempt));

		userInterface.welcomeUser(localUser.getId());
	}

	public void setConnectionParametersProcedure() {
		String domain = userInterface.userPrompt("Enter the server's domain:");
		ConnectionToDB.setDomain(domain);
		String port = userInterface.userPrompt("Enter the server's port number:");
		ConnectionToDB.setPort(Integer.parseInt(port));
		String username = userInterface.userPrompt("Enter the username:");
		ConnectionToDB.setUsername(username);
		String password = userInterface.userPrompt("Enter the password:");
		ConnectionToDB.setPassword(password);
		String schema = userInterface.userPrompt("Enter the schema:");
		ConnectionToDB.setSchema(schema);
		//creates the users-table in case the server doesn't have it yet.
		UserDAO.createUsersTable();
	}


	public void createNewUserProcedure() {
		if(localUser!=null) {
			localUser.logout();
		}
		String newUserId = userInterface.userPrompt("choose a new user id:");
		User user = new User(newUserId);
		String newPassword = userInterface.userPrompt("choose a new password:");
		user.createUser(newPassword);
		userInterface.userMessage("now log in with the credentials you just chose:");
		setLocalUser();
		passwordLoop();
	}


	/**
	 * Creates the settings directory if it doesn't exist yet.
	 */
	private static void createSettingsDir() {
		//create the settings dir if it doesn't exist (thank you so much git)
		File settings = new File("res/settings");
		if(!settings.exists()) {
			settings.mkdir();
		}
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
			UserManager.getInstance().deleteLocalUser();
			break;

		}
	}















}
