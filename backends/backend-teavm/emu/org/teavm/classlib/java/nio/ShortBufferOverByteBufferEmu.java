package org.teavm.classlib.java.nio;

import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(valueStr = "java.nio.ShortBufferOverByteBuffer", updateCode = true)
public abstract class ShortBufferOverByteBufferEmu extends TShortBufferOverByteBuffer implements HasArrayBufferView {

    public ShortBufferOverByteBufferEmu(int start, int capacity, TByteBufferImpl byteBuffer, int position, int limit, boolean readOnly) {
        super(start, capacity, byteBuffer, position, limit, readOnly);
    }

    @Override
    @Emulate
    public Int8ArrayWrapper getTypedArray() {
        HasArrayBufferView buff = (HasArrayBufferView)byteByffer;
        return buff.getTypedArray();
    }

    @Override
    @Emulate
    public int getElementSize() {
        return 2;
    }
}