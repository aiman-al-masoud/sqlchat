package view.abstrct;

public interface InputUI {
		
		/**
		 * Displays a prompt and calls AbstractUI back (onResponse) once the response is ready.		 * @param text
		 */
		public void showPrompt(String text);
		
		/**
		 * Adds an AbstractUI as a listener
		 */
		public void addListener(AbstractUI abstractUI);
		
	}