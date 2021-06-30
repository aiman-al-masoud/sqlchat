package controller;

public class Command {

	public SessionServices serviceCode;
	public String[] args;
	
	public Command(SessionServices serviceCode, String[] args) {
		this.serviceCode = serviceCode;
		this.args = args;
	}
	
	
	public String toString() {
		
		String concat = "";
		for(String arg : args) {
			concat+=arg+" ";
		}
		
		return "["+serviceCode.toString()+", "+concat+"]";
	}
	
	
}
