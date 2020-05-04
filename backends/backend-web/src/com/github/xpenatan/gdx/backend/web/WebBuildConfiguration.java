package com.github.xpenatan.gdx.backend.web;

import java.io.File;
import java.net.URL;
import com.badlogic.gdx.utils.Array;

/**
 * @author xpenatan
 */
public abstract class WebBuildConfiguration {
	
	private static final StringBuilder sb = new StringBuilder();
	
	
	public static void log(String msg) {
		sb.append("| " + msg + "\n");
	}
	
	public static void flush() {
		System.err.println(sb);
		sb.setLength(0);
	}
	
	public static void logHeader(String text) {
		String msg = "";
		msg +="\n" + "#################################################################\n";
		msg += "|\n| " + text + "\n|";
		msg += "\n" + "#################################################################\n";

		sb.append(msg + "\n");
	}
	
	public static void logEnd() {
		String msg = "\n#################################################################";
		sb.append(msg + "\n");
	}

	public boolean acceptClasspath(URL url) {
		return true;
	}

	public abstract Class getMainClass();

	public abstract String getWebAppPath();

	public abstract void assetsClasspath (Array<String> classPaths);

	/**
	 * 
	 * @param paths
	 * @return true to generate a file which contains all assets patch
	 */
	public abstract boolean assetsPath (Array<File> paths);

	public abstract boolean minifying ();
}
