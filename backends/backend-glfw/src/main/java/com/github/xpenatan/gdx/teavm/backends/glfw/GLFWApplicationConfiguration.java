package com.github.xpenatan.gdx.teavm.backends.glfw;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.math.GridPoint2;
import com.github.xpenatan.gdx.teavm.backends.glfw.graphics.gl.GLFWGLGraphics;
import com.github.xpenatan.gdx.teavm.backends.glfw.utils.GLFW;
import java.io.PrintStream;

public class GLFWApplicationConfiguration extends GLFWWindowConfiguration {
    boolean disableAudio = false;

    /**
     * The maximum number of threads to use for network requests. Default is {@link Integer#MAX_VALUE}.
     */
    int maxNetThreads = Integer.MAX_VALUE;

    public int audioDeviceSimultaneousSources = 16;
    public int audioDeviceBufferSize = 512;
    public int audioDeviceBufferCount = 9;

    public enum GLEmulation {
        ANGLE_GLES20, GL20, GL30, GL31, GL32
    }

    public GLEmulation glEmulation = GLEmulation.GL20;
    int gles30ContextMajorVersion = 3;
    int gles30ContextMinorVersion = 2;

    public int r = 8, g = 8, b = 8, a = 8;
    public int depth = 16, stencil = 0;
    public int samples = 0;
    public boolean transparentFramebuffer;

    public int idleFPS = 60;
    public int foregroundFPS = 0;

    public  boolean pauseWhenMinimized = true;
    public boolean pauseWhenLostFocus = false;

    String preferencesDirectory = ".prefs/";
    FileType preferencesFileType = FileType.External;

    public HdpiMode hdpiMode = HdpiMode.Logical;

    boolean debug = false;
    PrintStream debugStream = System.err;

    static GLFWApplicationConfiguration copy(GLFWApplicationConfiguration config) {
        GLFWApplicationConfiguration copy = new GLFWApplicationConfiguration();
        copy.set(config);
        return copy;
    }

    void set(GLFWApplicationConfiguration config) {
        super.setWindowConfiguration(config);
        disableAudio = config.disableAudio;
        audioDeviceSimultaneousSources = config.audioDeviceSimultaneousSources;
        audioDeviceBufferSize = config.audioDeviceBufferSize;
        audioDeviceBufferCount = config.audioDeviceBufferCount;
        glEmulation = config.glEmulation;
        gles30ContextMajorVersion = config.gles30ContextMajorVersion;
        gles30ContextMinorVersion = config.gles30ContextMinorVersion;
        r = config.r;
        g = config.g;
        b = config.b;
        a = config.a;
        depth = config.depth;
        stencil = config.stencil;
        samples = config.samples;
        transparentFramebuffer = config.transparentFramebuffer;
        idleFPS = config.idleFPS;
        foregroundFPS = config.foregroundFPS;
        pauseWhenMinimized = config.pauseWhenMinimized;
        pauseWhenLostFocus = config.pauseWhenLostFocus;
        preferencesDirectory = config.preferencesDirectory;
        preferencesFileType = config.preferencesFileType;
        hdpiMode = config.hdpiMode;
        debug = config.debug;
        debugStream = config.debugStream;
    }

    /**
     * @param visibility whether the window will be visible on creation. (default true)
     */
    public void setInitialVisible(boolean visibility) {
        this.initialVisible = visibility;
    }

    /**
     * Whether to disable audio or not. If set to true, the returned audio class instances like {@link Audio} or {@link Music}
     * will be mock implementations.
     */
    public void disableAudio(boolean disableAudio) {
        this.disableAudio = disableAudio;
    }

    /**
     * Sets the maximum number of threads to use for network requests.
     */
    public void setMaxNetThreads(int maxNetThreads) {
        this.maxNetThreads = maxNetThreads;
    }

    /**
     * Sets the audio device configuration.
     *
     * @param simultaneousSources the maximum number of sources that can be played simultaniously (default 16)
     * @param bufferSize          the audio device buffer size in samples (default 512)
     * @param bufferCount         the audio device buffer count (default 9)
     */
    public void setAudioConfig(int simultaneousSources, int bufferSize, int bufferCount) {
        this.audioDeviceSimultaneousSources = simultaneousSources;
        this.audioDeviceBufferSize = bufferSize;
        this.audioDeviceBufferCount = bufferCount;
    }

    /**
     * Sets which OpenGL version to use to emulate OpenGL ES. If the given major/minor version is not supported, the backend falls
     * back to OpenGL ES 2.0 emulation through OpenGL 2.0. The default parameters for major and minor should be 3 and 2
     * respectively to be compatible with Mac OS X. Specifying major version 4 and minor version 2 will ensure that all OpenGL ES
     * 3.0 features are supported. Note however that Mac OS X does only support 3.2.
     *
     * @param glVersion         which OpenGL ES emulation version to use
     * @param gles3MajorVersion OpenGL ES major version, use 3 as default
     * @param gles3MinorVersion OpenGL ES minor version, use 2 as default
     * @see <a href= "http://legacy.lwjgl.org/javadoc/org/lwjgl/opengl/ContextAttribs.html"> LWJGL OSX ContextAttribs note</a>
     */
    public void setOpenGLEmulation(GLEmulation glVersion, int gles3MajorVersion, int gles3MinorVersion) {
        this.glEmulation = glVersion;
        this.gles30ContextMajorVersion = gles3MajorVersion;
        this.gles30ContextMinorVersion = gles3MinorVersion;
    }

