package com.github.xpenatan.gdx.teavm.backends.glfw;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Os;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.github.xpenatan.gdx.teavm.backends.glfw.audio.GLFWAudio;
import com.github.xpenatan.gdx.teavm.backends.glfw.audio.mock.MockAudio;
import com.github.xpenatan.gdx.teavm.backends.glfw.utils.Callback;
import com.github.xpenatan.gdx.teavm.backends.glfw.utils.GLFW;
import com.github.xpenatan.gdx.teavm.backends.glfw.utils.GLUtil;
import com.github.xpenatan.gdx.teavm.backends.glfw.utils.OpenGL;
import com.github.xpenatan.gdx.teavm.backends.shared.SharedApplicationLogger;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;

public class GLFWApplication implements GLFWApplicationBase {
    private final GLFWApplicationConfiguration config;
    final Array<GLFWWindow> windows = new Array<>();
    private volatile GLFWWindow currentWindow;
    private GLFWAudio audio;
    private final Files files;
    private final Net net;
    private final ObjectMap<String, Preferences> preferences = new ObjectMap<>();
    private final GLFWClipboard clipboard;
    private int logLevel = LOG_INFO;
    private ApplicationLogger applicationLogger;
    private volatile boolean running = true;
    private final Array<Runnable> runnables = new Array<>();
    private final Array<Runnable> executedRunnables = new Array<>();
    private final Array<LifecycleListener> lifecycleListeners = new Array<>();
    private static GLFW.GLFWErrorCallback errorCallback;
    private static GLVersion glVersion;
    private static Callback glDebugCallback;
    private int targetFramerate = -2;
    private boolean haveWindowsRendered = false;
    private final Array<GLFWWindow> closedWindows = new Array<>();

    static void initializeGlfw() {
        if (errorCallback == null) {
            GLFWNativesLoader.load();
            errorCallback = GLFW.createPrint();
            GLFW.setErrorCallback(errorCallback);
            if (SharedLibraryLoader.os == Os.MacOsX)
                GLFW.initHint(GLFW.GLFW_ANGLE_PLATFORM_TYPE, GLFW.GLFW_ANGLE_PLATFORM_TYPE_METAL);
            GLFW.initHint(GLFW.GLFW_JOYSTICK_HAT_BUTTONS, GLFW.GLFW_FALSE);
            if (!GLFW.init()) {
                throw new GdxRuntimeException("Unable to initialize GLFW");
            }
        }
    }

    static void loadANGLE() {
        try {
            Class angleLoader = Class.forName("com.badlogic.gdx.backends.lwjgl3.angle.ANGLELoader");
            Method load = angleLoader.getMethod("load");
            load.invoke(angleLoader);
        } catch (ClassNotFoundException t) {
            return;
        } catch (Throwable t) {
            throw new GdxRuntimeException("Couldn't load ANGLE.", t);
        }
    }

    static void postLoadANGLE() {
        try {
            Class angleLoader = Class.forName("com.badlogic.gdx.backends.lwjgl3.angle.ANGLELoader");
            Method load = angleLoader.getMethod("postGlfwInit");
            load.invoke(angleLoader);
        } catch (ClassNotFoundException t) {
            return;
        } catch (Throwable t) {
            throw new GdxRuntimeException("Couldn't load ANGLE.", t);
        }
    }

    public GLFWApplication(ApplicationListener listener) {
        this(listener, new GLFWApplicationConfiguration());
    }

