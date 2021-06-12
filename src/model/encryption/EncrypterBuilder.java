package model.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import rsa.RSA;


/**
 * EncrypterBuilder handles the creation and setup
 * of an encrypter for other parts of the project.
 *
 */

public class EncrypterBuilder {


	private static EncrypterBuilder instance;

	private static File defaultEncrypterFile = new File("res/encrypters/encrypter.ser");

	private EncrypterBuilder() {

	}

	public static EncrypterBuilder getInstance() {
		if(instance ==null) {
			instance = new EncrypterBuilder();
		}
		return instance;
	}


	/**
	 * Get the old encypter if there was any.
	 * @return
	 */
	public EncrypterIF getDefaultEncrypter() {
		
		//if no encrypter was found on disk, make and save a new one 
		if(!defaultEncrypterFile.exists())
			return getNewEncrypter();

		//else load the old encrypter
		return load(defaultEncrypterFile.getPath());
	}


	/**
	 * Make a new encrypter and store it.
	 * @return
	 */
	public EncrypterIF getNewEncrypter() {
		
		//create the dir if it doesn't exist
		if(!new File("res/encrypters").exists()) {
			new File("res/encrypters").mkdir();
		}
		
		
		EncrypterRSA rsaEncr =  new EncrypterRSA(new RSA(300));
		
		rsaEncr.save(defaultEncrypterFile.getPath());
		
		return rsaEncr;
	}

	
	public static EncrypterIF load(String pathname) {
		try {
			ObjectInputStream objInputStream = new ObjectInputStream(new FileInputStream(new File(pathname)));
			return (EncrypterIF)objInputStream.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}



}
