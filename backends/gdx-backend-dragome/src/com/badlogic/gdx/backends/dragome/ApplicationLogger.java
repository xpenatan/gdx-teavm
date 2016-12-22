package com.badlogic.gdx.backends.dragome;

public abstract interface ApplicationLogger {
	public abstract void log(String paramString1, String paramString2);

	public abstract void log(String paramString1, String paramString2, Throwable paramThrowable);

	public abstract void error(String paramString1, String paramString2);

	public abstract void error(String paramString1, String paramString2, Throwable paramThrowable);

	public abstract void debug(String paramString1, String paramString2);

	public abstract void debug(String paramString1, String paramString2, Throwable paramThrowable);
}