package com.github.xpenatan.gdx.teavm.backend.web.webaudio.howler;

import org.teavm.jso.JSClass;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

@JSClass
public class HowlPannerAttr implements JSObject {

    @JSProperty()
    public native void setConeInnerAngle (int angle);
}