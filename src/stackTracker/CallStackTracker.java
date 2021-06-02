package stackTracker;

import java.util.ArrayList;
import java.util.Map.Entry;

public class CallStackTracker {

	
	/**
	 * Returns the list of methods this method is getting called from.
	 * @return
	 */
	
	public static ArrayList<String> getCallStack(){
		ArrayList<String> results = new ArrayList<String>();
		
		for(Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
			
			for(StackTraceElement elem: entry.getValue()) {
				results.add(elem.toString());
			}
		}
		return results;
	}
	
	
	/**
	 * checks if a given method is contained in the call stack.
	 * @param methodName
	 * @return
	 */
	public static boolean callStackContains(String methodOrClassName) {
		for(String nameOfMethod : getCallStack()) {
			if(nameOfMethod.contains(methodOrClassName)) {
				return true;
			}
		}
		return false;
	}
	
	
	
	public static void main(String[] args) {
		
		ArrayList<String> methods = getCallStack();
		for(String method : methods) {
			System.out.println(method);
		}
		
		System.out.println(callStackContains("notify"));
		
	}
	
	
	
	
	
}
