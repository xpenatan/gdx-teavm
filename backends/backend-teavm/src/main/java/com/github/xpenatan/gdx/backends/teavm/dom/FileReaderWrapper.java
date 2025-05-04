package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.typedarrays.ArrayBuffer;

public abstract class FileReaderWrapper implements EventTargetWrapper, JSObject {

    @JSMethod
    public abstract void readAsDataURL(FileWrapper file);

    @JSMethod
    public abstract void readAsArrayBuffer(FileWrapper file);

    @JSMethod
    public abstract void readAsText(FileWrapper file);

    @JSProperty("result")
    public abstract ArrayBuffer getResultAsArrayBuffer();

    @JSProperty("result")
    public abstract String getResultAsString();

    @JSBody(script = "return new FileReader();")
    public static native FileReaderWrapper create();
}