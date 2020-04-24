package com.github.xpenatan.gdx.backends.teavm;

import com.github.xpenatan.gdx.backend.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backend.web.dom.HTMLDocumentWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.TeaCanvas;
import com.github.xpenatan.gdx.backends.teavm.dom.TeaTypedArrays;
import com.github.xpenatan.gdx.backends.teavm.dom.TeaWindow;

/**
 * @author xpenatan
 */
public class TeaApplicationConfiguration extends WebApplicationConfiguration {

	public TeaApplicationConfiguration(String canvasID) {
		window = new TeaWindow();
		HTMLDocumentWrapper document = window.getDocument();
		canvasHelper = new TeaCanvas(document.getCanvas(canvasID));
		new TeaTypedArrays();
	}
}
