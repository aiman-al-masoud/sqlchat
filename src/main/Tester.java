package main;

import java.util.ArrayList;

import messages.Message;
import messages.MessageDAO;
import users.User;
import users.UserDAO;

public class Tester {

	public static void main(String[] args) {

		//UserDAO.saveUser(new User("capra", "covfefe"));


		/*
		ArrayList<User> users = UserDAO.selectAll();

		for(User user : users) {
			System.out.println(user);
		}


		System.out.println("recipient "+users.get(1));
		UserDAO.messageUser(users.get(1), users.get(0), "you suck");
		 */

		//MessageDAO.messageUser(new User("capra", "password"), new User("luna","napoli"),  "Scei una capla");

		/*
		//pull messages for a user
		ArrayList<Message> messages = MessageDAO.pullMessages(new User("capra"));

		for(Message message : messages) {
			System.out.println(message);
		}

		System.out.println("--------------");

		//list all of the users
		for(User user : UserDAO.selectAll()) {
			System.out.println(user);
		}
		 */

		//trying to call methods in MessageDAO from classes other than User
		//MessageDAO.messageUser(capra, banana, "hhhhhalaaamujaddara");



		//create a new user 
		User trump = new User("trump"); 
		trump.createUser("trumpPass");
		
		/*
		User capra = new User("capra");
		User banana = new User("banana");


		banana.logIn("capraPass");

		for(int i =0; i<10; i++) {
			banana.sendMessage("capra", "sei stupido! "+i);
		}

		*/


		//capra.logIn("capraPass");
		//capra.sendMessage("banana", "ciao!");

		//banana.logIn("capraPass");
		//for(Message message : banana.pullMessages()) {
		//System.out.println(message);
		//}




	}

}
