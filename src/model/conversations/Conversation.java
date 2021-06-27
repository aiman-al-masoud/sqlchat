package model.conversations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import daos.MessageDAO;
import daos.UserDAO;
import io.FileIO;
import model.encryption.EncrypterBuilder;
import model.encryption.EncrypterIF;
import model.user.User;
import model.user.UserManager;

/**
 * The id of a conversation is the cocatenation of the names
 * of the participants with a separator 
 * 
 *
 */

public class Conversation extends File{


	public static File conversationsDir = new File("res"+File.separator+"conversations");
	private ArrayList<Message> messages;
	
	private ArrayList<String> participants;
	static char SEPARATOR = '+'; 
	
	
	public Conversation(String id) {
		
		//get this conversation from the relative directory
		super(conversationsDir.getPath()+File.separator+id);
		
		//initialize the participants list
		participants = new ArrayList<String>();

		
		try {

			//split the id by the SEPARATOR to get each of participants' id
			for(String participant : id.split("\\"+SEPARATOR+"")) { //unfortunately, this SEPATATOR needs to be escaped
				participants.add(participant);
			}
		}catch(Exception e ) {
			//in case there's just one participant
			participants.add(id);
		}
			
		//make a new list of messages
		messages = new ArrayList<Message>();
		
		//if this conversation file doesn't exist on disk yet, create it
		if(!exists()) {
			create();
		}
		
		//load the old messages from the file.
		loadMessages();
	}

	
	
	/**
	 * Adds a new participant to this conversation, and changes the name of its file on disk
	 * @param participant
	 */
	public void addParticipant(String participant) {
		this.participants.add(participant);
		this.renameTo(new File(conversationsDir.getPath()+File.separator+getId()));
	}
	

	
	/**
	 * Appends a single message to this Conversation
	 */
	public void appendMessage(Message message) {
		messages.add(message);
		FileIO.append(conversationsDir.getPath()+File.separator+getId(), message.toString()+"\n");
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

	
	/**
	 * The id is a concatenation of the names of the participants separated by the SEPARATOR.
	 * @return
	 */
	public String getId() {
		String id = "";
		for(String participant : participants) {
			id+=participant+SEPARATOR;
		}
		return id.charAt(id.length()-1)==SEPARATOR? id.substring(0, id.length()-1) : id;
	}



	@Override
	public String toString() {
		return getId();
	}

	

	/**
	 * 'Broadcasts' the message to all of the members of the conversation.
	 * Encrypting the message for each one of them.
	 * @param message
	 */
	public void sendMessage(String message) {
		EncrypterIF encr = EncrypterBuilder.getInstance().getDefaultEncrypter();
		User sender = UserManager.getInstance().getLocalUser();
		
		this.appendMessage(new Message(System.currentTimeMillis(), sender.getId(), message));
		
		for(String participant : participants) {
			String publicKey = UserDAO.getPublicKey(participant);			
			encr.setEncryptionKey(new String[] {publicKey.split("\\s+")[0].trim(),  publicKey.split("\\s+")[1].trim()});
			String encryptedMessage =  encr.encrypt(message);
			MessageDAO.messageUser(participant, sender, encryptedMessage);
		}
	}

	

	
	
	
	
	
	



}
