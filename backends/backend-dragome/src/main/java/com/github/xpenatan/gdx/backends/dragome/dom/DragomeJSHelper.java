package com.github.xpenatan.gdx.backends.dragome.dom;

import org.w3c.dom.XMLHttpRequest;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.Window;
import com.dragome.web.html.dom.w3c.HTMLImageElementExtension;
import com.github.xpenatan.gdx.backend.web.WebAgentInfo;
import com.github.xpenatan.gdx.backend.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backend.web.WebJSHelper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLDocumentWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLImageElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.StorageWrapper;
import com.github.xpenatan.gdx.backend.web.dom.WindowWrapper;
import com.github.xpenatan.gdx.backend.web.dom.XMLHttpRequestWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLContextAttributesWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLRenderingContextWrapper;
import com.github.xpenatan.gdx.backend.web.soundmanager.SoundManagerWrapper;

/**
 * @author xpenatan
 */
public class DragomeJSHelper implements WebJSHelper {

	private WebAgentInfo agentInfo;
	private HTMLCanvasElementWrapper canvasWrapper;

	private DragomeWindow dragomeWindow;

	public DragomeJSHelper(WebAgentInfo agentInfo, HTMLCanvasElementWrapper canvasWrapper) {
		this.agentInfo = agentInfo;
		this.canvasWrapper = canvasWrapper;
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
		ScriptHelper.put("canvas", canvasWrapper, this);
		ScriptHelper.put("attr", attributes, this);
		Object con = ScriptHelper.eval("canvas.node.getContext(contextName, attr.node)", this);
		ScriptHelper.put("con", con, null);
		WebGLRenderingContextWrapper context = ScriptHelper.evalCasting("con", WebGLRenderingContextWrapper.class, null);
		return context;
	}

	@Override
	public HTMLCanvasElementWrapper getCanvas() {
		return canvasWrapper;
	}

	@Override
	public WindowWrapper getCurrentWindow() {
		if(dragomeWindow == null)
			dragomeWindow = new DragomeWindow();
		return dragomeWindow;
	}

	@Override
	public WebAgentInfo getAgentInfo() {
		return agentInfo;
	}

	@Override
	public HTMLImageElementWrapper createImageElement () {
		HTMLElementWrapper createElement = getCurrentWindow().getDocument().createElement("img");
		HTMLImageElementWrapper imageElement = JsCast.castTo(createElement, HTMLImageElementWrapper.class);
		return imageElement;
	}

	@Override
	public XMLHttpRequestWrapper creatHttpRequest () {
		XMLHttpRequestWrapper request = ScriptHelper.evalCasting("new XMLHttpRequest()", XMLHttpRequestWrapper.class, null);
		return request;
	}

	@Override
	public StorageWrapper getStorage () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SoundManagerWrapper createSoundManager () {
		// TODO Auto-generated method stub
		return null;
	}
}
