package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface DataTransferWrapper extends JSObject {

    @JSProperty
    DataTransferItemArrayWrapper getItems();

    @JSProperty
    FileListWrapper getFiles();
}