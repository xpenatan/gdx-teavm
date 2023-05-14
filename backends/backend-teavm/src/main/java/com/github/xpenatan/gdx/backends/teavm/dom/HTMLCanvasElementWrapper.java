package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSObject;

/**
 * @author xpenatan
 */
public interface HTMLCanvasElementWrapper extends HTMLElementWrapper, EventTargetWrapper, JSObject {

    HTMLDocumentWrapper getOwnerDocument();

    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);

    String toDataURL(String type);

    WebJSObject getContext(String value);
}
