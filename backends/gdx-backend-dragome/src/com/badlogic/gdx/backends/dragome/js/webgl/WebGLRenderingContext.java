/*******************************************************************************
 * Copyright 2016 Natan Guilherme.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.backends.dragome.js.webgl;

import org.w3c.dom.html.HTMLImageElement;

import com.badlogic.gdx.backends.dragome.js.typedarrays.ArrayBuffer;
import com.badlogic.gdx.backends.dragome.js.typedarrays.ArrayBufferView;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Float32Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Int32Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.SharedArrayBuffer;
import com.dragome.web.html.dom.html5canvas.interfaces.HTMLCanvasElement;
import com.dragome.web.html.dom.html5canvas.interfaces.ImageData;

/** From https://dxr.mozilla.org/mozilla-central/source/dom/webidl/WebGLRenderingContext.webidl <br>
 * Match the same methods to use with javascript.
 *
 * @author xpenatan */
public interface WebGLRenderingContext {
	public static final int UNSIGNED_BYTE = 0x1401;
	public static final int RGBA = 0x1908;

	/* WebGL-specific enums */
	public static final int UNPACK_FLIP_Y_WEBGL = 0x9240;
	public static final int UNPACK_PREMULTIPLY_ALPHA_WEBGL = 0x9241;
	public static final int CONTEXT_LOST_WEBGL = 0x9242;

	public WebGLContextAttributes getContextAttributes ();

	public boolean isContextLost ();

	public Object getSupportedExtensions ();

	public Object getExtension (String name);

	public void activeTexture (int texture);

	public void attachShader (WebGLProgram program, WebGLShader shader);

	public void bindAttribLocation (WebGLProgram program, int index, String name);

	public void bindBuffer (int target, WebGLBuffer buffer);

	public void bindFramebuffer (int target, WebGLFramebuffer framebuffer);

	public void bindRenderbuffer (int target, WebGLRenderbuffer renderbuffer);

	public void bindTexture (int target, WebGLTexture texture);

	public void blendColor (float red, float green, float blue, float alpha);

	public void blendEquation (int mode);

	public void blendEquationSeparate (int modeRGB, int modeAlpha);

	public void blendFunc (int sfactor, int dfactor);

	public void blendFuncSeparate (int srcRGB, int dstRGB, int srcAlpha, int dstAlpha);

	public void bufferData (int target, int size, int usage);

	public void bufferData (int target, ArrayBufferView data, int usage);

	public void bufferData (int target, ArrayBuffer data, int usage);

	public void bufferData (int target, SharedArrayBuffer data, int usage);

	public void bufferSubData (int target, int offset, ArrayBufferView data);

	public void bufferSubData (int target, int offset, ArrayBuffer data);

	public void bufferSubData (int target, int offset, SharedArrayBuffer data);

	public int checkFramebufferStatus (int target);

	public void clear (int mask);

	public void clearColor (float red, float green, float blue, float alpha);

	public void clearDepth (float depth);

	public void clearStencil (int s);

	public void colorMask (boolean red, boolean green, boolean blue, boolean alpha);

	public void compileShader (WebGLShader shader);

	public void compressedTexImage2D (int target, int level, int internalformat, int width, int height, int border,
		ArrayBufferView data);

	public void compressedTexSubImage2D (int target, int level, int xoffset, int yoffset, int width, int height, int format,
		ArrayBufferView data);

	public void copyTexImage2D (int target, int level, int internalformat, int x, int y, int width, int height, int border);

	public void copyTexSubImage2D (int target, int level, int xoffset, int yoffset, int x, int y, int width, int height);

	public WebGLBuffer createBuffer ();

	public WebGLFramebuffer createFramebuffer ();

	public WebGLProgram createProgram ();

	public WebGLRenderbuffer createRenderbuffer ();

	public WebGLShader createShader (int type);

	public WebGLTexture createTexture ();

	public void cullFace (int mode);

	public void deleteBuffer (WebGLBuffer buffer);

	public void deleteFramebuffer (WebGLFramebuffer framebuffer);

	public void deleteProgram (WebGLProgram program);

