package controller;

import java.io.File;
import java.util.ArrayList;

import daos.UserDAO;
import io.ConnectionToDB;
import model.conversations.Conversation;
import model.conversations.ConversationManager;
import model.conversations.Message;
import model.user.User;
import model.user.UserListener;
import model.user.UserManager;


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
	 * (Probably User Interface Objects)
	 */
	SessionListener userInterface;


	/**
	 * To keep track of the user.
	 */
	User localUser;

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
		case "LS-U":
			//lists all of the registered accounts on this server
			this.userInterface.userMessage("ALL OF THE ACCOUNTS ON THIS SERVER:");
			for(String user : UserDAO.selectAll()) {
				userInterface.userMessage(user);
			}
			break;
		case "DELACC":
			//deletes the current account
			if(localUser.isLoggedIn()) {
				String response = userInterface.userPrompt("Are you sure you want to delete this account? (y/n)");
				if(response.toUpperCase().equals("N")) {
					return;
				}
				String passwordAttempt = userInterface.userPrompt("Confirm your password:");
				boolean success = localUser.deleteUser(passwordAttempt);
				userInterface.userMessage("Deleted: "+success);
			}
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

	
	/**
	 * This procedure indirectly calls the UI to get the username.
	 */
	public void setLocalUser() {
		String userId;
		
		//tell the user interface to fetch a user name
		do {
			userId = userInterface.userPrompt("Enter your user id:");
			localUser = new User(userId);
			
		//while the user doesn't exist, keep on asking for a new username	
		}while(!localUser.exists());
		
		//save the new local user
		UserManager.getInstance().saveLocalUser(localUser);
		//add this Session to the User's listeners 
		localUser.addListener(this);
	}


	/**
	 * This procedure indirectly calls the user interface to retrieve a password attempt from the user
	 */
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

	
	
	/**
	 * This procedure indirectly uses the userinterface to get all of the parameters from the user
	 */
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
	public void onEnteredConversation(Conversation conversation) {
		userInterface.conversationLoop(conversation);
	}

	@Override
	public void onExitedConversation() {
		userInterface.exitConversation(ConversationManager.getInstance().getConversations());		
	}

	@Override
	public void onLoggingOut() {
		UserManager.getInstance().deleteLocalUser();
	}

	@Override
	public void onLoggingIn() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMessages(ArrayList<Message> messages) {
		userInterface.printMessages(messages);
	}



}
