package org.teavm.classlib.java.nio;

import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Float32ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(valueStr = "java.nio.FloatBufferOverByteBuffer", updateCode = true)
public abstract class FloatBufferOverByteBufferEmu extends TFloatBufferOverByteBuffer implements HasArrayBufferView {

    @Emulate
    Float32ArrayWrapper backupArray;
    @Emulate
    Float32ArrayWrapper floatArray;
    @Emulate
    int positionCache;
    @Emulate
    int remainingCache;

    public FloatBufferOverByteBufferEmu(int start, int capacity, TByteBufferImpl byteBuffer, int position, int limit, boolean readOnly) {
        super(start, capacity, byteBuffer, position, limit, readOnly);
    }

    @Override
    @Emulate
    public ArrayBufferViewWrapper getArrayBufferView() {
        // Int8Array
        Int8ArrayWrapper int8Array = (Int8ArrayWrapper)getOriginalArrayBufferView();
        if(floatArray == null) {
            floatArray = TypedArrays.createFloat32Array(int8Array.getBuffer());
        }

        int position1 = position();
        int remaining1 = remaining();
        if(backupArray == null || positionCache != position1 || remaining1 != remainingCache) {
            positionCache = position1;
            remainingCache = remaining1;
            backupArray = floatArray.subarray(position1, remaining1);
        }
        return backupArray;
    }

    @Override
    @Emulate
    public ArrayBufferViewWrapper getOriginalArrayBufferView() {
        HasArrayBufferView buff = (HasArrayBufferView)byteByffer;
        return buff.getOriginalArrayBufferView();
    }

    @Override
    @Emulate
    public int getElementSize() {
        return 4;
    }
}