package org.teavm.classlib.java.nio;

import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int32ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(valueStr = "java.nio.TIntBufferOverArray", updateCode = true)
public abstract class IntBufferOverArrayEmu extends TIntBufferOverArray implements HasArrayBufferView {

    public IntBufferOverArrayEmu(int start, int capacity, int[] array, int position, int limit, boolean readOnly) {
        super(start, capacity, array, position, limit, readOnly);
    }

    @Override
    @Emulate
    public ArrayBufferViewWrapper getArrayBufferView() {
        Int32ArrayWrapper originalBuffer = (Int32ArrayWrapper)getOriginalArrayBufferView();
        return originalBuffer;
    }

    @Override
    @Emulate
    public ArrayBufferViewWrapper getOriginalArrayBufferView() {
        return TypedArrays.getTypedArray(array);
    }

    @Override
    @Emulate
    public int getElementSize() {
        return 4;
    }
}