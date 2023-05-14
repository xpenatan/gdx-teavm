package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface DragEventWrapper extends EventWrapper, JSObject {

    @JSProperty
    DataTransferWrapper getDataTransfer();
}