	public void deleteRenderbuffer (WebGLRenderbuffer renderbuffer);

	public void deleteShader (WebGLShader shader);

	public void deleteTexture (WebGLTexture texture);

	public void depthFunc (int func);

	public void depthMask (boolean flag);

	public void depthRange (float zNear, float zFar);

	public void detachShader (WebGLProgram program, WebGLShader shader);

	public void disable (int cap);

	public void disableVertexAttribArray (int index);

	public void drawArrays (int mode, int first, int count);

	public void drawElements (int mode, int count, int type, int offset);

	public void enable (int cap);

	public void enableVertexAttribArray (int index);

	public void finish ();

	public void flush ();

	public void framebufferRenderbuffer (int target, int attachment, int renderbuffertarget, WebGLRenderbuffer renderbuffer);

	public void framebufferTexture2D (int target, int attachment, int textarget, WebGLTexture texture, int level);

	public void frontFace (int mode);

	public void generateMipmap (int target);

	public WebGLActiveInfo getActiveAttrib (WebGLProgram program, int index);

	public WebGLActiveInfo getActiveUniform (WebGLProgram program, int index);

	public Object getAttachedShaders (WebGLProgram program);

	public int getAttribLocation (WebGLProgram program, String name);

	public Object getBufferParameter (int target, int pname);

	public Object getParameter (int pname);

	public int getError ();

	public Object getFramebufferAttachmentParameter (int target, int attachment, int pname);

	public Object getProgramParameter (WebGLProgram program, int pname);

	public String getProgramInfoLog (WebGLProgram program);

	public Object getRenderbufferParameter (int target, int pname);

	public Object getShaderParameter (WebGLShader shader, int pname);

	public WebGLShaderPrecisionFormat getShaderPrecisionFormat (int shadertype, int precisiontype);

	public String getShaderInfoLog (WebGLShader shader);

	public String getShaderSource (WebGLShader shader);

	public Object getTexParameter (int target, int pname);

	public Object getUniform (WebGLProgram program, WebGLUniformLocation location);

	public WebGLUniformLocation getUniformLocation (WebGLProgram program, String name);

	public Object getVertexAttrib (int index, int pname);

	public int getVertexAttribOffset (int index, int pname);

	public void hint (int target, int mode);

	public boolean isBuffer (WebGLBuffer buffer);

	public boolean isEnabled (int cap);

	public boolean isFramebuffer (WebGLFramebuffer framebuffer);

	public boolean isProgram (WebGLProgram program);

	public boolean isRenderbuffer (WebGLRenderbuffer renderbuffer);

	public boolean isShader (WebGLShader shader);

	public boolean isTexture (WebGLTexture texture);

	public void lineWidth (float width);

	public void linkProgram (WebGLProgram program);

	public void pixelStorei (int pname, int param);

	public void polygonOffset (float factor, float units);

	public void readPixels (int x, int y, int width, int height, int format, int type, ArrayBufferView pixels);

	public void renderbufferStorage (int target, int internalformat, int width, int height);

	public void sampleCoverage (float value, boolean invert);

	public void scissor (int x, int y, int width, int height);

	public void shaderSource (WebGLShader shader, String source);

	public void stencilFunc (int func, int ref, int mask);

	public void stencilFuncSeparate (int face, int func, int ref, int mask);

	public void stencilMask (int mask);

	public void stencilMaskSeparate (int face, int mask);

	public void stencilOp (int fail, int zfail, int zpass);

	public void stencilOpSeparate (int face, int fail, int zfail, int zpass);

	public void texImage2D (int target, int level, int internalformat, int width, int height, int border, int format, int type,
		ArrayBufferView pixels);

	public void texImage2D (int target, int level, int internalformat, int format, int type, ImageData pixels);

	public void texImage2D (int target, int level, int internalformat, int format, int type, HTMLImageElement image);

	public void texImage2D (int target, int level, int internalformat, int format, int type, HTMLCanvasElement canvas);

	public void texImage2D (int target, int level, int internalformat, int format, int type, HTMLVideoElement video);

