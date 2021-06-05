package model.conversations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.FileIO;
import model.users.User;

public class Conversation extends File{


	public static File conversationsDir = new File("res"+File.separator+"conversations");
	ArrayList<Message> messages;
	User localUser;
	
	
	User otherUser;


	public Conversation(User otherUser) {
		super(conversationsDir.getPath()+File.separator+otherUser.getId());
		
		this.otherUser  = otherUser;
		
		messages = new ArrayList<Message>();
		if(!exists()) {
			create();
		}
		loadMessages();
	}


	
	/**
	 * Appends a single message to this Conversation
	 */
	public void appendMessage(Message message) {
		messages.add(message);
		FileIO.append(getPath(), message.toString()+"\n");
	}
	
	
	/**
	 * Append new messages to the list, as well as the file on disk.
	 * @param messages
	 */
	public void appendMessages(ArrayList<Message> messages) {
		for(Message newMessage : messages) {
			appendMessage(newMessage);
		}
	}
	
	
	


	/**
	 * Load messages from the file on disk.
	 */
	private void loadMessages() {
		Message message = null;
		for(String messageString : FileIO.read(getPath()).split("\n") ) {
			if((message = Message.buildMessage(messageString))!=null) {
				messages.add(message);
			}
		}
	}


	/**
	 * Get all of the messages.
	 * @return
	 */
	public ArrayList<Message> getMessages(){
		return messages;
	}




	/**
	 * Create the conversation file on disk.
	 */
	public void create() {
		try {
			createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	
	
	


	@Override
	public String toString() {
		
		
		return otherUser.getId();
	}

	
	


	/**
	 * Tester
	 * @param args
	 */
	public static void main(String[] args) {

	}

	
	
	
	
	



}
