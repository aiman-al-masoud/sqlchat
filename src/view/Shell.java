package view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import controller.AbstractUI;
import controller.Command;
import controller.Session;
import controller.SessionServices;
import io.FileIO;
import model.conversations.Conversation;
import model.conversations.Message;

/**
 * Shell is a user-interface class that makes a set of 
 * i/o methods available to a "Session" controller.
 * 
 * The Session controller calls Shell's methods, 
 * and sometimes gets called/or called-back too.
 * 
 *
 */

public class Shell extends AbstractUI{


	/**
	 * To get user input.
	 */
	Scanner scanner;

	
	
	public Shell(Session controller) {
		super(controller);
		
		//get a new stdin scanner
		scanner = new Scanner(System.in);
	}

	
	
	public static Command parseCommand(String commandText) {
		
		
		String[] commandParts = commandText.split("\\s+");
		String commandName = commandParts[0].toUpperCase();
		
		SessionServices commandCode;
		try {
			commandCode = SessionServices.valueOf(commandName);
		}catch(IllegalArgumentException e) {
			commandCode = SessionServices.NOTACMD;
			String[] args = {commandParts[0]};
			return new Command(commandCode, args);
		}
		
		
		String[] args;
		if(commandParts.length == 1) {
			args = new String[0];
		}else {
			args = Arrays.copyOfRange(commandParts, 1, commandParts.length);
		}
		
	
		
		return new Command(commandCode, args);
	}
	
	
	
	

	/**
	 * Wait for user input, then pass the arguments to the controller.
	 */
	@Override
	public void mainLoop() {
		while(true) {
			String commandText = scanner.nextLine();
			Command command  = parseCommand(commandText);
			session.runCommand(command);
		}
	}


	/**
	 * Prints all of the available conversations on screen
	 */
	@Override
	public void listConversations(ArrayList<Conversation> conversations) {
		System.out.println("saved conversations:");
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
		//print all of the old messages of the conversation just once
		for(Message msg : conversation.getMessages()) {
			System.out.println(msg.prettyToString());
		}

		//start the conversation loop
		while(session.isInConversation()) {
			String command = scanner.nextLine();
			this.session.conversationCommand(command);
		}	
	}


	/**
	 * Clears the screen and re-prints a list of saved conversations. 
	 */
	@Override
	public void exitConversation(ArrayList<Conversation> conversations) {
		for(int i=0; i<100;i++) {
			System.out.println("");
		}
		listConversations(conversations);
		System.out.println();
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
		System.out.println(FileIO.read("res/documentation/shellHelp"));
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


	

	@Override
	public void userMessage(String message) {
		System.out.println(message);
	}


	@Override
	public String waitForUserResponse(String message) {
		System.out.println(message);
		String response = scanner.nextLine();
		return response;
	}


	










}
