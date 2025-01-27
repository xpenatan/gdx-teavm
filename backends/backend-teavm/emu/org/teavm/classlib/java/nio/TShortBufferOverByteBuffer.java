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
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayNative;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayWrapper;

abstract class TShortBufferOverByteBuffer extends TShortBufferImpl implements HasArrayBufferView {
    TByteBufferImpl byteByffer;
    boolean readOnly;
    int start;

    public TShortBufferOverByteBuffer(int start, int capacity, TByteBufferImpl byteBuffer, int position, int limit,
            boolean readOnly) {
        super(capacity, position, limit);
        this.start = start;
        this.byteByffer = byteBuffer;
        this.readOnly = readOnly;
    }

    @Override
    public Int8ArrayWrapper getArrayBufferView() {
        return byteByffer.getArrayBufferView();
    }

    @Override
    public void setInt8ArrayNative(Int8ArrayNative array) {
        byteByffer.setInt8ArrayNative(array);
    }

    @Override
    boolean isArrayPresent() {
        return false;
    }

    @Override
    short[] getArray() {
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
