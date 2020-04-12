package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLDocument;

import com.github.xpenatan.gdx.backend.web.dom.HTMLDocumentWrapper;
import com.github.xpenatan.gdx.backend.web.dom.WindowWrapper;

public class TeaWindow implements WindowWrapper {

	Window window;

	public static WindowWrapper getCurrent() {
		return new TeaWindow(Window.current());
	}

	private TeaWindow(Window window) {
		this.window = window;
	}

	@Override
	public HTMLDocumentWrapper getDocument() {
		HTMLDocument document = window.getDocument();
		return new TeaDocument(document);
	}

}
