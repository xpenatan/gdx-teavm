package com.github.xpenatan.gdx.backends.teavm;

import com.github.xpenatan.gdx.backends.teavm.dom.impl.TeaSoundManager;
import com.github.xpenatan.gdx.backends.teavm.dom.impl.TeaWindow;
import com.github.xpenatan.gdx.backends.teavm.dom.DocumentWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.HTMLImageElementWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.StorageWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.WindowWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.XMLHttpRequestWrapper;
import com.github.xpenatan.gdx.backends.teavm.soundmanager.SoundManagerWrapper;
import org.teavm.jso.JSObject;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.Storage;

/**
 * @author xpenatan
 */
public class TeaJSHelper extends WebJSHelper implements JSObject {

    private WebAgentInfo agentInfo;
    private HTMLCanvasElementWrapper canvasWrapper;
    private TeaJSGraphics graphics;
    private TeaJSApplication application;

    public TeaJSHelper(WebAgentInfo agentInfo, HTMLCanvasElementWrapper canvasWrapper) {
        this.agentInfo = agentInfo;
        this.canvasWrapper = canvasWrapper;
        this.graphics = new TeaJSGraphics();
        this.application = new TeaJSApplication();
    }

    @Override
    public HTMLCanvasElementWrapper getCanvas() {
        return canvasWrapper;
    }

    @Override
    public WindowWrapper getCurrentWindow() {
        return new TeaWindow();
    }

    @Override
    public WebAgentInfo getAgentInfo() {
        return agentInfo;
    }

    @Override
    public HTMLImageElementWrapper createImageElement() {
        DocumentWrapper document = getCurrentWindow().getDocument();
        return (HTMLImageElementWrapper)document.createElement("img");
    }

    @Override
    public XMLHttpRequestWrapper creatHttpRequest() {
        return (XMLHttpRequestWrapper)XMLHttpRequest.create();
    }

    @Override
    public StorageWrapper getStorage() {
        StorageWrapper storage = (StorageWrapper)Storage.getLocalStorage();
        return storage;
    }

    @Override
    public SoundManagerWrapper createSoundManager() {
        return new TeaSoundManager();
    }

    @Override
    public WebJSGraphics getGraphics() {
        return graphics;
    }

    @Override
    public WebJSApplication getApplication() {
        return application;
    }
}
