package com.github.xpenatan.gdx.teavm.backend.web.dom;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.dom.events.EventTarget;
import org.teavm.jso.file.File;
import org.teavm.jso.typedarrays.ArrayBuffer;

public abstract class FileReaderWrapper implements EventTarget, JSObject {

    @JSMethod
    public abstract void readAsDataURL(File file);

    @JSMethod
    public abstract void readAsArrayBuffer(File file);

    @JSMethod
    public abstract void readAsText(File file);

    @JSProperty("result")
    public abstract ArrayBuffer getResultAsArrayBuffer();

    @JSProperty("result")
    public abstract String getResultAsString();

    @JSBody(script = "return new FileReader();")
    public static native FileReaderWrapper create();
}