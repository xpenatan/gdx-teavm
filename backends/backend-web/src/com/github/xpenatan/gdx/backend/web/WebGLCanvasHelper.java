package com.github.xpenatan.gdx.backend.web;

import com.github.xpenatan.gdx.backend.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLRenderingContextWrapper;

/**
 * @author xpenatan
 */
public interface WebGLCanvasHelper {

	public HTMLCanvasElementWrapper getCanvas();

	public WebGLRenderingContextWrapper getGLContext(WebApplicationConfiguration config);
}
