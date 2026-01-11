package com.github.xpenatan.gdx.teavm.backend.web.gl;

import org.teavm.jso.JSProperty;
import org.teavm.jso.webgl.WebGLContextAttributes;

/**
 * @author xpenatan
 */
public abstract class WebGLContextAttributesExt extends WebGLContextAttributes {

    @JSProperty
    public abstract void setPowerPreference(String powerPreference);
}