package com.github.xpenatan.gdx.backends.teavm.agent;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface TeaAgentInfo extends JSObject {

    @JSProperty
    boolean isFirefox();

    @JSProperty
    boolean isChrome();

    @JSProperty
    boolean isSafari();

    @JSProperty
    boolean isOpera();

    @JSProperty
    boolean isIE();

    @JSProperty
    boolean isMacOS();

    @JSProperty
    boolean isLinux();

    @JSProperty
    boolean isWindows();
}
