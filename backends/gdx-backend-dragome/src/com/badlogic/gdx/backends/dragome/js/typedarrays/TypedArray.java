package com.badlogic.gdx.backends.dragome.js.typedarrays;

public interface TypedArray<ArrayType> extends ArrayBufferView
{
	int getLength();
	void set(ArrayType array, int offset);
	ArrayType subarray(int begin);
	ArrayType subarray(int begin, int end);
}
