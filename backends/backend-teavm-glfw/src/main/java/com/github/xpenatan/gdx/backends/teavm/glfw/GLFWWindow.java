package com.github.xpenatan.gdx.backends.teavm.glfw;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Os;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.github.xpenatan.gdx.backends.teavm.glfw.utils.GLFW;
import java.util.HashMap;
import java.util.Map;
import org.teavm.interop.Address;
import org.teavm.interop.Function;

public class GLFWWindow implements Disposable {
    private static final Map<Long, GLFWWindow> windows = new HashMap<>();
    private long windowHandle;
    final ApplicationListener listener;
    private final Array<LifecycleListener> lifecycleListeners;
    final GLFWApplicationBase application;
    private boolean listenerInitialized = false;
    GLFWWindowListener windowListener;
    private GLFWGraphics graphics;
    private GLFWInput input;
    private final GLFWApplicationConfiguration config;
    private final Array<Runnable> runnables = new Array<>();
    private final Array<Runnable> executedRunnables = new Array<>();
    private final int[] tmpBuffer;
    private final int[] tmpBuffer2;
    boolean iconified = false;
    boolean focused = false;
    boolean asyncResized = false;
    private boolean requestRendering = false;
    private boolean visible = false;

    public static GLFWWindow byAddress(Address windowHandle) {
        return windows.get(windowHandle.toLong());
    }

    private final GLFW.GLFWWindowFocusCallback focusCallback = Function.get(GLFW.GLFWWindowFocusCallback.class, GLFWWindow.class, "focusCallback")/* = new GLFWWindowFocusCallback() {
        @Override
        public void invoke(Address windowHandle, final boolean focused) {
            postRunnable(new Runnable() {
                @Override
                public void run() {
                    if (focused) {
                        if (config.pauseWhenLostFocus) {
                            synchronized (lifecycleListeners) {
                                for (LifecycleListener lifecycleListener : lifecycleListeners) {
                                    lifecycleListener.resume();
                                }
                            }
                            listener.resume();
                        }
                        if (windowListener != null) {
                            windowListener.focusGained();
                        }
                    } else {
                        if (windowListener != null) {
                            windowListener.focusLost();
                        }
                        if (config.pauseWhenLostFocus) {
                            synchronized (lifecycleListeners) {
                                for (LifecycleListener lifecycleListener : lifecycleListeners) {
                                    lifecycleListener.pause();
                                }
                            }
                            listener.pause();
                        }
                    }
                    NativeWindow.this.focused = focused;
                }
            });
        }
    }*/;

    public static void focusCallback(Address windowHandle, boolean focused) {
        try {
            GLFWWindow window = windows.get(windowHandle.toLong());
            window.postRunnable(() -> {
                if (focused) {
                    if (window.config.pauseWhenLostFocus) {
                        synchronized (window.lifecycleListeners) {
                            for (LifecycleListener lifecycleListener : window.lifecycleListeners) {
                                lifecycleListener.resume();
                            }
                        }
                        window.listener.resume();
                    }
                    if (window.windowListener != null) {
                        window.windowListener.focusGained();
                    }
                } else {
                    if (window.windowListener != null) {
                        window.windowListener.focusLost();
                    }
                    if (window.config.pauseWhenLostFocus) {
                        synchronized (window.lifecycleListeners) {
                            for (LifecycleListener lifecycleListener : window.lifecycleListeners) {
                                lifecycleListener.pause();
                            }
                        }
                        window.listener.pause();
                    }
                }
                window.focused = focused;
            });
        } catch (Throwable t) {
            Gdx.app.error("NativeWindow", "Error in focusCallback", t);
        }
    }

    private final GLFW.GLFWWindowIconifyCallback iconifyCallback = Function.get(GLFW.GLFWWindowIconifyCallback.class, GLFWWindow.class, "iconifyCallback");

    private final GLFW.GLFWWindowMaximizeCallback maximizeCallback = Function.get(GLFW.GLFWWindowMaximizeCallback.class, GLFWWindow.class, "maximizeCallback");

    private final GLFW.GLFWWindowCloseCallback closeCallback = Function.get(GLFW.GLFWWindowCloseCallback.class, GLFWWindow.class, "closeCallback");

    private final GLFW.GLFWDropCallback dropCallback = Function.get(GLFW.GLFWDropCallback.class, GLFWWindow.class, "dropCallback");

    private final GLFW.GLFWWindowRefreshCallback refreshCallback = Function.get(GLFW.GLFWWindowRefreshCallback.class, GLFWWindow.class, "refreshCallback");

