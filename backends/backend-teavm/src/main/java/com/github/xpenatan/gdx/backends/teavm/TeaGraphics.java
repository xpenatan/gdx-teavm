package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.github.xpenatan.gdx.backends.teavm.dom.DocumentWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.HTMLElementWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.StyleWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.WindowWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.impl.TeaWindow;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGLRenderingContextWrapper;
import com.github.xpenatan.gdx.backends.teavm.util.TeaJSHelper;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.webgl.WebGLContextAttributes;

/**
 * @author xpenatan
 */
public class TeaGraphics implements Graphics {

    private WebGLRenderingContextWrapper context;
    protected HTMLCanvasElementWrapper canvas;
    protected TeaApplicationConfiguration config;
    protected GL20 gl;
    protected GLVersion glVersion;

    float fps = 0;
    long lastTimeStamp = System.currentTimeMillis();
    long frameId = -1;
    float deltaTime = 0;
    float time = 0;
    int frames;

    public TeaGraphics(TeaApplicationConfiguration config) {
        this.config = config;
        HTMLCanvasElement a;
        TeaWindow window = new TeaWindow();
        DocumentWrapper document = window.getDocument();
        HTMLElementWrapper elementID = document.getElementById(config.canvasID);
        this.canvas = (HTMLCanvasElementWrapper)elementID;
        this.context = getGLContext(canvas, config);
        gl = config.useDebugGL ? new TeaGL20Debug(context) : new TeaGL20(context);
        String versionString = gl.glGetString(GL20.GL_VERSION);
        String vendorString = gl.glGetString(GL20.GL_VENDOR);
        String rendererString = gl.glGetString(GL20.GL_RENDERER);
        glVersion = new GLVersion(Application.ApplicationType.WebGL, versionString, vendorString, rendererString);

        if(config.width >= 0 || config.height >= 0) {
            if(config.isFixedSizeApplication()) {
                setCanvasSize(config.width, config.height);
            }
            else {
                TeaWindow currentWindow = TeaWindow.get();
                int width = currentWindow.getClientWidth() - config.padHorizontal;
                int height = currentWindow.getClientHeight() - config.padVertical;
                double density = config.usePhysicalPixels ? getNativeScreenDensity() : 1;
                setCanvasSize((int)(density * width), (int)(density * height));
            }
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
        return setFullscreenJSNI(this, canvas, displayMode.width, displayMode.height);
    }

    @Override
    public boolean setWindowedMode(int width, int height) {
        if(isFullscreen()) exitFullscreen();
        // don't set canvas for resizable applications, resize handler will do it
        if(!config.isAutoSizeApplication()) {
            setCanvasSize(width, height);
        }
        return true;
    }

    void setCanvasSize(int width, int height) {
        canvas.setWidth(width);
        canvas.setHeight(height);
        if(config.usePhysicalPixels) {
            //TODO Not tested
            double density = getNativeScreenDensity();
            StyleWrapper style = canvas.getStyle();
            style.setProperty("width", width / density + StyleWrapper.Unit.PX.getType());
            style.setProperty("height", height / density + StyleWrapper.Unit.PX.getType());
        }
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
        return isFullscreenJSNI();
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

    public boolean setFullscreenJSNI(TeaGraphics graphics, HTMLCanvasElementWrapper canvas, int screenWidth, int screenHeight) {
        FullscreenChanged fullscreenChanged = new FullscreenChanged() {
            @Override
            public void fullscreenChanged() {
            }
        };
        return setFullscreen(fullscreenChanged, canvas, screenWidth, screenHeight);
    }

    @JSBody(params = {"fullscreenChanged", "element", "screenWidth", "screenHeight"}, script = "" +
            "\t\t// Attempt to use the non-prefixed standard API (https://fullscreen.spec.whatwg.org)\n" +
            "\t\tif (element.requestFullscreen) {\n" +
            "\t\t\telement.width = screenWidth;\n" +
            "\t\t\telement.height = screenHeight;\n" +
            "\t\t\telement.requestFullscreen();\n" +
            "\t\t\tdocument\n" +
            "\t\t\t\t\t.addEventListener(\n" +
            "\t\t\t\t\t\t\t\"fullscreenchange\",\n" +
            "\t\t\t\t\t\t\tfullscreenChanged, false);\n" +
            "\t\t\treturn true;\n" +
            "\t\t}\n" +
            "\t\t// Attempt to the vendor specific variants of the API\n" +
            "\t\tif (element.webkitRequestFullScreen) {\n" +
            "\t\t\telement.width = screenWidth;\n" +
            "\t\t\telement.height = screenHeight;\n" +
            "\t\t\telement.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);\n" +
            "\t\t\tdocument\n" +
            "\t\t\t\t\t.addEventListener(\n" +
            "\t\t\t\t\t\t\t\"webkitfullscreenchange\",\n" +
            "\t\t\t\t\t\t\tfullscreenChanged, false);\n" +
            "\t\t\treturn true;\n" +
            "\t\t}\n" +
            "\t\tif (element.mozRequestFullScreen) {\n" +
            "\t\t\telement.width = screenWidth;\n" +
            "\t\t\telement.height = screenHeight;\n" +
            "\t\t\telement.mozRequestFullScreen();\n" +
            "\t\t\tdocument\n" +
            "\t\t\t\t\t.addEventListener(\n" +
            "\t\t\t\t\t\t\t\"mozfullscreenchange\",\n" +
            "\t\t\t\t\t\t\tfullscreenChanged, false);\n" +
            "\t\t\treturn true;\n" +
            "\t\t}\n" +
            "\t\tif (element.msRequestFullscreen) {\n" +
            "\t\t\telement.width = screenWidth;\n" +
            "\t\t\telement.height = screenHeight;\n" +
            "\t\t\telement.msRequestFullscreen();\n" +
            "\t\t\tdocument\n" +
            "\t\t\t\t\t.addEventListener(\n" +
            "\t\t\t\t\t\t\t\"msfullscreenchange\",\n" +
            "\t\t\t\t\t\t\tfullscreenChanged, false);\n" +
            "\t\t\treturn true;\n" +
            "\t\t}\n" +
            "\n" +
            "\t\treturn false;")
    private static native boolean setFullscreen(FullscreenChanged fullscreenChanged, HTMLCanvasElementWrapper element, int screenWidth, int screenHeight);

    public void exitFullscreen() {
        exitFullscreenJS();
    }

    @JSBody(script = "" +
            "if (document.exitFullscreen)\n" +
            "document.exitFullscreen();\n" +
            "if (document.msExitFullscreen)\n" +
            "document.msExitFullscreen();\n" +
            "if (document.webkitExitFullscreen)\n" +
            "document.webkitExitFullscreen();\n" +
            "if (document.mozExitFullscreen)\n" +
            "document.mozExitFullscreen();\n" +
            "if (document.webkitCancelFullScreen) // Old WebKit\n" +
            "document.webkitCancelFullScreen();")
    private static native boolean exitFullscreenJS();

    public boolean isFullscreenJSNI() {
        return isFullscreenNATIVE();
    }

    @JSBody(script = "" +
            "// Standards compliant check for fullscreen\n" +
            "if (\"fullscreenElement\" in document) {\n" +
            "return document.fullscreenElement != null;\n" +
            "}" +
            "// Vendor prefixed versions of standard check\n" +
            "if (\"msFullscreenElement\" in document) {\n" +
            "return document.msFullscreenElement != null;\n" +
            "}" +
            "if (\"webkitFullscreenElement\" in document) {\n" +
            "return document.webkitFullscreenElement != null;\n" +
            "}" +
            "if (\"mozFullScreenElement\" in document) { // Yes, with a capital 'S'\n" +
            "return document.mozFullScreenElement != null;\n" +
            "}" +
            "// Older, non-standard ways of checking for fullscreen\n" +
            "if (\"webkitIsFullScreen\" in document) {\n" +
            "return document.webkitIsFullScreen;\n" +
            "}" +
            "if (\"mozFullScreen\" in document) {\n" +
            "return document.mozFullScreen;\n" +
            "}" +
            "return false")
    private static native boolean isFullscreenNATIVE();

    @JSFunctor
    public interface FullscreenChanged extends org.teavm.jso.JSObject {
        void fullscreenChanged();
    }

    public WebGLRenderingContextWrapper getGLContext(HTMLCanvasElementWrapper canvasWrapper, TeaApplicationConfiguration config) {
        WebGLContextAttributes attr = WebGLContextAttributes.create();
        attr.setAlpha(config.alpha);
        attr.setAntialias(config.antialiasing);
        attr.setStencil(config.stencil);
        attr.setPremultipliedAlpha(config.premultipliedAlpha);
        attr.setPreserveDrawingBuffer(config.preserveDrawingBuffer);
        HTMLCanvasElement canvas = (HTMLCanvasElement)canvasWrapper;
        WebGLRenderingContextWrapper context = (WebGLRenderingContextWrapper)canvas.getContext("webgl", attr);
        return context;
    }
}
