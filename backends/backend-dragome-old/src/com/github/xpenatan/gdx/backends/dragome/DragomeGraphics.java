/*******************************************************************************
 * Copyright 2016 Natan Guilherme.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.github.xpenatan.gdx.backends.dragome;

import org.w3c.dom.Element;
import org.w3c.dom.webgl.WebGLContextAttributes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.w3c.HTMLCanvasElementExtension;
import com.dragome.web.html.dom.w3c.WebGLRenderingContextExtension;

/**
 * @author xpenatan
 */
public class DragomeGraphics implements Graphics {
    GLVersion glVersion;
    HTMLCanvasElementExtension canvas;
    GL20 gl;
    String extensions;
    float fps = 0;
    long lastTimeStamp = System.currentTimeMillis();
    long frameId = -1;
    float deltaTime = 0;
    float time = 0;
    int frames;
    DragomeApplicationConfiguration config;
    DragomeApplication app;

    public DragomeGraphics(DragomeApplication app, DragomeApplicationConfiguration config) {
        this.app = app;
        Element canvasElement = app.elementBySelector.getElementBySelector("canvas");
        canvas = JsCast.castTo(canvasElement, HTMLCanvasElementExtension.class);
        this.config = config;
    }

    public boolean init() {
        WebGLContextAttributes attributes = ScriptHelper.evalCasting("{premultipliedAlpha:false}", WebGLContextAttributes.class, null);
        attributes.setAntialias(config.antialiasing);
        attributes.setStencil(config.stencil);
        attributes.setAlpha(config.alpha);
        attributes.setPremultipliedAlpha(config.premultipliedAlpha);
        attributes.setPreserveDrawingBuffer(config.preserveDrawingBuffer);

        WebGLRenderingContextExtension context = findWebGLContext(attributes);
        if(context == null) return false;

        if(config.useDebugGL)
            gl = new DragomeGL20Debug(context);
        else
            gl = new DragomeGL20(context);

        String versionString = gl.glGetString(GL20.GL_VERSION);
        String vendorString = gl.glGetString(GL20.GL_VENDOR);
        String rendererString = gl.glGetString(GL20.GL_RENDERER);
// glVersion = new GLVersion(Application.ApplicationType.WebGL, versionString, vendorString, rendererString); //FIXME needs fix
        return true;
    }

    private WebGLRenderingContextExtension findWebGLContext(WebGLContextAttributes attributes) {
// String[] contextNames= new String[] { "experimental-webgl", "webgl", "moz-webgl", "webkit-webgl", "webkit-3d" };
// for (String contextName : contextNames)
// {
// Object context= canvas.getContext(contextName, attributes);
// if (context != null)
// return (WebGLRenderingContextExtension) context;
// }

        String[] contextNames = new String[]{"experimental-webgl", "webgl", "moz-webgl", "webkit-webgl", "webkit-3d"};
        ScriptHelper.put("canvas", canvas, this);
        ScriptHelper.put("attr", attributes, this);
        for(String contextName : contextNames) {
            ScriptHelper.put("contextName", contextName, this);
            Object con = ScriptHelper.eval("canvas.node.getContext(contextName, attr.node)", this);
            if(con != null) {
                ScriptHelper.put("con", con, null);
                return ScriptHelper.evalCasting("con", WebGLRenderingContextExtension.class, null);
            }
        }
        return null;
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
        return null;
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
        return false;
    }

    @Override
    public Monitor getPrimaryMonitor() {
        return null;
    }

    @Override
    public Monitor getMonitor() {
        return null;
    }

    @Override
    public Monitor[] getMonitors() {
        return null;
    }

    @Override
    public DisplayMode[] getDisplayModes() {
        return new DisplayMode[]{new DisplayMode(getScreenWidth(), getScreenHeight(), 60, 8) {
        }};
    }

    private int getScreenWidth() {
        return ScriptHelper.evalInt("window.screen.width", this);
    }

    private int getScreenHeight() {
        return ScriptHelper.evalInt("window.screen.height", this);
    }

    @Override
    public DisplayMode[] getDisplayModes(Monitor monitor) {
        return getDisplayModes();
    }

    @Override
    public DisplayMode getDisplayMode() {
        return new DisplayMode(getScreenWidth(), getScreenHeight(), 60, 8) {
        };
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
    public void setVSync(boolean vsync) {
    }

    @Override
    public BufferFormat getBufferFormat() {
        return new BufferFormat(8, 8, 8, 0, 16, config.stencil ? 8 : 0, 0, false);
    }

    @Override
    public boolean supportsExtension(String extension) {
        if(extensions == null)
            extensions = Gdx.gl.glGetString(GL20.GL_EXTENSIONS);
        return extensions != null ? extensions.contains(extension) : false;
    }

    @Override
    public void setContinuousRendering(boolean isContinuous) {
    }

    @Override
    public boolean isContinuousRendering() {
        return true;
    }

    @Override
    public void requestRendering() {
    }

    @Override
    public boolean isFullscreen() {
        return false;
    }

    @Override
    public Cursor newCursor(Pixmap pixmap, int xHotspot, int yHotspot) {
        return null;
    }

    @Override
    public void setCursor(Cursor cursor) {
    }

    @Override
    public void setSystemCursor(SystemCursor systemCursor) {
    }

    @Override
    public GLVersion getGLVersion() {
        return glVersion;
    }

    @Override
    public void setUndecorated(boolean undecorated) {
    }

    @Override
    public void setResizable(boolean resizable) {
    }

    @Override
    public void setGL20(GL20 gl20) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setGL30(GL30 gl30) {
        // TODO Auto-generated method stub

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
    public float getBackBufferScale() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setForegroundFPS(int fps) {
        // TODO Auto-generated method stub

    }
}
