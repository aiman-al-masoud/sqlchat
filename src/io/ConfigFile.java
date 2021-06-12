package io;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigFile extends File {

	public ConfigFile(String pathname) {
		super(pathname);
		if(!exists()) {
			create();
		}
		
	}
	
	
	public void create() {
		try {
			this.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * inserts a key-value pair in the module. 
	 * If the added key was already in the Module,
	 * its value gets OVERWRITTEN!
	 * @param key
	 * @param value
	 */
	public void put(String key, String value) {

		//get the old value of the key
		String oldValue = get(key);
		
		//get this file's text
		String text = FileIO.read(this.getPath());
		
		//if it's the first time you're putting this key in, add a new key-value line
		if(oldValue==null) {
			text+=key+" : "+value+"\n";
		}else {
			//else replace oldValue with new value
			text = text.replace(key+" : "+oldValue, key+" : "+value);
		}
		
		//push changes
		FileIO.write(this.getPath(), text);
		
	}
	
	
	
	/**
	 * Returns the value associated to a key, null if not found.
	 * In case the value is a referenced file, it returns 
	 * the contents of that referenced file.
	 * @param key
	 * @return
	 */
	public String get(String key) {
		//get this file's text
		String text = FileIO.read(this.getPath());
		String value = null;
		try {
			//try matching the pattern: key : value\n 
			Pattern pattern = Pattern.compile(key+" : (.*?)\n");
			
			Matcher matcher = pattern.matcher(text);
			matcher.find();
			
			//get the value
			value = matcher.group(1);
			
		}catch(Exception e) {/*do nothing*/}
		
		return value;
	}
	
	
	/**
	 * Removes a key and its associated value.
	 * @param key
	 */
	public void remove(String key) {
		String value = get(key);
		if(value==null) {
			return; //no key to remove
		}
		String newText = FileIO.read(this.getPath()).replace(key+" : "+value+"\n", "");
		FileIO.write(this.getPath(), newText);
		
	}

	
	

}
