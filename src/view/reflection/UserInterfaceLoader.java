package view.reflection;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import view.interfaces.UserInterface;


public class UserInterfaceLoader {


	/**
	 * Loads a UserInterface object from a jar. 
	 * IMPORTANT: Make sure there's only one class in the jar that implements UserInterface!
	 * 
	 * @param jarpath
	 * @return
	 */
	public static UserInterface load(String jarpath) {


		try {

			//get the jar file's entries
			JarFile jarFile = new JarFile(jarpath);
			Enumeration<JarEntry> jarEntries = jarFile.entries();


			//create the classloader
			URL[] urls = { new URL("jar:file:"+jarpath+"!/") };
			URLClassLoader classLoader = URLClassLoader.newInstance(urls);

			Class c = null;
			

			while (jarEntries.hasMoreElements()) {
				JarEntry jarEntry = jarEntries.nextElement();
				if(jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")){
					continue;
				}

				// -6 because of .class
				String className = jarEntry.getName().substring(0,jarEntry.getName().length()-6);
				className = className.replace('/', '.');
				c=  classLoader.loadClass(className);
				
				
				try {
					return (UserInterface)c.newInstance();
				}catch(ClassCastException e) {
					//riprova: sarai fortunato!
				}
			}


			
			
		}catch(Exception e) {
		}
		
		return null;


	}


	
	

}
