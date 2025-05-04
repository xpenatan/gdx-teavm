package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface DocumentWrapper extends ElementWrapper {

    @JSProperty
    ElementWrapper getDocumentElement();

    @JSProperty
    String getVisibilityState();

    @JSProperty
    String getCompatMode();

    HTMLElementWrapper getElementById(String id);

    NodeWrapper createTextNode(NodeWrapper text);

    HTMLElementWrapper createElement(String value);

    @JSProperty
    NodeWrapper getBody();
}