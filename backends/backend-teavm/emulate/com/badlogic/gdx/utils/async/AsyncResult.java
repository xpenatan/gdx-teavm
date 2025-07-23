package com.badlogic.gdx.utils.async;

public class AsyncResult<T> {
    private final T result;

    AsyncResult(T result) {
        this.result = result;
    }

    public boolean isDone() {
        return true;
    }

    public T get() {
        return result;
    }
}