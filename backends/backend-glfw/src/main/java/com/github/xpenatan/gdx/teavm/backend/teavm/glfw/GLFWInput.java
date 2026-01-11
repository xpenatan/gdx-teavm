package com.github.xpenatan.gdx.teavm.backend.teavm.glfw;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Disposable;

public interface GLFWInput extends Input, Disposable {

    void windowHandleChanged(long windowHandle);

    void update();

    void prepareNext();

    void resetPollingStates();
}
