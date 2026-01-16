package com.github.xpenatan.gdx.teavm.backends.glfw.audio;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.utils.Disposable;

public interface GLFWAudio extends Audio, Disposable {

    void update();
}
