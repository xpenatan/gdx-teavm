package com.github.xpenatan.gdx.teavm.backends.web.utils;

import org.teavm.jso.JSBody;

public class WebNavigator {

    @JSBody(script = "return navigator.platform;")
    public static native String getPlatform();

    @JSBody(script = "return navigator.userAgent;")
    public static native String getUserAgent();
}
