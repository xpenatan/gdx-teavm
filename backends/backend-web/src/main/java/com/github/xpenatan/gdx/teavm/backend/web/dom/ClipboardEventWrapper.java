package com.github.xpenatan.gdx.teavm.backend.web.dom;

import org.teavm.jso.JSProperty;
import org.teavm.jso.dom.events.Event;

/**
 * @author xpenatan
 */
public interface ClipboardEventWrapper extends Event {

    @JSProperty
    DataTransferWrapper getClipboardData();
}
