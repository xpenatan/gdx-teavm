package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface ClipboardEventWrapper extends EventWrapper {

    @JSProperty
    DataTransferWrapper getClipboardData();
}
