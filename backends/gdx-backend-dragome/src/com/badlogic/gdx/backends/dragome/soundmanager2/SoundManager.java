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

package com.badlogic.gdx.backends.dragome.soundmanager2;

import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.commons.javascript.ScriptHelper;

/** Ported from GWT backend
 * @author xpenatan */
public class SoundManager {

	public interface SoundManagerCallback {
		@MethodAlias(local_alias= "onready")
		public void onready ();
		@MethodAlias(local_alias= "ontimeout")
		public void ontimeout (String status, String errorType);
	}

	public static SoundManager getInstance () {
		Object node = ScriptHelper.eval("window.soundManager", null);
		SoundManager manager = new SoundManager();
		ScriptHelper.put("manager", manager, null);
		ScriptHelper.put("manager.node", node, null);
		return manager;
	};

	public String getVersion () {
		return (String)ScriptHelper.eval("this.node.version", this);
	}

	public String getUrl () {
		return (String)ScriptHelper.eval("this.node.url", this);
	}

	public void setUrl (String url) {
		ScriptHelper.put("$1", url, this);
		ScriptHelper.evalNoResult("this.node.url=$1", this);
	}

	public void setDebugMode (boolean debug) {
		ScriptHelper.put("$1", debug, this);
		ScriptHelper.evalNoResult("this.node.debugMode=$1", this);
	}

	public boolean getDebugMode () {
		return ScriptHelper.evalBoolean("this.node.debugMode", this);
	}

	public void setFlashVersion (int version) {
		ScriptHelper.put("$1", version, this);
		ScriptHelper.evalNoResult("this.node.flashVersion=$1", this);
	}

	public int getFlashVersion () {
		return ScriptHelper.evalInt("this.node.flashVersion", this);
	}

	public void reboot () {
		ScriptHelper.evalNoResult("this.node.reboot()", this);
	}

	public boolean ok () {
		return ScriptHelper.evalBoolean("this.node.ok()", this);
	}

	/** Creates a new sound object from the supplied url.
	 * @param url the location of the sound file.
	 * @return the created sound object. */
	public static SMSound createSound (String url) {
		ScriptHelper.put("url", url, null);
		Object node = ScriptHelper.eval("window.soundManager.createSound({url: url})", null);
		SMSound sound = new SMSound();
		ScriptHelper.put("sound", sound, null);
		ScriptHelper.put("sound.node", node, null);
		return sound;
	}

	public static void init (String moduleBaseURL, int flashVersion, boolean preferFlash, SoundManagerCallback callback) {
		ScriptHelper.put("moduleBaseURL", moduleBaseURL, null);
		ScriptHelper.put("flashVersion", flashVersion, null);
		ScriptHelper.put("preferFlash", preferFlash, null);
		ScriptHelper.put("callback", callback, null);
		ScriptHelper.evalNoResult("window.soundManager = new SoundManager();", null);
		ScriptHelper.evalNoResult("window.soundManager.setup({ "
			+ "url: moduleBaseURL,"
			+ "flashVersion: flashVersion,"
			+ "preferFlash: preferFlash,"
			+ "onready: function(){callback.onready()},"
			+ "ontimeout: function(status) {callback.ontimeout(status.success, (typeof status.error === 'undefined') ? '' : status.error.type)}"
			+ "})", null);
		ScriptHelper.evalNoResult("window.soundManager.beginDelayedInit();", null);
	};

}