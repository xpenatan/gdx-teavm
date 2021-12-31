package com.github.xpenatan.gdx.backends.web;

import com.github.xpenatan.gdx.backends.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backends.web.dom.HTMLImageElementWrapper;
import com.github.xpenatan.gdx.backends.web.dom.StorageWrapper;
import com.github.xpenatan.gdx.backends.web.dom.WindowWrapper;
import com.github.xpenatan.gdx.backends.web.dom.XMLHttpRequestWrapper;
import com.github.xpenatan.gdx.backends.web.gl.WebGLRenderingContextWrapper;
import com.github.xpenatan.gdx.backends.web.soundmanager.SoundManagerWrapper;

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

	public SoundManagerWrapper createSoundManager();
}
