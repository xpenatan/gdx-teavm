package com.github.xpenatan.gdx.teavm.backends.glfw.graphics;

import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWWindow;

/** Creates the graphics implementation associated with a GLFW window. */
@FunctionalInterface
public interface GLFWGraphicsFactory {
    /** Creates a non-null graphics implementation for the newly created native window. */
    GLFWGraphics create(GLFWWindow window);
}
