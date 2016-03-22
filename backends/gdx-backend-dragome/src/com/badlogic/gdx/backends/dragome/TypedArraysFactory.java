package com.badlogic.gdx.backends.dragome;

import org.w3c.dom.typedarray.ArrayBuffer;
import org.w3c.dom.typedarray.ArrayBufferView;

import com.dragome.commons.javascript.ScriptHelper;

public class TypedArraysFactory
{
	public native static <T extends ArrayBufferView> T createInstanceOf(Class<T> type, ArrayBuffer buffer);
	public native static <T extends ArrayBufferView> T createInstanceOf(Class<T> type, ArrayBuffer buffer, int byteOffset);
	public native static <T extends ArrayBufferView> T createInstanceOf(Class<T> type, ArrayBuffer buffer, int byteOffset, int length);
	public native static <T extends ArrayBufferView> T createInstanceOf(Class<T> type, int length);
	public native static <T extends ArrayBufferView> T createInstanceOf(Class<T> type, float[] array);

	public boolean checkDataViewSupport()
	{
		return ScriptHelper.evalBoolean("!!(window.DataView)", this);
	}

	public boolean checkUint8ClampedArraySupport()
	{
		return ScriptHelper.evalBoolean("!!(window.Uint8ClampedArray)", this);
	}

	public boolean runtimeSupportCheck()
	{
		return ScriptHelper.evalBoolean("!!(window.ArrayBuffer)", this);
	}
}
