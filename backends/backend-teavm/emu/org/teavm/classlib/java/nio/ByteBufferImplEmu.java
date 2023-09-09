package org.teavm.classlib.java.nio;

import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;
import org.teavm.jso.JSObject;

@Emulate(valueStr = "java.nio.ByteBufferImpl", updateCode = true)
public abstract class ByteBufferImplEmu extends TByteBufferImpl implements HasArrayBufferView {

    public ByteBufferImplEmu(int capacity, boolean direct) {
        super(capacity, direct);
    }

    public ByteBufferImplEmu(int start, int capacity, byte[] array, int position, int limit, boolean direct, boolean readOnly) {
        super(start, capacity, array, position, limit, direct, readOnly);
    }

    @Override
    @Emulate
    public Int8ArrayWrapper getTypedArray() {
        Object array = array();
        return ArrayBufferUtil.getArrayBufferView((JSObject)array);
    }

    @Override
    @Emulate
    public int getElementSize() {
        return 1;
    }
}