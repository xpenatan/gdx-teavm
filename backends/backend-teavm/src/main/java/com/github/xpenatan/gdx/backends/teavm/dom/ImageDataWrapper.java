package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.typedarrays.Uint8ClampedArray;

/**
 * @author xpenatan
 */
public abstract class ImageDataWrapper implements JSObject {
    // ImageData

    @JSProperty
    public abstract int getWidth();

    @JSProperty
    public abstract int getHeight();

    @JSProperty
    public abstract Uint8ClampedArray getData();

    @JSBody(params = { "width", "height" }, script = "return new ImageData(width, height);")
    public static native ImageDataWrapper create(int width, int height);

    @JSBody(params = { "array", "width", "height" }, script = "return new ImageData(array, width, height, { colorSpace: 'srgb' });")
    public static native ImageDataWrapper create(Uint8ClampedArray array, int width, int height);
}
