
package com.github.xpenatan.gdx.backend.web.utils;

/**
 * https://www.w3.org/TR/webstorage/#storage-0
 * @author xpenatan
 */
public abstract class Storage {
	public final boolean localStorageSupported = checkStorageSupport("localStorage");
	public final boolean sessionStorageSupported = checkStorageSupport("sessionStorage");


	private static Storage storage;

	public static Storage getInstance() {
		return Storage.storage;
	}


	public static void setInstance(Storage storage) {
		Storage.storage = storage;
	}

	public abstract Storage getLocalStorageIfSupported ();
	public abstract boolean checkStorageSupport (String type);
	public abstract int getLength ();
	public abstract String key (int index);
	public abstract void setItem (String key, String value);
	public abstract String getItem (String key);
	public abstract void removeItem (String key);
	public abstract void clear ();
}