package com.github.xpenatan.gdx.backends.teavm.gl;

import com.github.xpenatan.gdx.backends.teavm.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.HTMLImageElementWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.HTMLVideoElementWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.ImageDataWrapper;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.typedarrays.ArrayBufferView;
import org.teavm.jso.typedarrays.Int32Array;
import org.teavm.jso.webgl.WebGLBuffer;
import org.teavm.jso.webgl.WebGLProgram;
import org.teavm.jso.webgl.WebGLRenderingContext;
import org.teavm.jso.webgl.WebGLTexture;
import org.teavm.jso.webgl.WebGLUniformLocation;

/**
 * @author xpenatan
 */
public interface WebGL2RenderingContextWrapper extends WebGLRenderingContext {

    void beginQuery(int target, WebGLQueryWrapper query);

    void beginTransformFeedback(int primitiveMode);

    void bindBufferBase(int target, int index, WebGLBuffer buffer);

    void bindBufferRange(int target, int index, WebGLBuffer buffer, int offset, int size);

    void bindSampler(int unit, WebGLSamplerWrapper sampler);

    void bindTransformFeedback(int target, WebGLTransformFeedbackWrapper id);

    void bindVertexArray(WebGLVertexArrayObjectWrapper array);

    void blitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter);

    void clearBufferfi(int buffer, int drawbuffer, float depth, int stencil);

    void clearBufferfv(int buffer, int drawbuffer, JSObject value);

    void clearBufferiv(int buffer, int drawbuffer, JSObject value);

    void clearBufferuiv(int buffer, int drawbuffer, JSObject value);

// Commented out in GL30 interface
// int clientWaitSync (WebGLSync sync, int flags, /* GLint64 */int timeout)/*-{
// throw "UnsupportedOperation";
// }-*/;

// Commented out in GL30 interface
// void compressedTexImage3D (int target, int level, int internalformat, int width, int height, int depth,
// int border, ArrayBufferView data)/*-{
// throw "UnsupportedOperation";
// }-*/;

// Commented out in GL30 interface
// void compressedTexSubImage3D (int target, int level, int xoffset, int yoffset, int zoffset, int width,
// int height, int depth, int format, ArrayBufferView data)/*-{
// throw "UnsupportedOperation";
//
// }-*/;

    void copyBufferSubData(int readTarget, int writeTarget, int readOffset, int writeOffset, int size);

    void copyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width, int height);

    WebGLQueryWrapper createQuery();

    WebGLSamplerWrapper createSampler();

    WebGLTransformFeedbackWrapper createTransformFeedback();

    WebGLVertexArrayObjectWrapper createVertexArray();

    void deleteQuery(WebGLQueryWrapper query);

    void deleteSampler(WebGLSamplerWrapper sampler);

// Commented out in GL30 interface
// void deleteSync (WebGLSync sync)/*-{
// this.deleteSync(sync);
// }-*/;

    void deleteTransformFeedback(WebGLTransformFeedbackWrapper transformFeedback);

    void deleteVertexArray(WebGLVertexArrayObjectWrapper vertexArray);

    void drawArraysInstanced(int mode, int first, int count, int instanceCount);

    void drawBuffers(Int32Array buffers);

    void drawElementsInstanced(int mode, int count, int type, int offset, int instanceCount);

    void drawRangeElements(int mode, int start, int end, int count, int type, int offset);

    void texImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, int offset);

    void endQuery(int target);

    void endTransformFeedback();

// Commented out in GL30 interface
// WebGLSync fenceSync (int condition, int flags)/*-{
// throw "UnsupportedOperation";
//
// }-*/;

    void framebufferTextureLayer(int target, int attachment, WebGLTexture texture, int level, int layer);

    String getActiveUniformBlockName(WebGLProgram program, int uniformBlockIndex);

    int getActiveUniformBlockParameteri(WebGLProgram program, int uniformBlockIndex, int pname);

    <T extends ArrayBufferView> T getActiveUniformBlockParameterv(WebGLProgram program, int uniformBlockIndex, int pname);

    boolean getActiveUniformBlockParameterb(WebGLProgram program, int uniformBlockIndex, int pname);

    JSArray<Integer> getActiveUniformsi(WebGLProgram program, Int32Array uniformIndices, int pname);

    JSArray<Boolean> getActiveUniformsb(WebGLProgram program, Int32Array uniformIndices, int pname);

    int getFragDataLocation(WebGLProgram program, String name);

    // Returning an int but GL type is GLint64 and GL30 interface uses LongBuffer. JS does not support long
    // so we return an int, not sure how else to preserve the long values at this time.
    int getParameteri64(int pname);

    WebGLQueryWrapper getQuery(int target, int pname);

    boolean getQueryParameterb(WebGLQueryWrapper query, int pname);

    int getQueryParameteri(WebGLQueryWrapper query, int pname);

    float getSamplerParameterf(WebGLSamplerWrapper sampler, int pname);

    int getSamplerParameteri(WebGLSamplerWrapper sampler, int pname);

