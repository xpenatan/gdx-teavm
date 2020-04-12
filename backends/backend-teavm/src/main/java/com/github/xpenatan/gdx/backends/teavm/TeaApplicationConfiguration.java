package com.github.xpenatan.gdx.backends.teavm;

import com.github.xpenatan.gdx.backend.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backend.web.WebGraphics;
import com.github.xpenatan.gdx.backend.web.dom.HTMLDocumentWrapper;
import com.github.xpenatan.gdx.backend.web.dom.WindowWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.TeaWindow;

/**
 * @author xpenatan
 */
public class TeaApplicationConfiguration extends WebApplicationConfiguration {

	public TeaApplicationConfiguration(String canvasID) {
		WindowWrapper window = TeaWindow.getCurrent();
		HTMLDocumentWrapper document = window.getDocument();
		canvas = document.getCanvas(canvasID);
	}
}
