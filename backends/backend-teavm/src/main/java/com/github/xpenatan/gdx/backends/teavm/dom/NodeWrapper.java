package com.github.xpenatan.gdx.backends.teavm.dom;

/**
 * @author xpenatan
 */
public interface NodeWrapper extends EventTargetWrapper {
    public NodeWrapper getParentNode();

    public void appendChild(NodeWrapper node);
}
