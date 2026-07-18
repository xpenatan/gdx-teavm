package com.github.xpenatan.gdx.teavm.examples.shared;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.utils.TimeUtils;

public final class ExampleFpsLogger {
    private static final long LOG_INTERVAL_NANOS = 1_000_000_000L;

    private final FPSLogger logger = new FPSLogger();
    private FileHandle output;
    private long lastFileLogTime = TimeUtils.nanoTime();

    public ExampleFpsLogger() {
        output = prepareOutput();
    }

    public void log() {
        logger.log();

        long now = TimeUtils.nanoTime();
        if(now - lastFileLogTime < LOG_INTERVAL_NANOS) {
            return;
        }
        lastFileLogTime = now;

        if(output != null) {
            try {
                output.writeString("FPSLogger: fps: " + Gdx.graphics.getFramesPerSecond() + "\n", true);
            }
            catch(RuntimeException ignored) {
                output = null;
            }
        }
    }

    private FileHandle prepareOutput() {
        FileHandle[] candidates = {
                Gdx.files.local("fps.log"),
                Gdx.files.external("fps.log")
        };
        for(FileHandle candidate : candidates) {
            try {
                candidate.writeString("", false);
                return candidate;
            }
            catch(RuntimeException ignored) {
            }
        }
        return null;
    }
}
