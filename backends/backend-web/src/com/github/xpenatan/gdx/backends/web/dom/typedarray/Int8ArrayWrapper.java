package com.github.xpenatan.gdx.backends.web.dom.typedarray;

import com.badlogic.gdx.utils.ByteArray;

/**
 * @author xpenatan
 */
public interface Int8ArrayWrapper extends ArrayBufferViewWrapper {
	// Int8Array
	public static final int BYTES_PER_ELEMENT = 1;

	public int getLength();

	public byte get(int index);

	public void set(int index, byte value);

	public void set(Int8ArrayWrapper array);

	public void set(Int8ArrayWrapper array, int offset);

	public void set(ByteArray array);

	public void set(ByteArray array, int offset);

	public Int8ArrayWrapper subarray(int start, int end);
}
