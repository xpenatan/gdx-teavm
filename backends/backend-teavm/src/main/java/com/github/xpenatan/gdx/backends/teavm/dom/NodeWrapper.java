package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface NodeWrapper extends EventTargetWrapper {
    @JSProperty
    NodeWrapper getParentNode();

    void appendChild(NodeWrapper node);
}