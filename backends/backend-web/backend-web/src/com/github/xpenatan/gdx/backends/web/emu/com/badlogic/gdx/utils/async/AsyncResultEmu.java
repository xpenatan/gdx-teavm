package com.github.xpenatan.gdx.backends.web.emu.com.badlogic.gdx.utils.async;

import com.badlogic.gdx.utils.async.AsyncResult;
import com.github.xpenatan.gdx.backends.web.emu.Emulate;

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