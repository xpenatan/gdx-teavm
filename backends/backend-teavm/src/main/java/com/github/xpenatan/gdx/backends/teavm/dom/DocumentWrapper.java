package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface DocumentWrapper extends ElementWrapper {

    public ElementWrapper getDocumentElement();

    @JSProperty
    public String getVisibilityState();

    public String getCompatMode();

    HTMLElementWrapper getElementById(String id);

    public NodeWrapper createTextNode(NodeWrapper text);

    public HTMLElementWrapper createElement(String value);

    public NodeWrapper getBody();
}
