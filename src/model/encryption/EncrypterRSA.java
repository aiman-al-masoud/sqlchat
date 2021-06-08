package model.encryption;




import rsa.RSA;

public class EncrypterRSA  extends AbstractEncrypter{

	public RSA rsa;
	int numberOfBaseTenDigits = 300;

	//e, n
	String[] encryptionKey;

	public EncrypterRSA(RSA rsa) {
		this.rsa = rsa;
	}


	@Override
	public String encrypt(String plaintext) {
		//plaintext, public exponent, product of two primes
		return rsa.encryptText(plaintext, encryptionKey[0].trim(), encryptionKey[1].trim());
	}

	@Override
	public String decipher(String ciphertext) {
		return rsa.decryptText(ciphertext);
	}

	@Override
	public void setEncryptionKey(String[] encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	@Override
	public void setDecryptionKey(String[] decryptionKey) {
		//no need to implement this here
	}


	@Override
	public String[] getPublicKey() {
		String [] publicKey = new String[2];
		publicKey[0] = rsa.getPublicKey()[0].toString();
		publicKey[1] = rsa.getPublicKey()[1].toString();
		return publicKey;
	}











}
