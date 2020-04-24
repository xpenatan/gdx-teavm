package com.github.xpenatan.gdx.backend.web;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author xpenatan
 */
public abstract class WebBuildConfiguration {

	public boolean acceptClasspath(URL url) {
		return true;
	}

	public abstract Class getMainClass();

	public abstract String getWebAppPath();

	public abstract void assetsClasspath (ArrayList<String> classPaths);

	public abstract void assetsPath (ArrayList<File> paths);

	public abstract boolean minifying ();
}
