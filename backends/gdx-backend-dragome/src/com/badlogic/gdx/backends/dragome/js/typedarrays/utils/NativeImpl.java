/*******************************************************************************
 * Copyright 2016 Natan Guilherme.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.backends.dragome.js.typedarrays.utils;

import com.badlogic.gdx.backends.dragome.js.typedarrays.ArrayBuffer;
import com.badlogic.gdx.backends.dragome.js.typedarrays.DataView;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Float32Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Float64Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Int16Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Int32Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Int8Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Uint16Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Uint32Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Uint8Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Uint8ClampedArray;
import com.badlogic.gdx.backends.dragome.js.typedarrays.utils.TypedArrays.Impl;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsDelegateFactory;

public class NativeImpl extends Impl {

	@Override
	public ArrayBuffer createArrayBuffer (int length) {
		return ArrayBuffer.create(length);
	}

	@Override
	public DataView createDataView (ArrayBuffer buffer) {
		return DataView.create(buffer);
	}

	@Override
	public DataView createDataView (ArrayBuffer buffer, int byteOffset) {
		return DataView.create(buffer, byteOffset);
	}

	@Override
	public DataView createDataView (ArrayBuffer buffer, int byteOffset, int byteLength) {
		return DataView.create(buffer, byteOffset, byteLength);
	}

	@Override
	public Float32Array createFloat32Array (ArrayBuffer buffer) {
		return Float32Array.create(buffer);
	}

	@Override
	public Float32Array createFloat32Array (ArrayBuffer buffer, int byteOffset) {
		return Float32Array.create(buffer, byteOffset);
	}

	@Override
	public Float32Array createFloat32Array (ArrayBuffer buffer, int byteOffset, int length) {
		return Float32Array.create(buffer, byteOffset, length);
	}

	@Override
	public Float32Array createFloat32Array (float[] array) {
		ScriptHelper.put("array", array, this);
		Object eval = ScriptHelper.eval("new Float32Array(array)", this);
		return JsDelegateFactory.createFrom(eval, Float32Array.class);
	}

	@Override
	public Float32Array createFloat32Array (int length) {
		return Float32Array.create(length);
	}

	@Override
	public Float64Array createFloat64Array (ArrayBuffer buffer) {
		return Float64Array.create(buffer);
	}

	@Override
	public Float64Array createFloat64Array (ArrayBuffer buffer, int byteOffset) {
		return Float64Array.create(buffer, byteOffset);
	}

	@Override
	public Float64Array createFloat64Array (ArrayBuffer buffer, int byteOffset, int length) {
		return Float64Array.create(buffer, byteOffset, length);
	}

	@Override
	public Float64Array createFloat64Array (double[] array) {
		ScriptHelper.put("array", array, this);
		Object eval = ScriptHelper.eval("new Float64Array(array)", this);
		return JsDelegateFactory.createFrom(eval, Float64Array.class);
	}

	@Override
	public Float64Array createFloat64Array (int length) {
		return Float64Array.create(length);
	}

	@Override
	public Int16Array createInt16Array (ArrayBuffer buffer) {
		return Int16Array.create(buffer);
	}

	@Override
	public Int16Array createInt16Array (ArrayBuffer buffer, int byteOffset) {
		return Int16Array.create(buffer, byteOffset);
	}

	@Override
	public Int16Array createInt16Array (ArrayBuffer buffer, int byteOffset, int length) {
		return Int16Array.create(buffer, byteOffset, length);
	}

	@Override
	public Int16Array createInt16Array (int length) {
		return Int16Array.create(length);
	}

	@Override
	public Int16Array createInt16Array (short[] array) {
		ScriptHelper.put("array", array, this);
		Object eval = ScriptHelper.eval("new Int16Array(array)", this);
		return JsDelegateFactory.createFrom(eval, Int16Array.class);
	}

	@Override
	public Int32Array createInt32Array (ArrayBuffer buffer) {
		return Int32Array.create(buffer);
	}

	@Override
	public Int32Array createInt32Array (ArrayBuffer buffer, int byteOffset) {
		return Int32Array.create(buffer, byteOffset);
	}

	@Override
	public Int32Array createInt32Array (ArrayBuffer buffer, int byteOffset, int length) {
		return Int32Array.create(buffer, byteOffset, length);
	}

	@Override
	public Int32Array createInt32Array (int length) {
		return Int32Array.create(length);
	}

	@Override
	public Int32Array createInt32Array (int[] array) {
		ScriptHelper.put("array", array, this);
		Object eval = ScriptHelper.eval("new Int32Array(array)", this);
		return JsDelegateFactory.createFrom(eval, Int32Array.class);
	}

	@Override
	public Int8Array createInt8Array (ArrayBuffer buffer) {
		return Int8Array.create(buffer);
	}

	@Override
	public Int8Array createInt8Array (ArrayBuffer buffer, int byteOffset) {
		return Int8Array.create(buffer, byteOffset);
	}

	@Override
	public Int8Array createInt8Array (ArrayBuffer buffer, int byteOffset, int length) {
		return Int8Array.create(buffer, byteOffset, length);
	}

	@Override
	public Int8Array createInt8Array (byte[] array) {
		ScriptHelper.put("array", array, this);
		Object eval = ScriptHelper.eval("new Int8Array(array)", this);
		return JsDelegateFactory.createFrom(eval, Int8Array.class);
	}

	@Override
	public Int8Array createInt8Array (int length) {
		return Int8Array.create(length);
	}

	@Override
	public Uint16Array createUint16Array (ArrayBuffer buffer) {
		return Uint16Array.create(buffer);
	}

	@Override
	public Uint16Array createUint16Array (ArrayBuffer buffer, int byteOffset) {
		return Uint16Array.create(buffer, byteOffset);
	}

	@Override
	public Uint16Array createUint16Array (ArrayBuffer buffer, int byteOffset, int length) {
		return Uint16Array.create(buffer, byteOffset, length);
	}

	@Override
	public Uint16Array createUint16Array (int length) {
		return Uint16Array.create(length);
	}

	@Override
	public Uint16Array createUint16Array (int[] array) {
		ScriptHelper.put("array", array, this);
		Object eval = ScriptHelper.eval("new Uint16Array(array)", this);
		return JsDelegateFactory.createFrom(eval, Uint16Array.class);
	}

	@Override
	public Uint32Array createUint32Array (ArrayBuffer buffer) {
		return Uint32Array.create(buffer);
	}

	@Override
	public Uint32Array createUint32Array (ArrayBuffer buffer, int byteOffset) {
		return Uint32Array.create(buffer, byteOffset);
	}

	@Override
	public Uint32Array createUint32Array (ArrayBuffer buffer, int byteOffset, int length) {
		return Uint32Array.create(buffer, byteOffset, length);
	}

	@Override
	public Uint32Array createUint32Array (double[] array) {
		ScriptHelper.put("array", array, this);
		Object eval = ScriptHelper.eval("new Uint32Array(array)", this);
		return JsDelegateFactory.createFrom(eval, Uint32Array.class);
	}

	@Override
	public Uint32Array createUint32Array (int length) {
		return Uint32Array.create(length);
	}

	@Override
	public Uint32Array createUint32Array (long[] array) {
		int len = array.length;
		double[] temp = new double[len];
		for (int i = 0; i < len; ++i) {
			temp[i] = array[i];
		}
		ScriptHelper.put("array", temp, this);
		Object eval = ScriptHelper.eval("new Uint32Array(array)", this);
		return JsDelegateFactory.createFrom(eval, Uint32Array.class);
	}

	@Override
	public Uint8Array createUint8Array (ArrayBuffer buffer) {
		return Uint8Array.create(buffer);
	}

	@Override
	public Uint8Array createUint8Array (ArrayBuffer buffer, int byteOffset) {
		return Uint8Array.create(buffer, byteOffset);
	}

	@Override
	public Uint8Array createUint8Array (ArrayBuffer buffer, int byteOffset, int length) {
		return Uint8Array.create(buffer, byteOffset, length);
	}

	@Override
	public Uint8Array createUint8Array (int length) {
		return Uint8Array.create(length);
	}

	@Override
	public Uint8Array createUint8Array (short[] array) {
		ScriptHelper.put("array", array, this);
		Object eval = ScriptHelper.eval("new Uint8Array(array)", this);
		return JsDelegateFactory.createFrom(eval, Uint8Array.class);
	}

	@Override
	public Uint8ClampedArray createUint8ClampedArray (ArrayBuffer buffer) {
			return Uint8ClampedArray.createClamped(buffer);
	}

	@Override
	public Uint8ClampedArray createUint8ClampedArray (ArrayBuffer buffer, int byteOffset) {
			return Uint8ClampedArray.createClamped(buffer, byteOffset);
	}

	@Override
	public Uint8ClampedArray createUint8ClampedArray (ArrayBuffer buffer, int byteOffset, int length) {
			return Uint8ClampedArray.createClamped(buffer, byteOffset, length);
	}

	@Override
	public Uint8ClampedArray createUint8ClampedArray (int length) {
			return Uint8ClampedArray.createClamped(length);
	}

	@Override
	public Uint8ClampedArray createUint8ClampedArray (short[] array) {
			return Uint8ClampedArray.createClamped(array);
	}

	protected boolean checkDataViewSupport () 
	{
		return ScriptHelper.evalBoolean("!!(window.DataView)", this);
	}
	
	
	protected boolean checkUint8ClampedArraySupport ()
	{
		return ScriptHelper.evalBoolean("!!(window.Uint8ClampedArray)", this);
	}

	@Override
	protected boolean runtimeSupportCheck ()
	{
		return ScriptHelper.evalBoolean("!!(window.ArrayBuffer)", this);
	}
}
