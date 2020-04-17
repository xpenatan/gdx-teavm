package com.github.xpenatan.gdx.backend.web.gl;

import org.w3c.dom.html.HTMLImageElement;

import com.github.xpenatan.gdx.backend.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLVideoElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.ImageDataWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.ArrayBufferWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.Float32ArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.FloatArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.Int32ArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.LongArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.ObjectArrayWrapper;

public interface WebGLRenderingContextWrapper {

	public static final int UNPACK_PREMULTIPLY_ALPHA_WEBGL = 0x9241;
	public static final int RGBA = 0x1908;
	public static final int UNSIGNED_BYTE = 0x1401;

	public int getDrawingBufferWidth();

	public int getDrawingBufferHeight();

	public WebGLContextAttributesWrapper getContextAttributes();

	public boolean isContextLost();

	public ObjectArrayWrapper<String> getSupportedExtensions();

	public Object getExtension(String name);

	public void activeTexture(int texture);

	public void attachShader(WebGLProgramWrapper program, WebGLShaderWrapper shader);

	public void bindAttribLocation(WebGLProgramWrapper program, int index, String name);

	public void bindBuffer(int target, WebGLBufferWrapper buffer);

	public void bindFramebuffer(int target, WebGLFramebufferWrapper framebuffer);

	public void bindRenderbuffer(int target, WebGLRenderbufferWrapper renderbuffer);

	public void bindTexture(int target, WebGLTextureWrapper texture);

	public void blendColor(float red, float green, float blue, float alpha);

	public void blendEquation(int mode);

	public void blendEquationSeparate(int modeRGB, int modeAlpha);

	public void blendFunc(int sfactor, int dfactor);

