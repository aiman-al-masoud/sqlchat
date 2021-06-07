package view;

import java.util.ArrayList;
import java.util.Scanner;

import model.conversations.Conversation;
import model.conversations.ConversationManager;
import model.conversations.Message;
import model.users.User;
import model.users.UserManager;
import model.users.User.UserListener;

public class Shell implements UserListener{


	User localUser;

	Scanner scanner;
	
	public Shell() {
	}


	public void startShell(){

		scanner = new Scanner(System.in);

		//get the current user 
		localUser = UserManager.getInstance().getLocalUser();


		//if no user is currently saved, ask for a userId
		if(localUser==null) {
			setLocalUser();
		}


		//keep prompting the user for the password till they get it right
		String passwordAttempt;
		do {
			//prompt user to enter their password
			System.out.println("Please enter your password to log in:");
			passwordAttempt = scanner.nextLine();

			if(passwordAttempt.toUpperCase().trim().equals("LOGOUT")) {
				this.setLocalUser();
			}

		}while(!localUser.logIn(passwordAttempt));



		//if they got it right...
		System.out.println("Welcome "+localUser.getId()+"!");

		//add this Shell to the User's listeners list
		localUser.addListener(this);


		//start the thread that polls the remote server for incoming messages
		localUser.startPullingMessages();


		//give user some guidance
		System.out.println("COMMANDS:");
		System.out.println("ls: list conversations. open [conversationName]. end: end conversation. ");

		//start the infinite send-receive loop
		while(true) {


			//input the id of the contact you want to message
			String command = scanner.nextLine();

			//if command is to exit, terminate the program
			if(command.toUpperCase().trim().equals("EXIT")) {
				//exit with no error code.
				System.exit(0);
			}



			//if the user is in a conversation, the "command" is just a message, unless it's an end-conversation command
			if(localUser.isInConversation()) {

				//end the conversation
				if(command.toUpperCase().trim().equals("END")) {
					exitConversation();
					continue;
				}

				//send a message
				localUser.sendMessage( command);
				continue;
			}


			//if the command is a "ls", then list the available conversations
			if(command.toUpperCase().trim().equals("LS")) {
				listConversations();
			}


			//if the command is to open a conversation, print all of its previous messages
			if(command.split("\\s+")[0].toUpperCase().trim().equals("OPEN")) {
				openConversation(command.split("\\s+")[1].trim());
			}


		}


	}


	public void listConversations() {
		for(Conversation conv : ConversationManager.getInstance().getConversations()) {
			System.out.println(conv);
		}
	}


	public void openConversation(String convName) {

		Conversation currentConversation = ConversationManager.getInstance().getConversation(convName);
		localUser.enterConversation(currentConversation);
		for(Message msg : currentConversation.getMessages()) {
			System.out.println(msg.prettyToString());
		}
	}


	public void exitConversation() {
		System.out.print("\n\n\n\n\n\n\n\n\n\n\n");  
		System.out.flush();  
		localUser.exitConversation();
		listConversations();
	}





	public void setLocalUser() {
		UserManager.getInstance().saveLocalUser(null);	
		System.out.println("Please enter your username:");
		String userId =  scanner.nextLine();
		localUser = new User(userId);
		UserManager.getInstance().saveLocalUser(localUser);	
	}




	@Override
	public void update(ArrayList<Message> messages) {

		//remember to include this in a SwingUtilities.invokeLater(new Runnable) 
		//if you're gonna update a Swing gui component.

		for(Message message : messages) {
			System.out.println(message.prettyToString());
		}
	}




}
