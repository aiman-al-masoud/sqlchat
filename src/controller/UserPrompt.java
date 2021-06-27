package controller;

import java.util.ArrayList;

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
	
	
	
	
	
	
	
	

}