    /**
     * Sets the bit depth of the color, depth and stencil buffer as well as multi-sampling.
     *
     * @param r       red bits (default 8)
     * @param g       green bits (default 8)
     * @param b       blue bits (default 8)
     * @param a       alpha bits (default 8)
     * @param depth   depth bits (default 16)
     * @param stencil stencil bits (default 0)
     * @param samples MSAA samples (default 0)
     */
    public void setBackBufferConfig(int r, int g, int b, int a, int depth, int stencil, int samples) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.depth = depth;
        this.stencil = stencil;
        this.samples = samples;
    }

    /**
     * Set transparent window hint. Results may vary on different OS and GPUs. Usage with the ANGLE backend is less consistent.
     *
     * @param transparentFramebuffer
     */
    public void setTransparentFramebuffer(boolean transparentFramebuffer) {
        this.transparentFramebuffer = transparentFramebuffer;
    }

    /**
     * Sets the polling rate during idle time in non-continuous rendering mode. Must be positive. Default is 60.
     */
    public void setIdleFPS(int fps) {
        this.idleFPS = fps;
    }

    /**
     * Sets the target framerate for the application. The CPU sleeps as needed. Must be positive. Use 0 to never sleep. Default is
     * 0.
     */
    public void setForegroundFPS(int fps) {
        this.foregroundFPS = fps;
    }

    /**
     * Sets whether to pause the application {@link ApplicationListener#pause()} and fire
     * {@link LifecycleListener#pause()}/{@link LifecycleListener#resume()} events on when window is minimized/restored.
     **/
    public void setPauseWhenMinimized(boolean pauseWhenMinimized) {
        this.pauseWhenMinimized = pauseWhenMinimized;
    }

    /**
     * Sets whether to pause the application {@link ApplicationListener#pause()} and fire
     * {@link LifecycleListener#pause()}/{@link LifecycleListener#resume()} events on when window loses/gains focus.
     **/
    public void setPauseWhenLostFocus(boolean pauseWhenLostFocus) {
        this.pauseWhenLostFocus = pauseWhenLostFocus;
    }

    /**
     * Sets the directory where {@link Preferences} will be stored, as well as the file type to be used to store them. Defaults to
     * "$USER_HOME/.prefs/" and {@link FileType#External}.
     */
    public void setPreferencesConfig(String preferencesDirectory, FileType preferencesFileType) {
        this.preferencesDirectory = preferencesDirectory;
        this.preferencesFileType = preferencesFileType;
    }

    /**
     * Defines how HDPI monitors are handled. Operating systems may have a per-monitor HDPI scale setting. The operating system
     * may report window width/height and mouse coordinates in a logical coordinate system at a lower resolution than the actual
     * physical resolution. This setting allows you to specify whether you want to work in logical or raw pixel units. See
     * {@link HdpiMode} for more information. Note that some OpenGL functions like {@link GL20#glViewport(int, int, int, int)} and
     * {@link GL20#glScissor(int, int, int, int)} require raw pixel units. Use {@link HdpiUtils} to help with the conversion if
     * HdpiMode is set to {@link HdpiMode#Logical}. Defaults to {@link HdpiMode#Logical}.
     */
    public void setHdpiMode(HdpiMode mode) {
        this.hdpiMode = mode;
    }

    /**
     * Enables use of OpenGL debug message callbacks. If not supported by the core GL driver (since GL 4.3), this uses the
     * KHR_debug, ARB_debug_output or AMD_debug_output extension if available. By default, debug messages with NOTIFICATION
     * severity are disabled to avoid log spam.
     * <p>
     * You can call with {@link System#err} to output to the "standard" error output stream.
     * <p>
     * Use {@link GLFWApplication#setGLDebugMessageControl(GLFWApplication.GLDebugMessageSeverity, boolean)} to enable or
     * disable other severity debug levels.
     */
    public void enableGLDebugOutput(boolean enable, PrintStream debugOutputStream) {
        debug = enable;
        debugStream = debugOutputStream;
    }

    /**
     * @return the currently active {@link DisplayMode} of the primary monitor
     */
    public static DisplayMode getDisplayMode() {
        GLFWApplication.initializeGlfw();
        GLFW.GLFWVidMode videoMode = GLFW.getVideoMode(GLFW.getPrimaryMonitor());
        return new GLFWGLGraphics.NativeDisplayMode(GLFW.getPrimaryMonitor(), videoMode.width, videoMode.height,
                videoMode.refreshRate, videoMode.redBits + videoMode.greenBits + videoMode.blueBits);
    }

    /**
     * @return the currently active {@link DisplayMode} of the given monitor
     */
    public static DisplayMode getDisplayMode(Monitor monitor) {
        GLFWApplication.initializeGlfw();
        GLFW.GLFWVidMode videoMode = GLFW.getVideoMode(((GLFWGLGraphics.NativeMonitor) monitor).monitorHandle);
        return new GLFWGLGraphics.NativeDisplayMode(((GLFWGLGraphics.NativeMonitor) monitor).monitorHandle, videoMode.width, videoMode.height,
                videoMode.refreshRate, videoMode.redBits + videoMode.greenBits + videoMode.blueBits);
    }

    /**
     * @return the available {@link DisplayMode}s of the primary monitor
     */
    public static DisplayMode[] getDisplayModes() {
        GLFWApplication.initializeGlfw();
        GLFW.GLFWVidMode[] videoModes = GLFW.getVideoModes(GLFW.getPrimaryMonitor());
        DisplayMode[] result = new DisplayMode[videoModes.length];
        for (int i = 0; i < result.length; i++) {
            GLFW.GLFWVidMode videoMode = videoModes[i];
            result[i] = new GLFWGLGraphics.NativeDisplayMode(GLFW.getPrimaryMonitor(), videoMode.width, videoMode.height,
                    videoMode.refreshRate, videoMode.redBits + videoMode.greenBits + videoMode.blueBits);
        }
        return result;
    }

    /**
     * @return the available {@link DisplayMode}s of the given {@link Monitor}
     */
    public static DisplayMode[] getDisplayModes(Monitor monitor) {
        GLFWApplication.initializeGlfw();
        GLFW.GLFWVidMode[] videoModes = GLFW.getVideoModes(((GLFWGLGraphics.NativeMonitor) monitor).monitorHandle);
        DisplayMode[] result = new DisplayMode[videoModes.length];
        for (int i = 0; i < result.length; i++) {
            GLFW.GLFWVidMode videoMode = videoModes[i];
            result[i] = new GLFWGLGraphics.NativeDisplayMode(((GLFWGLGraphics.NativeMonitor) monitor).monitorHandle, videoMode.width,
                    videoMode.height, videoMode.refreshRate, videoMode.redBits + videoMode.greenBits + videoMode.blueBits);
        }
        return result;
    }

    /**
     * @return the primary {@link Monitor}
     */
    public static Monitor getPrimaryMonitor() {
        GLFWApplication.initializeGlfw();
        return toNativeMonitor(GLFW.getPrimaryMonitor());
    }

    /**
     * @return the connected {@link Monitor}s
     */
    public static Monitor[] getMonitors() {
        GLFWApplication.initializeGlfw();
        long[] glfwMonitors = GLFW.getMonitors();
        Monitor[] monitors = new Monitor[glfwMonitors.length];
        for (int i = 0; i < glfwMonitors.length; i++) {
            monitors[i] = toNativeMonitor(glfwMonitors[i]);
        }
        return monitors;
    }

    public static GLFWGLGraphics.NativeMonitor toNativeMonitor(long glfwMonitor) {
        int[] tmp = new int[1];
        int[] tmp2 = new int[1];
        GLFW.getMonitorPos(glfwMonitor, tmp, tmp2);
        int virtualX = tmp[0];
        int virtualY = tmp2[0];
        String name = GLFW.getMonitorName(glfwMonitor);
        return new GLFWGLGraphics.NativeMonitor(glfwMonitor, virtualX, virtualY, name);
    }

    public static GridPoint2 calculateCenteredWindowPosition(GLFWGLGraphics.NativeMonitor monitor, int newWidth, int newHeight) {
        int[] tmp = new int[1];
        int[] tmp2 = new int[1];
        int[] tmp3 = new int[1];
        int[] tmp4 = new int[1];

        DisplayMode displayMode = getDisplayMode(monitor);

        GLFW.getMonitorWorkarea(monitor.monitorHandle, tmp, tmp2, tmp3, tmp4);
        int workareaWidth = tmp3[0];
        int workareaHeight = tmp4[0];

        int minX, minY, maxX, maxY;

        // If the new width is greater than the working area, we have to ignore stuff like the taskbar for centering and use the
        // whole monitor's size
        if (newWidth > workareaWidth) {
            minX = monitor.virtualX;
            maxX = displayMode.width;
        } else {
            minX = tmp[0];
            maxX = workareaWidth;
        }
        // The same is true for height
        if (newHeight > workareaHeight) {
            minY = monitor.virtualY;
            maxY = displayMode.height;
        } else {
            minY = tmp2[0];
            maxY = workareaHeight;
        }

        return new GridPoint2(Math.max(minX, minX + (maxX - newWidth) / 2), Math.max(minY, minY + (maxY - newHeight) / 2));
    }
}
