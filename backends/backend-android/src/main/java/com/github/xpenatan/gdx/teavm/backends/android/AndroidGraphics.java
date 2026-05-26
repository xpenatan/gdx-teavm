package com.github.xpenatan.gdx.teavm.backends.android;

import com.badlogic.gdx.AbstractGraphics;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.GL31;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.github.xpenatan.gdx.teavm.backends.android.graphics.AndroidGL20;

public class AndroidGraphics extends AbstractGraphics {
    private final BufferFormat bufferFormat;
    private GLVersion glVersion = new GLVersion(Application.ApplicationType.Android, "OpenGL ES 2.0", "", "");
    private boolean glInitialized;
    private int width;
    private int height;
    private long lastFrameTime = -1;
    private float deltaTime;
    private long frameCounterStart;
    private int frames;
    private int fps;
    private long frameId = -1;
    private boolean continuousRendering;
    private boolean renderRequested;
    private int foregroundFPS;
    private GL20 gl20;
    private GL30 gl30;
    private GL31 gl31;
    private GL32 gl32;

    public AndroidGraphics(AndroidApplicationConfiguration config) {
        bufferFormat = new BufferFormat(config.r, config.g, config.b, config.a, config.depth, config.stencil,
                config.numSamples, config.coverageSampling);
        continuousRendering = config.continuousRendering;
        foregroundFPS = config.foregroundFPS;
        setGL20(new AndroidGL20());
    }

    void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    void initiateGL() {
        if(glInitialized || gl20 == null) {
            return;
        }
        glInitialized = true;
        String versionString = safeGLString(GL20.GL_VERSION);
        String vendorString = safeGLString(GL20.GL_VENDOR);
        String rendererString = safeGLString(GL20.GL_RENDERER);
        glVersion = new GLVersion(Application.ApplicationType.Android, versionString, vendorString, rendererString);
    }

    private String safeGLString(int name) {
        String value = gl20.glGetString(name);
        return value == null ? "" : value;
    }

    void update() {
        long time = System.nanoTime();
        if(lastFrameTime == -1) {
            lastFrameTime = time;
        }
        deltaTime = (time - lastFrameTime) / 1000000000.0f;
        lastFrameTime = time;

        if(time - frameCounterStart >= 1000000000) {
            fps = frames;
            frames = 0;
            frameCounterStart = time;
        }
        frames++;
        frameId++;
        renderRequested = false;
    }

    @Override
    public boolean isGL30Available() {
        return gl30 != null;
    }

    @Override
    public boolean isGL31Available() {
        return gl31 != null;
    }

    @Override
    public boolean isGL32Available() {
        return gl32 != null;
    }

    @Override
    public GL20 getGL20() {
        return gl20;
    }

    @Override
    public GL30 getGL30() {
        return gl30;
    }

    @Override
    public GL31 getGL31() {
        return gl31;
    }

    @Override
    public GL32 getGL32() {
        return gl32;
    }

    @Override
    public void setGL20(GL20 gl20) {
        this.gl20 = gl20;
        Gdx.gl = gl20;
        Gdx.gl20 = gl20;
    }

    @Override
    public void setGL30(GL30 gl30) {
        this.gl30 = gl30;
        if(gl30 != null) {
            Gdx.gl = gl30;
            Gdx.gl20 = gl30;
            Gdx.gl30 = gl30;
        }
    }

    @Override
    public void setGL31(GL31 gl31) {
        this.gl31 = gl31;
        if(gl31 != null) {
            Gdx.gl = gl31;
            Gdx.gl20 = gl31;
            Gdx.gl30 = gl31;
            Gdx.gl31 = gl31;
        }
    }

    @Override
    public void setGL32(GL32 gl32) {
        this.gl32 = gl32;
        if(gl32 != null) {
            Gdx.gl = gl32;
            Gdx.gl20 = gl32;
            Gdx.gl30 = gl32;
            Gdx.gl31 = gl32;
            Gdx.gl32 = gl32;
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getBackBufferWidth() {
        return width;
    }

    @Override
    public int getBackBufferHeight() {
        return height;
    }

    @Override
    public float getBackBufferScale() {
        return 1;
    }

    @Override
    public int getSafeInsetLeft() {
        return 0;
    }

    @Override
    public int getSafeInsetTop() {
        return 0;
    }

    @Override
    public int getSafeInsetBottom() {
        return 0;
    }

    @Override
    public int getSafeInsetRight() {
        return 0;
    }

    @Override
    public long getFrameId() {
        return frameId;
    }

    @Override
    public float getDeltaTime() {
        return deltaTime;
    }

    @Override
    public int getFramesPerSecond() {
        return fps;
    }

    @Override
    public GraphicsType getType() {
        return GraphicsType.AndroidGL;
    }

    @Override
    public GLVersion getGLVersion() {
        return glVersion;
    }

    @Override
    public float getPpiX() {
        return 160;
    }

    @Override
    public float getPpiY() {
        return 160;
    }

    @Override
    public float getPpcX() {
        return getPpiX() / 2.54f;
    }

    @Override
    public float getPpcY() {
        return getPpiY() / 2.54f;
    }

    @Override
    public boolean supportsDisplayModeChange() {
        return false;
    }

    @Override
    public Monitor getPrimaryMonitor() {
        return new Monitor(0, 0, "Android") {};
    }

    @Override
    public Monitor getMonitor() {
        return getPrimaryMonitor();
    }

    @Override
    public Monitor[] getMonitors() {
        return new Monitor[] { getPrimaryMonitor() };
    }

    @Override
    public DisplayMode[] getDisplayModes() {
        return new DisplayMode[] { getDisplayMode() };
    }

    @Override
    public DisplayMode[] getDisplayModes(Monitor monitor) {
        return getDisplayModes();
    }

    @Override
    public DisplayMode getDisplayMode() {
        return new DisplayMode(width, height, 60, 32) {};
    }

    @Override
    public DisplayMode getDisplayMode(Monitor monitor) {
        return getDisplayMode();
    }

    @Override
    public boolean setFullscreenMode(DisplayMode displayMode) {
        return false;
    }

    @Override
    public boolean setWindowedMode(int width, int height) {
        return false;
    }

    @Override
    public void setTitle(String title) {
    }

    @Override
    public void setUndecorated(boolean undecorated) {
    }

    @Override
    public void setResizable(boolean resizable) {
    }

    @Override
    public void setVSync(boolean vsync) {
    }

    @Override
    public void setForegroundFPS(int fps) {
        foregroundFPS = fps;
    }

    public int getForegroundFPS() {
        return foregroundFPS;
    }

    @Override
    public BufferFormat getBufferFormat() {
        return bufferFormat;
    }

    @Override
    public boolean supportsExtension(String extension) {
        if(extension == null || gl20 == null) {
            return false;
        }
        String extensions = gl20.glGetString(GL20.GL_EXTENSIONS);
        return extensions != null && extensions.contains(extension);
    }

    @Override
    public void setContinuousRendering(boolean isContinuous) {
        continuousRendering = isContinuous;
    }

    @Override
    public boolean isContinuousRendering() {
        return continuousRendering;
    }

    @Override
    public void requestRendering() {
        renderRequested = true;
    }

    public boolean isRenderRequested() {
        return renderRequested;
    }

    @Override
    public boolean isFullscreen() {
        return true;
    }

    @Override
    public Cursor newCursor(Pixmap pixmap, int xHotspot, int yHotspot) {
        return null;
    }

    @Override
    public void setCursor(Cursor cursor) {
    }

    @Override
    public void setSystemCursor(Cursor.SystemCursor systemCursor) {
    }
}
