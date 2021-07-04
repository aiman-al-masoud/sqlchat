package model.encryption;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class Encryption {


	/**
	 * Instance of this Singleton.
	 */
	private static Encryption instance;
	
	/**
	 * Path to the folder where the keys are stored.
	 */
	private static String KEYS_PATH = "res"+File.separator+"encryption"+File.separator+"keys";
	
	/**
	 * Path to my current public key.
	 */
	private static String PUBLIC_KEY_PATH = KEYS_PATH+File.separator+"public";
	
	/**
	 * Path to my current private key.
	 */
	private static String PRIVATE_KEY_PATH = KEYS_PATH+File.separator+"private";
	
	/**
	 * My current private key.
	 */
	private static Key myCurrentPrivateKey;
	
	/**
	 * My current public key.
	 */
	private static Key myCurrentPublicKey;


	

	/**
	 * Make sure that the local keys are present.
	 */
	private Encryption(){
		
		//try loading my current key-pair, if any:
		myCurrentPublicKey = loadKey(PUBLIC_KEY_PATH, KeyTypes.PUBLIC);
		myCurrentPrivateKey = loadKey(PRIVATE_KEY_PATH, KeyTypes.PRIVATE);

		
		//create the keys folder if it didn't exist.
		File keysFolder = new File(KEYS_PATH);
		if(!keysFolder.exists()) {
			keysFolder.mkdirs();
		}
		
		//if the stored keys are null, generate and store a new key-pair
		if(myCurrentPublicKey==null|myCurrentPrivateKey==null) {
			renewKeyPair();
		}

	}



	/**
	 * Get the instance of this Singleton.
	 * @return
	 */
	public static Encryption getInstance() {

		if(instance==null) {
			instance = new Encryption();
		}

		return instance;
	}





	/**
	 * Cipher a plaintext with a given Key.
	 * @param key
	 * @param plaintext
	 * @return
	 * @throws Exception
	 */
	public String cipher(Key key, String plaintext) {

		String encrString = null;
		
		try {
			//get a cipher in the "encrypt" mode
			Cipher encryptCipher = Cipher.getInstance("RSA");
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);

			//get the plaintext's bytes
			byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);


			//encrypt the bytes
			byte[] encrBytes = encryptCipher.doFinal(plaintextBytes);

			//bytes to base 64
			encrString = Base64.getEncoder().encodeToString(encrBytes);

		}catch(Exception e) {
			
		}
	
		//return the encrypted string
		return encrString;

	}




	/**
	 * Attempt to decipher a string with a given key.
	 * @param key
	 * @param cipherstring
	 * @return
	 * @throws Exception
	 */
	public String decipher(Key key, String cipherstring) {

		try {
			//get an instance of a "decipherer"
			Cipher decryptCipher = Cipher.getInstance("RSA");
			decryptCipher.init(Cipher.DECRYPT_MODE, key);

			//decode the bytes from base 64
			byte[] encrMessageBytes = Base64.getDecoder().decode(cipherstring.getBytes());

			//decipher the bytes
			byte[] decryptedBytes =  decryptCipher.doFinal(encrMessageBytes);

			//interpret the deciphered bytes as UNICODE
			String decryptedMessage = new String(decryptedBytes, StandardCharsets.UTF_8);

			//return the deciphered string
			return decryptedMessage;
		}catch(Exception e) {

		}
		
		return null;
	}




	/**
	 * Decipher a cipherstring using my own private key.
	 * @param cipherstring
	 * @return
	 */
	public String decipher(String cipherstring) {
		return decipher(myCurrentPrivateKey, cipherstring);
	}




	/**
	 * Generate a new key pair.
	 * @return
	 */
	public KeyPair generateKeyPair() {
		//make an instance of the key-generator
		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance("RSA");
			//initialize the generator with a given number of bits per key
			generator.initialize(2048);
			//get a new key-pair
			KeyPair keyPair = generator.generateKeyPair();

			return keyPair;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}


	
	/**
	 * Get a new random key-pair and store it.
	 */
	public void renewKeyPair() {

		KeyPair keyPair = generateKeyPair();
		myCurrentPublicKey = keyPair.getPublic();
		myCurrentPrivateKey = keyPair.getPrivate();
		this.saveKey(myCurrentPublicKey, PUBLIC_KEY_PATH);
		this.saveKey(myCurrentPrivateKey, PRIVATE_KEY_PATH);
	}



	/**
	 * Save a key locally.
	 * @param key
	 * @param pathname
	 * @throws IOException
	 */
	public void saveKey(Key key, String pathname) {

		//new fileoutputstream and write the bytes of the key to a file
		try (FileOutputStream fos = new FileOutputStream(pathname)) {
			fos.write(key.getEncoded());
		}catch(IOException e) {
			e.printStackTrace();
		}
	}



	/**
	 * Used in loadKey to specify: public or private
	 *
	 */
	public enum KeyTypes{
		PUBLIC, PRIVATE;
	}


	/**
	 * Load a key stored on disk.
	 * @param pathname
	 * @param keyType
	 * @return
	 * @throws Exception
	 */
	public Key loadKey(String pathname, KeyTypes keyType) {

		try {
			//get the stored key's bytes 
			File keyFile = new File(pathname);
			byte[] keysBytes = Files.readAllBytes(keyFile.toPath());


			//re-create the key using its bytes from the file
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			EncodedKeySpec keySpecs;


			if(keyType==KeyTypes.PUBLIC) {
				keySpecs = new X509EncodedKeySpec(keysBytes);
				return keyFactory.generatePublic(keySpecs);
			}

			if(keyType==KeyTypes.PRIVATE) {
				keySpecs = new PKCS8EncodedKeySpec(keysBytes);
				return keyFactory.generatePrivate(keySpecs);
			}	

		}catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
			//this can't happen
		} catch (IOException e) {
			e.printStackTrace();
		} 

		return null;
	}


	/**
	 * Build a public key object given the key's modulus and public exponent.
	 * @param modulus
	 * @param publicExponent
	 * @return
	 * @throws Exception
	 */
	public PublicKey buildPublicKey(String publicExponent, String modulus) {
		
		try {
			
			RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(publicExponent));
			KeyFactory factory = KeyFactory.getInstance("RSA");
			return factory.generatePublic(spec);
		}catch(Exception e) {
			
		}
		
		return null;
	}
	
	
	public PublicKey getCurrentPublicKey() {
		return (PublicKey)myCurrentPublicKey;
	}
	
	
	public PrivateKey getCurrentPrivateKey() {
		return (PrivateKey)myCurrentPrivateKey;
	}
	
	
	
	/**
	 * Get the public key in the form:
	 * publicExponent modulus
	 * @return
	 */
	public String getPublicKeyString() {
		RSAPublicKey key = (RSAPublicKey)myCurrentPublicKey;
		return key.getPublicExponent()+" "+key.getModulus();
	}
	
	
	


	/**
	 * Hash a string.
	 * @param plaintext
	 * @return
	 */
	public static String hash(String plaintext) {

		MessageDigest digest =null;
		try {
			//uses sha256
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {}

		//hash the text into an array of bytes
		byte[] hashArray = digest.digest(plaintext.getBytes());
		String binaryString = "";

		//convert the hash into binarystring form
		for(byte b : hashArray) {
			binaryString+=Integer.toBinaryString(b);
		}

		//represent the hash as a hexadecimal number
		return new BigInteger(binaryString,2).toString(16);
	}



}
