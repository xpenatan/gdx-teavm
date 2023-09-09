package org.teavm.classlib.java.nio;

import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayWrapper;
import org.teavm.jso.JSObject;

public class ArrayBufferUtil {
    @org.teavm.jso.JSBody(params = {"array"}, script = "" +
            "return array.data;")
    public static native Int8ArrayWrapper getArrayBufferView(JSObject array);
}