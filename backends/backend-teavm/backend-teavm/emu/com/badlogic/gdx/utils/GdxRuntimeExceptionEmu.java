package com.badlogic.gdx.utils;

/**
 * Emulated hack.
 * throw a GdxRuntimeException does not work with teavm. We just rethrow it to make it work.
 *
 * @author xpenatan
 */

import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(GdxRuntimeException.class)
public class GdxRuntimeExceptionEmu extends RuntimeException {
    private static final long serialVersionUID = -7034897190745766939L;

    public GdxRuntimeExceptionEmu() {
        super();
//		throw new RuntimeException();
    }

    public GdxRuntimeExceptionEmu(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public GdxRuntimeExceptionEmu(String message, Throwable cause) {
        super(message, cause);
//		throw new RuntimeException(message, cause);
    }

    public GdxRuntimeExceptionEmu(String message) {
        super(message);

//		throw new RuntimeException(message);
    }

    public GdxRuntimeExceptionEmu(Throwable cause) {
        super(cause);
//		throw new RuntimeException(cause);
    }
}
