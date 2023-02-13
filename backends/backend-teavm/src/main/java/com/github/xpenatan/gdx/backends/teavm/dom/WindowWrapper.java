package com.github.xpenatan.gdx.backends.teavm.dom;

/**
 * @author xpenatan
 */
public interface WindowWrapper {

    public DocumentWrapper getDocument();

    public void requestAnimationFrame(Runnable runnable);

    public TimerWrapper getTimer();

    public LocationWrapper getLocation();

    public int getClientWidth();

    public int getClientHeight();

    public void addEventListener(String type, EventListenerWrapper listener);
}
