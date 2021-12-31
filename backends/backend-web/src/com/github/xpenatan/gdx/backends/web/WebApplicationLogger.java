package com.github.xpenatan.gdx.backends.web;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationLogger;

/**
 * @author xpenatan
 */
public class WebApplicationLogger implements ApplicationLogger {

	private int logLevel = Application.LOG_INFO;

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	public int getLogLevel() {
		return logLevel;
	}

	@Override
	public void log (String tag, String message) {
		if (logLevel >= Application.LOG_INFO) {
			System.out.println(tag + ": " + message);
		}
	}

	@Override
	public void log (String tag, String message, Throwable exception) {
		if (logLevel >= Application.LOG_INFO) {
			System.out.println(tag + ": " + message);
			exception.printStackTrace();
		}
	}

	@Override
	public void error (String tag, String message) {
		if (logLevel >= Application.LOG_ERROR) {
			System.err.println(tag + ": " + message);
		}
	}

	@Override
	public void error (String tag, String message, Throwable exception) {
		if (logLevel >= Application.LOG_ERROR) {
			System.err.println(tag + ": " + message);
			exception.printStackTrace();
		}
	}

	@Override
	public void debug (String tag, String message) {
		if (logLevel >= Application.LOG_DEBUG) {
			System.err.println(tag + ": " + message);
		}
	}

	@Override
	public void debug (String tag, String message, Throwable exception) {
		if (logLevel >= Application.LOG_DEBUG) {
			System.err.println(tag + ": " + message);
			exception.printStackTrace();
		}
	}
}
