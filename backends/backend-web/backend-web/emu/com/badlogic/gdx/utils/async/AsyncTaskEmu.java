package com.badlogic.gdx.utils.async;

import com.github.xpenatan.gdx.backends.web.gen.Emulate;

@Emulate(AsyncTask.class)
public interface AsyncTaskEmu<T> {
    public T call() throws Exception;
}