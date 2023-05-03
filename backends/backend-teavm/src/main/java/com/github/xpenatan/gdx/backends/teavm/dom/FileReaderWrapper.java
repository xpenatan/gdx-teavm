package com.github.xpenatan.gdx.backends.teavm.dom;

import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferWrapper;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public abstract class FileReaderWrapper implements EventTargetWrapper, JSObject {

    @JSMethod
    public abstract void readAsDataURL(FileWrapper file);

    @JSMethod
    public abstract void readAsArrayBuffer(FileWrapper file);

    @JSMethod
    public abstract void readAsText(FileWrapper file);

    @JSProperty("result")
    public abstract ArrayBufferWrapper getResultAsArrayBuffer();

    @JSProperty("result")
    public abstract String getResultAsString();

    @JSBody(script = "return new FileReader();")
    public static native FileReaderWrapper create();
}