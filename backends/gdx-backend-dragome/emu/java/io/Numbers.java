/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package java.io;

import com.badlogic.gdx.backends.dragome.js.typedarrays.Float32Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Int32Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Int8Array;

public class Numbers {

	static final double LN2 = Math.log(2);

	public static final int floatToIntBits (float f) {
		wfa.set(0, f);
		return wia.get(0);
	}

	static Int8Array wba = Int8Array.create(4);
	static Int32Array wia = Int32Array.create(wba.getBuffer(), 0, 1);
	static Float32Array wfa = Float32Array.create(wba.getBuffer(), 0, 1);

	public static final float intBitsToFloat (int i) {
		wia.set(0, i);
		return wfa.get(0);
	}

	public static final long doubleToLongBits (Double d) {
		throw new RuntimeException("NYI");
	}

	public static final double longBitsToDouble (long l) {
		throw new RuntimeException("NYI");
	}

	public static long doubleToRawLongBits (double value) {
		throw new RuntimeException("NYI: Numbers.doubleToRawLongBits");
	}
}
