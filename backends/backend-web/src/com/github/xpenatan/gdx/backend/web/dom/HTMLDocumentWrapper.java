package com.github.xpenatan.gdx.backend.web.dom;

/**
 * @author xpenatan
 */
public interface HTMLDocumentWrapper {

	public ElementWrapper getDocumentElement();

	public HTMLCanvasElementWrapper getCanvas(String id);
}
