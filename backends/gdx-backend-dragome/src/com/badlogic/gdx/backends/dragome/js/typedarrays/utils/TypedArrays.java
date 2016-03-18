package com.badlogic.gdx.backends.dragome.js.typedarrays.utils;

import com.badlogic.gdx.backends.dragome.js.typedarrays.ArrayBuffer;
import com.badlogic.gdx.backends.dragome.js.typedarrays.DataView;

public class TypedArrays
{
	public abstract static class Impl
	{
		public abstract ArrayBuffer createArrayBuffer(int length);

		public DataView createDataView(ArrayBuffer buffer)
		{
			return createDataView(buffer, 0, buffer.get_byteLength());
		}

		public DataView createDataView(ArrayBuffer buffer, int offset)
		{
			return createDataView(buffer, offset, buffer.get_byteLength() - offset);
		}

		public abstract DataView createDataView(ArrayBuffer buffer, int byteOffset, int byteLength);
	}
}
