package com.github.xpenatan.gdx.backends.web;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
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
		for (int i = 0; i < jarFiles.length; i++) {
			URL url = jarFiles[i];
			String path = url.getPath();
			File file = new File(path);
			try {
				JarFile jarFile = new JarFile(file);
				ZipEntry entry = jarFile.getEntry(name);
				if(entry != null) {
					InputStream inputStream = jarFile.getInputStream(entry);
					return inputStream;
				}
			} catch(Exception e) {
			}
		}
		return super.getResourceAsStream(name);
	}
}
