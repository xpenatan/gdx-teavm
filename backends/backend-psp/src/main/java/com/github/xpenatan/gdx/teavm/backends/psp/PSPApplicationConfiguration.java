package com.github.xpenatan.gdx.teavm.backends.psp;

import com.github.xpenatan.gdx.teavm.backends.psp.config.backend.TeaPSPBackend;

public class PSPApplicationConfiguration {

    /**
     *  FIXME Experimental and may not work correctly.
     *
     *  Logs Memory Usage in the PSP debug console.
     *  Must compile with {@link TeaPSPBackend#debugMemory} true
     */
    public boolean logMemory = false;
    public int logMemoryDelayMilli = 3000;
}