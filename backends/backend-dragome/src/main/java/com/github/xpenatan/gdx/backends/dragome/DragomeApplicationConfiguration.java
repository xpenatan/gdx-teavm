package com.github.xpenatan.gdx.backends.dragome;

import org.w3c.dom.Element;
import com.dragome.web.html.dom.w3c.BrowserDomHandler;
import com.github.xpenatan.gdx.backend.web.WebAgentInfo;
import com.github.xpenatan.gdx.backend.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backend.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backends.dragome.dom.DragomeJSHelper;
import com.github.xpenatan.gdx.backends.dragome.dom.DragomeTypedArrays;

/**
 * @author xpenatan
 */
public class DragomeApplicationConfiguration extends WebApplicationConfiguration {

	public DragomeApplicationConfiguration(String canvasID) {
		BrowserDomHandler elementBySelector = new BrowserDomHandler();
		Element canvasElem111 = elementBySelector.getElementBySelector(canvasID);
		HTMLCanvasElementWrapper canvasElement111 = (HTMLCanvasElementWrapper)canvasElem111;
		WebAgentInfo agentInfo = DragomeWebAgent.computeAgentInfo();
		WebApplicationConfiguration.JSHelper = new DragomeJSHelper(agentInfo, canvasElement111);
		new DragomeTypedArrays();
	}
}
