package com.github.xpenatan.gdx.backend.web.dom;

public interface CanvasPixelArrayWrapper {
	// CanvasPixelArray
	public int getLength();
	public byte getElement(int index);
	public void setElement(int index, byte value);
}
