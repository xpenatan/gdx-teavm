package com.github.xpenatan.gdx.backends.teavm.dom.impl;

import com.github.xpenatan.gdx.backends.teavm.dom.DocumentWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.EventListenerWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.LocationWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.TimerWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.WindowWrapper;
import org.teavm.jso.browser.AnimationFrameCallback;
import org.teavm.jso.browser.Location;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.EventListener;

/**
 * @author xpenatan
 */
public class TeaWindow implements WindowWrapper, AnimationFrameCallback {

    private static final TeaWindow TEA_WINDOW = new TeaWindow();;

    public static TeaWindow get() {
        return TEA_WINDOW;
    }

    private Window window;
    private Runnable runnable;
    private TeaTimer timer;

    public TeaWindow() {
        this.window = Window.current();
        timer = new TeaTimer();
    }

    @Override
    public DocumentWrapper getDocument() {
        DocumentWrapper document = (DocumentWrapper)window.getDocument();
        return document;
    }

    @Override
    public void requestAnimationFrame(Runnable runnable) {
        this.runnable = runnable;
        Window.requestAnimationFrame(this);
    }

    @Override
    public void onAnimationFrame(double arg0) {
        Runnable toRun = runnable;
        runnable = null;
        toRun.run();
    }

    @Override
    public TimerWrapper getTimer() {
        return timer;
    }

    @Override
    public LocationWrapper getLocation() {
        Location location = window.getLocation();
        return (LocationWrapper)location;
    }

    @Override
    public int getClientWidth() {
        return window.getDocument().getDocumentElement().getClientWidth();
    }

    @Override
    public int getClientHeight() {
        return window.getDocument().getDocumentElement().getClientHeight();
    }

    @Override
    public void addEventListener(String type, EventListenerWrapper listener) {
        EventListener<?> eListener = (EventListener<?>)listener;
        window.addEventListener(type, eListener);
    }
}
