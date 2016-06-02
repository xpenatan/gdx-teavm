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

package com.badlogic.gdx.backends.dragome.js.typedarrays;

import com.dragome.commons.DelegateCode;
import com.dragome.commons.javascript.ScriptHelper;

/** @author xpenatan */
public interface Uint8Array extends ArrayBufferView {

	final int BYTES_PER_ELEMENT = 1;

	@DelegateCode(ignore = true)
	public static Uint8Array create (ArrayBuffer buffer) {
		Uint8Array node = ScriptHelper.evalCasting("new Uint8Array(buffer.node)", Uint8Array.class, null);
		return node;
	}

	@DelegateCode(ignore = true)
	public static Uint8Array create (ArrayBuffer buffer, int byteOffset) {
		Uint8Array node = ScriptHelper.evalCasting("new Uint8Array(buffer.node, byteOffset)", Uint8Array.class, null);
		return node;
	};

	@DelegateCode(ignore = true)
	public static Uint8Array create (ArrayBuffer buffer, int byteOffset, int length) {
		Uint8Array node = ScriptHelper.evalCasting("new Uint8Array(buffer.node, byteOffset, length)", Uint8Array.class, null);
		return node;
	};
	
	@DelegateCode(ignore = true)
	public static Uint8Array create (int length) {
		Uint8Array node = ScriptHelper.evalCasting("new Uint8Array(length)", Uint8Array.class, null);
		return node;
	};

	int get_length ();
	@DelegateCode(eval = "this.node[$1]")
	short get (int index);
	@DelegateCode(eval = "this.node[$1] = $2")
	void set (int index, int value);
	void set (Uint8Array array, int offset);
	void set (int[] array, int offset);
	void set (short[] array, int offset);
	Uint8Array subarray (int begin);
	Uint8Array subarray (int begin, int end);
}
