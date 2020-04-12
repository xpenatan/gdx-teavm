package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import com.github.xpenatan.gdx.backend.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLDocumentWrapper;
import com.github.xpenatan.gdx.backend.web.dom.ElementWrapper;

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
		HTMLCanvasElement canvas = (HTMLCanvasElement)document.getElementById(id);
		return new TeaCanvas(canvas);
	}

}