    public static void iconifyCallback(Address windowHandle, boolean iconified) {
        try {
            GLFWWindow window = windows.get(windowHandle.toLong());
            window.postRunnable(() -> {
                if (window.windowListener != null) {
                    window.windowListener.iconified(iconified);
                }
                window.iconified = iconified;
                if (iconified) {
                    if (window.config.pauseWhenMinimized) {
                        synchronized (window.lifecycleListeners) {
                            for (LifecycleListener lifecycleListener : window.lifecycleListeners) {
                                lifecycleListener.pause();
                            }
                        }
                        window.listener.pause();
                    }
                } else {
                    if (window.config.pauseWhenMinimized) {
                        synchronized (window.lifecycleListeners) {
                            for (LifecycleListener lifecycleListener : window.lifecycleListeners) {
                                lifecycleListener.resume();
                            }
                        }
                        window.listener.resume();
                    }
                }
            });
        } catch (Throwable t) {
            Gdx.app.error("NativeWindow", "Error in iconifyCallback", t);
        }
    }

    public static void maximizeCallback(Address windowHandle, boolean maximized) {
        try {
            GLFWWindow window = windows.get(windowHandle.toLong());
            window.postRunnable(() -> {
                if (window.windowListener != null) {
                    window.windowListener.maximized(maximized);
                }
            });
        } catch (Throwable t) {
            Gdx.app.error("NativeWindow", "Error in maximizeCallback", t);
        }
    }

    public static void closeCallback(Address windowHandle) {
        try {
            GLFWWindow window = windows.get(windowHandle.toLong());
            window.postRunnable(() -> {
                if (window.windowListener != null) {
                    if (!window.windowListener.closeRequested()) {
                        GLFW.setWindowShouldClose(windowHandle.toLong(), false);
                    }
                }
            });
        } catch (Throwable t) {
            Gdx.app.error("NativeWindow", "Error in closeCallback", t);
        }
    }

    public static void dropCallback(Address windowHandle, int count, long names) {
        try {
            GLFWWindow window = windows.get(windowHandle.toLong());
            final String[] files = GLFW.dropNames(count, names);
            window.postRunnable(() -> {
                if (window.windowListener != null) {
                    window.windowListener.filesDropped(files);
                }
            });
        } catch (Throwable t) {
            Gdx.app.error("NativeWindow", "Error in dropCallback", t);
        }
    }

    public static void refreshCallback(Address windowHandle) {
        try {
            GLFWWindow window = windows.get(windowHandle.toLong());
            window.postRunnable(() -> {
                if (window.windowListener != null) {
                    window.windowListener.refreshRequested();
                }
            });
        } catch (Throwable t) {
            Gdx.app.error("NativeWindow", "Error in refreshCallback", t);
        }
    }
    GLFWWindow(ApplicationListener listener, Array<LifecycleListener> lifecycleListeners, GLFWApplicationConfiguration config,
               GLFWApplicationBase application) {
        this.listener = listener;
        this.lifecycleListeners = lifecycleListeners;
        this.windowListener = config.windowListener;
        this.config = config;
        this.application = application;
        this.tmpBuffer = new int[1];
        this.tmpBuffer2 = new int[1];
    }

    void create(long windowHandle) {
        this.windowHandle = windowHandle;
        this.input = application.createInput(this);
        this.graphics = new GLFWGraphics(this);

        windows.put(windowHandle, this);

        GLFW.setWindowFocusCallback(windowHandle, focusCallback);
        GLFW.setWindowIconifyCallback(windowHandle, iconifyCallback);
        GLFW.setWindowMaximizeCallback(windowHandle, maximizeCallback);
        GLFW.setWindowCloseCallback(windowHandle, closeCallback);
        GLFW.setDropCallback(windowHandle, dropCallback);
        GLFW.setWindowRefreshCallback(windowHandle, refreshCallback);

        if (windowListener != null) {
            windowListener.created(this);
        }
    }

    /**
     * @return the {@link ApplicationListener} associated with this window
     **/
    public ApplicationListener getListener() {
        return listener;
    }

    /**
     * @return the {@link GLFWWindowListener} set on this window
     **/
    public GLFWWindowListener getWindowListener() {
        return windowListener;
    }

    public void setWindowListener(GLFWWindowListener listener) {
        this.windowListener = listener;
    }

    /**
     * Post a {@link Runnable} to this window's event queue. Use this if you access statics like {@link Gdx#graphics} in your
     * runnable instead of {@link Application#postRunnable(Runnable)}.
     */
    public void postRunnable(Runnable runnable) {
        synchronized (runnables) {
            runnables.add(runnable);
        }
    }

