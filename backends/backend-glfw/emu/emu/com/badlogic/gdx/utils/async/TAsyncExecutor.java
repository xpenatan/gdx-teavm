package emu.com.badlogic.gdx.utils.async;

import com.badlogic.gdx.utils.Disposable;

public class TAsyncExecutor implements Disposable {

    public TAsyncExecutor(int maxConcurrent) {
    }

    public TAsyncExecutor(int maxConcurrent, String name) {
    }

    public <T> TAsyncResult<T> submit(final TAsyncTask<T> task) {
        return new TAsyncResult(task);
    }

    @Override
    public void dispose() {
    }
}
