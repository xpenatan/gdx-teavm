package com.badlogic.gdx.utils.async;

import com.badlogic.gdx.utils.Disposable;

public class AsyncExecutor implements Disposable {

    public AsyncExecutor(int maxConcurrent) {
    }

    public AsyncExecutor(int maxConcurrent, String name) {
    }

    public <T> AsyncResult<T> submit(final AsyncTask<T> task) {
        return new AsyncResult(task);
    }

    @Override
    public void dispose() {
    }
}
