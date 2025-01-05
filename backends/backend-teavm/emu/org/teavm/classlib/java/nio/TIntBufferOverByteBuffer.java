/*
 *  Copyright 2014 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.nio;

import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.HasArrayBufferView;
import org.teavm.jso.typedarrays.Int8Array;

abstract class TIntBufferOverByteBuffer extends TIntBufferImpl implements HasArrayBufferView {
    org.teavm.classlib.java.nio.TByteBufferImpl byteByffer;
    boolean readOnly;
    int start;

    public TIntBufferOverByteBuffer(int start, int capacity, org.teavm.classlib.java.nio.TByteBufferImpl byteBuffer, int position, int limit,
                                    boolean readOnly) {
        super(capacity, position, limit);
        this.start = start;
        this.byteByffer = byteBuffer;
        this.readOnly = readOnly;
    }

    @Override
    public Int8Array getArrayBufferView() {
        return byteByffer.getArrayBufferView();
    }

    @Override
    boolean isArrayPresent() {
        return false;
    }

    @Override
    int[] getArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    int getArrayOffset() {
        throw new UnsupportedOperationException();
    }

    @Override
    boolean readOnly() {
        return readOnly;
    }
}
