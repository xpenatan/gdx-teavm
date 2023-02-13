package com.github.xpenatan.gdx.backends.teavm.util;

import com.github.xpenatan.gdx.backends.teavm.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaGraphics;
import com.github.xpenatan.gdx.backends.teavm.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGLRenderingContextWrapper;

@Deprecated
public interface WebJSGraphics {
    double getNativeScreenDensity();

    int getScreenWidthJSNI();

    int getScreenHeightJSNI();

    boolean setFullscreenJSNI(TeaGraphics graphics, HTMLCanvasElementWrapper canvas, int screenWidth, int screenHeight);

    void exitFullscreen();

    boolean isFullscreenJSNI();

    WebGLRenderingContextWrapper getGLContext(HTMLCanvasElementWrapper canvasWrapper, WebApplicationConfiguration config);
}
