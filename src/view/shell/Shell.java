package view.shell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import controller.Command;
import controller.Session;
import controller.SessionServices;
import io.FileIO;
import model.conversations.Conversation;
import model.conversations.Message;
import view.abstrct.AbstractUI;

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

		//set the input component
		this.setInputComponent(new ShellInput(this));
	}


	/**
	 * Wait for user input, then pass the arguments to the controller.
	 */
	@Override
	public void mainLoop() {
		while(true) {
			String commandText = scanner.nextLine();
			Command command  = Parser.parseCommand(commandText);
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
	 * Clears the screen and re-prints a list of saved conversations. 
	 */
	@Override
	public void exitConversation(ArrayList<Conversation> conversations) {
		this.goHome();
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
	public void displayRegisteredUsers(ArrayList<String> allRegisteredUsers) {
		userMessage("ALL OF THE ACCOUNTS ON THIS SERVER:");
		for(String userId : allRegisteredUsers) {
			userMessage(userId);
		}
	}



	@Override
	public void goHome() {
		userMessage("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
	}


	@Override
	public void displayConversation(Conversation conversation) {
		//print all of the old messages of the conversation just once
		this.printMessages(conversation.getMessages());
	}













}
