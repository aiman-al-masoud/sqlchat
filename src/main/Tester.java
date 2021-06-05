package main;

import java.util.ArrayList;

import daos.MessageDAO;
import daos.UserDAO;
import model.conversations.Message;
import model.encryption.EncrypterRSA;
import model.users.User;
import rsa.RSA;

public class Tester {

	public static void main(String[] args) {
		
		RSA rsa = new RSA(300);
		
		EncrypterRSA enc = new EncrypterRSA(rsa);
		
		String[] key = new String[2];
		
		key[0] = enc.rsa.getPublicKey()[0].toString();
		key[1] = enc.rsa.getPublicKey()[1].toString();
		
		enc.setEncryptionKey(key);
		
		
		String encrypted = enc.encrypt("ciao");
		
		
		String deciphered = enc.decipher(encrypted);
		
		
		System.out.println(encrypted);
		System.out.println(deciphered);
		
		
	}

}
