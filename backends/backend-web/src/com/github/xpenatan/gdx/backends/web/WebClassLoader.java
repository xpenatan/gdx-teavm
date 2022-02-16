package com.github.xpenatan.gdx.backends.web;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author xpenatan
 */
public class WebClassLoader extends URLClassLoader {

	private URL[] jarFiles;
	private HashMap<String, ArrayList<String>> fileMap = new HashMap<>();

	public WebClassLoader(URL[] classPaths, ClassLoader parent) {
		super(classPaths, parent);
		this.jarFiles = classPaths;
	}

	@Override
	public URL getResource(String name) {
		return getRes(name);
	}

	private URL getRes(String name) {
		URL resource = super.getResource(name);
		for (int i = 0; i < jarFiles.length; i++) {
			URL url = jarFiles[i];
			String path = url.getPath();
			String finalFile = "jar:file:" + path + "!/";
			File file = new File(path);
			if(!file.isDirectory()) {
				try {
					JarFile jarFile = new JarFile(file);
					Enumeration<JarEntry> entries = jarFile.entries();
					while(entries.hasMoreElements()) {
						JarEntry jarEntry = entries.nextElement();
						if(!jarEntry.isDirectory()) {
							String jarEntryName = jarEntry.getName();
							if(jarEntryName.equals(name)) {
								String filee = finalFile + jarEntryName;
								return new URL(filee);
							}
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					ArrayList<String> allClasses = getAllFiles(path);
					String resName = name.replace("\\","/");
					for(int j = 0; j < allClasses.size(); j++) {
						String className = allClasses.get(j);
						if(className.contains(resName)) {
							String filee = finalFile + className;
							return new URL(filee);
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		return resource;
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		for (int i = 0; i < jarFiles.length; i++) {
			URL url = jarFiles[i];
			String path = url.getPath();
			File file = new File(path);
			if(!file.isDirectory()) {
				try {
					JarFile jarFile = new JarFile(file);
					ZipEntry entry = jarFile.getEntry(name);
					if(entry != null) {
						return jarFile.getInputStream(entry);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			else {
				ArrayList<String> allClasses = getAllFiles(path);
				String resName = name.replace("\\","/");
				for(int j = 0; j < allClasses.size(); j++) {
					String className = allClasses.get(j);
					if(className.contains(resName)) {
						return super.getResourceAsStream(name);
					}
				}
			}
		}

		if(name.startsWith("org/teavm/")) {
			return super.getResourceAsStream(name);
		}

		return null;
	}

	private ArrayList<String> getAllFiles(String path) {
		ArrayList<String> paths = fileMap.get(path);
		if(paths == null) {
			File rootFile = new File(path);
			paths = new ArrayList<>();
			getAllFiles(rootFile, paths);
			fileMap.put(path, paths);
		}
		return paths;
	}

	private void getAllFiles(File rootFile, ArrayList<String> out) {
		String[] list = rootFile.list();
		if(list != null) {
			for(int i = 0; i < list.length; i++) {
				String subFileStr = list[i];
				File subFile = new File(rootFile, subFileStr);
				if(subFile.isDirectory()) {
					getAllFiles(subFile, out);
				}
				else {
					String path = subFile.getPath();
					path = path.replace("\\","/");
					out.add(path);
				}
			}
		}
	}

	/**
	 * Convert packages to individual classes
	 */
	public ArrayList<String> getPreserveClasses(ArrayList<String> classesToPreserve) {
		// TeaVM only accept individual classes. We need to obtain all classes from package (if its set).
		// If it's not a package just add the class.
		ArrayList<String> array = new ArrayList<>();

		for(int i = 0; i < classesToPreserve.size(); i++) {
			String className = classesToPreserve.get(i);
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
			if(!file.isDirectory()) {
				try {
					JarFile jarFile = new JarFile(file);
					Enumeration<JarEntry> entries = jarFile.entries();
					while(entries.hasMoreElements()) {
						JarEntry jarEntry = entries.nextElement();
						String name = jarEntry.getName();
						if(name.startsWith(packagePath)) {
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
			else {
				ArrayList<String> allClasses = getAllFiles(path);
				for(int j = 0; j < allClasses.size(); j++) {
					String name = allClasses.get(j);
					if(name.startsWith(packagePath)) {
						if(name.endsWith(".class")) {
							String className = name.replace("\\", ".").replace("/", ".").replace(".class", "");
							array.add(className);
						}
					}
				}
			}
		}
	}
}
