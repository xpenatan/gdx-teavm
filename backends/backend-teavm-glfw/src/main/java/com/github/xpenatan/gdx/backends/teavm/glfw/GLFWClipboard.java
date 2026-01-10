package com.github.xpenatan.gdx.backends.teavm.glfw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Clipboard;
import com.github.xpenatan.gdx.backends.teavm.glfw.utils.GLFW;

/**
 * Clipboard implementation for desktop that uses the system clipboard via GLFW.
 *
 * @author mzechner
 */
public class GLFWClipboard implements Clipboard {
    @Override
    public boolean hasContents() {
        String contents = getContents();
        return contents != null && !contents.isEmpty();
    }

    @Override
    public String getContents() {
        return GLFW.getClipboardString(((GLFWGraphics) Gdx.graphics).getWindow().getWindowHandle());
    }

    @Override
    public void setContents(String content) {
        GLFW.setClipboardString(((GLFWGraphics) Gdx.graphics).getWindow().getWindowHandle(), content);
    }
}
