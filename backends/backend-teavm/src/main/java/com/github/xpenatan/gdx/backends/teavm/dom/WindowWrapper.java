package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;

/**
 * @author xpenatan
 */
public interface WindowWrapper {

    DocumentWrapper getDocument();

    void requestAnimationFrame(Runnable runnable);

    LocationWrapper getLocation();

    int getClientWidth();

    int getClientHeight();

    void addEventListener(String type, EventListener<Event> listener);
}
