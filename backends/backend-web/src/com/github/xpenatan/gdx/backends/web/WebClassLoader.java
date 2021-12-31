package com.github.xpenatan.gdx.backends.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author xpenatan
 */
public class WebClassLoader extends URLClassLoader {

	private URL[] classPaths;

	public WebClassLoader(URL[] classPaths, ClassLoader parent) {
		super(classPaths, parent);
		this.classPaths = classPaths;
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		for (int i = 0; i < classPaths.length; i++) {
			URL url = classPaths[i];
			String path = url.getPath();

			String newPath = path + name;
			File file = new File(newPath);

			if (file.exists()) {
				URI uri = file.toURI();
				try {
					return uri.toURL().openStream();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return super.getResourceAsStream(name);
	}
}
