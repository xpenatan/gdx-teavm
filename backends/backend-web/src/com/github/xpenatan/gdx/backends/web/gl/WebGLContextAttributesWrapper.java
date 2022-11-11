package com.github.xpenatan.gdx.backends.web.gl;

/**
 * @author xpenatan
 */
public interface WebGLContextAttributesWrapper {
    // WebGLContextAttributes
    public boolean getAlpha();

    public void setAlpha(boolean alpha);

    public boolean getDepth();

    public void setDepth(boolean depth);

    public boolean getStencil();

    public void setStencil(boolean stencil);

    public boolean getAntialias();

    public void setAntialias(boolean antialias);

    public boolean getPremultipliedAlpha();

    public void setPremultipliedAlpha(boolean premultipliedAlpha);

    public boolean getPreserveDrawingBuffer();

    public void setPreserveDrawingBuffer(boolean preserveDrawingBuffer);
}
