package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.webgl.WebGLContextAttributes;
import org.teavm.jso.webgl.WebGLRenderingContext;

import com.github.xpenatan.gdx.backend.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backend.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLDocumentWrapper;
import com.github.xpenatan.gdx.backends.teavm.TeaGLContext;

public class TeaCanvas implements HTMLCanvasElementWrapper {

	private HTMLCanvasElement canvas;

	public TeaCanvas(HTMLCanvasElement canvas) {
		this.canvas = canvas;
	}

	@Override
	public int scrollTop() {
		return canvas.getScrollTop();
	}

	@Override
	public HTMLDocumentWrapper getOwnerDocument() {
		return null;
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

	@Override
	public TeaGLContext getGLContext(WebApplicationConfiguration config) {
		WebGLContextAttributes attr = WebGLContextAttributes.create();
		attr.setAlpha(config.alpha);
		attr.setAntialias(config.antialiasing);
		attr.setStencil(config.stencil);
		attr.setPremultipliedAlpha(config.premultipliedAlpha);
		attr.setPreserveDrawingBuffer(config.preserveDrawingBuffer);
		WebGLRenderingContext context = (WebGLRenderingContext)canvas.getContext("webgl", attr);
		return new TeaGLContext(context);
	}

}
