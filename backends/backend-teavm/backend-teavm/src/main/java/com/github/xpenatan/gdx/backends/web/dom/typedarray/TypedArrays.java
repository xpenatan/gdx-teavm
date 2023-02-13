package com.github.xpenatan.gdx.backends.web.dom.typedarray;

/**
 * @author xpenatan
 */
public abstract class TypedArrays {

    private static TypedArrays instance;

    public static TypedArrays getInstance() {
        return instance;
    }

    public static void setInstance(TypedArrays instance) {
        TypedArrays.instance = instance;
    }

    public abstract Float32ArrayWrapper createFloat32Array(int length);

    public abstract Float32ArrayWrapper createFloat32Array(ArrayBufferWrapper buffer);

    public abstract Float32ArrayWrapper createFloat32Array(ArrayBufferWrapper buffer, int offset);

    public abstract Float32ArrayWrapper createFloat32Array(ArrayBufferWrapper buffer, int offset, int length);

    public abstract Int32ArrayWrapper createInt32Array(int length);

    public abstract Int32ArrayWrapper createInt32Array(ArrayBufferWrapper buffer);

    public abstract Int32ArrayWrapper createInt32Array(ArrayBufferWrapper buffer, int offset);

    public abstract Int32ArrayWrapper createInt32Array(ArrayBufferWrapper buffer, int offset, int length);

    public abstract Int16ArrayWrapper createInt16Array(int length);

    public abstract Int16ArrayWrapper createInt16Array(ArrayBufferWrapper buffer);

    public abstract Int16ArrayWrapper createInt16Array(ArrayBufferWrapper buffer, int offset);

    public abstract Int16ArrayWrapper createInt16Array(ArrayBufferWrapper buffer, int offset, int length);

    public abstract Int8ArrayWrapper createInt8Array(int length);

    public abstract Int8ArrayWrapper createInt8Array(ArrayBufferWrapper buffer);

    public abstract Int8ArrayWrapper createInt8Array(ArrayBufferWrapper buffer, int offset);

    public abstract Int8ArrayWrapper createInt8Array(ArrayBufferWrapper buffer, int offset, int length);

    public abstract Uint8ArrayWrapper createUint8Array(int length);

    public abstract Float64ArrayWrapper createFloat64Array(int length);
}
