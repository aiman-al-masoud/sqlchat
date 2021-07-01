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
import model.user.LocalUser;


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

		//get the currently saved user 
		localUser = LocalUser.getInstance().getLocalUser();

		//add this Session to the User's listeners 
		if(localUser!=null) {
			localUser.addListener(this);
		}
	}


	public void startSession(){

		//if no user is currently saved, ask for a userId		
		if(localUser==null) {
			chooseUser();
		}

		//ask the user for their password 
		askForPassword();

		//start the thread that polls the remote server for incoming messages
		localUser.startPullingMessages();

		//start the main program loop
		userInterface.mainLoop();
	}



	/**
	 * Accepts a Command and executes it
	 * @param command
	 */
	public void runCommand(Command command) {

		//get the command's code
		SessionServices serviceCode = command.serviceCode;

		//get the inserted arguments
		String[] args = command.args;


		//commands for the user when not logged in:
		if(!localUser.isLoggedIn()) {
			this.loggedOutCommand(serviceCode, args);
			return;
		}

		
		//commands for the user when logged in and NOT in a conversation:
		if(localUser.isLoggedIn() && !localUser.isInConversation()) {
			this.loggedInCommand(serviceCode, args);
			return;
		}
		
		//commands when inside a conversation
		if(localUser.isInConversation()) {
			this.inConversationCommand(serviceCode, args);
			return;
		}
		
	}
	
	
	
	private void commonCommand(SessionServices serviceCode, String[] args) {
		
		switch(serviceCode) {
		
		case CLS:
			//clears the screen
			userInterface.goHome();
			break;
		case EXIT:
			//terminate the program with no error code.
			System.exit(0);
			break;
		case HELP:
			//display some help
			userInterface.displayHelp();
			break;
		case LSU:
			//lists all of the registered accounts on this server
			ArrayList<String> allRegisteredUsers = UserDAO.selectAll();
			userInterface.displayRegisteredUsers(allRegisteredUsers);
			break;
		case CONFIG:
			setConnectionParametersProcedure();
			break;
		case LOGOUT:
			//log the current user out			
			if(localUser!=null) {
				localUser.logout();
			}	
			break;	
		default:
		case NOTACMD:
			String wrongCommand = args.length==0 ? "" :args[0]; 
			userInterface.userMessage("'"+wrongCommand+"' not recognized as a command!\n Please enter 'help' for a list of valid commands.");
			break;
		}
		
	}
	
	
	private void loggedOutCommand(SessionServices serviceCode, String[] args) {
		
		switch(serviceCode) {
		case SIGNUP:
			createNewUserProcedure();
			break;
		default:
			commonCommand(serviceCode, args);
			break;
		}
	}
	
	private void loggedInCommand(SessionServices serviceCode, String[] args) {
		
		switch(serviceCode) {
		case RM:
			//deletes a conversation
			ConversationManager.getInstance().removeConversation(args[0].trim());
			break;
		case OPEN:
			//open a conversation
			if(localUser.isLoggedIn()) {
				Conversation conversation = ConversationManager.getInstance().getConversation(args[0].trim());
				localUser.enterConversation(conversation);	
			}
			break;
		case LS:
			//list the conversations
			if(localUser.isLoggedIn()) {
				userInterface.listConversations(ConversationManager.getInstance().getConversations());
			}
			break;
		case CHKEY:
			//changes the public key
			localUser.changeEncrypter();
			userInterface.userMessage("NEW PUBLIC KEY: "+localUser.getPublicKey());
			break;	
		case DELACC:
			confirmDeleteAccount();
			break;
		default:
			commonCommand(serviceCode, args);
			break;
		}
	}
	
	
	
	private void inConversationCommand(SessionServices serviceCode, String[] args) {
		
		switch(serviceCode) {
		
		case END:
			//end conversation
			localUser.exitConversation();
			break;
		default:
		case SENDMSG:
			String message = "";
			for(String word : args) {
				message+=word+" ";
			}
			localUser.sendMessage(message);
			break;
	
		}
		
	}
	


	/**
	 * This procedure indirectly calls the UI to get the username.
	 */
	public void chooseUser() {

		UserPrompt userPrompt = new UserPrompt(SessionServices.CHUSER);
		userPrompt.addPrompt("Enter your user id:");
		userInterface.startPrompt(userPrompt);
	}


	/**
	 * This procedure indirectly calls the user interface to retrieve a password attempt from the user
	 */
	public void askForPassword() {
		UserPrompt userPrompt = new UserPrompt(SessionServices.AUTHENTICATE);
		userPrompt.addPrompt("Enter your password:");
		userInterface.startPrompt(userPrompt);
	}





	/**
	 * This procedure indirectly uses the userinterface to get all of the parameters from the user
	 */
	public void setConnectionParametersProcedure() {

		//create and set up a new user-prompt
		UserPrompt userPrompt = new UserPrompt(SessionServices.CONFIG);
		userPrompt.addPrompt("Enter the server's domain:");
		userPrompt.addPrompt("Enter the server's port number:");
		userPrompt.addPrompt("Enter the username:");
		userPrompt.addPrompt("Enter the password:");
		userPrompt.addPrompt("Enter the schema:");

		//call the interface to display it
		userInterface.startPrompt(userPrompt);

	}


	public void createNewUserProcedure() {
		if(localUser!=null) {
			localUser.logout();
		}

		//create a userprompt
		UserPrompt userPrompt = new UserPrompt(SessionServices.SIGNUP);
		userPrompt.addPrompt("choose a new user id:");
		userPrompt.addPrompt("choose a new password:");

		//call the UI to display the userprompt
		userInterface.startPrompt(userPrompt);		
	}





	/**
	 * Gets called back by the UI (SessionListener) when 
	 * it's done getting input from user.
	 * @param userInput
	 * @param serviceCode
	 */
	public void callback(String[] userInput, SessionServices serviceCode) {

		switch(serviceCode) {

		case CONFIG:

			ConnectionToDB.setDomain(userInput[0].trim());
			ConnectionToDB.setPort(Integer.parseInt(userInput[1].trim()));
			ConnectionToDB.setUsername(userInput[2].trim());
			ConnectionToDB.setPassword(userInput[3].trim());
			ConnectionToDB.setSchema(userInput[4].trim());
			//creates the users-table in case the server doesn't have it yet.
			UserDAO.createUsersTable();

			break;
		case DELACC:

			//if confirm delete = n, terminate
			if(userInput[0].toUpperCase().trim().equals("N")) {
				return;
			}

			boolean success = localUser.deleteUser(userInput[1]);
			userInterface.userMessage("Deleted: "+success);

			break;
		case CHUSER:


			String userId = userInput[0].trim();
			//String passwordAttempt = userInput[1];
			User user = new User(userId);

			//check if user exists on server
			if(!user.exists()) {
				userInterface.userMessage("user does not exist!");
				this.chooseUser();
				return;
			}

			this.localUser = user;
			localUser.addListener(this);
			LocalUser.getInstance().saveLocalUser(user);

			break;
		case SIGNUP:
			user = new User(userInput[0].trim());
			user.createUser(userInput[1]);
			userInterface.userMessage("account created successfully!");
			break;
		case AUTHENTICATE:


			success = localUser.logIn(userInput[0]);

			if(!success) {
				askForPassword();
			}

			userInterface.welcomeUser(localUser.getId());
			break;
		default:
			break;

		}

	}


	public void confirmDeleteAccount() {

		//just return if current user is not logged in
		if(!localUser.isLoggedIn()) {
			return;
		}

		UserPrompt userPrompt = new UserPrompt(SessionServices.DELACC);
		userPrompt.addPrompt("Are you sure you want to delete this account? (y/n)");
		userPrompt.addPrompt("Confirm your password:");
		userInterface.startPrompt(userPrompt);
	}



	//USER LISTENER METHODS------------------
	@Override
	public void onEnteredConversation(Conversation conversation) {
		userInterface.displayConversation(conversation);
	}

	@Override
	public void onExitedConversation() {
		userInterface.exitConversation(ConversationManager.getInstance().getConversations());		
	}

	@Override
	public void onLoggingOut() {
		LocalUser.getInstance().deleteLocalUser();
	}

	@Override
	public void onLoggingIn() {
		//
	}

	@Override
	public void onMessages(ArrayList<Message> messages) {
		userInterface.printMessages(messages);
	}

	///////////////////////////////////////////////////////



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





}
