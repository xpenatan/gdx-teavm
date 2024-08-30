package org.teavm.classlib.java.nio;

import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int16ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(valueStr = "java.nio.TShortBufferOverArray", updateCode = true)
public abstract class ShortBufferOverArrayEmu extends TShortBufferOverArray implements HasArrayBufferView {

    public ShortBufferOverArrayEmu(int start, int capacity, short[] array, int position, int limit, boolean readOnly) {
        super(start, capacity, array, position, limit, readOnly);
    }

    @Override
    @Emulate
    public ArrayBufferViewWrapper getArrayBufferView() {
        Int16ArrayWrapper originalBuffer = (Int16ArrayWrapper)getOriginalArrayBufferView();
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
        return 2;
    }
}