package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.dom.events.Event;

/**
 * @author xpenatan
 */
public interface DragEventWrapper extends Event, JSObject {

    @JSProperty
    DataTransferWrapper getDataTransfer();
}