package emu.com.badlogic.gdx.utils.async;

public interface AsyncTask<T> {
    public T call() throws Exception;
}