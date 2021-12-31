package com.github.xpenatan.gdx.backends.dragome;

import com.dragome.commons.javascript.ScriptHelper;
import com.github.xpenatan.gdx.backends.web.WebAgentInfo;

/**
 * @author xpenatan
 */
public class DragomeWebAgent implements WebAgentInfo {

	public static WebAgentInfo computeAgentInfo() {
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
		DragomeWebAgent agent = new DragomeWebAgent();
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