	public void texParameterf (int target, int pname, float param);

	public void texParameteri (int target, int pname, int param);

	public void texSubImage2D (int target, int level, int xoffset, int yoffset, int width, int height, int format, int type,
		ArrayBufferView pixels);

	public void texSubImage2D (int target, int level, int xoffset, int yoffset, int format, int type, ImageData pixels);

	public void texSubImage2D (int target, int level, int xoffset, int yoffset, int format, int type, HTMLImageElement image); // May
																																										// throw
																																										// DOMException

	public void texSubImage2D (int target, int level, int xoffset, int yoffset, int format, int type, HTMLCanvasElement canvas);

	public void texSubImage2D (int target, int level, int xoffset, int yoffset, int format, int type, HTMLVideoElement video);

	public void uniform1f (WebGLUniformLocation location, float x);

	public void uniform1fv (WebGLUniformLocation location, Float32Array v);

	public void uniform1fv (WebGLUniformLocation location, Object v);

	public void uniform1i (WebGLUniformLocation location, int x);

	public void uniform1iv (WebGLUniformLocation location, Int32Array v);

	public void uniform1iv (WebGLUniformLocation location, Object v);

	public void uniform2f (WebGLUniformLocation location, float x, float y);

	public void uniform2fv (WebGLUniformLocation location, Float32Array v);

	public void uniform2fv (WebGLUniformLocation location, Object v);

	public void uniform2i (WebGLUniformLocation location, int x, int y);

	public void uniform2iv (WebGLUniformLocation location, Int32Array v);

	public void uniform2iv (WebGLUniformLocation location, Object v);

	public void uniform3f (WebGLUniformLocation location, float x, float y, float z);

	public void uniform3fv (WebGLUniformLocation location, Float32Array v);

	public void uniform3fv (WebGLUniformLocation location, Object v);

	public void uniform3i (WebGLUniformLocation location, int x, int y, int z);

	public void uniform3iv (WebGLUniformLocation location, Int32Array v);

	public void uniform3iv (WebGLUniformLocation location, Object v);

	public void uniform4f (WebGLUniformLocation location, float x, float y, float z, float w);

	public void uniform4fv (WebGLUniformLocation location, Float32Array v);

	public void uniform4fv (WebGLUniformLocation location, Object v);

	public void uniform4i (WebGLUniformLocation location, int x, int y, int z, int w);

	public void uniform4iv (WebGLUniformLocation location, Int32Array v);

	public void uniform4iv (WebGLUniformLocation location, Object v);

	public void uniformMatrix2fv (WebGLUniformLocation location, boolean transpose, Float32Array value);

	public void uniformMatrix2fv (WebGLUniformLocation location, boolean transpose, Object value);

	public void uniformMatrix3fv (WebGLUniformLocation location, boolean transpose, Float32Array value);

	public void uniformMatrix3fv (WebGLUniformLocation location, boolean transpose, Object value);

	public void uniformMatrix4fv (WebGLUniformLocation location, boolean transpose, Float32Array value);

	public void uniformMatrix4fv (WebGLUniformLocation location, boolean transpose, float[] value);

	public void useProgram (WebGLProgram program);

	public void validateProgram (WebGLProgram program);

	public void vertexAttrib1f (int indx, float x);

	public void vertexAttrib1fv (int indx, Float32Array values);

	public void vertexAttrib1fv (int indx, Object values);

	public void vertexAttrib2f (int indx, float x, float y);

	public void vertexAttrib2fv (int indx, Float32Array values);

	public void vertexAttrib2fv (int indx, Object values);

	public void vertexAttrib3f (int indx, float x, float y, float z);

	public void vertexAttrib3fv (int indx, Float32Array values);

	public void vertexAttrib3fv (int indx, Object values);

	public void vertexAttrib4f (int indx, float x, float y, float z, float w);

	public void vertexAttrib4fv (int indx, Float32Array values);

	public void vertexAttrib4fv (int indx, Object values);

	public void vertexAttribPointer (int indx, int size, int type, boolean normalized, int stride, int offset);

	public void viewport (int x, int y, int width, int height);
}
