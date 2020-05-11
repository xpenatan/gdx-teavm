package com.github.xpenatan.gdx.backend.web.dom;

/**
 * @author xpenatan
 */
public interface NodeWrapper extends EventTargetWrapper {
	public NodeWrapper getParentNode();

	public void appendChild(NodeWrapper node);
}
