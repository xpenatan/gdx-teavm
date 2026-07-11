package com.github.xpenatan.gdx.teavm.backends.glfw.graphics;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.utils.Disposable;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWWindow;

/**
 * Graphics lifecycle used by {@link GLFWWindow}.
 * <p>
 * Implementations may initialize asynchronously. The application listener is not created or rendered until
 * {@link #isReady()} returns {@code true}.
 */
public interface GLFWGraphics extends Graphics, Disposable {
    GLFWWindow getWindow();

    /** Returns whether the application listener may be initialized and rendered. */
    boolean isReady();

    /** Refreshes logical and framebuffer dimensions after a native-window resize. */
    void updateFramebufferInfo();

    /** Advances renderer initialization or per-frame state. */
    void update();

    /** Opens renderer state used while application listeners create, render, or dispose resources. */
    void beginFrame();

    /** Closes renderer state opened by {@link #beginFrame()} and presents when appropriate. */
    void endFrame();

    /** Makes this window current for the renderer. */
    void makeCurrent();

    /** Performs any renderer-specific initial framebuffer clear. */
    void initialClear();

    int getLogicalWidth();

    int getLogicalHeight();
}
