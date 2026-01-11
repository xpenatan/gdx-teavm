package com.github.xpenatan.gdx.backends.teavm.gl;

import org.teavm.jso.JSProperty;
import org.teavm.jso.webgl.WebGLContextAttributes;

/**
 * @author xpenatan
 */
public abstract class WebGLContextAttributesExt extends WebGLContextAttributes {

    @JSProperty
    public abstract void setPowerPreference(String powerPreference);
}