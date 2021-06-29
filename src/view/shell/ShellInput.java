package view.shell;

import java.util.Scanner;

import view.abstrct.AbstractUI;
import view.abstrct.InputUI;

public class ShellInput implements InputUI {

	AbstractUI abstractUI;
	
	Scanner scanner;
	
	public ShellInput(AbstractUI abstractUI){
		this.scanner =new Scanner(System.in);
		addListener(abstractUI);
	}
	
	
	@Override
	public void showPrompt(String text) {
		System.out.println(text);
		String response = scanner.nextLine();
		abstractUI.onResponse(response);
	}

	@Override
	public void addListener(AbstractUI abstractUI) {
		this.abstractUI = abstractUI;
	}
	
	
	
	
	
	
	
	
	
	
	
	

	
	
}
