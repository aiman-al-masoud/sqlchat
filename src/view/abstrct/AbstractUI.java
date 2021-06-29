package view.abstrct;

import java.util.ArrayList;

import controller.Session;
import controller.SessionListener;
import controller.UserPrompt;




/**
 * AbstractUI is to be implemented by any concrete UI (eg: shell or GUI).
 * 
 * AbstractUI stays on the lookout for orders from Session (ie: the controller).
 * 
 * AbstractUI could receive the order to display a prompt. In that case,
 * it will display the prompt's questions, store the user's responses,
 * and send them back to the Session when they're all ready.
 *
 */
public abstract class AbstractUI implements SessionListener {

	
	/**
	 * The controller calls the methods of Shell, and/or gets called back by Shell. 
	 */
	protected Session session;
	
	/**
	 * A unit of user response-strings.
	 */
	ArrayList<String> responses;
	
	/**
	 * The current user prompt
	 */
	UserPrompt currentPrompt;
	
	
	/**
	 * input UI component.
	 */
	InputUI inputUI;
	
	
	public AbstractUI(Session session) {
		this.session = session;
		session.addListener(this);
		responses = new ArrayList<String>();
	}
		
	
	public void setInputComponent(InputUI inputUI) {
		this.inputUI  = inputUI;
	}
	
	
	public void startPrompt(UserPrompt userPrompt) {
		currentPrompt = userPrompt;
		displayNextPrompt();
	}
	
	
	
	/**
	 * Tells the UI component to display the next prompt, if any.
	 */
	public void displayNextPrompt() {
		String promptText = currentPrompt.nextPrompt();
		
		//there are still more prompts to go
		if(promptText!=null) {
			inputUI.showPrompt(promptText);
			return;
		}
		
		//no more prompts
		deliverToSession();	
	}
	
	
	
	
	/**
	 * Called by the UI component responsible for holding user-inputted text
	 * @param response
	 */
	public void onResponse(String response) {
		
		//store the response
		responses.add(response);
		
		//display the next prompt
		displayNextPrompt();
		
	}
	
	
	
	/**
	 * Delivers all of the user responses to the Session once all of them were gathered.
	 */
	public void deliverToSession() {
				
		String[] respns = responses.toArray(new String[0]);
		
		//re-set state
		responses.clear();
		
		//deliver the responses to the session
		session.callback(respns , currentPrompt.serviceCode);
	}
	
	
	
	
	
	

	
	

	
	
	


	
	

}
