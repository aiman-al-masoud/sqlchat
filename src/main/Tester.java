package main;

import java.util.ArrayList;

import daos.MessageDAO;
import daos.UserDAO;
import model.conversations.Message;
import model.encryption.EncrypterBuilder;
import model.encryption.EncrypterRSA;
import model.users.User;
import rsa.RSA;

public class Tester {

	public static void main(String[] args) {
		
		//UserDAO.createUsersTable();
		
		//User capra = new User("capra");
		//capra.createUser("capraPass");
		
		User banana = new User("banana");
		banana.createUser("bananaPass");
		
		
	}

}
