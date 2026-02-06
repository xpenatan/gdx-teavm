package com.github.xpenatan.gdx.teavm.backends.web;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.github.xpenatan.gdx.teavm.backends.web.dom.HTMLDocumentExt;
import com.github.xpenatan.gdx.teavm.backends.web.dom.impl.WebWindow;
import com.github.xpenatan.gdx.teavm.backends.web.gl.WebGL2RenderingContextExt;
import com.github.xpenatan.gdx.teavm.backends.web.gl.WebGLContextAttributesExt;
import com.github.xpenatan.gdx.teavm.backends.web.gl.WebGLRenderingContextExt;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.webgl.WebGLContextAttributes;
import org.teavm.jso.webgl.WebGLRenderingContext;

/**
 * @author xpenatan
 */
public class WebGLGraphics extends WebGraphics {
    private WebGLRenderingContext context;
    protected GL20 gl20;
    protected GL30 gl30;
    protected GLVersion glVersion;

    public WebGLGraphics(WebApplicationConfiguration config) {
        this.config = config;
        WebWindow window = new WebWindow();
        HTMLDocumentExt document = window.getDocument();
        HTMLElement elementID = document.getElementById(config.canvasID);
        this.canvas = (HTMLCanvasElement)elementID;

        WebGLContextAttributesExt attr = (WebGLContextAttributesExt)WebGLContextAttributes.create();
        attr.setAlpha(config.alpha);
        attr.setAntialias(config.antialiasing);
        attr.setStencil(config.stencil);
        attr.setPremultipliedAlpha(config.premultipliedAlpha);
        attr.setPreserveDrawingBuffer(config.preserveDrawingBuffer);
        attr.setPowerPreference(config.powerPreference);

        if (config.useGL30) {
            context = (WebGLRenderingContext)canvas.getContext("webgl2", attr);
        }

        if (config.useGL30 && context != null) {
            // WebGL2 supported
            this.gl30 = config.useDebugGL ? new WebGL30Debug((WebGL2RenderingContextExt)context)
                    : new WebGL30((WebGL2RenderingContextExt)context);
            this.gl20 = gl30;
        } else {
            context = (WebGLRenderingContext)canvas.getContext("webgl", attr);
            this.gl20 = config.useDebugGL ? new WebGL20Debug((WebGLRenderingContextExt)context) : new WebGL20((WebGLRenderingContextExt)context);
        }

        String versionString = gl20.glGetString(GL20.GL_VERSION);
        String vendorString = gl20.glGetString(GL20.GL_VENDOR);
        String rendererString = gl20.glGetString(GL20.GL_RENDERER);
        glVersion = new GLVersion(Application.ApplicationType.WebGL, versionString, vendorString, rendererString);

        if(config.width >= 0 || config.height >= 0) {
            if(config.isFixedSizeApplication()) {
                setCanvasSize(config.width, config.height, false);
            }
            else {
                WebWindow currentWindow = WebWindow.get();
                int width = currentWindow.getClientWidth() - config.padHorizontal;
                int height = currentWindow.getClientHeight() - config.padVertical;
                setCanvasSize(width, height, config.usePhysicalPixels);
            }
        }

        context.viewport(0, 0, getWidth(), getHeight());

        // listen to fullscreen changes
        addFullscreenChangeListener(canvas, new FullscreenChanged() {
          @Override
          public void fullscreenChanged() {
              // listening to fullscreen mode changes
          }
        });
    }

    @Override
    public boolean supportsExtension(String extensionName) {
        // Contrary to regular OpenGL, WebGL extensions need to be explicitly enabled before they can be used. See
        // https://developer.mozilla.org/en-US/docs/Web/API/WebGL_API/Using_Extensions
        // Thus, it is not safe to use an extension just because context.getSupportedExtensions() tells you it is available.
        // We need to call getExtension() to enable it.
        if(context != null) {
            return context.getExtension(extensionName) != null;
        }
        return false;
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
    public void setGL20(GL20 gl20) {
        this.gl20 = gl20;
        Gdx.gl = gl20;
        Gdx.gl20 = gl20;
    }

    @Override
    public void setGL30(GL30 gl30) {
        this.gl30 = gl30;
        if (gl30 != null) {
            this.gl20 = gl30;
            Gdx.gl = gl20;
            Gdx.gl20 = gl20;
            Gdx.gl30 = gl30;
        }
    }

    @Override
    public GLVersion getGLVersion() {
        return glVersion;
    }
}
