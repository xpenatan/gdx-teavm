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

package com.badlogic.gdx.backends.dragome;

import org.w3c.dom.Element;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.dragome.js.webgl.WebGLContextAttributes;
import com.badlogic.gdx.backends.dragome.js.webgl.WebGLFactory;
import com.badlogic.gdx.backends.dragome.js.webgl.WebGLRenderingContext;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsDelegateFactory;
import com.dragome.web.html.dom.html5canvas.interfaces.HTMLCanvasElement;

/** @author xpenatan */
public class DragomeGraphics implements Graphics {

	HTMLCanvasElement canvas;
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

	public DragomeGraphics (DragomeApplication app, DragomeApplicationConfiguration config) {
		this.app = app;
		Element canvasElement = app.elementBySelector.getElementBySelector("canvas");
		canvas = JsDelegateFactory.createFromNode(canvasElement, HTMLCanvasElement.class);
		this.config = config;
	}

	public boolean init () {
		ScriptHelper.put("canvas", canvas, this);
		WebGLContextAttributes attributes = WebGLFactory.create();
		attributes.setAntialias(config.antialiasing);
		attributes.setStencil(config.stencil);
		attributes.setAlpha(config.alpha);
		attributes.setPremultipliedAlpha(config.premultipliedAlpha);
		attributes.setPreserveDrawingBuffer(config.preserveDrawingBuffer);
		ScriptHelper.evalNoResult("var names = [ 'experimental-webgl', 'webgl', 'moz-webgl', 'webkit-webgl', 'webkit-3d']", this);
		ScriptHelper.evalNoResult("var obj; for ( var i = 0; i < names.length; i++) { try {var ctx = canvas.node.getContext(names[i], attributes); if (ctx != null) { obj = ctx; } } catch (e) { } }", this);
		Object instance =  ScriptHelper.eval("obj", this);
		if (instance == null) return false;
		WebGLRenderingContext context = JsDelegateFactory.createFrom(instance, WebGLRenderingContext.class);
		if(config.useDebugGL)
			gl = new DragomeGL20Debug(context);
		else
			gl = new DragomeGL20(context);
		return true;
	}

	public void update () {
		long currTimeStamp = System.currentTimeMillis();
		deltaTime = (currTimeStamp - lastTimeStamp) / 1000.0f;
		lastTimeStamp = currTimeStamp;
		time += deltaTime;
		frames++;
		if (time > 1) {
			this.fps = frames;
			time = 0;
			frames = 0;
		}
	}

	@Override
	public boolean isGL30Available () {
		return false;
	}

	@Override
	public GL20 getGL20 () {
		return gl;
	}

	@Override
	public GL30 getGL30 () {
		return null;
	}

	@Override
	public int getWidth () {
		return canvas.getWidth();
	}

	@Override
	public int getHeight () {
		return canvas.getHeight();
	}

	@Override
	public int getBackBufferWidth () {
		return canvas.getWidth();
	}

	@Override
	public int getBackBufferHeight () {
		return canvas.getHeight();
	}

	@Override
	public long getFrameId () {
		return frameId;
	}

	@Override
	public float getDeltaTime () {
		return deltaTime;
	}

	@Override
	public float getRawDeltaTime () {
		return deltaTime;
	}

	@Override
	public int getFramesPerSecond () {
		return (int)fps;
	}

	@Override
	public GraphicsType getType () {
		return GraphicsType.WebGL;
	}

	@Override
	public float getPpiX () {
		return 96;
	}

	@Override
	public float getPpiY () {
		return 96;
	}

	@Override
	public float getPpcX () {
		return 96 / 2.54f;
	}

	@Override
	public float getPpcY () {
		return 96 / 2.54f;
	}

	@Override
	public float getDensity () {
		return 96.0f / 160;
	}

	@Override
	public boolean supportsDisplayModeChange () {
		return false;
	}

	@Override
	public Monitor getPrimaryMonitor () {
		return null;
	}

	@Override
	public Monitor getMonitor () {
		return null;
	}

	@Override
	public Monitor[] getMonitors () {
		return null;
	}

	@Override
	public DisplayMode[] getDisplayModes () {
		return null;
	}

	@Override
	public DisplayMode[] getDisplayModes (Monitor monitor) {
		return null;
	}

	@Override
	public DisplayMode getDisplayMode () {
		return null;
	}

	@Override
	public DisplayMode getDisplayMode (Monitor monitor) {
		return null;
	}

	@Override
	public boolean setFullscreenMode (DisplayMode displayMode) {
		return false;
	}

	@Override
	public boolean setWindowedMode (int width, int height) {
		return false;
	}

	@Override
	public void setTitle (String title) {
	}

	@Override
	public void setVSync (boolean vsync) {
	}

	@Override
	public BufferFormat getBufferFormat () {
		return new BufferFormat(8, 8, 8, 0, 16, config.stencil ? 8 : 0, 0, false);
	}

	@Override
	public boolean supportsExtension (String extension) {
		if (extensions == null) extensions = Gdx.gl.glGetString(GL20.GL_EXTENSIONS);
		return extensions.contains(extension);
	}

	@Override
	public void setContinuousRendering (boolean isContinuous) {
	}

	@Override
	public boolean isContinuousRendering () {
		return true;
	}

	@Override
	public void requestRendering () {
	}

	@Override
	public boolean isFullscreen () {
		return false;
	}

	@Override
	public Cursor newCursor (Pixmap pixmap, int xHotspot, int yHotspot) {
		return null;
	}

	@Override
	public void setCursor (Cursor cursor) {
	}

	@Override
	public void setSystemCursor (SystemCursor systemCursor) {
	}
}
