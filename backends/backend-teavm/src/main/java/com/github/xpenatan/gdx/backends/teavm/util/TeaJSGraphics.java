package com.github.xpenatan.gdx.backends.teavm.util;

import com.github.xpenatan.gdx.backends.teavm.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.WebGraphics;
import com.github.xpenatan.gdx.backends.teavm.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGLRenderingContextWrapper;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.webgl.WebGLContextAttributes;

@Deprecated
public class TeaJSGraphics implements WebJSGraphics {

    @Override
    public double getNativeScreenDensity() {
        return getNativeScreenDensityJS();
    }

    @JSBody(script = "return devicePixelRatio || 1;")
    private static native int getNativeScreenDensityJS();

    @Override
    public int getScreenWidthJSNI() {
        return getScreenWidth();
    }

    @JSBody(script = "return screen.width;")
    private static native int getScreenWidth();

    @Override
    public int getScreenHeightJSNI() {
        return getScreenHeight();
    }

    @JSBody(script = "return screen.height;")
    private static native int getScreenHeight();

    @Override
    public boolean setFullscreenJSNI(WebGraphics graphics, HTMLCanvasElementWrapper canvas, int screenWidth, int screenHeight) {
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

    @Override
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

    @Override
    public boolean isFullscreenJSNI() {
        return isFullscreen();
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
    private static native boolean isFullscreen();

    @JSFunctor
    public interface FullscreenChanged extends org.teavm.jso.JSObject {
        void fullscreenChanged();
    }

    @Override
    public WebGLRenderingContextWrapper getGLContext(HTMLCanvasElementWrapper canvasWrapper, WebApplicationConfiguration config) {
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
