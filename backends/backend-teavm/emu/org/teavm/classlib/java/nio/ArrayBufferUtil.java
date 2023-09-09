package org.teavm.classlib.java.nio;

import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Float32ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int16ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int32ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Uint8ArrayWrapper;
import java.nio.Buffer;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public class ArrayBufferUtil {
    @org.teavm.jso.JSBody(params = {"array"}, script = "" +
            "return array.data;")
    static native Int8ArrayWrapper getArrayBufferView(JSObject array);

    public static Int8ArrayWrapper getInt8Array(Buffer buffer) {
        if(buffer instanceof HasArrayBufferView) {
            return ((HasArrayBufferView)buffer).getTypedArray();
        }
        return null;
    }

    public static int getElementSize(Buffer buffer) {
        if(buffer instanceof HasArrayBufferView) {
            return ((HasArrayBufferView)buffer).getElementSize();
        }
        return 1;
    }

    @JSBody(params = { "array" }, script = "return Float32Array.from(array);")
    public static native Float32ArrayWrapper fromF32(ArrayBufferViewWrapper array);

    @JSBody(params = { "array" }, script = "return Int32Array.from(array);")
    public static native Int32ArrayWrapper fromI32(ArrayBufferViewWrapper array);

    @JSBody(params = { "array" }, script = "return Int16Array.from(array);")
    public static native Int16ArrayWrapper fromI16(ArrayBufferViewWrapper arra);

    @JSBody(params = { "array" }, script = "return Uint8Array.from(array);")
    public static native Uint8ArrayWrapper fromUI8(ArrayBufferViewWrapper array);
}