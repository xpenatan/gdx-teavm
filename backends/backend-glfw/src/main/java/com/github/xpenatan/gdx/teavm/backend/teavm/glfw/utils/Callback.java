package com.github.xpenatan.gdx.teavm.backend.teavm.glfw.utils;

import org.teavm.interop.Function;

public abstract class Callback extends Function {
    private Callback() {
    }

    public abstract void invoke();
}
