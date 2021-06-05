package main;

import daos.UserDAO;
import model.users.User;
import model.users.UserManager;
import rsa.RSA;

public class Main {

	public static void main(String[] args) {
	
		
		User capra = new User("capra");
	
		//capra.createUser("capraPass");
		
		User banana  = new User("banana");
		
		//banana.createUser("bananaPass");
		
		
		String publicKey = UserDAO.getPublicKey("banana");

		System.out.println(publicKey);
		
		
	}

}
