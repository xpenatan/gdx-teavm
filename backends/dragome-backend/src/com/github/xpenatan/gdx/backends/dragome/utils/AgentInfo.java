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

package com.github.xpenatan.gdx.backends.dragome.utils;

import com.dragome.commons.javascript.ScriptHelper;

/** Ported from GWT backend
 * @author xpenatan */
public class AgentInfo {
	
	public static AgentInfo computeAgentInfo () {
		ScriptHelper.evalNoResult("var userAgent = navigator.userAgent.toLowerCase();", null);
		Object node = ScriptHelper.eval("{ isFirefox : userAgent.indexOf('firefox') != -1,"
			+ "isChrome : userAgent.indexOf('chrome') != -1,"
			+ "isSafari : userAgent.indexOf('safari') != -1,"
			+ "isOpera : userAgent.indexOf('opera') != -1,"
			+ "isIE : userAgent.indexOf('msie') != -1,"
			+ "isMacOS : userAgent.indexOf('mac') != -1,"
			+ "isLinux : userAgent.indexOf('linux') != -1,"
			+ "isWindows : userAgent.indexOf('win') != -1"
			+ "}", null);
		AgentInfo agent = new AgentInfo();
		ScriptHelper.put("agent", agent, null);
		ScriptHelper.put("agent.node", node, null);
		return agent;
	}

	public boolean isFirefox () {
		return ScriptHelper.evalBoolean("this.node.isFirefox", this);
	}

	public boolean isChrome () {
		return ScriptHelper.evalBoolean("this.node.isChrome", this);
	}

	public boolean isSafari () {
		return ScriptHelper.evalBoolean("this.node.isSafari", this);
	}

	public boolean isOpera () {
		return ScriptHelper.evalBoolean("this.node.isOpera", this);
	}

	public boolean isIE () {
		return ScriptHelper.evalBoolean("this.node.isIE", this);
	}

	public boolean isMacOS () {
		return ScriptHelper.evalBoolean("this.node.isMacOS", this);
	}

	public boolean isLinux () {
		return ScriptHelper.evalBoolean("this.node.isLinux", this);
	}

	public boolean isWindows () {
		return ScriptHelper.evalBoolean("this.node.isWindows", this);
	}
}