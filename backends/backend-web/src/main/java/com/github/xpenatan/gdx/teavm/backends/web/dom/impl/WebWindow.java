package com.github.xpenatan.gdx.teavm.backends.web.dom.impl;

import com.github.xpenatan.gdx.teavm.backends.web.dom.HTMLDocumentExt;
import org.teavm.jso.browser.AnimationFrameCallback;
import org.teavm.jso.browser.Location;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;

/**
 * @author xpenatan
 */
public class WebWindow implements AnimationFrameCallback {

    private static final WebWindow TEA_WINDOW = new WebWindow();;

    public static WebWindow get() {
        return TEA_WINDOW;
    }

    private Window window;
    private Runnable runnable;

    public WebWindow() {
        this.window = Window.current();
    }

    public HTMLDocumentExt getDocument() {
        return (HTMLDocumentExt)window.getDocument();
    }

    public void requestAnimationFrame(Runnable runnable) {
        this.runnable = runnable;
        Window.requestAnimationFrame(this);
    }

    public void onAnimationFrame(double arg0) {
        Runnable toRun = runnable;
        runnable = null;
        toRun.run();
    }

    public Location getLocation() {
        Location location = window.getLocation();
        return location;
    }

    public int getClientWidth() {
        return window.getInnerWidth();
    }

    public int getClientHeight() {
        return window.getInnerHeight();
    }

    public void addEventListener(String type, EventListener<Event> listener) {
        window.addEventListener(type, listener);
    }
}
