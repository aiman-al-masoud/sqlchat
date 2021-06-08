package model.encryption;

import java.io.Serializable;
import java.util.ArrayList;

public interface EncrypterIF extends Serializable{

	
	public void setEncryptionKey(String[] encryptionKey);
	public void setDecryptionKey(String[] decryptionKey);
	public String encrypt(String plaintext);
	public String decipher(String ciphertext);
	public String[] getPublicKey();
	public void save(String pathname);
	
	
	
}
