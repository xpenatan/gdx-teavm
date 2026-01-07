package com.github.xpenatan.gdx.backends.teavm.glfw;

import org.teavm.interop.Function;

public abstract class TeaCallback extends Function {
    private TeaCallback() {
    }

    public abstract void invoke();
}
