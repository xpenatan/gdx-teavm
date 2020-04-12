package com.github.xpenatan.gdx.backends.teavm;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public abstract class TeaConfiguration {

	public boolean acceptClasspath(URL url) {
		return true;
	}

	public abstract String getMain();

	public abstract String getTargetDirectory();

	public abstract void assetsClasspath (ArrayList<String> classPaths);

	public abstract void assetsPath (ArrayList<File> paths);

	public abstract boolean minifying ();

}
