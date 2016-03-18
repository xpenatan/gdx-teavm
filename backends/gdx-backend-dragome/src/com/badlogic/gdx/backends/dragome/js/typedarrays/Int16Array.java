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
import com.dragome.web.enhancers.jsdelegate.JsDelegateFactory;

/** @author xpenatan */
public interface Int16Array extends ArrayBufferView {

	final int BYTES_PER_ELEMENT = 2;

	int get_length ();

	@DelegateCode(eval = "this.node[$1]")
	short get (int index);

	@DelegateCode(eval = "this.node[$1] = $2")
	void set (int index, int value);

	void set (Int16Array array, int offset);

	void set (short[] array, int offset);

	void set (int[] array, int offset);

	Int16Array subarray (int begin, int end);
}
