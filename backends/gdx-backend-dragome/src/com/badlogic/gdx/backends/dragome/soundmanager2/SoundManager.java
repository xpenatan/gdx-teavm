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
public interface SoundManager {
	
	public interface SoundManagerCallback {
		@MethodAlias(local_alias= "onready")
		public void onready ();
		@MethodAlias(local_alias= "ontimeout")
		public void ontimeout (String status, String errorType);
	}

	public static SoundManager getInstance () {
		return ScriptHelper.evalCasting("window.soundManager", SoundManager.class, null);
	};

	public String getVersion (); 
	public String getUrl ();
	public void setUrl (String url);
	public void setDebugMode (boolean debug);
	public boolean getDebugMode ();
	public void setFlashVersion (int version);
	public int getFlashVersion ();

	/** Creates a new sound object from the supplied url.
	 * @param url the location of the sound file.
	 * @return the created sound object. */
	public static SMSound createSound (String url) {
		return ScriptHelper.evalCasting("window.soundManager.createSound({url: url})", SMSound.class, null);
	}

	public void reboot ();
	public boolean ok ();

	public static void init (String moduleBaseURL, int flashVersion, boolean preferFlash, SoundManagerCallback callback) {
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