package main;


import controller.Command;
import controller.Session;
import view.shell.Shell;

public class Main {

	public static void main(String[] args) {
		
		//make a new session (controller) object
		Session session = new Session();
		
		//make a new shell (view) object
		Shell shell = new Shell(session);
		
		//NB: the name of the program is NOT counted as an argument
		//concatenate the arguments
		String startCommand = "";
		for(String arg : args) {
			startCommand+=arg+" ";
		}
		
		//in case the command is not empty, execute it and then terminate the program 
		if(!startCommand.trim().isEmpty()) {
			
			Command command = Shell.parseCommand(startCommand);
			session.runCommand(command);
			System.exit(0);
		}
		
		//else start the session
		session.startSession();
		
		
		
	}
	
	
	
	
	
	
	

}
