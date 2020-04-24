package com.github.xpenatan.gdx.backends.dragome.dom;

import org.w3c.dom.html.HTMLCanvasElement;
import com.dragome.commons.javascript.ScriptHelper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLDocumentWrapper;

/**
 * @author xpenatan
 */
public class DragomeCanvas implements HTMLCanvasElementWrapper {

	public HTMLCanvasElement canvas;

	public DragomeCanvas(HTMLCanvasElement canvas) {
		this.canvas = canvas;
	}

	@Override
	public int scrollTop() {
		return 0;
	}

	@Override
	public HTMLDocumentWrapper getOwnerDocument() {
		ScriptHelper.put("canvas", canvas, null);
		return  ScriptHelper.evalCasting("canvas.node.ownerDocument", HTMLDocumentWrapper.class, null);
	}

	@Override
	public int getWidth() {
		return canvas.getWidth();
	}

	@Override
	public void setWidth(int width) {
		canvas.setWidth(width);
	}

	@Override
	public int getHeight() {
		return canvas.getHeight();
	}

	@Override
	public void setHeight(int height) {
		canvas.setHeight(height);
	}
}
