package view;

import java.util.ArrayList;
import java.util.Scanner;

import controller.Session;
import controller.SessionListener;
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

public class Shell implements SessionListener{


	/**
	 * To get user input.
	 */
	Scanner scanner;

	/**
	 * The controller calls the methods of Shell, and/or gets called back by Shell. 
	 */
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


	/**
	 * Prompts the user to enter some text and returns it to the controller.
	 */
	@Override
	public String userPrompt(String message) {
		System.out.println(message);
		String response  = scanner.nextLine();
		return response ;
	}


	@Override
	public void userMessage(String message) {
		System.out.println(message);
	}


	










}
