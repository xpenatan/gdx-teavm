package com.github.xpenatan.gdx.teavm.backends.web.dom.impl;

import com.github.xpenatan.gdx.teavm.backends.web.dom.HTMLDocumentExt;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
    // Keep callbacks on one TeaVM-managed Java thread so frames can suspend and resume serially.
    private static final ThreadPoolExecutor FRAME_EXECUTOR = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, "gdx-web-render");
                thread.setDaemon(true);
                return thread;
            }
        });

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
        FRAME_EXECUTOR.execute(toRun);
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