    public GLFWApplication(ApplicationListener listener, GLFWApplicationConfiguration config) {
        if (config.glEmulation == GLFWApplicationConfiguration.GLEmulation.ANGLE_GLES20) loadANGLE();
        initializeGlfw();
        setApplicationLogger(new SharedApplicationLogger());

        this.config = config = GLFWApplicationConfiguration.copy(config);
        if (config.title == null) config.title = listener.getClass().getSimpleName();

        Gdx.app = this;
        if (!config.disableAudio) {
            try {
                this.audio = createAudio(config);
            } catch (Throwable t) {
                log("Lwjgl3Application", "Couldn't initialize audio, disabling audio", t);
                this.audio = new MockAudio();
            }
        } else {
            this.audio = new MockAudio();
        }
        Gdx.audio = audio;
        this.files = Gdx.files = createFiles();
        this.net = Gdx.net = new GLFWNet(config);
        this.clipboard = new GLFWClipboard();

        GLFWWindow window = createWindow(config, listener, 0);
        if (config.glEmulation == GLFWApplicationConfiguration.GLEmulation.ANGLE_GLES20) postLoadANGLE();
        windows.add(window);
        try {
            loop();
            cleanupWindows();
        } catch (Throwable t) {
            t.printStackTrace(System.out);
        } finally {
            cleanup();
        }
    }

    private boolean isWindowRendered(boolean isRendered, boolean update) {
        boolean flag = isRendered | update;
        return flag;
    }

    int frame = 0;

    protected void loop() {

        while (running && windows.size > 0) {
            // FIXME put it on a separate thread
            audio.update();

            haveWindowsRendered = false;
            closedWindows.clear();
            loopWindow();
            GLFW.pollEvents();

            if (loopRunnables()) {
                // Must follow Runnables execution so changes done by Runnables are reflected
                // in the following render.
                for (GLFWWindow window : windows) {
                    if (!window.getGraphics().isContinuousRendering()) window.requestRendering();
                }
            }
            loopClosedWindows();
            loopSleep();
        }
    }

    private void loopClosedWindows() {
        int size = closedWindows.size;
        int ci = 0;
        while(ci < size) {
            GLFWWindow closedWindow = closedWindows.get(ci);
            if (windows.size == 1) {
                // Lifecycle listener methods have to be called before ApplicationListener methods. The
                // application will be disposed when _all_ windows have been disposed, which is the case,
                // when there is only 1 window left, which is in the process of being disposed.
                loopLifeCycle();
            }
            closedWindow.dispose();

            windows.removeValue(closedWindow, false);
            ci++;
        }
    }

    private void loopSleep() {
        if (!haveWindowsRendered) {
            // Sleep a few milliseconds in case no rendering was requested
            // with continuous rendering disabled.
            try {
                Thread.sleep(1000 / config.idleFPS);
            } catch (InterruptedException e) {
                // ignore
            }
        } else if (targetFramerate > 0) {
//            sync.sync(targetFramerate); // sleep as needed to meet the target framerate
        }
    }

    private void loopLifeCycle() {
        for (int i = lifecycleListeners.size - 1; i >= 0; i--) {
            LifecycleListener l = lifecycleListeners.get(i);
            l.pause();
            l.dispose();
        }
        lifecycleListeners.clear();
    }

    private boolean loopRunnables() {
        boolean shouldRequestRendering = runnables.size > 0;
        executedRunnables.clear();
        executedRunnables.addAll(runnables);
        runnables.clear();

        int c = 0;
        int size = executedRunnables.size;
        while(c < size) {
            Runnable runnable = executedRunnables.get(c);
            runnable.run();
            c++;
        }
        return shouldRequestRendering;
    }

    private void loopWindow() {
        int wi = 0;
        targetFramerate = -2;
        while(wi < windows.size) {
            GLFWWindow window = windows.get(wi);
            if (currentWindow != window) {
                window.makeCurrent();
                currentWindow = window;
            }
            if (targetFramerate == -2)  {
                targetFramerate = window.getConfig().foregroundFPS;
            }
            haveWindowsRendered = isWindowRendered(haveWindowsRendered, window.update());
            if (window.shouldClose()) {
                closedWindows.add(window);
            }
            wi++;
        }
    }

    protected void cleanupWindows() {
        for (LifecycleListener lifecycleListener : lifecycleListeners) {
            lifecycleListener.pause();
            lifecycleListener.dispose();
        }
        for (GLFWWindow window : windows) {
            window.dispose();
        }
        windows.clear();
    }

