package emu.com.badlogic.gdx.utils.async;

public interface TAsyncTask<T> {
    public T call() throws Exception;
}