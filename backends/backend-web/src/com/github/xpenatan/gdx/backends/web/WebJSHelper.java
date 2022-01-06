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
public abstract class WebJSHelper {
	static WebJSHelper JSHelper;

	public static WebJSHelper get() {
		return JSHelper;
	}

	public abstract HTMLCanvasElementWrapper getCanvas();

	public abstract WebGLRenderingContextWrapper getGLContext(WebApplicationConfiguration config);

	public abstract WindowWrapper getCurrentWindow();

	public abstract WebAgentInfo getAgentInfo();

	public abstract HTMLImageElementWrapper createImageElement();

	public abstract XMLHttpRequestWrapper creatHttpRequest();

	public abstract StorageWrapper getStorage();

	public abstract SoundManagerWrapper createSoundManager();

	public abstract WebJSGraphics getGraphics();
}
