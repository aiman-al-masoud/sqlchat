package main;


import model.controller.Session;
import view.Shell;

public class Main {

	public static void main(String[] args) {
		
		Session session = new Session();
		
		Shell shell = new Shell(session);
		
		//NB: the name of the program is NOT counted as an argument
		//concatenate the arguments
		String startCommand = "";
		for(String arg : args) {
			startCommand+=arg+" ";
		}
		
		
		if(!startCommand.trim().isEmpty()) {
			session.runCommand(startCommand);
			System.exit(0);
		}
		
		session.startSession();
		
		
		
		
		
	}
	
	
	
	
	
	
	

}
