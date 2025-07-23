package com.badlogic.gdx.utils.async;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class AsyncExecutor implements Disposable {

    public AsyncExecutor(int maxConcurrent) {
    }

    public AsyncExecutor(int maxConcurrent, String name) {
    }

    public <T> AsyncResult<T> submit(final AsyncTask<T> task) {
        T result = null;
        try {
            result = task.call();
        }
        catch(Throwable t) {
            throw new GdxRuntimeException("Could not submit AsyncTask: " + t.getMessage(), t);
        }
        return new AsyncResult(result);
    }

    @Override
    public void dispose() {
    }
}
