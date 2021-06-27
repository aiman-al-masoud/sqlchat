package controller;

import java.util.ArrayList;

public abstract class AbstractUI implements SessionListener {

	
	/**
	 * The controller calls the methods of Shell, and/or gets called back by Shell. 
	 */
	protected Session session;
	
	
	public AbstractUI(Session session) {
		this.session = session;
		session.addListener(this);
	}
	
	
	
	@Override
	public void mainLoop() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void displayPrompt(UserPrompt userPrompt) {
		
		//to store the user's responses
		ArrayList<String> userResponses = new ArrayList<String> ();
		
		//for each prompt, display it and wait for the user to enter the message
		for(String promptMessage : userPrompt.prompts) {
			String response = waitForUserResponse(promptMessage); 
			userResponses.add(response);
		}
		
		//convert the list to an array
		String[] userInput = new String[userResponses.size()];
		userResponses.toArray(userInput); 
		
		//deliver the user's input to the "session" controller asynch.sly
		session.callback(userInput, userPrompt.serviceCode);
	}
	
	
	
	
	
	
	
	

	
	

	
	
	


	
	

}
