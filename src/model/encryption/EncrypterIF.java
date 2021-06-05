package model.encryption;

import java.util.ArrayList;

public interface EncrypterIF {

	
	public void setEncryptionKey(String[] encryptionKey);
	public void setDecryptionKey(String[] decryptionKey);
	public String encrypt(String plaintext);
	public String decipher(String ciphertext);
	public String[] getPublicKey();
	
	
	
}
