package com.github.xpenatan.gdx.teavm.backends.glfw;

import com.badlogic.gdx.Application;
import com.github.xpenatan.gdx.teavm.backends.glfw.audio.GLFWAudio;

public interface GLFWApplicationBase extends Application {

    GLFWAudio createAudio(GLFWApplicationConfiguration config);

    GLFWInput createInput(GLFWWindow window);
}
