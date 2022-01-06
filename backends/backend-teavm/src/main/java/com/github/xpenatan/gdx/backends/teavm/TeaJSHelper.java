package com.github.xpenatan.gdx.backends.teavm;

import com.github.xpenatan.gdx.backends.teavm.dom.impl.TeaSoundManager;
import com.github.xpenatan.gdx.backends.teavm.dom.impl.TeaWindow;
import com.github.xpenatan.gdx.backends.web.WebJSGraphics;
import org.teavm.jso.JSObject;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.Storage;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.webgl.WebGLContextAttributes;

import com.github.xpenatan.gdx.backends.web.WebAgentInfo;
import com.github.xpenatan.gdx.backends.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backends.web.WebJSHelper;
import com.github.xpenatan.gdx.backends.web.dom.DocumentWrapper;
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
public class TeaJSHelper extends WebJSHelper implements JSObject {

	private WebAgentInfo agentInfo;
	private HTMLCanvasElementWrapper canvasWrapper;
	private TeaJSGraphics graphics;

	public TeaJSHelper(WebAgentInfo agentInfo, HTMLCanvasElementWrapper canvasWrapper) {
		this.agentInfo = agentInfo;
		this.canvasWrapper = canvasWrapper;
		this.graphics = new TeaJSGraphics();
	}

	@Override
	public WebGLRenderingContextWrapper getGLContext(WebApplicationConfiguration config) {
		WebGLContextAttributes attr = WebGLContextAttributes.create();
		attr.setAlpha(config.alpha);
		attr.setAntialias(config.antialiasing);
		attr.setStencil(config.stencil);
		attr.setPremultipliedAlpha(config.premultipliedAlpha);
		attr.setPreserveDrawingBuffer(config.preserveDrawingBuffer);
		HTMLCanvasElement canvas = (HTMLCanvasElement)canvasWrapper;
		WebGLRenderingContextWrapper context = (WebGLRenderingContextWrapper)canvas.getContext("webgl", attr);
		return context;
	}

	@Override
	public HTMLCanvasElementWrapper getCanvas() {
		return canvasWrapper;
	}

	@Override
	public WindowWrapper getCurrentWindow() {
		return new TeaWindow();
	}

	@Override
	public WebAgentInfo getAgentInfo() {
		return agentInfo;
	}

	@Override
	public HTMLImageElementWrapper createImageElement() {
		DocumentWrapper document = getCurrentWindow().getDocument();
		return (HTMLImageElementWrapper) document.createElement("img");
	}

	@Override
	public XMLHttpRequestWrapper creatHttpRequest() {
		return (XMLHttpRequestWrapper) XMLHttpRequest.create();
	}

	@Override
	public StorageWrapper getStorage() {
		StorageWrapper storage = (StorageWrapper)Storage.getLocalStorage();
		return storage;
	}

	@Override
	public SoundManagerWrapper createSoundManager() {
		return new TeaSoundManager();
	}

	@Override
	public WebJSGraphics getGraphics() {
		return graphics;
	}
}
