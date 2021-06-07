package main;

import daos.UserDAO;
import model.users.User;
import model.users.UserManager;
import rsa.RSA;

public class Main {

	public static void main(String[] args) {
	
		
		User capra = new User("capra");
		//capra.createUser("capraPass");
		
		User banana = new User("banana");
		//banana.createUser("bananaPass");
		
		
		
		/*
		RSA rsa = new RSA(100);

		
		String encr = rsa.encryptText("ciao", rsa.getPublicKey()[0].toString(), rsa.getPublicKey()[1].toString());
		System.out.println(encr);
		*/
		
	}

}
