package com.github.xpenatan.gdx.examples.teavm.audio;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

@JSFunctor
public interface HowlEventFunction extends JSObject {
    void onEvent();
}