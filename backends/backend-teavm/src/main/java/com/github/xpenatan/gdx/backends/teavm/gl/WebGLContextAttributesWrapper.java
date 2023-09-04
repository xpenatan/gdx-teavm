package com.github.xpenatan.gdx.backends.teavm.gl;

import org.teavm.jso.JSObject;

/**
 * @author xpenatan
 */
public interface WebGLContextAttributesWrapper extends JSObject {
    // WebGLContextAttributes
    boolean getAlpha();

    void setAlpha(boolean alpha);

    boolean getDepth();

    void setDepth(boolean depth);

    boolean getStencil();

    void setStencil(boolean stencil);

    boolean getAntialias();

    void setAntialias(boolean antialias);

    boolean getPremultipliedAlpha();

    void setPremultipliedAlpha(boolean premultipliedAlpha);

    boolean getPreserveDrawingBuffer();

    void setPreserveDrawingBuffer(boolean preserveDrawingBuffer);
}
