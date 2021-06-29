package model.user;

import java.io.File;
import java.io.IOException;

import io.FileIO;

/**
 * This is a singleton class for the purpose of managing user persistence. 
 * @author aiman
 *
 */

public class LocalUser {

	private User localUser;
	private static LocalUser instance;
	private static File localUserFile = new File("res"+File.separator+"settings"+File.separator+"localUser");

	public static LocalUser getInstance() {

		if(instance==null) {
			instance = new LocalUser();
		}

		return instance;
	}

	
	public User getLocalUser() {
		return localUser;
	}



	private LocalUser() {
		loadLocalUser();
	}


	/**
	 * Load the localUser object
	 */

	private void loadLocalUser(){
		
		//create localUserFile if it doesn't exist
		if(!localUserFile.exists()) {
			try {
				localUserFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//get the id saved in the localUser file
		String localUserId = FileIO.read(localUserFile.getPath());

		//if the file's empty, localUser = null
		if(localUserId.trim().equals("")) {
			localUser = null;
		}else {
			//else initialize a User object
			localUser = new User(localUserId.trim());
		}
		
	}


	/**
	 * Change localUser object and overwrite file content.
	 * @param user
	 */
	public void saveLocalUser(User user){
		localUser = user;
		
		try {
			FileIO.write(localUserFile.getPath(), localUser.getId());
		}catch(NullPointerException e) {
			FileIO.write(localUserFile.getPath(), "");
		}
	}

	
	public void deleteLocalUser() {
		saveLocalUser(null);
	}
	
	


}
