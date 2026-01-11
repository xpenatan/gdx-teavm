package com.github.xpenatan.gdx.teavm.backend.shared.config;

public interface TeaBuildReflectionListener {
    boolean shouldEnableReflection(String fullClassName);
}
