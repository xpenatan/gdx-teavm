package com.github.xpenatan.gdx.backends.teavm.dom.audio;

import com.github.xpenatan.gdx.backends.teavm.dom.HTMLElementWrapper;
import org.teavm.jso.JSProperty;

public interface HTMLMediaElementWrapper extends HTMLElementWrapper {

    void play();
    void pause();

    @JSProperty
    void setCurrentTime(double time);

    @JSProperty
    double getCurrentTime();

    @JSProperty
    boolean isLoop();

    @JSProperty
    void setLoop(boolean isLooping);

    @JSProperty
    boolean isPaused();

    @JSProperty
    void setSrc(String url);

    @JSProperty
    String getSrc();

    @JSProperty
    Boolean isMuted();

    @JSProperty
    void setMuted(boolean flag);
}