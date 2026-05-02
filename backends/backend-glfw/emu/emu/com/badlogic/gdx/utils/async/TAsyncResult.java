package emu.com.badlogic.gdx.utils.async;

import emu.com.badlogic.gdx.assets.TAssetLoadingTask;
import org.teavm.jso.browser.TimerHandler;

public class TAsyncResult<T> implements TimerHandler {
    private TAssetLoadingTask loadingTask;
    private boolean isDone;
    private int count;

    TAsyncResult(TAsyncTask<T> task) {
        loadingTask = (TAssetLoadingTask)task;

        //TODO FIX ME
//        Window.setTimeout(this, 0);
        onTimer();
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