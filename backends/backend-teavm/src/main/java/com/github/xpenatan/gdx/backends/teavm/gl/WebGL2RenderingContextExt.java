package com.github.xpenatan.gdx.backends.teavm.gl;

import org.teavm.jso.JSMethod;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.typedarrays.Int32Array;
import org.teavm.jso.typedarrays.Uint32Array;
import org.teavm.jso.webgl.WebGL2RenderingContext;
import org.teavm.jso.webgl.WebGLProgram;
import org.teavm.jso.webgl.WebGLQuery;
import org.teavm.jso.webgl.WebGLSampler;
import org.teavm.jso.webgl.WebGLUniformLocation;

public interface WebGL2RenderingContextExt extends WebGLRenderingContextExt, WebGL2RenderingContext {

    @JSMethod("getActiveUniformBlockParameter")
    int getActiveUniformBlockParameteri(WebGLProgram program, int uniformBlockIndex, int pname);

    @JSMethod("getActiveUniformBlockParameter")
    Uint32Array getActiveUniformBlockParameterv(WebGLProgram program, int uniformBlockIndex, int pname);

    @JSMethod("getActiveUniformBlockParameter")
    boolean getActiveUniformBlockParameterb(WebGLProgram program, int uniformBlockIndex, int pname);

    @JSMethod("getActiveUniforms")
    JSArray<Boolean> getActiveUniformsb(WebGLProgram program, Int32Array uniformIndices, int pname);

    @JSMethod("getActiveUniforms")
    JSArray<Integer> getActiveUniformsi(WebGLProgram program, Int32Array uniformIndices, int pname);

    @JSMethod("getQueryParameter")
    int getQueryParameteri(WebGLQuery query, int pname);

    @JSMethod("getQueryParameter")
    boolean getQueryParameterb(WebGLQuery query, int pname);

    @JSMethod("getSamplerParameter")
    float getSamplerParameterf(WebGLSampler sampler, int pname);

    @JSMethod("getSamplerParameter")
    int getSamplerParameteri(WebGLSampler sampler, int pname);

    void clearBufferuiv(int buffer, int drawbuffer, Int32Array values);

    void uniform1uiv(WebGLUniformLocation location, Int32Array value, int srcOffset, int srcLength);

    void uniform3uiv(WebGLUniformLocation location, Int32Array value, int srcOffset, int srcLength);

    void uniform4uiv(WebGLUniformLocation location, Int32Array value, int srcOffset, int srcLength);

    void texImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, int offset);

    void texSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, int offset);

}
