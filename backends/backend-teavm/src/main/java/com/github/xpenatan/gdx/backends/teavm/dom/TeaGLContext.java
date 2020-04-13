package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.webgl.WebGLRenderingContext;

import com.github.xpenatan.gdx.backend.web.gl.WebGLRenderingContextWrapper;

public class TeaGLContext implements WebGLRenderingContextWrapper {

	private WebGLRenderingContext context;

	public TeaGLContext(WebGLRenderingContext context) {
		this.context = context;
	}

//	@JSBody(params = { "x", "y", "width", "height" }, script = "context.viewport(x, y, width, height);")
//	public native void JSviewport(int x, int y, int width, int height);
//

//	@JSBody(params = { "mask" }, script = "context.clear(mask);")
//	public native void JSclear(int mask);

//	@JSBody(params = { "red", "green", "blue", "alpha" }, script = "this.viewport(red, green, blue, alpha);")
//	public native void viewport(float red, float green, float blue, float alpha);

	@Override
	public void viewport(int x, int y, int width, int height) {
		context.viewport(x, y, width, height);
	}

	@Override
	public void clear(int mask) {
		context.clear(mask);
	}

	@Override
	public void clearColor(float red, float green, float blue, float alpha) {
		context.clearColor(red, green, blue, alpha);
	}
}
