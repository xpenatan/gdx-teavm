package com.badlogic.gdx.utils;

/**
 * Emulated hack.
 * throw a GdxRuntimeException does not work with teavm. We just rethrow it to make it work.
 *
 * @author xpenatan
 */
public class GdxRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -7034897190745766939L;

    public GdxRuntimeException() {
        super();
//		throw new RuntimeException();
    }

    public GdxRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public GdxRuntimeException(String message, Throwable cause) {
        super(message, cause);
//		throw new RuntimeException(message, cause);
    }

    public GdxRuntimeException(String message) {
        super(message);

//		throw new RuntimeException(message);
    }

    public GdxRuntimeException(Throwable cause) {
        super(cause);
//		throw new RuntimeException(cause);
    }
}
