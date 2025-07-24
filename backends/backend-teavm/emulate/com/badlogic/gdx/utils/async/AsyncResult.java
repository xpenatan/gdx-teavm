package com.badlogic.gdx.utils.async;

import com.badlogic.gdx.assets.AssetLoadingTask;
import org.teavm.jso.browser.TimerHandler;
import org.teavm.jso.browser.Window;

public class AsyncResult<T> implements TimerHandler {
    private AssetLoadingTask loadingTask;
    private boolean isDone;

    AsyncResult(AsyncTask<T> task) {
        loadingTask = (AssetLoadingTask)task;
        Window.setTimeout(this, 0);
    }

    public boolean isDone() {
        return isDone;
    }

    public T get() {
        return null;
    }

    @Override
    public void onTimer() {
        isDone = true;
        try {
            loadingTask.call();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}