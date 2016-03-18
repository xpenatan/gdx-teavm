package com.badlogic.gdx.backends.dragome.js.typedarrays;

import com.dragome.commons.DelegateCode;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsDelegateFactory;

public class FerTypedArraysFactory
{
	@DelegateCode(ignore= true)
	public static <T extends ArrayBufferView> T create(Class<T> type, ArrayBuffer buffer)
	{
		ScriptHelper.put("buffer", buffer, null);
		String script= "new " + type.getSimpleName() + "(buffer.node)";
		Object instance= ScriptHelper.eval(script, null);
		T node= (T) JsDelegateFactory.createFrom(instance, type);
		return node;
	}

	@DelegateCode(ignore= true)
	public static <T extends ArrayBufferView> T create(Class<T> type, ArrayBuffer buffer, int byteOffset)
	{
		ScriptHelper.put("buffer", buffer, null);
		ScriptHelper.put("byteOffset", byteOffset, null);
		String script= "new " + type.getSimpleName() + "(buffer.node, byteOffset);";
		Object instance= ScriptHelper.eval(script, null);
		T node= JsDelegateFactory.createFrom(instance, type);
		return node;
	};

	@DelegateCode(ignore= true)
	public static <T extends ArrayBufferView> T create(Class<T> type, ArrayBuffer buffer, int byteOffset, int length)
	{
		ScriptHelper.put("buffer", buffer, null);
		ScriptHelper.put("byteOffset", byteOffset, null);
		ScriptHelper.put("length", length, null);
		String script= "new " + type.getSimpleName() + "(buffer.node, byteOffset, length);";
		Object instance= ScriptHelper.eval(script, null);
		T node= JsDelegateFactory.createFrom(instance, type);
		return node;
	};

	@DelegateCode(ignore= true)
	public static <T extends ArrayBufferView> T create(Class<T> type, int length)
	{
		ScriptHelper.put("length", length, null);
		String script= "new " + type.getSimpleName() + "(length);";
		Object instance= ScriptHelper.eval(script, null);
		T node= JsDelegateFactory.createFrom(instance, type);
		return node;
	};

	public <T extends ArrayBufferView> T createFloat32Array(Class<T> type, float[] array)
	{
		ScriptHelper.put("array", array, this);
		String script= "new " + type.getSimpleName() + "(array)";
		Object eval= ScriptHelper.eval(script, this);
		return (T) JsDelegateFactory.createFrom(eval, type);
	}


	public Float32Array createFloat32Array(ArrayBuffer buffer)
	{
		return create(Float32Array.class, buffer, 0, getElementCount(buffer.get_byteLength(), Float32Array.BYTES_PER_ELEMENT));
	}

	public Float32Array createFloat32Array(ArrayBuffer buffer, int byteOffset)
	{
		return create(Float32Array.class, buffer, byteOffset, getElementCount(buffer.get_byteLength() - byteOffset, Float32Array.BYTES_PER_ELEMENT));
	}

	public Float32Array createFloat32Array(float[] array)
	{
		Float32Array result= createFloat32Array(array.length);
		result.set(array, 0);
		return result;
	}

	public Float32Array createFloat32Array(int length)
	{
		return createFloat32Array(createArrayBuffer(length * Float32Array.BYTES_PER_ELEMENT));
	}

	/** Get the number of elements in a number of bytes, throwing an exception if it isn't an integral number.
	 *
	 * @param byteLength
	 * @param elemLength length of each element in bytes
	 * @return count of elements
	 * @throws IllegalArgumentException if {@code byteLength} isn't an integral multiple of {@code elemLength} */
	protected static int getElementCount(int byteLength, int elemLength)
	{
		int count= byteLength / elemLength;
		if (count * elemLength != byteLength)
		{
			throw new IllegalArgumentException();
		}
		return count;
	}

	@DelegateCode(ignore = true)
	public static ArrayBuffer createArrayBuffer (int length) {
		ScriptHelper.put("lenght", length, null);
		Object instance = ScriptHelper.eval("new ArrayBuffer(length);", null);
		ArrayBuffer node = JsDelegateFactory.createFrom(instance, ArrayBuffer.class);
		return node;
	}

	public Uint32Array createUint32Array(long[] array)
	{
		int len= array.length;
		double[] temp= new double[len];
		for (int i= 0; i < len; ++i)
		{
			temp[i]= array[i];
		}
		ScriptHelper.put("array", temp, this);
		Object eval= ScriptHelper.eval("new Uint32Array(array)", this);
		return JsDelegateFactory.createFrom(eval, Uint32Array.class);
	}

	protected boolean checkDataViewSupport()
	{
		return ScriptHelper.evalBoolean("!!(window.DataView)", this);
	}

	protected boolean checkUint8ClampedArraySupport()
	{
		return ScriptHelper.evalBoolean("!!(window.Uint8ClampedArray)", this);
	}

	protected boolean runtimeSupportCheck()
	{
		return ScriptHelper.evalBoolean("!!(window.ArrayBuffer)", this);
	}

}
