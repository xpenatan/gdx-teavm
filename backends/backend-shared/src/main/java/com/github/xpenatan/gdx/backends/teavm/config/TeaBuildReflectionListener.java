package com.github.xpenatan.gdx.backends.teavm.config;

public interface TeaBuildReflectionListener {
    boolean shouldEnableReflection(String fullClassName);
}
