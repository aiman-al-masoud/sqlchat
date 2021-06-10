package main;


import model.controller.Session;
import view.Shell;

public class Main {

	public static void main(String[] args) {
		
		Session session = new Session();
		
		Shell shell = new Shell(session);
		
		session.startSession();
		
	}
	
	
	
	
	
	
	

}
