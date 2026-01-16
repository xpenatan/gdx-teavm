package com.github.xpenatan.gdx.teavm.backends.shared.config;

public interface TeaBuildReflectionListener {
    boolean shouldEnableReflection(String fullClassName);
}
