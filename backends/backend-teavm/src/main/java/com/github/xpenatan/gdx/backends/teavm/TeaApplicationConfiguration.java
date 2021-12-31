package com.github.xpenatan.gdx.backends.teavm;

import com.github.xpenatan.gdx.backends.web.WebAgentInfo;
import com.github.xpenatan.gdx.backends.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backends.web.dom.DocumentWrapper;
import com.github.xpenatan.gdx.backends.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backends.web.dom.HTMLElementWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.TeaJSHelper;
import com.github.xpenatan.gdx.backends.teavm.dom.TeaTypedArrays;
import com.github.xpenatan.gdx.backends.teavm.dom.TeaWebAgent;
import com.github.xpenatan.gdx.backends.teavm.dom.impl.TeaWindow;

/**
 * @author xpenatan
 */
public class TeaApplicationConfiguration extends WebApplicationConfiguration {

	public TeaApplicationConfiguration(String canvasID) {
		TeaWindow window = new TeaWindow();
		DocumentWrapper document = window.getDocument();
		WebAgentInfo agentInfo = TeaWebAgent.computeAgentInfo();
		HTMLElementWrapper elementID = document.getElementById(canvasID);
		HTMLCanvasElementWrapper htmlCanvasDocument = (HTMLCanvasElementWrapper)elementID;
		JSHelper = new TeaJSHelper(agentInfo, htmlCanvasDocument);
		new TeaTypedArrays();
	}
}
