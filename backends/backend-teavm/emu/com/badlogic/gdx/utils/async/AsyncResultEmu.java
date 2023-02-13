package com.badlogic.gdx.utils.async;

import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(AsyncResult.class)
public class AsyncResultEmu<T> {
    private final T result;

    AsyncResultEmu(T result) {
        this.result = result;
    }

    public boolean isDone() {
        return true;
    }

    public T get() {
        return result;
    }
}