package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.webgl.WebGLRenderingContext;
import org.w3c.dom.html.HTMLImageElement;

import com.github.xpenatan.gdx.backend.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLVideoElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.ImageDataWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.ArrayBufferWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.Float32ArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.FloatArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.Int32ArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.LongArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.ObjectArrayWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLActiveInfoWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLBufferWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLContextAttributesWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLFramebufferWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLProgramWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLRenderbufferWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLRenderingContextWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLShaderWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLTextureWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLUniformLocationWrapper;

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

	@Override
	public int getDrawingBufferWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDrawingBufferHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public WebGLContextAttributesWrapper getContextAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isContextLost() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ObjectArrayWrapper<String> getSupportedExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getExtension(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void activeTexture(int texture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void attachShader(WebGLProgramWrapper program, WebGLShaderWrapper shader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindAttribLocation(WebGLProgramWrapper program, int index, String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindBuffer(int target, WebGLBufferWrapper buffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindFramebuffer(int target, WebGLFramebufferWrapper framebuffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindRenderbuffer(int target, WebGLRenderbufferWrapper renderbuffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindTexture(int target, WebGLTextureWrapper texture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void blendColor(float red, float green, float blue, float alpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void blendEquation(int mode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void blendEquationSeparate(int modeRGB, int modeAlpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void blendFunc(int sfactor, int dfactor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void blendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bufferData(int target, long size, int usage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bufferData(int target, ArrayBufferViewWrapper data, int usage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bufferData(int target, ArrayBufferWrapper data, int usage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bufferSubData(int target, long offset, ArrayBufferViewWrapper data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bufferSubData(int target, long offset, ArrayBufferWrapper data) {
		// TODO Auto-generated method stub

	}

	@Override
	public int checkFramebufferStatus(int target) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clearDepth(float depth) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearStencil(int s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void compileShader(WebGLShaderWrapper shader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void copyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height,
			int border) {
		// TODO Auto-generated method stub

	}

	@Override
	public void copyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public WebGLBufferWrapper createBuffer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebGLFramebufferWrapper createFramebuffer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebGLProgramWrapper createProgram() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebGLRenderbufferWrapper createRenderbuffer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebGLShaderWrapper createShader(int type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebGLTextureWrapper createTexture() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cullFace(int mode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteBuffer(WebGLBufferWrapper buffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteFramebuffer(WebGLFramebufferWrapper framebuffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteProgram(WebGLProgramWrapper program) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteRenderbuffer(WebGLRenderbufferWrapper renderbuffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteShader(WebGLShaderWrapper shader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteTexture(WebGLTextureWrapper texture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void depthFunc(int func) {
		// TODO Auto-generated method stub

	}

	@Override
	public void depthMask(boolean flag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void depthRange(float zNear, float zFar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void detachShader(WebGLProgramWrapper program, WebGLShaderWrapper shader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disable(int cap) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disableVertexAttribArray(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawArrays(int mode, int first, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawElements(int mode, int count, int type, long offset) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enable(int cap) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableVertexAttribArray(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

	@Override
	public void framebufferRenderbuffer(int target, int attachment, int renderbuffertarget,
			WebGLRenderbufferWrapper renderbuffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void framebufferTexture2D(int target, int attachment, int textarget, WebGLTextureWrapper texture,
			int level) {
		// TODO Auto-generated method stub

	}

	@Override
	public void frontFace(int mode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateMipmap(int target) {
		// TODO Auto-generated method stub

	}

	@Override
	public WebGLActiveInfoWrapper getActiveAttrib(WebGLProgramWrapper program, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebGLActiveInfoWrapper getActiveUniform(WebGLProgramWrapper program, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectArrayWrapper<WebGLShaderWrapper> getAttachedShaders(WebGLProgramWrapper program) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getAttribLocation(WebGLProgramWrapper program, String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getParameter(int pname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getBufferParameter(int target, int pname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getError() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getFramebufferAttachmentParameter(int target, int attachment, int pname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getProgramParameter(WebGLProgramWrapper program, int pname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProgramInfoLog(WebGLProgramWrapper program) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getRenderbufferParameter(int target, int pname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getShaderParameter(WebGLShaderWrapper shader, int pname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShaderInfoLog(WebGLShaderWrapper shader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShaderSource(WebGLShaderWrapper shader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getTexParameter(int target, int pname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getUniform(WebGLProgramWrapper program, WebGLUniformLocationWrapper location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebGLUniformLocationWrapper getUniformLocation(WebGLProgramWrapper program, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getVertexAttrib(int index, int pname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getVertexAttribOffset(int index, int pname) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void hint(int target, int mode) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isBuffer(WebGLBufferWrapper buffer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled(int cap) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFramebuffer(WebGLFramebufferWrapper framebuffer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProgram(WebGLProgramWrapper program) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRenderbuffer(WebGLRenderbufferWrapper renderbuffer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isShader(WebGLShaderWrapper shader) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTexture(WebGLTextureWrapper texture) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void lineWidth(float width) {
		// TODO Auto-generated method stub

	}

	@Override
	public void linkProgram(WebGLProgramWrapper program) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pixelStorei(int pname, int param) {
		// TODO Auto-generated method stub

	}

	@Override
	public void polygonOffset(float factor, float units) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readPixels(int x, int y, int width, int height, int format, int type, ArrayBufferViewWrapper pixels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderbufferStorage(int target, int internalformat, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sampleCoverage(float value, boolean invert) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scissor(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void shaderSource(WebGLShaderWrapper shader, String source) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stencilFunc(int func, int ref, int mask) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stencilFuncSeparate(int face, int func, int ref, int mask) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stencilMask(int mask) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stencilMaskSeparate(int face, int mask) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stencilOp(int fail, int zfail, int zpass) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stencilOpSeparate(int face, int fail, int zfail, int zpass) {
		// TODO Auto-generated method stub

	}

	@Override
	public void texImage2D(int target, int level, int internalformat, int width, int height, int border, int format,
			int type, ArrayBufferViewWrapper pixels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void texImage2D(int target, int level, int internalformat, int format, int type, ImageDataWrapper pixels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void texImage2D(int target, int level, int internalformat, int format, int type, HTMLImageElement image) {
		// TODO Auto-generated method stub

	}

	@Override
	public void texImage2D(int target, int level, int internalformat, int format, int type,
			HTMLCanvasElementWrapper canvas) {
		// TODO Auto-generated method stub

	}

	@Override
	public void texImage2D(int target, int level, int internalformat, int format, int type,
			HTMLVideoElementWrapper video) {
		// TODO Auto-generated method stub

	}

	@Override
	public void texParameterf(int target, int pname, float param) {
		// TODO Auto-generated method stub

	}

	@Override
	public void texParameteri(int target, int pname, int param) {
		// TODO Auto-generated method stub

	}

	@Override
	public void texSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format,
			int type, ArrayBufferViewWrapper pixels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type,
			ImageDataWrapper pixels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type,
			HTMLImageElement image) {
		// TODO Auto-generated method stub

	}

	@Override
	public void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type,
			HTMLCanvasElementWrapper canvas) {
		// TODO Auto-generated method stub

	}

	@Override
	public void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type,
			HTMLVideoElementWrapper video) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform1f(WebGLUniformLocationWrapper location, float x) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform1fv(WebGLUniformLocationWrapper location, Float32ArrayWrapper v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform1fv(WebGLUniformLocationWrapper location, FloatArrayWrapper v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform1i(WebGLUniformLocationWrapper location, int x) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform1iv(WebGLUniformLocationWrapper location, Int32ArrayWrapper v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform1iv(WebGLUniformLocationWrapper location, LongArrayWrapper v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform2f(WebGLUniformLocationWrapper location, float x, float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform2fv(WebGLUniformLocationWrapper location, Float32ArrayWrapper v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform2fv(WebGLUniformLocationWrapper location, FloatArrayWrapper v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform2i(WebGLUniformLocationWrapper location, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform2iv(WebGLUniformLocationWrapper location, Int32ArrayWrapper v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform2iv(WebGLUniformLocationWrapper location, LongArrayWrapper v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform3f(WebGLUniformLocationWrapper location, float x, float y, float z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform3fv(WebGLUniformLocationWrapper location, Float32ArrayWrapper v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform3fv(WebGLUniformLocationWrapper location, FloatArrayWrapper v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform3i(WebGLUniformLocationWrapper location, int x, int y, int z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform3iv(WebGLUniformLocationWrapper location, Int32ArrayWrapper v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform3iv(WebGLUniformLocationWrapper location, LongArrayWrapper v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform4f(WebGLUniformLocationWrapper location, float x, float y, float z, float w) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform4fv(WebGLUniformLocationWrapper location, Float32ArrayWrapper v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform4fv(WebGLUniformLocationWrapper location, FloatArrayWrapper v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform4i(WebGLUniformLocationWrapper location, int x, int y, int z, int w) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform4iv(WebGLUniformLocationWrapper location, Int32ArrayWrapper v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform4iv(WebGLUniformLocationWrapper location, LongArrayWrapper v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniformMatrix2fv(WebGLUniformLocationWrapper location, boolean transpose, Float32ArrayWrapper value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniformMatrix2fv(WebGLUniformLocationWrapper location, boolean transpose, FloatArrayWrapper value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniformMatrix3fv(WebGLUniformLocationWrapper location, boolean transpose, Float32ArrayWrapper value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniformMatrix3fv(WebGLUniformLocationWrapper location, boolean transpose, FloatArrayWrapper value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniformMatrix4fv(WebGLUniformLocationWrapper location, boolean transpose, Float32ArrayWrapper value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniformMatrix4fv(WebGLUniformLocationWrapper location, boolean transpose, FloatArrayWrapper value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void useProgram(WebGLProgramWrapper program) {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateProgram(WebGLProgramWrapper program) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexAttrib1f(int indx, float x) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexAttrib1fv(int indx, Float32ArrayWrapper values) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexAttrib1fv(int indx, FloatArrayWrapper values) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexAttrib2f(int indx, float x, float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexAttrib2fv(int indx, Float32ArrayWrapper values) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexAttrib2fv(int indx, FloatArrayWrapper values) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexAttrib3f(int indx, float x, float y, float z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexAttrib3fv(int indx, Float32ArrayWrapper values) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexAttrib3fv(int indx, FloatArrayWrapper values) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexAttrib4f(int indx, float x, float y, float z, float w) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexAttrib4fv(int indx, Float32ArrayWrapper values) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexAttrib4fv(int indx, FloatArrayWrapper values) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, long offset) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform1iv(WebGLUniformLocationWrapper location, int[] v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform1fv(WebGLUniformLocationWrapper loc, float[] v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform2fv(WebGLUniformLocationWrapper loc, float[] v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform2iv(WebGLUniformLocationWrapper loc, int[] v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform3fv(WebGLUniformLocationWrapper loc, float[] v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform3iv(WebGLUniformLocationWrapper loc, int[] v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform4fv(WebGLUniformLocationWrapper loc, float[] v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniform4iv(WebGLUniformLocationWrapper loc, int[] v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniformMatrix2fv(WebGLUniformLocationWrapper loc, boolean transpose, float[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniformMatrix3fv(WebGLUniformLocationWrapper loc, boolean transpose, float[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniformMatrix4fv(WebGLUniformLocationWrapper loc, boolean transpose, float[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getParameterInt(int pname) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getParameterFloat(int pname) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getParameterString(int pname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getProgramParameterInt(WebGLProgramWrapper program, int pname) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getProgramParameterBoolean(WebGLProgramWrapper program, int pname) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getShaderParameterBoolean(WebGLShaderWrapper shader, int pname) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getShaderParameterInt(WebGLShaderWrapper shader, int pname) {
		// TODO Auto-generated method stub
		return 0;
	}
}
