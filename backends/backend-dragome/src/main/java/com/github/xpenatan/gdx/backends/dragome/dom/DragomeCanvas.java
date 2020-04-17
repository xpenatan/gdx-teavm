package com.github.xpenatan.gdx.backends.dragome.dom;

import org.w3c.dom.html.HTMLCanvasElement;
import org.w3c.dom.webgl.WebGLContextAttributes;

import com.dragome.commons.javascript.ScriptHelper;
import com.github.xpenatan.gdx.backend.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backend.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLDocumentWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLContextAttributesWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLRenderingContextWrapper;

public class DragomeCanvas implements HTMLCanvasElementWrapper {

	private HTMLCanvasElement canvas;

	public DragomeCanvas(HTMLCanvasElement canvas) {
		this.canvas = canvas;
	}

	@Override
	public WebGLRenderingContextWrapper getGLContext(WebApplicationConfiguration config) {

		WebGLContextAttributesWrapper attributes = ScriptHelper.evalCasting("{premultipliedAlpha:false}", WebGLContextAttributesWrapper.class, null);
		attributes.setAntialias(config.antialiasing);
		attributes.setStencil(config.stencil);
		attributes.setAlpha(config.alpha);
		attributes.setPremultipliedAlpha(config.premultipliedAlpha);
		attributes.setPreserveDrawingBuffer(config.preserveDrawingBuffer);

		ScriptHelper.put("contextName", "webgl", this);
		ScriptHelper.put("canvas", canvas, this);
		ScriptHelper.put("attr", attributes, this);
		Object con = ScriptHelper.eval("canvas.node.getContext(contextName, attr.node)", this);
		ScriptHelper.put("con", con, null);
		WebGLRenderingContextWrapper context = ScriptHelper.evalCasting("con", WebGLRenderingContextWrapper.class, null);
		return context;
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
