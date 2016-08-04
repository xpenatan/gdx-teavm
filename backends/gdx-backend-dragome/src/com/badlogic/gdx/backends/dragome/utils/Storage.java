
package com.badlogic.gdx.backends.dragome.utils;

import com.dragome.commons.javascript.ScriptHelper;

/** https://www.w3.org/TR/webstorage/#storage-0
 * @author xpenatan */
public class Storage {
	static final boolean localStorageSupported = checkStorageSupport("localStorage");
	static final boolean sessionStorageSupported = checkStorageSupport("sessionStorage");

	public static Storage getLocalStorageIfSupported () {
		Storage storage = null;
		if (localStorageSupported || sessionStorageSupported) {
			Object node = ScriptHelper.eval("window.localStorage", null);
			storage = new Storage();
			ScriptHelper.put("storage", storage, null);
			ScriptHelper.put("storage.node", node, null);
		}
		return storage;
	}

	public static boolean checkStorageSupport (String type) {
		ScriptHelper.put("type", type, null);
		ScriptHelper.evalBoolean("var flag=false;try{window[type].setItem('HelloWorld','HelloWorld');window[type].removeItem('HelloWorld');flag=true;}catch(e){flag=false;}", null);
		return ScriptHelper.evalBoolean("flag", null);
	}

	public int getLength () {
		return ScriptHelper.evalInt("this.node.length", this);
	}

	public String key (int index) {
		ScriptHelper.put("$1", index, this);
		return (String)ScriptHelper.eval("this.node.key($1)", this);
	}

	public void setItem (String key, String value) {
		ScriptHelper.put("$1", key, this);
		ScriptHelper.put("$2", value, this);
		ScriptHelper.evalNoResult("this.node.setItem($1, $2)", this);
	}

	public String getItem (String key) {
		ScriptHelper.put("$1", key, this);
		return (String)ScriptHelper.eval("this.node.getItem($1)", this);
	}

	public void removeItem (String key) {
		ScriptHelper.put("$1", key, this);
		ScriptHelper.evalNoResult("this.node.removeItem($1)", this);
	}

	public void clear () {
		ScriptHelper.evalNoResult("this.node.clear()", this);
	}

}