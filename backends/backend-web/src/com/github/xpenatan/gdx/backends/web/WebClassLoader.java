package com.github.xpenatan.gdx.backends.web;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author xpenatan
 */
public class WebClassLoader extends URLClassLoader {

	private URL[] jarFiles;

	public WebClassLoader(URL[] classPaths, ClassLoader parent) {
		super(classPaths, parent);
		this.jarFiles = classPaths;
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		InputStream inputStream = null;
		for (int i = 0; i < jarFiles.length; i++) {
			URL url = jarFiles[i];
			String path = url.getPath();
			File file = new File(path);
			try {
				JarFile jarFile = new JarFile(file);
				ZipEntry entry = jarFile.getEntry(name);
				if(entry != null) {
					inputStream = jarFile.getInputStream(entry);
					break;
				}
			} catch(Exception e) {
			}
		}
		// Only accept TeaVM classes if it's not in jarFiles array
		if(name.startsWith("org/teavm/")) {
			inputStream = super.getResourceAsStream(name);
		}
		return inputStream;
	}

	/**
	 * Convert packages to individual classes
	 */
	public ArrayList<String> getPreserveClasses(ArrayList<String> preservedClasses) {
		// TeaVM only accept individual classes. We need to obtain all classes from package (if its set).
		// If it's not a package just add the class.
		ArrayList<String> array = new ArrayList<>();

		for(int i = 0; i < preservedClasses.size(); i++) {
			String className = preservedClasses.get(i);
			String packagePath = className.replace(".", "/");
			URL resource = getResource(packagePath + ".class");
			if(resource == null) {
				// Should be a package
				addClasses(array, packagePath);
			}
			else {
				array.add(className);
			}
		}
		return array;
	}

	private void addClasses(ArrayList<String> array, String packagePath) {
		for (int i = 0; i < jarFiles.length; i++) {
			URL url = jarFiles[i];
			String path = url.getPath();
			File file = new File(path);
			try {
				JarFile jarFile = new JarFile(file);
				Enumeration<JarEntry> entries = jarFile.entries();
				while(entries.hasMoreElements()) {
					JarEntry jarEntry = entries.nextElement();
					String name = jarEntry.getName();
					if(name.startsWith(packagePath) && !name.contains("$")) {
						if(name.endsWith(".class")) {
							String className = name.replace("\\", ".").replace("/", ".").replace(".class", "");
							array.add(className);
						}
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
