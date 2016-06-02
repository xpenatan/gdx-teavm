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
public interface Int8Array extends ArrayBufferView {

	final int BYTES_PER_ELEMENT = 1;

	@DelegateCode(ignore = true)
	public static Int8Array create (ArrayBuffer buffer) {
		Int8Array node = ScriptHelper.evalCasting("new Int8Array(buffer.node)", Int8Array.class, null);
		return node;
	}

	@DelegateCode(ignore = true)
	public static Int8Array create (ArrayBuffer buffer, int byteOffset) {
		Int8Array node = ScriptHelper.evalCasting("new Int8Array(buffer.node, byteOffset)", Int8Array.class, null);
		return node;
	};

	@DelegateCode(ignore = true)
	public static Int8Array create (ArrayBuffer buffer, int byteOffset, int length) {
		Int8Array node = ScriptHelper.evalCasting("new Int8Array(buffer.node, byteOffset, length)", Int8Array.class, null);
		return node;
	};

	@DelegateCode(ignore = true)
	public static Int8Array create (int length) {
		Int8Array node = ScriptHelper.evalCasting("new Int8Array(length)", Int8Array.class, null);
		return node;
	};

	int get_length ();
	@DelegateCode(eval = "this.node[$1]")
	byte get (int index);
	@DelegateCode(eval = "this.node[$1] = $2")
	void set (int index, int value);
	void set (Int8Array array, int offset);
	void set (byte[] array, int offset);
	void set (int[] array, int offset);
	Int8Array subarray (int begin);
	Int8Array subarray (int begin, int end);
}
