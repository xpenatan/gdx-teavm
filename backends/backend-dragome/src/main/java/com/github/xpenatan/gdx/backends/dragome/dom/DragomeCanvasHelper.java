package com.github.xpenatan.gdx.backends.dragome.dom;

import com.dragome.commons.javascript.ScriptHelper;
import com.github.xpenatan.gdx.backend.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backend.web.WebGLCanvasHelper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLContextAttributesWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLRenderingContextWrapper;

/**
 * @author xpenatan
 */
public class DragomeCanvasHelper implements WebGLCanvasHelper {

	private DragomeCanvas canvasWrapper;

	public DragomeCanvasHelper(DragomeCanvas canvasWrapper) {
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
		ScriptHelper.put("canvas", canvasWrapper.canvas, this);
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
}
