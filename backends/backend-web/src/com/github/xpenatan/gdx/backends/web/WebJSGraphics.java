package com.github.xpenatan.gdx.backends.web;

import com.github.xpenatan.gdx.backends.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backends.web.gl.WebGLRenderingContextWrapper;

public interface WebJSGraphics {
    double getNativeScreenDensity();
    int getScreenWidthJSNI();
    int getScreenHeightJSNI();
    boolean setFullscreenJSNI(WebGraphics graphics, HTMLCanvasElementWrapper canvas, int screenWidth, int screenHeight);
    void exitFullscreen();
    boolean isFullscreenJSNI();
    WebGLRenderingContextWrapper getGLContext(HTMLCanvasElementWrapper canvasWrapper, WebApplicationConfiguration config);
}
