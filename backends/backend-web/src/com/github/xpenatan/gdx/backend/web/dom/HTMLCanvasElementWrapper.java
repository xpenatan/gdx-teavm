package com.github.xpenatan.gdx.backend.web.dom;

import com.github.xpenatan.gdx.backend.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backend.web.gl.WebGLRenderingContextWrapper;

public interface HTMLCanvasElementWrapper extends ElementWrapper {

	public HTMLDocumentWrapper getOwnerDocument();
	public int getWidth();
	public void setWidth(int width);
	public int getHeight();
	public void setHeight(int height);
	public WebGLRenderingContextWrapper getGLContext(WebApplicationConfiguration config);
}
