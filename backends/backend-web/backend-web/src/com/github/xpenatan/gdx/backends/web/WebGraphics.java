package com.github.xpenatan.gdx.backends.web;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.github.xpenatan.gdx.backends.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backends.web.dom.WindowWrapper;
import com.github.xpenatan.gdx.backends.web.gl.WebGLRenderingContextWrapper;

/**
 * @author xpenatan
 */
public class WebGraphics implements Graphics {

    private WebGLRenderingContextWrapper context;
    protected HTMLCanvasElementWrapper canvas;
    protected WebApplicationConfiguration config;
    protected GL20 gl;
    protected GLVersion glVersion;

    float fps = 0;
    long lastTimeStamp = System.currentTimeMillis();
    long frameId = -1;
    float deltaTime = 0;
    float time = 0;
    int frames;

    private WebJSGraphics jsGraphics;

    public WebGraphics(WebApplicationConfiguration config) {
        WebJSHelper webJSHelper = WebJSHelper.get();
        this.config = config;
        this.canvas = webJSHelper.getCanvas();
        this.jsGraphics = webJSHelper.getGraphics();
        this.context = jsGraphics.getGLContext(webJSHelper.getCanvas(), config);
        gl = new WebGL20(context);
        String versionString = gl.glGetString(GL20.GL_VERSION);
        String vendorString = gl.glGetString(GL20.GL_VENDOR);
        String rendererString = gl.glGetString(GL20.GL_RENDERER);
        glVersion = new GLVersion(Application.ApplicationType.WebGL, versionString, vendorString, rendererString);

        if(config.width >= 0 || config.height >= 0) {
            // update canvas size
            setWindowedMode(config.width, config.height);
        }
    }

    public void update() {
        long currTimeStamp = System.currentTimeMillis();
        deltaTime = (currTimeStamp - lastTimeStamp) / 1000.0f;
        lastTimeStamp = currTimeStamp;
        time += deltaTime;
        frames++;
        if(time > 1) {
            this.fps = frames;
            time = 0;
            frames = 0;
        }
    }

    @Override
    public boolean isGL30Available() {
        return false;
    }

    @Override
    public GL20 getGL20() {
        return gl;
    }

    @Override
    public GL30 getGL30() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setGL20(GL20 gl20) {
        this.gl = gl20;
        Gdx.gl = gl20;
        Gdx.gl20 = gl20;
    }

    @Override
    public void setGL30(GL30 gl30) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getWidth() {
        return canvas.getWidth();
    }

    @Override
    public int getHeight() {
        return canvas.getHeight();
    }

    @Override
    public int getBackBufferWidth() {
        return canvas.getWidth();
    }

    @Override
    public int getBackBufferHeight() {
        return canvas.getHeight();
    }

    @Override
    public int getSafeInsetLeft() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSafeInsetTop() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSafeInsetBottom() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSafeInsetRight() {
        // TODO Auto-generated method stub
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
    public float getRawDeltaTime() {
        return deltaTime;
    }

    @Override
    public int getFramesPerSecond() {
        return (int)fps;
    }

    @Override
    public GraphicsType getType() {
        return GraphicsType.WebGL;
    }

    @Override
    public GLVersion getGLVersion() {
        return glVersion;
    }

    @Override
    public float getPpiX() {
        return 96;
    }

    @Override
    public float getPpiY() {
        return 96;
    }

    @Override
    public float getPpcX() {
        return 96 / 2.54f;
    }

    @Override
    public float getPpcY() {
        return 96 / 2.54f;
    }

    @Override
    public float getDensity() {
        return 96.0f / 160;
    }

    @Override
    public boolean supportsDisplayModeChange() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Monitor getPrimaryMonitor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Monitor getMonitor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Monitor[] getMonitors() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DisplayMode[] getDisplayModes() {
        return new DisplayMode[]{getDisplayMode()};
    }

    @Override
    public DisplayMode[] getDisplayModes(Monitor monitor) {
        return getDisplayModes();
    }

    @Override
    public DisplayMode getDisplayMode() {
        double density = config.usePhysicalPixels ? jsGraphics.getNativeScreenDensity() : 1;
        return new DisplayMode((int)(jsGraphics.getScreenWidthJSNI() * density), (int)(jsGraphics.getScreenHeightJSNI() * density), 60, 8) {
        };
    }

    @Override
    public DisplayMode getDisplayMode(Monitor monitor) {
        return getDisplayMode();
    }

    @Override
    public boolean setFullscreenMode(DisplayMode displayMode) {
        DisplayMode supportedMode = getDisplayMode();
        if(displayMode.width != supportedMode.width && displayMode.height != supportedMode.height) return false;
        return jsGraphics.setFullscreenJSNI(this, canvas, displayMode.width, displayMode.height);
    }

    @Override
    public boolean setWindowedMode(int width, int height) {
        if(isFullscreen()) jsGraphics.exitFullscreen();

        if(width > 0 && height > 0) {
            setCanvasSize(width, height);
        }
        else {
            WindowWrapper currentWindow = WebJSHelper.get().getCurrentWindow();
            int newWidth = currentWindow.getClientWidth();
            int newHeight = currentWindow.getClientHeight();
            setCanvasSize(newWidth, newHeight);
        }
        return true;
    }

    void setCanvasSize(int width, int height) {
        double density = config.usePhysicalPixels ? jsGraphics.getNativeScreenDensity() : 1;
        canvas.setWidth((int)(width * density));
        canvas.setHeight((int)(height * density));
    }

    @Override
    public void setTitle(String title) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setUndecorated(boolean undecorated) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setResizable(boolean resizable) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setVSync(boolean vsync) {
        // TODO Auto-generated method stub

    }

    @Override
    public BufferFormat getBufferFormat() {
        return new BufferFormat(8, 8, 8, 0, 16, config.stencil ? 8 : 0, 0, false);
    }

    @Override
    public boolean supportsExtension(String extensionName) {
        // Contrary to regular OpenGL, WebGL extensions need to be explicitly enabled before they can be used. See
        // https://developer.mozilla.org/en-US/docs/Web/API/WebGL_API/Using_Extensions
        // Thus, it is not safe to use an extension just because context.getSupportedExtensions() tells you it is available.
        // We need to call getExtension() to enable it.
        return context.getExtension(extensionName) != null;
    }

    @Override
    public void setContinuousRendering(boolean isContinuous) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isContinuousRendering() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void requestRendering() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isFullscreen() {
        return jsGraphics.isFullscreenJSNI();
    }

    @Override
    public Cursor newCursor(Pixmap pixmap, int xHotspot, int yHotspot) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCursor(Cursor cursor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setSystemCursor(SystemCursor systemCursor) {
        // TODO Auto-generated method stub

    }

    @Override
    public float getBackBufferScale() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setForegroundFPS(int fps) {
        // TODO Auto-generated method stub

    }
}
