package view;

import java.util.ArrayList;
import java.util.Scanner;

import model.controller.Session;
import model.controller.Session.SessionListener;
import model.conversations.Conversation;
import model.conversations.ConversationManager;
import model.conversations.Message;
import model.users.User;
import model.users.UserManager;
import model.users.UserStatus;
import model.users.User.UserListener;

/**
 * 
 * 
 *
 */

public class Shell implements SessionListener{


	Scanner scanner;

	Session controller;

	public Shell(Session controller) {

		//start listening to the controller
		this.controller = controller;
		controller.addListener(this);

		//get a new stdin scanner
		scanner = new Scanner(System.in);

	}


	/**
	 * Wait for user input, then pass the arguments to the controller.
	 */
	@Override
	public void mainLoop() {
		while(true) {
			String command = scanner.nextLine();
			controller.runCommand(command);
		}
	}


	/**
	 * Prints all of the available conversations on screen
	 */
	@Override
	public void listConversations(ArrayList<Conversation> conversations) {
		for(Conversation conv : conversations) {
			System.out.println(conv);
		}
	}


	/**
	 * Displays messages from a conversation and starts listening to user input of messages to that conversation.
	 * @param conversation
	 */
	@Override
	public void conversationLoop(Conversation conversation) {
		//print all of the old messages of the conversation once
		for(Message msg : conversation.getMessages()) {
			System.out.println(msg.prettyToString());
		}

		//start the conversation loop
		while(controller.isInConversation()) {
			String command = scanner.nextLine();
			this.controller.conversationCommand(command);
		}	
	}


	/**
	 * Clears the screen and re-prints a list of saved conversations. 
	 */
	@Override
	public void exitConversation(ArrayList<Conversation> conversations) {
		System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");  
		listConversations(conversations);
	}

	/**
	 * Prompts the user to input their username.
	 */
	@Override
	public String getUsernameProcedure() {
		System.out.println("Please enter your username:");
		String userId =  scanner.nextLine(); 
		return userId;
	}


	
	/**
	 * Prompts the user to authenticate themselves (entering their password).
	 */
	@Override
	public String getPasswordAttempt() {
		//prompt user to enter their password
		System.out.println("Please enter your password to log in:");
		String passwordAttempt = scanner.nextLine();
		return passwordAttempt;
	}
	

	/**
	 * if they got it right...
	 */
	public void welcomeUser(String userId) {
		System.out.println("Welcome back "+userId+"!");
	}


	/**
	 * Displays some quick instructions on how to interact with this shell.
	 */
	@Override
	public void displayHelp() {
		System.out.println("COMMANDS:");
		System.out.println("ls: list conversations. open [conversationName]. end: end conversation. ");
	}


	/**
	 * Prints new incoming messages
	 */
	@Override
	public void printMessages(ArrayList<Message> messages) {
		for(Message message : messages) {
			System.out.println(message.prettyToString());
		}
	}


	










}
