package com.github.xpenatan.gdx.backends.teavm.util;

import com.github.xpenatan.gdx.backends.teavm.dom.impl.TeaWindow;
import com.github.xpenatan.gdx.backends.teavm.dom.DocumentWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.HTMLImageElementWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.StorageWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.WindowWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.XMLHttpRequestWrapper;
import org.teavm.jso.JSObject;
import org.teavm.jso.ajax.XMLHttpRequest;
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

    private HTMLCanvasElementWrapper canvasWrapper;

    public TeaJSHelper(HTMLCanvasElementWrapper canvasWrapper) {
        this.canvasWrapper = canvasWrapper;
    }

    public HTMLCanvasElementWrapper getCanvas() {
        return canvasWrapper;
    }

    public WindowWrapper getCurrentWindow() {
        return new TeaWindow();
    }

    public HTMLImageElementWrapper createImageElement() {
        DocumentWrapper document = getCurrentWindow().getDocument();
        return (HTMLImageElementWrapper)document.createElement("img");
    }

    public XMLHttpRequestWrapper creatHttpRequest() {
        return (XMLHttpRequestWrapper)XMLHttpRequest.create();
    }

    public StorageWrapper getStorage() {
        StorageWrapper storage = (StorageWrapper)Storage.getLocalStorage();
        return storage;
    }
}
