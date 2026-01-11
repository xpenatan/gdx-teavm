package com.github.xpenatan.gdx.teavm.backend.web.dom;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.file.FileList;

public interface DataTransferWrapper extends JSObject {

    @JSProperty
    FileList getFiles();

    @JSMethod
    String getData(String format);

    @JSMethod
    void setData(String format, String data);
}