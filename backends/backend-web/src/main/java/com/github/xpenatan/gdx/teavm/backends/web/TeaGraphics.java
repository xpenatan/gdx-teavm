package com.github.xpenatan.gdx.teavm.backends.web;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.GL31;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.css.CSSStyleDeclaration;
import org.teavm.jso.dom.html.HTMLCanvasElement;

/**
 * @author xpenatan
 */
public abstract class TeaGraphics implements Graphics {
    protected HTMLCanvasElement canvas;
    protected TeaApplicationConfiguration config;

    float fps = 0;
    long lastTimeStamp = System.currentTimeMillis();
    long frameId = -1;
    float deltaTime = 0;
    float time = 0;
    int frames;

    public TeaGraphics() {
    }

    public TeaGraphics(TeaApplicationConfiguration config) {
        this.config = config;
    }

    public void render(ApplicationListener listener) {
        listener.render();
    }

    public void resize(ApplicationListener appListener, int width, int height) {
        Gdx.gl.glViewport(0, 0, width, height);
        appListener.resize(width, height);
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
    public boolean isGL31Available() {
        return false;
    }

    @Override
    public boolean isGL32Available() {
        return false;
    }

    @Override
    public GL20 getGL20() {
        return null;
    }

    @Override
    public GL30 getGL30() {
        return null;
    }

    @Override
    public GL31 getGL31() {
        return null;
    }

    @Override
    public GL32 getGL32() {
        return null;
    }

    @Override
    public void setGL20(GL20 gl20) {
    }

    @Override
    public void setGL30(GL30 gl30) {
    }

    @Override
    public void setGL31(GL31 gl31) {
    }

    @Override
    public void setGL32(GL32 gl32) {
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
        return null;
    }

    @Override
    public float getPpiX () {
        return 96f * (float)getNativeScreenDensity();
    }

    @Override
    public float getPpiY () {
        return 96f * (float)getNativeScreenDensity();
    }

    @Override
    public float getPpcX () {
        return getPpiX() / 2.54f;
    }

    @Override
    public float getPpcY () {
        return getPpiY() / 2.54f;
    }

    @Override
    public float getDensity() {
        float ppiX = getPpiX();
        return (ppiX > 0 && ppiX <= Float.MAX_VALUE) ? ppiX / 160f : 1f;
    }

    @Override
    public boolean supportsDisplayModeChange() {
        return true;
    }

    static class TeaMonitor extends Monitor {
      protected TeaMonitor (int virtualX, int virtualY, String name) {
        super(virtualX, virtualY, name);
      }
    }

    @Override
    public Monitor getPrimaryMonitor() {
        return new TeaMonitor(0, 0, "Primary Monitor");
    }

    @Override
    public Monitor getMonitor() {
        return getPrimaryMonitor();
    }

    @Override
    public Monitor[] getMonitors() {
      return new Monitor[] {getPrimaryMonitor()};
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
        double density = config.usePhysicalPixels ? getNativeScreenDensity() : 1;
        return new DisplayMode((int)(getScreenWidthNATIVE() * density), (int)(getScreenHeightNATIVE() * density), 60, 8) {
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
        return enterFullscreen(canvas, displayMode.width, displayMode.height);
    }

    @Override
    public boolean setWindowedMode(int width, int height) {
        if(isFullscreen()) exitFullscreen();
        // don't set canvas for resizable applications, resize handler will do it
        if(!config.isAutoSizeApplication()) {
            setCanvasSize(width, height, config.usePhysicalPixels);
        }
        return true;
    }

    protected void setCanvasSize(int width, int height, boolean usePhysicalPixels) {
        // event calls us with logical pixel size, so if we use physical pixels internally,
        // we need to convert them
        double density = 1;
        if(usePhysicalPixels) {
            density = getNativeScreenDensity();
        }
        int w = (int)(width * density);
        int h = (int)(height * density);
        canvas.setWidth(w);
        canvas.setHeight(h);
        if(usePhysicalPixels) {
            CSSStyleDeclaration style = canvas.getStyle();
            style.setProperty("width", width + "px");
            style.setProperty("height", height + "px");
        }
    }

    @Override
    public void setTitle(String title) {
        Window.current().getDocument().setTitle(title);
    }

    @Override
    public void setUndecorated(boolean undecorated) {
        // not available
    }

    @Override
    public void setResizable(boolean resizable) {
        // not available
    }

    @Override
    public void setVSync(boolean vsync) {
        // not available
    }

    @Override
    public BufferFormat getBufferFormat() {
        return new BufferFormat(8, 8, 8, 0, 16, config.stencil ? 8 : 0, 0, false);
    }

    @Override
    public void setContinuousRendering(boolean isContinuous) {
        // not available
    }

    @Override
    public boolean isContinuousRendering() {
        return true;
    }

    @Override
    public void requestRendering() {
        // not available
    }

    @Override
    public Cursor newCursor(Pixmap pixmap, int xHotspot, int yHotspot) {
        return new TeaCursor(pixmap, xHotspot, yHotspot);
    }

    @Override
    public void setCursor(Cursor cursor) {
      canvas.getStyle().setProperty("cursor", ((TeaCursor)cursor).cssCursorProperty);
    }

    @Override
    public void setSystemCursor(SystemCursor systemCursor) {
      canvas.getStyle().setProperty("cursor", TeaCursor.getNameForSystemCursor(systemCursor));
    }

    @Override
    public float getBackBufferScale() {
        return getBackBufferWidth() / (float)getWidth();
    }

    @Override
    public boolean supportsExtension(String extension) {
        return false;
    }

    @Override
    public void setForegroundFPS(int fps) {
        // not available
    }

    // ##################### NATIVE CALLS #####################

    public double getNativeScreenDensity() {
        return getNativeScreenDensityNATIVE();
    }

    @JSBody(script = "return devicePixelRatio || 1;")
    private static native int getNativeScreenDensityNATIVE();

    @JSBody(script = "return screen.width;")
    private static native int getScreenWidthNATIVE();

    @JSBody(script = "return screen.height;")
    private static native int getScreenHeightNATIVE();

    @JSBody(params = {"element", "fullscreenChanged"}, script = "" +
            "if (element.requestFullscreen) {\n" +
            "   document.addEventListener(\"fullscreenchange\", fullscreenChanged, false);\n" +
            "}\n" +
            "// Attempt to the vendor specific variants of the API\n" +
            "if (element.webkitRequestFullScreen) {\n" +
            "   document.addEventListener(\"webkitfullscreenchange\", fullscreenChanged, false);\n" +
            "}\n" +
            "if (element.mozRequestFullScreen) {\n" +
            "   document.addEventListener(\"mozfullscreenchange\", fullscreenChanged, false);\n" +
            "}\n" +
            "if (element.msRequestFullscreen) {\n" +
            "   document.addEventListener(\"msfullscreenchange\", fullscreenChanged, false);\n" +
            "}")
    protected static native void addFullscreenChangeListener(HTMLCanvasElement element, FullscreenChanged fullscreenChanged);

    @JSFunctor
    public interface FullscreenChanged extends org.teavm.jso.JSObject {
        void fullscreenChanged();
    }

    public boolean enterFullscreen(HTMLCanvasElement element, int screenWidth, int screenHeight) {
        return enterFullscreenNATIVE(element, screenWidth, screenHeight);
    }

    @JSBody(params = {"element", "screenWidth", "screenHeight"}, script = "" +
            "if (element.requestFullscreen) {\n" +
            "   element.width = screenWidth;\n" +
            "   element.height = screenHeight;\n" +
            "   element.requestFullscreen();\n" +
            "   return true;\n" +
            "}\n" +
            "// Attempt to the vendor specific variants of the API\n" +
            "if (element.webkitRequestFullScreen) {\n" +
            "   element.width = screenWidth;\n" +
            "   element.height = screenHeight;\n" +
            "   element.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);\n" +
            "   return true;\n" +
            "}\n" +
            "if (element.mozRequestFullScreen) {\n" +
            "   element.width = screenWidth;\n" +
            "   element.height = screenHeight;\n" +
            "   element.mozRequestFullScreen();\n" +
            "   return true;\n" +
            "}\n" +
            "if (element.msRequestFullscreen) {\n" +
            "   element.width = screenWidth;\n" +
            "   element.height = screenHeight;\n" +
            "   element.msRequestFullscreen();\n" +
            "   return true;\n" +
            "}\n" +
            "\n" +
            "return false;")
    private static native boolean enterFullscreenNATIVE(HTMLCanvasElement element, int screenWidth, int screenHeight);

    public void exitFullscreen() {
        exitFullscreenNATIVE();
    }

    @JSBody(script = "" +
            "if (document.exitFullscreen)\n" +
            "  document.exitFullscreen();\n" +
            "if (document.msExitFullscreen)\n" +
            "  document.msExitFullscreen();\n" +
            "if (document.webkitExitFullscreen)\n" +
            "  document.webkitExitFullscreen();\n" +
            "if (document.mozExitFullscreen)\n" +
            "  document.mozExitFullscreen();\n" +
            "if (document.webkitCancelFullScreen) // Old WebKit\n" +
            "  document.webkitCancelFullScreen();")
    private static native boolean exitFullscreenNATIVE();

    @Override
    public boolean isFullscreen() {
        return isFullscreenNATIVE();
    }

    @JSBody(script = "" +
            "// Standards compliant check for fullscreen\n" +
            "if (\"fullscreenElement\" in document) {\n" +
            "  return document.fullscreenElement != null;\n" +
            "}" +
            "// Vendor prefixed versions of standard check\n" +
            "if (\"msFullscreenElement\" in document) {\n" +
            "  return document.msFullscreenElement != null;\n" +
            "}" +
            "if (\"webkitFullscreenElement\" in document) {\n" +
            "  return document.webkitFullscreenElement != null;\n" +
            "}" +
            "if (\"mozFullScreenElement\" in document) { // Yes, with a capital 'S'\n" +
            "  return document.mozFullScreenElement != null;\n" +
            "}" +
            "// Older, non-standard ways of checking for fullscreen\n" +
            "if (\"webkitIsFullScreen\" in document) {\n" +
            "  return document.webkitIsFullScreen;\n" +
            "}" +
            "if (\"mozFullScreen\" in document) {\n" +
            "  return document.mozFullScreen;\n" +
            "}" +
            "return false")
    private static native boolean isFullscreenNATIVE();
}
