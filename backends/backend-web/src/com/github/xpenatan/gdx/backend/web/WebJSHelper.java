package com.github.xpenatan.gdx.backend.web;

import com.github.xpenatan.gdx.backend.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLImageElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.StorageWrapper;
import com.github.xpenatan.gdx.backend.web.dom.WindowWrapper;
import com.github.xpenatan.gdx.backend.web.dom.XMLHttpRequestWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLRenderingContextWrapper;

/**
 * @author xpenatan
 */
public interface WebJSHelper {

	public HTMLCanvasElementWrapper getCanvas();

	public WebGLRenderingContextWrapper getGLContext(WebApplicationConfiguration config);

	public WindowWrapper getCurrentWindow();

	public WebAgentInfo getAgentInfo();

	public HTMLImageElementWrapper createImageElement();

	public XMLHttpRequestWrapper creatHttpRequest();

	public StorageWrapper getStorage();
}
