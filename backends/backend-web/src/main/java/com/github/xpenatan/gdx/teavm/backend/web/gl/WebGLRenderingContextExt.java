package com.github.xpenatan.gdx.teavm.backend.web.gl;

import org.teavm.jso.JSMethod;
import org.teavm.jso.dom.html.HTMLVideoElement;
import org.teavm.jso.webgl.WebGLRenderingContext;

public interface WebGLRenderingContextExt extends WebGLRenderingContext {
    void texImage2D(int target, int level, int internalformat, int format, int type, HTMLVideoElement video);

    // Returning an int but GL type is GLint64 and GL30 interface uses LongBuffer. JS does not support long
    // so we return an int, not sure how else to preserve the long values at this time.
    @JSMethod("getParameter")
    int getParameteri64(int pname);
}
