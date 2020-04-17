package com.github.xpenatan.gdx.backend.web.dom.typedarray;

public interface ObjectArrayWrapper<E> {
	int getLength();
	void setLength(int length);
	E getElement(int index);
	void setElement(int index, E value);
}
