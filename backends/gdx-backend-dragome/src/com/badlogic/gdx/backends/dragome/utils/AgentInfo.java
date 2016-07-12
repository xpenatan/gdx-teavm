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

package com.badlogic.gdx.backends.dragome.utils;

import com.dragome.commons.DelegateCode;
import com.dragome.commons.javascript.ScriptHelper;

/** Ported from GWT backend
 * @author xpenatan */
public interface AgentInfo {
	
	@DelegateCode(ignore = true)
	public static AgentInfo computeAgentInfo () {
		ScriptHelper.evalNoResult("var userAgent = navigator.userAgent.toLowerCase();", null);
		return ScriptHelper.evalCasting("{ isFirefox : userAgent.indexOf('firefox') != -1,"
			+ "isChrome : userAgent.indexOf('chrome') != -1,"
			+ "isSafari : userAgent.indexOf('safari') != -1,"
			+ "isOpera : userAgent.indexOf('opera') != -1,"
			+ "isIE : userAgent.indexOf('msie') != -1,"
			+ "isMacOS : userAgent.indexOf('mac') != -1,"
			+ "isLinux : userAgent.indexOf('linux') != -1,"
			+ "isWindows : userAgent.indexOf('win') != -1"
			+ "}", AgentInfo.class, null);
	}

	@DelegateCode(eval = "this.node.isFirefox")
	public boolean isFirefox ();
	@DelegateCode(eval = "this.node.isChrome")
	public boolean isChrome ();
	@DelegateCode(eval = "this.node.isSafari")
	public boolean isSafari ();
	@DelegateCode(eval = "this.node.isOpera")
	public boolean isOpera ();
	@DelegateCode(eval = "this.node.isIE")
	public boolean isIE ();
	@DelegateCode(eval = "this.node.isMacOS")
	public boolean isMacOS ();
	@DelegateCode(eval = "this.node.isLinux")
	public boolean isLinux ();
	@DelegateCode(eval = "this.node.isWindows")
	public boolean isWindows ();
}
