package model.encryption;

import java.io.File;
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
		RSA rsa = RSA.load(defaultEncrypterFile.getPath());
		return new EncrypterRSA(rsa);
	}


	/**
	 * Make a new encrypter and store it.
	 * @return
	 */
	public EncrypterIF getNewEncrypter() {
		RSA rsa = new RSA(300);
		rsa.save(defaultEncrypterFile.getPath());
		return new EncrypterRSA(rsa);
	}




}
