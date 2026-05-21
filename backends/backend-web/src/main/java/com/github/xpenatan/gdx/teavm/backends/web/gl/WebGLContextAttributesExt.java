package com.github.xpenatan.gdx.teavm.backends.web.gl;

import org.teavm.jso.JSClass;
import org.teavm.jso.JSProperty;
import org.teavm.jso.webgl.WebGLContextAttributes;

/**
 * @author xpenatan
 */
@JSClass(transparent = true)
public abstract class WebGLContextAttributesExt extends WebGLContextAttributes {

    @JSProperty
    public abstract void setPowerPreference(String powerPreference);
}
