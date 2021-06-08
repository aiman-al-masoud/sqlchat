package model.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import rsa.RSA;

public abstract class AbstractEncrypter implements EncrypterIF{



	public void save(String pathname){
		try {
			ObjectOutputStream objOutStream = new ObjectOutputStream(new FileOutputStream(pathname));
			objOutStream.writeObject(this);
			objOutStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	



}
