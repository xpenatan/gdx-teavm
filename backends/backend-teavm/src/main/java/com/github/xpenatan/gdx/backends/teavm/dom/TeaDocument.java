package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.dom.html.HTMLDocument;
import com.github.xpenatan.gdx.backend.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLDocumentWrapper;
import com.github.xpenatan.gdx.backend.web.dom.ElementWrapper;

/**
 * @author xpenatan
 */
public class TeaDocument implements HTMLDocumentWrapper {

	private HTMLDocument document;

	public TeaDocument(HTMLDocument document) {
		this.document = document;
	}

	@Override
	public ElementWrapper getDocumentElement() {
		return null;
	}

	@Override
	public HTMLCanvasElementWrapper getCanvas(String id) {
		HTMLCanvasElementWrapper canvas = (HTMLCanvasElementWrapper)document.getElementById(id);
		return canvas;
	}

}
