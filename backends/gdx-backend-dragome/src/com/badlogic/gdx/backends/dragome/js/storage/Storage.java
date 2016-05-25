/*******************************************************************************
 * Copyright 2016 Natan Guilherme.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.backends.dragome.js.storage;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.w3c.ArrayBufferFactory;

/** https://www.w3.org/TR/webstorage/#storage-0
 * @author xpenatan */
public interface Storage { // FIXME need to finish this class.
	static final boolean localStorageSupported = checkStorageSupport("localStorage");
	static final boolean sessionStorageSupported = checkStorageSupport("sessionStorage");

	public static ArrayBufferFactory getLocalStorageIfSupported () {
		ArrayBufferFactory storage = null;
		if (localStorageSupported || sessionStorageSupported) {
			Object instance = ScriptHelper.eval("window.localStorage", null);
			storage = JsCast.castTo(instance, ArrayBufferFactory.class);
		}
		return storage;
	}

	public static boolean checkStorageSupport (String type) {
		ScriptHelper.put("type", type, null);
		String cmd = "" + " try {    " + "       $wnd[type].setItem('HelloWorld', 'HelloWorld'); "
			+ "       $wnd[type].removeItem('HelloWorld'); " + "       return true; " + "     } catch (e) { "
			+ "       return false;  " + "     }   ";
		return ScriptHelper.evalBoolean(cmd, null);
	}

	int getLength ();

	String key (int index);

	String getItem (String key);

	void setItem (String key, String value);

	void removeItem (String key);

	void clear ();
}
