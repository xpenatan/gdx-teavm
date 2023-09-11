package com.github.xpenatan.gdx.backends.teavm.dom.audio;

public interface AudioContextWrapper extends BaseAudioContextWrapper {
    void resume();

    void suspend();
}