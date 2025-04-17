package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface HTMLCanvasElementWrapper extends HTMLElementWrapper, EventTargetWrapper {
    @JSProperty
    HTMLDocumentWrapper getOwnerDocument();
    @JSProperty
    int getWidth();
    @JSProperty
    void setWidth(int width);
    @JSProperty
    int getHeight();
    @JSProperty
    void setHeight(int height);
    String toDataURL(String type);
    WebJSObject getContext(String value);
}