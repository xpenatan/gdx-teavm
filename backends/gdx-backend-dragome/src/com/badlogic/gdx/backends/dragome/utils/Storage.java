
package com.badlogic.gdx.backends.dragome.utils;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsCast;

/** https://www.w3.org/TR/webstorage/#storage-0
 * @author xpenatan */
public interface Storage {
	static final boolean localStorageSupported = checkStorageSupport("localStorage");
	static final boolean sessionStorageSupported = checkStorageSupport("sessionStorage");

	public static Storage getLocalStorageIfSupported () {
		Storage storage = null;
		if (localStorageSupported || sessionStorageSupported) {
			Object instance = ScriptHelper.eval("window.localStorage", null);
			storage = JsCast.castTo(instance, Storage.class);
		}
		return storage;
	}

	public static boolean checkStorageSupport (String type) {
		ScriptHelper.evalBoolean("var flag=false;try{window[type].setItem('HelloWorld','HelloWorld');window[type].removeItem('HelloWorld');flag=true;}catch(e){flag=false;}", null);
		return ScriptHelper.evalBoolean("flag", null);
	}

	int getLength ();
	String key (int index);
	String getItem (String key);
	void setItem (String key, String value);
	void removeItem (String key);
	void clear ();
}