// Commented out in GL30 interface
// WebGLActiveInfo getTransformFeedbackVarying (WebGLProgram program, int index)/*-{
// throw "UnsupportedOperation";
// }-*/;

    int getUniformBlockIndex(WebGLProgram program, String uniformBlockName);

    JSArray<Integer> getUniformIndices(WebGLProgram program, String[] uniformNames);

    void invalidateFramebuffer(int target, Int32Array attachments);

    void invalidateSubFramebuffer(int target, Int32Array attachments, int x, int y, int width, int height);

    boolean isQuery(WebGLQueryWrapper query);

    boolean isSampler(WebGLSamplerWrapper sampler);

// Commented out in GL30 interface
// boolean isSync (WebGLSync sync)/*-{
// return this.isSync(sync);
// }-*/;

    boolean isTransformFeedback(WebGLTransformFeedbackWrapper transformFeedback);

    boolean isVertexArray(WebGLVertexArrayObjectWrapper vertexArray);

    void pauseTransformFeedback();

    void readBuffer(int src);

    void renderbufferStorageMultisample(int target, int samples, int internalformat, int width, int height);

    void resumeTransformFeedback();

    void samplerParameterf(WebGLSamplerWrapper sampler, int pname, float param);

    void samplerParameteri(WebGLSamplerWrapper sampler, int pname, int param);

    void texImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, int offset);

    void texImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, ArrayBufferView pixels);

    void texImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, ImageDataWrapper pixels);

    void texImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, HTMLImageElementWrapper image);

    void texImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, HTMLCanvasElementWrapper canvas);

    void texImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, HTMLVideoElementWrapper video);

// Commented out in GL30 interface
// void texStorage2D (int target, int levels, int internalformat, int width, int height)/*-{
// this.texStorage2D(target, levels, internalformat, width, height)
// }-*/;

// Commented out in GL30 interface
// void texStorage3D (int target, int levels, int internalformat, int width, int height, int depth)/*-{
// throw "UnsupportedOperation";
// }-*/;

    void texSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, ArrayBufferView pixels);

    void texSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, HTMLCanvasElementWrapper canvas);

    void texSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, int offset);

    void texSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, int offset);

    void transformFeedbackVaryings(WebGLProgram program, String[] varyings, int bufferMode);

// Commented out in GL30 interface
// void uniform1ui (WebGLUniformLocation location, int v0)/*-{
// this.uniform1ui(location, v0);
// }-*/;

    void uniform1uiv(WebGLUniformLocation location, JSObject value, int srcOffset, int srcLength);

    void uniform3uiv(WebGLUniformLocation location, JSObject value, int srcOffset, int srcLength);

    void uniform4uiv(WebGLUniformLocation location, JSObject value, int srcOffset, int srcLength);

    void uniformBlockBinding(WebGLProgram program, int uniformBlockIndex, int uniformBlockBinding);

    void uniformMatrix2x3fv(WebGLUniformLocation location, boolean transpose, JSObject value);


    void uniformMatrix2x4fv(WebGLUniformLocation location, boolean transpose, JSObject value, int srcOffset, int srcLength);

    void uniformMatrix3x2fv(WebGLUniformLocation location, boolean transpose, JSObject value, int srcOffset, int srcLength);

    void uniformMatrix3x4fv(WebGLUniformLocation location, boolean transpose, JSObject value, int srcOffset, int srcLength);

    void uniformMatrix4x2fv(WebGLUniformLocation location, boolean transpose, JSObject value, int srcOffset, int srcLength);

    void uniformMatrix4x3fv(WebGLUniformLocation location, boolean transpose, JSObject value, int srcOffset, int srcLength);

    void vertexAttribDivisor(int index, int divisor);

    void vertexAttribI4i(int index, int x, int y, int z, int w);

// Commented out in GL30 interface
// void vertexAttribI4iv (int index, VertexAttribIVSource values)/*-{
// throw "UnsupportedOperation";
// }-*/;

    void vertexAttribI4ui(int index, int x, int y, int z, int w);

// Commented out in GL30 interface
// void vertexAttribI4uiv (int index, VertexAttribUIVSource values)/*-{
// throw "UnsupportedOperation";
// }-*/;

    void vertexAttribIPointer(int index, int size, int type, int stride, int offset);
}