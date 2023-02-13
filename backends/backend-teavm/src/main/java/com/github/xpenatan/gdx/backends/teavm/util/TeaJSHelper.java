package com.github.xpenatan.gdx.backends.teavm.util;

import com.github.xpenatan.gdx.backends.teavm.dom.StorageWrapper;
import org.teavm.jso.JSObject;
import org.teavm.jso.browser.Storage;

/**
 * @author xpenatan
 */
@Deprecated
public class TeaJSHelper implements JSObject {

    public static TeaJSHelper JSHelper;

    public static TeaJSHelper get() {
        return JSHelper;
    }

    public TeaJSHelper() {
    }

    public StorageWrapper getStorage() {
        StorageWrapper storage = (StorageWrapper)Storage.getLocalStorage();
        return storage;
    }
}
