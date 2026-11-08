package com.github.xpenatan.gdx.teavm.backends.web.webaudio.howler;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

@JSFunctor
public interface HowlEventFunctionWithArgs extends JSObject {
    void onEvent(int soundId, JSObject data);
}
