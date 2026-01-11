package emu.com.badlogic.gdx.utils.async;

import emu.com.badlogic.gdx.assets.AssetLoadingTask;
import org.teavm.jso.browser.TimerHandler;
import org.teavm.jso.browser.Window;

public class AsyncResult<T> implements TimerHandler {
    private AssetLoadingTask loadingTask;
    private boolean isDone;
    private int count;

    AsyncResult(AsyncTask<T> task) {
        loadingTask = (AssetLoadingTask)task;
        Window.setTimeout(this, 0);
    }

    public boolean isDone() {
        count++;
        if(count > 2) {
            tick(); // If executed this block its possible that it was from finishLoading(). Javascript is async only.
        }
        return isDone;
    }

    public T get() {
        return null;
    }

    @Override
    public void onTimer() {
        tick();
    }

    private void tick() {
        isDone = true;
        try {
            loadingTask.call();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}