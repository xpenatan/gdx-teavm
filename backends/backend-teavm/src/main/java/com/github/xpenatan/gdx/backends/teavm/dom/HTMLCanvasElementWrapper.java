package com.github.xpenatan.gdx.backends.teavm.dom;

/**
 * @author xpenatan
 */
public interface HTMLCanvasElementWrapper extends HTMLElementWrapper, EventTargetWrapper {

    public HTMLDocumentWrapper getOwnerDocument();

    public int getWidth();

    public void setWidth(int width);

    public int getHeight();

    public void setHeight(int height);

    public String toDataURL(String type);

    public WebJSObject getContext(String value);
}
