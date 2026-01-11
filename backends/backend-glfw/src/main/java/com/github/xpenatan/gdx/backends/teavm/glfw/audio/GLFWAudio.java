package com.github.xpenatan.gdx.backends.teavm.glfw.audio;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.utils.Disposable;

public interface GLFWAudio extends Audio, Disposable {

    void update();
}
