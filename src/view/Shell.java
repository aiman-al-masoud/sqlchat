package view;

import java.util.ArrayList;
import java.util.Scanner;

import model.conversations.Conversation;
import model.conversations.ConversationManager;
import model.conversations.Message;
import model.users.User;
import model.users.UserManager;
import model.users.UserStatus;
import model.users.User.UserListener;

public class Shell implements UserListener{


	User localUser;

	Scanner scanner;


	public void startShell(){


		//get a new stdin scanner
		scanner = new Scanner(System.in);

		//get the current user 
		localUser = UserManager.getInstance().getLocalUser();

		//add this Shell to the User's listeners list
		try {
			localUser.addListener(this);
		}catch(NullPointerException e) {
			//if no user is currently saved, ask for a userId
			setLocalUserProcedure();
		}

		//ask the user for their password 
		loginProcedure();


		//start the thread that polls the remote server for incoming messages
		localUser.startPullingMessages();


		//give user some guidance
		displayHelp();


		//start the shell's infinite loop
		while(true) {
			String command = scanner.nextLine();
			runCommand(command);
		}


	}


	/**
	 * Accepts a string-command and tells localUser what to do.
	 * @param command
	 */

	public void runCommand(String command) {

		//if command is to exit, then terminate the program
		if(command.toUpperCase().trim().equals("EXIT")) {
			//exit with no error code.
			System.exit(0);
		}

		//if the command is to logout...
		if(command.toUpperCase().trim().equals("LOGOUT")) {
			localUser.logout();
		}

		//if the command is a "ls", then list the available conversations
		if(command.toUpperCase().trim().equals("LS")) {
			listConversations();
		}

		//if the command is "help", display help
		if(command.toUpperCase().trim().equals("HELP")) {
			displayHelp();
		}

		//if the command is to open a conversation...
		if(command.split("\\s+")[0].toUpperCase().trim().equals("OPEN")) {
			Conversation conversation = ConversationManager.getInstance().getConversation(command.split("\\s+")[1].trim());
			localUser.enterConversation(conversation);
		}
	}



	@Override
	public void update(ArrayList<Message> messages) {

		//display the messages on the shell
		for(Message message : messages) {
			System.out.println(message.prettyToString());
		}
	}

	@Override
	public void updateStatus(UserStatus status, Object[] objects) {

		switch(status) {

		case ENTERING_CONVERSATION:
			Conversation currentConv = (Conversation)objects[0];
			openConversation(currentConv);
			break;
		case EXITING_CONVERSATION:
			exitConversation();
			break;
		case LOGGING_OUT:
			setLocalUserProcedure();
			loginProcedure();
			break;

		}
	}









	///////GRAPHICS AND I/O/////////////////////////////////

	
	
	/**
	 * Prints all of the available conversations on screen
	 */
	public void listConversations() {
		for(Conversation conv : ConversationManager.getInstance().getConversations()) {
			System.out.println(conv);
		}
	}


	/**
	 * Displays messages from a conversation and starts listening to user input of messages to that conversation.
	 * @param conversation
	 */
	public void openConversation(Conversation conversation) {

		//print all of the old messages of the conversation once
		for(Message msg : conversation.getMessages()) {
			System.out.println(msg.prettyToString());
		}

		//while the user is in the conversation...
		while(localUser.isInConversation()) {

			//get the user's input
			String command = scanner.nextLine();

			//if command is "end", end the conversation
			if(command.toUpperCase().trim().equals("END")) {
				localUser.exitConversation();
				continue;
			}

			//else, send a message
			localUser.sendMessage(command);
			continue;
		}

	}


	/**
	 * Clears the screen and re-prints a list of saved conversations. 
	 */
	public void exitConversation() {
		System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");  
		listConversations();
	}


	/**
	 * Prompts the user to input their name and saves it
	 */
	public void setLocalUserProcedure() {
		UserManager.getInstance().deleteLocalUser();	
		System.out.println("Please enter your username:");
		String userId =  scanner.nextLine(); 
		UserManager.getInstance().saveLocalUser(new User(userId));	
		this.localUser = UserManager.getInstance().getLocalUser();
	}


	/**
	 * Prompts the user to authenticate themselves (entering their password).
	 */
	public void loginProcedure() {

		//keep prompting the user for the password till they get it right
		String passwordAttempt;
		do {
			//prompt user to enter their password
			System.out.println("Please enter your password to log in:");
			passwordAttempt = scanner.nextLine();

			if(passwordAttempt.toUpperCase().trim().equals("LOGOUT")) {
				this.setLocalUserProcedure();
			}

		}while(!localUser.logIn(passwordAttempt));

		//if they got it right...
		System.out.println("Welcome "+localUser.getId()+"!");
	}



	/**
	 * Displays some quick instructions.
	 */
	public void displayHelp() {
		System.out.println("COMMANDS:");
		System.out.println("ls: list conversations. open [conversationName]. end: end conversation. ");
	}










}
