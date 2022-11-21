package com.github.xpenatan.gdx.backends.web.emu.com.badlogic.gdx.utils.async;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.github.xpenatan.gdx.backends.web.emu.Emulate;

@Emulate(AsyncExecutor.class)
public class AsyncExecutorEmu implements Disposable {

    public AsyncExecutorEmu(int maxConcurrent) {
    }

    public AsyncExecutorEmu(int maxConcurrent, String name) {
    }

    public <T> AsyncResultEmu<T> submit(final AsyncTaskEmu<T> task) {
        T result = null;
        try {
            result = task.call();
        }
        catch(Throwable t) {
            throw new GdxRuntimeException("Could not submit AsyncTask: " + t.getMessage(), t);
        }
        return new AsyncResultEmu(result);
    }

    @Override
    public void dispose() {
    }
}
