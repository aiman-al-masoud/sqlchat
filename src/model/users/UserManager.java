package model.users;

import java.io.File;
import java.io.IOException;

import io.FileIO;

public class UserManager {

	private User localUser;
	private static UserManager instance;
	private static File localUserFile = new File("res"+File.separator+"settings"+File.separator+"localUser");

	public static UserManager getInstance() {

		if(instance==null) {
			instance = new UserManager();
		}

		return instance;
	}

	
	public User getLocalUser() {
		return localUser;
	}



	private UserManager() {
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
