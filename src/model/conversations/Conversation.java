package model.conversations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import io.FileIO;
import model.user.User;
import model.user.LocalUser;

/**
 * A Conversation is a collection of messages exchanged with the same correspondent.
 *
 */

public class Conversation extends File{


	public static File conversationsDir = new File("res"+File.separator+"conversations");
	private ArrayList<Message> messages;

	String counterpart;


	ConversationListener conversationListener;
	
	
	
	public Conversation(String id) {

		//get this conversation from the relative directory
		super(conversationsDir.getPath()+File.separator+id);

		//the id of the correspondent/counterpart
		counterpart = id;

		//make a new list of messages
		messages = new ArrayList<Message>();

		//if this conversation file doesn't exist on disk yet, create it
		if(!exists()) {
			create();
		}

		//load the old messages from the file.
		loadMessages();
		
		
		//add the local user as a listener to this conversation.
		conversationListener = LocalUser.getInstance().getLocalUser();
	}



	/**
	 * Appends a single message to this Conversation
	 * @param message
	 */
	public void appendMessage(Message message) {
		messages.add(message);
		FileIO.append(conversationsDir.getPath()+File.separator+counterpart, message.toString()+"\n");
		
		
		//notify this conversation's listeners
		ArrayList<Message> msgs = new ArrayList<Message>();
		msgs.add(message);
		conversationListener.onMessages(msgs);
	}

	

	/**
	 * Append new messages to this Conversation
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
			if((message = Message.loadMessage(messageString))!=null) {
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
		return counterpart;
	}


	/**
	 * Creates a message and sends it to the "counterpart".
	 * @param message
	 */
	public void sendMessage(String message) {
		User sender = LocalUser.getInstance().getLocalUser();
		Message msg = new Message(System.currentTimeMillis(), sender.getId(), message, counterpart);
		this.appendMessage(msg);
		msg.sendMe();
	}


	
	public String getId() {
		return counterpart;
	}










}
