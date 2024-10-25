package org.teavm.classlib.java.nio;

import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Float32ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(valueStr = "java.nio.TFloatBufferOverArray", updateCode = true)
public abstract class FloatBufferOverArrayEmu extends TFloatBufferOverArray implements HasArrayBufferView {

    public FloatBufferOverArrayEmu(int start, int capacity, float[] array, int position, int limit, boolean readOnly) {
        super(start, capacity, array, position, limit, readOnly);
    }

    @Override
    @Emulate
    public ArrayBufferViewWrapper getArrayBufferView() {
        Float32ArrayWrapper originalBuffer = (Float32ArrayWrapper)getOriginalArrayBufferView();
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