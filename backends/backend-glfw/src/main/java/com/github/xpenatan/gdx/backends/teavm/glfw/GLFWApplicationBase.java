package com.github.xpenatan.gdx.backends.teavm.glfw;

import com.badlogic.gdx.Application;
import com.github.xpenatan.gdx.backends.teavm.glfw.audio.GLFWAudio;

public interface GLFWApplicationBase extends Application {

    GLFWAudio createAudio(GLFWApplicationConfiguration config);

    GLFWInput createInput(GLFWWindow window);
}
