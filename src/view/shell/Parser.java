package view.shell;

import java.util.Arrays;

import controller.Command;
import controller.SessionServices;

/**
 * Parses a text-based command to a Command object that can be passed to the Session. 
 * @author aiman
 *
 */

public class Parser {



	public static Command parseCommand(String commandText) {


		String[] commandParts = commandText.split("\\s+");
		String commandName = commandParts[0].toUpperCase();

		SessionServices commandCode;
		try {
			commandCode = SessionServices.valueOf(commandName);
		}catch(IllegalArgumentException e) {
			commandCode = SessionServices.NOTACMD;
			String[] args = {commandParts[0]};
			return new Command(commandCode, args);
		}


		String[] args;
		if(commandParts.length == 1) {
			args = new String[0];
		}else {
			args = Arrays.copyOfRange(commandParts, 1, commandParts.length);
		}



		return new Command(commandCode, args);
	}



}
