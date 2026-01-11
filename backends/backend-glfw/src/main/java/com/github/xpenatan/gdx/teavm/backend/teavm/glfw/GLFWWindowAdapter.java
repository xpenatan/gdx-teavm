
package com.github.xpenatan.gdx.teavm.backend.teavm.glfw;

/**
 * Convenience implementation of {@link GLFWWindowListener}. Derive from this class and only overwrite the methods you are
 * interested in.
 *
 * @author badlogic
 */
public class GLFWWindowAdapter implements GLFWWindowListener {
    @Override
    public void created(GLFWWindow window) {
    }

    @Override
    public void iconified(boolean isIconified) {
    }

    @Override
    public void maximized(boolean isMaximized) {
    }

    @Override
    public void focusLost() {
    }

    @Override
    public void focusGained() {
    }

    @Override
    public boolean closeRequested() {
        return true;
    }

    @Override
    public void filesDropped(String[] files) {
    }

    @Override
    public void refreshRequested() {
    }
}