	public void blendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha);

	public void bufferData(int target, long size, int usage);

	public void bufferData(int target, ArrayBufferViewWrapper data, int usage);

	public void bufferData(int target, ArrayBufferWrapper data, int usage);

	public void bufferSubData(int target, long offset, ArrayBufferViewWrapper data);

	public void bufferSubData(int target, long offset, ArrayBufferWrapper data);

	public int checkFramebufferStatus(int target);

	public void clear(int mask);

	public void clearColor(float red, float green, float blue, float alpha);

	public void clearDepth(float depth);

	public void clearStencil(int s);

	public void colorMask(boolean red, boolean green, boolean blue, boolean alpha);

	public void compileShader(WebGLShaderWrapper shader);

	public void copyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border);

	public void copyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height);

	public WebGLBufferWrapper createBuffer();

	public WebGLFramebufferWrapper createFramebuffer();

	public WebGLProgramWrapper createProgram();

	public WebGLRenderbufferWrapper createRenderbuffer();

	public WebGLShaderWrapper createShader(int type);

	public WebGLTextureWrapper createTexture();

	public void cullFace(int mode);

	public void deleteBuffer(WebGLBufferWrapper buffer);

	public void deleteFramebuffer(WebGLFramebufferWrapper framebuffer);

	public void deleteProgram(WebGLProgramWrapper program);

	public void deleteRenderbuffer(WebGLRenderbufferWrapper renderbuffer);

	public void deleteShader(WebGLShaderWrapper shader);

	public void deleteTexture(WebGLTextureWrapper texture);

	public void depthFunc(int func);

	public void depthMask(boolean flag);

	public void depthRange(float zNear, float zFar);

	public void detachShader(WebGLProgramWrapper program, WebGLShaderWrapper shader);

	public void disable(int cap);

	public void disableVertexAttribArray(int index);

	public void drawArrays(int mode, int first, int count);

	public void drawElements(int mode, int count, int type, long offset);

	public void enable(int cap);

	public void enableVertexAttribArray(int index);

	public void finish();

	public void flush();

	public void framebufferRenderbuffer(int target, int attachment, int renderbuffertarget, WebGLRenderbufferWrapper renderbuffer);

	public void framebufferTexture2D(int target, int attachment, int textarget, WebGLTextureWrapper texture, int level);

	public void frontFace(int mode);

	public void generateMipmap(int target);

	public WebGLActiveInfoWrapper getActiveAttrib(WebGLProgramWrapper program, int index);

	public WebGLActiveInfoWrapper getActiveUniform(WebGLProgramWrapper program, int index);

	public ObjectArrayWrapper<WebGLShaderWrapper> getAttachedShaders(WebGLProgramWrapper program);

	public int getAttribLocation(WebGLProgramWrapper program, String name);

	public Object getParameter(int pname);

	public int getParameterInt(int pname);

	public float getParameterFloat(int pname);

	public String getParameterString(int pname);

	public Object getBufferParameter(int target, int pname);

	public int getError();

	public Object getFramebufferAttachmentParameter(int target, int attachment, int pname);

	public Object getProgramParameter(WebGLProgramWrapper program, int pname);

	public int getProgramParameterInt(WebGLProgramWrapper program, int pname);

	public boolean getProgramParameterBoolean(WebGLProgramWrapper program, int pname);

	public String getProgramInfoLog(WebGLProgramWrapper program);

	public Object getRenderbufferParameter(int target, int pname);

	public Object getShaderParameter(WebGLShaderWrapper shader, int pname);

	public boolean getShaderParameterBoolean(WebGLShaderWrapper shader, int pname);

	public int getShaderParameterInt(WebGLShaderWrapper shader, int pname);

	public String getShaderInfoLog(WebGLShaderWrapper shader);

	public String getShaderSource(WebGLShaderWrapper shader);

	public Object getTexParameter(int target, int pname);

	public Object getUniform(WebGLProgramWrapper program, WebGLUniformLocationWrapper location);

	public WebGLUniformLocationWrapper getUniformLocation(WebGLProgramWrapper program, String name);

	public Object getVertexAttrib(int index, int pname);

	public long getVertexAttribOffset(int index, int pname);

	public void hint(int target, int mode);

	public boolean isBuffer(WebGLBufferWrapper buffer);

	public boolean isEnabled(int cap);

	public boolean isFramebuffer(WebGLFramebufferWrapper framebuffer);

	public boolean isProgram(WebGLProgramWrapper program);

	public boolean isRenderbuffer(WebGLRenderbufferWrapper renderbuffer);

	public boolean isShader(WebGLShaderWrapper shader);

	public boolean isTexture(WebGLTextureWrapper texture);

	public void lineWidth(float width);

	public void linkProgram(WebGLProgramWrapper program);

	public void pixelStorei(int pname, int param);

	public void polygonOffset(float factor, float units);

	public void readPixels(int x, int y, int width, int height, int format, int type, ArrayBufferViewWrapper pixels);

	public void renderbufferStorage(int target, int internalformat, int width, int height);

	public void sampleCoverage(float value, boolean invert);

	public void scissor(int x, int y, int width, int height);

	public void shaderSource(WebGLShaderWrapper shader, String source);

	public void stencilFunc(int func, int ref, int mask);

	public void stencilFuncSeparate(int face, int func, int ref, int mask);

	public void stencilMask(int mask);

	public void stencilMaskSeparate(int face, int mask);

	public void stencilOp(int fail, int zfail, int zpass);

	public void stencilOpSeparate(int face, int fail, int zfail, int zpass);

	public void texImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ArrayBufferViewWrapper pixels);

	public void texImage2D(int target, int level, int internalformat, int format, int type, ImageDataWrapper pixels);

	public void texImage2D(int target, int level, int internalformat, int format, int type, HTMLImageElement image);

	public void texImage2D(int target, int level, int internalformat, int format, int type, HTMLCanvasElementWrapper canvas);

	public void texImage2D(int target, int level, int internalformat, int format, int type, HTMLVideoElementWrapper video);

	public void texParameterf(int target, int pname, float param);

	public void texParameteri(int target, int pname, int param);

	public void texSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ArrayBufferViewWrapper pixels);

	public void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type, ImageDataWrapper pixels);

	public void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type, HTMLImageElement image);

	public void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type, HTMLCanvasElementWrapper canvas);

	public void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type, HTMLVideoElementWrapper video);

	public void uniform1f(WebGLUniformLocationWrapper location, float x);

	public void uniform1fv(WebGLUniformLocationWrapper location, Float32ArrayWrapper v);

	public void uniform1fv(WebGLUniformLocationWrapper location, FloatArrayWrapper v);

	public void uniform1i(WebGLUniformLocationWrapper location, int x);

	public void uniform1iv(WebGLUniformLocationWrapper location, Int32ArrayWrapper v);

	public void uniform1iv(WebGLUniformLocationWrapper location, LongArrayWrapper v);

	public void uniform2f(WebGLUniformLocationWrapper location, float x, float y);

	public void uniform2fv(WebGLUniformLocationWrapper location, Float32ArrayWrapper v);

	public void uniform2fv(WebGLUniformLocationWrapper location, FloatArrayWrapper v);

	public void uniform2i(WebGLUniformLocationWrapper location, int x, int y);

	public void uniform2iv(WebGLUniformLocationWrapper location, Int32ArrayWrapper v);

	public void uniform2iv(WebGLUniformLocationWrapper location, LongArrayWrapper v);

	public void uniform3f(WebGLUniformLocationWrapper location, float x, float y, float z);

	public void uniform3fv(WebGLUniformLocationWrapper location, Float32ArrayWrapper v);

	public void uniform3fv(WebGLUniformLocationWrapper location, FloatArrayWrapper v);

	public void uniform3i(WebGLUniformLocationWrapper location, int x, int y, int z);

	public void uniform3iv(WebGLUniformLocationWrapper location, Int32ArrayWrapper v);

	public void uniform3iv(WebGLUniformLocationWrapper location, LongArrayWrapper v);

	public void uniform4f(WebGLUniformLocationWrapper location, float x, float y, float z, float w);

	public void uniform4fv(WebGLUniformLocationWrapper location, Float32ArrayWrapper v);

	public void uniform4fv(WebGLUniformLocationWrapper location, FloatArrayWrapper v);

	public void uniform4i(WebGLUniformLocationWrapper location, int x, int y, int z, int w);

	public void uniform4iv(WebGLUniformLocationWrapper location, Int32ArrayWrapper v);

	public void uniform4iv(WebGLUniformLocationWrapper location, LongArrayWrapper v);

	public void uniformMatrix2fv(WebGLUniformLocationWrapper location, boolean transpose, Float32ArrayWrapper value);

	public void uniformMatrix2fv(WebGLUniformLocationWrapper location, boolean transpose, FloatArrayWrapper value);

	public void uniformMatrix3fv(WebGLUniformLocationWrapper location, boolean transpose, Float32ArrayWrapper value);

	public void uniformMatrix3fv(WebGLUniformLocationWrapper location, boolean transpose, FloatArrayWrapper value);

	public void uniformMatrix4fv(WebGLUniformLocationWrapper location, boolean transpose, Float32ArrayWrapper value);

	public void uniformMatrix4fv(WebGLUniformLocationWrapper location, boolean transpose, FloatArrayWrapper value);

	public void useProgram(WebGLProgramWrapper program);

	public void validateProgram(WebGLProgramWrapper program);

	public void vertexAttrib1f(int indx, float x);

	public void vertexAttrib1fv(int indx, Float32ArrayWrapper values);

	public void vertexAttrib1fv(int indx, FloatArrayWrapper values);

	public void vertexAttrib2f(int indx, float x, float y);

	public void vertexAttrib2fv(int indx, Float32ArrayWrapper values);

	public void vertexAttrib2fv(int indx, FloatArrayWrapper values);

	public void vertexAttrib3f(int indx, float x, float y, float z);

	public void vertexAttrib3fv(int indx, Float32ArrayWrapper values);

	public void vertexAttrib3fv(int indx, FloatArrayWrapper values);

	public void vertexAttrib4f(int indx, float x, float y, float z, float w);

	public void vertexAttrib4fv(int indx, Float32ArrayWrapper values);

	public void vertexAttrib4fv(int indx, FloatArrayWrapper values);

	public void vertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, long offset);

	public void viewport(int x, int y, int width, int height);

	public void uniform1iv(WebGLUniformLocationWrapper location, int[] v);

	public void uniform1fv(WebGLUniformLocationWrapper loc, float[] v);

	public void uniform2fv(WebGLUniformLocationWrapper loc, float[] v);

	public void uniform2iv(WebGLUniformLocationWrapper loc, int[] v);

	public void uniform3fv(WebGLUniformLocationWrapper loc, float[] v);

	public void uniform3iv(WebGLUniformLocationWrapper loc, int[] v);

	public void uniform4fv(WebGLUniformLocationWrapper loc, float[] v);

	public void uniform4iv(WebGLUniformLocationWrapper loc, int[] v);

	public void uniformMatrix2fv(WebGLUniformLocationWrapper loc, boolean transpose, float[] value);

	public void uniformMatrix3fv(WebGLUniformLocationWrapper loc, boolean transpose, float[] value);

	public void uniformMatrix4fv(WebGLUniformLocationWrapper loc, boolean transpose, float[] value);
}