    protected void cleanup() {
        GLFWCursor.disposeSystemCursors();
        audio.dispose();
        GLFW.setErrorCallback(null);
        errorCallback = null;
        if (glDebugCallback != null) {
            GLFW.setDebugCallback(null);
            glDebugCallback = null;
        }
        GLFW.terminate();
    }

    @Override
    public ApplicationListener getApplicationListener() {
        return currentWindow.getListener();
    }

    @Override
    public Graphics getGraphics() {
        return currentWindow.getGraphics();
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    public Input getInput() {
        return currentWindow.getInput();
    }

    @Override
    public Files getFiles() {
        return files;
    }

    @Override
    public Net getNet() {
        return net;
    }

    @Override
    public void debug(String tag, String message) {
        if (logLevel >= LOG_DEBUG) getApplicationLogger().debug(tag, message);
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_DEBUG) getApplicationLogger().debug(tag, message, exception);
    }

    @Override
    public void log(String tag, String message) {
        if (logLevel >= LOG_INFO) getApplicationLogger().log(tag, message);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_INFO) getApplicationLogger().log(tag, message, exception);
    }

    @Override
    public void error(String tag, String message) {
        if (logLevel >= LOG_ERROR) getApplicationLogger().error(tag, message);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_ERROR) getApplicationLogger().error(tag, message, exception);
    }

    @Override
    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public int getLogLevel() {
        return logLevel;
    }

    @Override
    public void setApplicationLogger(ApplicationLogger applicationLogger) {
        this.applicationLogger = applicationLogger;
    }

    @Override
    public ApplicationLogger getApplicationLogger() {
        return applicationLogger;
    }

    @Override
    public ApplicationType getType() {
        return ApplicationType.Desktop;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public long getJavaHeap() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    @Override
    public long getNativeHeap() {
        return getJavaHeap();
    }

    @Override
    public Preferences getPreferences(String name) {
        if (preferences.containsKey(name)) {
            return preferences.get(name);
        } else {
            Preferences prefs = new GLFWPreferences(
                    new GLFWFileHandle(new File(config.preferencesDirectory, name), config.preferencesFileType));
            preferences.put(name, prefs);
            return prefs;
        }
    }

    @Override
    public Clipboard getClipboard() {
        return clipboard;
    }

    @Override
    public void postRunnable(Runnable runnable) {
        runnables.add(runnable);
    }

    @Override
    public void exit() {
        running = false;
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
            lifecycleListeners.add(listener);
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
            lifecycleListeners.removeValue(listener, true);
    }

    @Override
    public GLFWAudio createAudio(GLFWApplicationConfiguration config) {
//		return new OpenALNativeAudio(config.audioDeviceSimultaneousSources, config.audioDeviceBufferCount,
//			config.audioDeviceBufferSize);
        return new MockAudio();
    }

    @Override
    public GLFWInput createInput(GLFWWindow window) {
        return new GLFWDefaultInput(window);
    }

    protected Files createFiles() {
        return new GLFWFiles();
    }

    /**
     * Creates a new {@link GLFWWindow} using the provided listener and {@link GLFWWindowConfiguration}.
     * <p>
     * This function only just instantiates a {@link GLFWWindow} and returns immediately. The actual window creation is postponed
     * with {@link Application#postRunnable(Runnable)} until after all existing windows are updated.
     */
    public GLFWWindow newWindow(ApplicationListener listener, GLFWWindowConfiguration config) {
        GLFWApplicationConfiguration appConfig = GLFWApplicationConfiguration.copy(this.config);
        appConfig.setWindowConfiguration(config);
        if (appConfig.title == null) appConfig.title = listener.getClass().getSimpleName();
        return createWindow(appConfig, listener, windows.get(0).getWindowHandle());
    }

    private GLFWWindow createWindow(final GLFWApplicationConfiguration config, ApplicationListener listener,
                                    final long sharedContext) {
        final GLFWWindow window = new GLFWWindow(listener, lifecycleListeners, config, this);
        if (sharedContext == 0) {
            // the main window is created immediately
            createWindow(window, config, sharedContext);
        } else {
            // creation of additional windows is deferred to avoid GL context trouble
            postRunnable(new Runnable() {
                public void run() {
                    createWindow(window, config, sharedContext);
                    windows.add(window);
                }
            });
        }
        return window;
    }

    void createWindow(GLFWWindow window, GLFWApplicationConfiguration config, long sharedContext) {
        long windowHandle = createwindow(config, sharedContext);
        window.create(windowHandle);

        for (int i = 0; i < 2; i++) {
            window.getGraphics().gl20.glClearColor(config.initialBackgroundColor.r, config.initialBackgroundColor.g,
                    config.initialBackgroundColor.b, config.initialBackgroundColor.a);
            window.getGraphics().gl20.glClear(OpenGL.GL_COLOR_BUFFER_BIT);
            GLFW.swapBuffers(windowHandle);
        }

        if (currentWindow != null) {
            // the call above to createwindow switches the OpenGL context to the newly created window,
            // ensure that the invariant "currentWindow is the window with the current active OpenGL context" holds
            currentWindow.makeCurrent();
        }

        OpenGL.glewInit();
    }

    static long createwindow(GLFWApplicationConfiguration config, long sharedContextWindow) {
        GLFW.defaultWindowHints();
        GLFW.windowHint(GLFW.GLFW_VISIBLE, config.initialVisible ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.windowHint(GLFW.GLFW_RESIZABLE, config.windowResizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.windowHint(GLFW.GLFW_MAXIMIZED, config.windowMaximized ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.windowHint(GLFW.GLFW_AUTO_ICONIFY, config.autoIconify ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);

        GLFW.windowHint(GLFW.GLFW_RED_BITS, config.r);
        GLFW.windowHint(GLFW.GLFW_GREEN_BITS, config.g);
        GLFW.windowHint(GLFW.GLFW_BLUE_BITS, config.b);
        GLFW.windowHint(GLFW.GLFW_ALPHA_BITS, config.a);
        GLFW.windowHint(GLFW.GLFW_STENCIL_BITS, config.stencil);
        GLFW.windowHint(GLFW.GLFW_DEPTH_BITS, config.depth);
        GLFW.windowHint(GLFW.GLFW_SAMPLES, config.samples);

        if (config.glEmulation == GLFWApplicationConfiguration.GLEmulation.GL30
                || config.glEmulation == GLFWApplicationConfiguration.GLEmulation.GL31
                || config.glEmulation == GLFWApplicationConfiguration.GLEmulation.GL32) {
            GLFW.windowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, config.gles30ContextMajorVersion);
            GLFW.windowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, config.gles30ContextMinorVersion);
            if (SharedLibraryLoader.os == Os.MacOsX) {
                // hints mandatory on OS X for GL 3.2+ context creation, but fail on Windows if the
                // WGL_ARB_create_context extension is not available
                // see: http://www.glfw.org/docs/latest/compat.html
                GLFW.windowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
                GLFW.windowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
            }
        } else {
            if (config.glEmulation == GLFWApplicationConfiguration.GLEmulation.ANGLE_GLES20) {
                GLFW.windowHint(GLFW.GLFW_CONTEXT_CREATION_API, GLFW.GLFW_EGL_CONTEXT_API);
                GLFW.windowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_OPENGL_ES_API);
                GLFW.windowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 2);
                GLFW.windowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0);
            }
        }

        if (config.transparentFramebuffer) {
            GLFW.windowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, GLFW.GLFW_TRUE);
        }

        if (config.debug) {
            GLFW.windowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);
        }

        long windowHandle = 0;

        if (config.fullscreenMode != null) {
            GLFW.windowHint(GLFW.GLFW_REFRESH_RATE, config.fullscreenMode.refreshRate);
            windowHandle = GLFW.createWindow(config.fullscreenMode.width, config.fullscreenMode.height, config.title,
                    config.fullscreenMode.getMonitor(), sharedContextWindow);
        } else {
            GLFW.windowHint(GLFW.GLFW_DECORATED, config.windowDecorated ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
            windowHandle = GLFW.createWindow(config.windowWidth, config.windowHeight, config.title, 0, sharedContextWindow);
        }
        if (windowHandle == 0) {
            throw new GdxRuntimeException("Couldn't create window");
        }
        GLFWWindow.setSizeLimits(windowHandle, config.windowMinWidth, config.windowMinHeight, config.windowMaxWidth,
                config.windowMaxHeight);
        if (config.fullscreenMode == null) {
            // Check for wayland
            if (System.getenv("WAYLAND_DISPLAY") != null) {
                if (config.windowX == -1 && config.windowY == -1) { // i.e., center the window
                    int windowWidth = Math.max(config.windowWidth, config.windowMinWidth);
                    int windowHeight = Math.max(config.windowHeight, config.windowMinHeight);
                    if (config.windowMaxWidth > -1) windowWidth = Math.min(windowWidth, config.windowMaxWidth);
                    if (config.windowMaxHeight > -1) windowHeight = Math.min(windowHeight, config.windowMaxHeight);

                    long monitorHandle = GLFW.getPrimaryMonitor();
                    if (config.windowMaximized && config.maximizedMonitor != null) {
                        monitorHandle = config.maximizedMonitor.monitorHandle;
                    }

                    GridPoint2 newPos = GLFWApplicationConfiguration.calculateCenteredWindowPosition(
                            GLFWApplicationConfiguration.toNativeMonitor(monitorHandle), windowWidth, windowHeight);
                    GLFW.setWindowPos(windowHandle, newPos.x, newPos.y);
                } else {
                    GLFW.setWindowPos(windowHandle, config.windowX, config.windowY);
                }
            }

            if (config.windowMaximized) {
                GLFW.maximizeWindow(windowHandle);
            }
        }
        if (config.windowIconPaths != null) {
            GLFWWindow.setIcon(windowHandle, config.windowIconPaths, config.windowIconFileType);
        }
        GLFW.makeContextCurrent(windowHandle);
        GLFW.swapInterval(config.vSyncEnabled ? 1 : 0);
        if (config.glEmulation == GLFWApplicationConfiguration.GLEmulation.ANGLE_GLES20) {
            try {
                Class gles = Class.forName("org.lwjgl.opengles.GLES");
                gles.getMethod("createCapabilities").invoke(gles);
            } catch (Throwable e) {
                throw new GdxRuntimeException("Couldn't initialize GLES", e);
            }
        }

        initiateGL(config.glEmulation == GLFWApplicationConfiguration.GLEmulation.ANGLE_GLES20);
        if (!glVersion.isVersionEqualToOrHigher(2, 0))
            throw new GdxRuntimeException("OpenGL 2.0 or higher with the FBO extension is required. OpenGL version: "
                    + glVersion.getVersionString() + "\n" + glVersion.getDebugVersionString());

        if (config.glEmulation != GLFWApplicationConfiguration.GLEmulation.ANGLE_GLES20 && !supportsFBO()) {
            throw new GdxRuntimeException("OpenGL 2.0 or higher with the FBO extension is required. OpenGL version: "
                    + glVersion.getVersionString() + ", FBO extension: false\n" + glVersion.getDebugVersionString());
        }

        if (config.debug) {
            if (config.glEmulation == GLFWApplicationConfiguration.GLEmulation.ANGLE_GLES20) {
                throw new IllegalStateException(
                        "ANGLE currently can't be used with with Lwjgl3ApplicationConfiguration#enableGLDebugOutput");
            }
            glDebugCallback = GLUtil.setupDebugMessageCallback(config.debugStream);
//			setGLDebugMessageControl(GLDebugMessageSeverity.NOTIFICATION, false);
        }

        return windowHandle;
    }

    private static void initiateGL(boolean useGLES20) {
        if (!useGLES20) {
            String versionString = OpenGL.glGetString(OpenGL.GL_VERSION);
            String vendorString = OpenGL.glGetString(OpenGL.GL_VENDOR);
            String rendererString = OpenGL.glGetString(OpenGL.GL_RENDERER);
            glVersion = new GLVersion(ApplicationType.Desktop, versionString, vendorString, rendererString);
        } else {
            try {
                Class gles = Class.forName("org.lwjgl.opengles.GLES20");
                Method getString = gles.getMethod("glGetString", int.class);
                String versionString = (String) getString.invoke(gles, OpenGL.GL_VERSION);
                String vendorString = (String) getString.invoke(gles, OpenGL.GL_VENDOR);
                String rendererString = (String) getString.invoke(gles, OpenGL.GL_RENDERER);
                glVersion = new GLVersion(ApplicationType.Desktop, versionString, vendorString, rendererString);
            } catch (Throwable e) {
                throw new GdxRuntimeException("Couldn't get GLES version string.", e);
            }
        }
    }

    private static boolean supportsFBO() {
        // FBO is in core since OpenGL 3.0, see https://www.opengl.org/wiki/Framebuffer_Object
        return glVersion.isVersionEqualToOrHigher(3, 0) || GLFW.extensionSupported("GL_EXT_framebuffer_object")
                || GLFW.extensionSupported("GL_ARB_framebuffer_object");
    }

    public GLFWApplicationConfiguration getConfig() {
        return config;
    }

    public enum GLDebugMessageSeverity {
//		HIGH(OpenGL.GL_DEBUG_SEVERITY_HIGH, KHRDebug.GL_DEBUG_SEVERITY_HIGH, ARBDebugOutput.GL_DEBUG_SEVERITY_HIGH_ARB,
//			AMDDebugOutput.GL_DEBUG_SEVERITY_HIGH_AMD), MEDIUM(OpenGL.GL_DEBUG_SEVERITY_MEDIUM, KHRDebug.GL_DEBUG_SEVERITY_MEDIUM,
//				ARBDebugOutput.GL_DEBUG_SEVERITY_MEDIUM_ARB, AMDDebugOutput.GL_DEBUG_SEVERITY_MEDIUM_AMD), LOW(
//					OpenGL.GL_DEBUG_SEVERITY_LOW, KHRDebug.GL_DEBUG_SEVERITY_LOW, ARBDebugOutput.GL_DEBUG_SEVERITY_LOW_ARB,
//					AMDDebugOutput.GL_DEBUG_SEVERITY_LOW_AMD), NOTIFICATION(OpenGL.GL_DEBUG_SEVERITY_NOTIFICATION,
//						KHRDebug.GL_DEBUG_SEVERITY_NOTIFICATION, -1, -1);
        ;

        final int gl43, khr, arb, amd;

        GLDebugMessageSeverity(int gl43, int khr, int arb, int amd) {
            this.gl43 = gl43;
            this.khr = khr;
            this.arb = arb;
            this.amd = amd;
        }
    }

    /**
     * Enables or disables GL debug messages for the specified severity level. Returns false if the severity level could not be
     * set (e.g. the NOTIFICATION level is not supported by the ARB and AMD extensions).
     * <p>
     * See {@link GLFWApplicationConfiguration#enableGLDebugOutput(boolean, PrintStream)}
     */
    public static boolean setGLDebugMessageControl(GLDebugMessageSeverity severity, boolean enabled) {
//		GLCapabilities caps = GL.getCapabilities();
//		final int GL_DONT_CARE = 0x1100; // not defined anywhere yet
//
//		if (caps.OpenGL43) {
//			OpenGL.glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, severity.gl43, (IntBuffer)null, enabled);
//			return true;
//		}
//
//		if (caps.GL_KHR_debug) {
//			KHRDebug.glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, severity.khr, (IntBuffer)null, enabled);
//			return true;
//		}
//
//		if (caps.GL_ARB_debug_output && severity.arb != -1) {
//			ARBDebugOutput.glDebugMessageControlARB(GL_DONT_CARE, GL_DONT_CARE, severity.arb, (IntBuffer)null, enabled);
//			return true;
//		}
//
//		if (caps.GL_AMD_debug_output && severity.amd != -1) {
//			AMDDebugOutput.glDebugMessageEnableAMD(GL_DONT_CARE, severity.amd, (IntBuffer)null, enabled);
//			return true;
//		}

        return false;
    }

}
