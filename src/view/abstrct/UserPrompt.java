package view.abstrct;

import java.util.ArrayList;

import controller.SessionServices;

public class UserPrompt {
	
	public ArrayList<String> prompts;
	public SessionServices serviceCode;
	
	public UserPrompt(SessionServices serviceCode) {
		this.prompts = new ArrayList<String>();
		this.serviceCode = serviceCode;
	}
	
	
	/**
	 * Prompts follow an order
	 * @param text
	 */
	public void addPrompt(String text) {
		prompts.add(text);
	}
	
	
	
	
	
	/**
	 * Gets the next prompt in the queue and removes it
	 * @return
	 */
	public String nextPrompt() {
		
		if(prompts.size()<=0) {
			return null;
		}
		
		String buffer = prompts.get(0);
		prompts.remove(0);
		return buffer;
	}
	
	
	
	
	
	

}
