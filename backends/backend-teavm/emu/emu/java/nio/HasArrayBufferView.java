package emu.java.nio;

import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferViewWrapper;

public interface HasArrayBufferView {
    ArrayBufferViewWrapper getTypedArray();
}