    /**
     * Sets the position of the window in logical coordinates. All monitors span a virtual surface together. The coordinates are
     * relative to the first monitor in the virtual surface.
     **/
    public void setPosition(int x, int y) {
        // Check for wayland
        if (System.getenv("WAYLAND_DISPLAY") != null) return;
        GLFW.setWindowPos(windowHandle, x, y);
    }

    /**
     * @return the window position in logical coordinates. All monitors span a virtual surface together. The coordinates are
     * relative to the first monitor in the virtual surface.
     **/
    public int getPositionX() {
        GLFW.getWindowPos(windowHandle, tmpBuffer, tmpBuffer2);
        return tmpBuffer[0];
    }

    /**
     * @return the window position in logical coordinates. All monitors span a virtual surface together. The coordinates are
     * relative to the first monitor in the virtual surface.
     **/
    public int getPositionY() {
        GLFW.getWindowPos(windowHandle, tmpBuffer, tmpBuffer2);
        return tmpBuffer2[0];
    }

    /**
     * Sets the visibility of the window. Invisible windows will still call their {@link ApplicationListener}
     */
    public void setVisible(boolean visible) {
        application.error("Gdx", "Experimental: setVisible is not yet implemented properly. Please report any bugs you find with it.");
        if (visible && !this.visible) {
            GLFW.setWindowAttrib(windowHandle, GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE);
        } else if (!visible && this.visible) {
            GLFW.setWindowAttrib(windowHandle, GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        }
        this.visible = visible;
    }

    /**
     * Closes this window and pauses and disposes the associated {@link ApplicationListener}.
     */
    public void closeWindow() {
        GLFW.setWindowShouldClose(windowHandle, true);
    }

    /**
     * Minimizes (iconifies) the window. Iconified windows do not call their {@link ApplicationListener} until the window is
     * restored.
     */
    public void iconifyWindow() {
        GLFW.iconifyWindow(windowHandle);
    }

    /**
     * Whether the window is iconfieid
     */
    public boolean isIconified() {
        return iconified;
    }

    /**
     * De-minimizes (de-iconifies) and de-maximizes the window.
     */
    public void restoreWindow() {
        GLFW.restoreWindow(windowHandle);
    }

    /**
     * Maximizes the window.
     */
    public void maximizeWindow() {
        GLFW.maximizeWindow(windowHandle);
    }

    /**
     * Brings the window to front and sets input focus. The window should already be visible and not iconified.
     */
    public void focusWindow() {
        GLFW.focusWindow(windowHandle);
    }

    public boolean isFocused() {
        return focused;
    }

    /**
     * Sets the icon that will be used in the window's title bar. Has no effect in macOS, which doesn't use window icons.
     *
     * @param image One or more images. The one closest to the system's desired size will be scaled. Good sizes include 16x16,
     *              32x32 and 48x48. Pixmap format {@link Pixmap.Format#RGBA8888 RGBA8888} is preferred so
     *              the images will not have to be copied and converted. The chosen image is copied, and the provided Pixmaps are not
     *              disposed.
     */
    public void setIcon(Pixmap... image) {
        setIcon(windowHandle, image);
    }

    static void setIcon(long windowHandle, String[] imagePaths, Files.FileType imageFileType) {
        if (SharedLibraryLoader.os == Os.MacOsX) return;

        Pixmap[] pixmaps = new Pixmap[imagePaths.length];
        for (int i = 0; i < imagePaths.length; i++) {
            pixmaps[i] = new Pixmap(Gdx.files.getFileHandle(imagePaths[i], imageFileType));
        }

        setIcon(windowHandle, pixmaps);

        for (Pixmap pixmap : pixmaps) {
            pixmap.dispose();
        }
    }

    static void setIcon(long windowHandle, Pixmap[] images) {
//        if (SharedLibraryLoader.os == Os.MacOsX) return;
//        if (getPlatform() == GLFW_PLATFORM_WAYLAND) return;
//
//        GLFWImage.Buffer buffer = GLFWImage.malloc(images.length);
//        Pixmap[] tmpPixmaps = new Pixmap[images.length];
//
//        for (int i = 0; i < images.length; i++) {
//            Pixmap pixmap = images[i];
//
//            if (pixmap.getFormat() != Pixmap.Format.RGBA8888) {
//                Pixmap rgba = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);
//                rgba.setBlending(Pixmap.Blending.None);
//                rgba.drawPixmap(pixmap, 0, 0);
//                tmpPixmaps[i] = rgba;
//                pixmap = rgba;
//            }
//
//            GLFWImage icon = GLFWImage.malloc();
//            icon.set(pixmap.getWidth(), pixmap.getHeight(), pixmap.getPixels());
//            buffer.put(icon);
//
//            icon.free();
//        }
//
//        setWindowIcon(windowHandle, buffer.buffer.length, buffer.toAddress().toLong());
//
//        buffer.free();
//        for (Pixmap pixmap : tmpPixmaps) {
//            if (pixmap != null) {
//                pixmap.dispose();
//            }
//        }
    }

    public void setTitle(CharSequence title) {
        GLFW.setWindowTitle(windowHandle, title.toString());
    }

    /**
     * Sets minimum and maximum size limits for the window. If the window is full screen or not resizable, these limits are
     * ignored. Use -1 to indicate an unrestricted dimension.
     */
    public void setSizeLimits(int minWidth, int minHeight, int maxWidth, int maxHeight) {
        setSizeLimits(windowHandle, minWidth, minHeight, maxWidth, maxHeight);
    }

    static void setSizeLimits(long windowHandle, int minWidth, int minHeight, int maxWidth, int maxHeight) {
        GLFW.setWindowSizeLimits(windowHandle, minWidth > -1 ? minWidth : GLFW.GLFW_DONT_CARE,
                minHeight > -1 ? minHeight : GLFW.GLFW_DONT_CARE, maxWidth > -1 ? maxWidth : GLFW.GLFW_DONT_CARE,
                maxHeight > -1 ? maxHeight : GLFW.GLFW_DONT_CARE);
    }

    GLFWGraphics getGraphics() {
        return graphics;
    }

    GLFWInput getInput() {
        return input;
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    void windowHandleChanged(Address windowHandle) {
        this.windowHandle = windowHandle.toLong();
        input.windowHandleChanged(windowHandle.toLong());
    }

    boolean update() {
        if (!listenerInitialized) {
            initializeListener();
        }
        synchronized (runnables) {
            executedRunnables.addAll(runnables);
            runnables.clear();
        }
        for (Runnable runnable : executedRunnables) {
            runnable.run();
        }
        boolean shouldRender = executedRunnables.size > 0 || graphics.isContinuousRendering();
        executedRunnables.clear();

        if (!iconified) input.update();

        synchronized (this) {
            shouldRender |= requestRendering && !iconified;
            requestRendering = false;
        }

        // In case glfw_async is used, we need to resize outside the GLFW
        if (asyncResized) {
            asyncResized = false;
            graphics.updateFramebufferInfo();
            graphics.gl20.glViewport(0, 0, graphics.getBackBufferWidth(), graphics.getBackBufferHeight());
            listener.resize(graphics.getWidth(), graphics.getHeight());
            graphics.update();
            listener.render();
            GLFW.swapBuffers(windowHandle);
            return true;
        }

        if (shouldRender) {
            graphics.update();
            listener.render();
            GLFW.swapBuffers(windowHandle);
        }

        if (!iconified) input.prepareNext();

        return shouldRender;
    }

    void requestRendering() {
        synchronized (this) {
            this.requestRendering = true;
        }
    }

    boolean shouldClose() {
        return GLFW.windowShouldClose(windowHandle);
    }

    GLFWApplicationConfiguration getConfig() {
        return config;
    }

    boolean isListenerInitialized() {
        return listenerInitialized;
    }

    void initializeListener() {
        if (!listenerInitialized) {
            listener.create();
            listener.resize(graphics.getWidth(), graphics.getHeight());
            listenerInitialized = true;
        }
    }

    void makeCurrent() {
        Gdx.graphics = graphics;
        Gdx.gl32 = graphics.getGL32();
        Gdx.gl31 = Gdx.gl32 != null ? Gdx.gl32 : graphics.getGL31();
        Gdx.gl30 = Gdx.gl31 != null ? Gdx.gl31 : graphics.getGL30();
        Gdx.gl20 = Gdx.gl30 != null ? Gdx.gl30 : graphics.getGL20();
        Gdx.gl = Gdx.gl20;
        Gdx.input = input;

        GLFW.makeContextCurrent(windowHandle);
    }

    @Override
    public void dispose() {
        listener.pause();
        listener.dispose();
        GLFWCursor.dispose(this);
        graphics.dispose();
        input.dispose();
        GLFW.setWindowFocusCallback(windowHandle, null);
        GLFW.setWindowIconifyCallback(windowHandle, null);
        GLFW.setWindowCloseCallback(windowHandle, null);
        GLFW.setDropCallback(windowHandle, null);
        GLFW.destroyWindow(windowHandle);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (windowHandle ^ (windowHandle >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        GLFWWindow other = (GLFWWindow) obj;
        if (windowHandle != other.windowHandle) return false;
        return true;
    }

    public void flash() {
        GLFW.requestWindowAttention(windowHandle);
    }
}
