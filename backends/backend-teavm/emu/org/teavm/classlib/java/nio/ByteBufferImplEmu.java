package org.teavm.classlib.java.nio;

import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;
import org.teavm.jso.JSObject;

@Emulate(valueStr = "java.nio.ByteBufferImpl", updateCode = true)
public abstract class ByteBufferImplEmu extends TByteBufferImpl implements HasArrayBufferView {

    @Emulate
    Int8ArrayWrapper backupArray;
    @Emulate
    int positionCache;
    @Emulate
    int remainingCache;

    public ByteBufferImplEmu(int start, int capacity, byte[] array, int position, int limit, boolean direct, boolean readOnly) {
        super(start, capacity, array, position, limit, direct, readOnly);
    }

    @Override
    @Emulate
    public ArrayBufferViewWrapper getArrayBufferView() {
        Int8ArrayWrapper int8Array = (Int8ArrayWrapper)getOriginalArrayBufferView();
        int position1 = position();
        int remaining1 = remaining();
        if(backupArray == null || positionCache != position1 || remaining1 != remainingCache) {
            positionCache = position1;
            remainingCache = remaining1;
            backupArray = int8Array.subarray(position1, remaining1);
        }
        return backupArray;
    }

    @Override
    @Emulate
    public ArrayBufferViewWrapper getOriginalArrayBufferView() {
        Object array = array();
        Int8ArrayWrapper int8Array = TypedArrays.getArrayBufferView((JSObject)array);
        return int8Array;
    }

    @Override
    @Emulate
    public int getElementSize() {
        return 1;
    }
}