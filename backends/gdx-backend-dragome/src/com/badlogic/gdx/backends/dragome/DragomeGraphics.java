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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.dragome.js.webgl.WebGLContextAttributes;
import com.badlogic.gdx.backends.dragome.js.webgl.WebGLRenderingContext;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.w3c.HTMLCanvasElementExtension;

/** @author xpenatan */
public class DragomeGraphics implements Graphics
{
	GLVersion glVersion;
	HTMLCanvasElementExtension canvas;
	GL20 gl;
	String extensions;
	float fps= 0;
	long lastTimeStamp= System.currentTimeMillis();
	long frameId= -1;
	float deltaTime= 0;
	float time= 0;
	int frames;
	DragomeApplicationConfiguration config;
	DragomeApplication app;

	public DragomeGraphics(DragomeApplication app, DragomeApplicationConfiguration config)
	{
		this.app= app;
		Element canvasElement= app.elementBySelector.getElementBySelector("canvas");
		canvas= JsCast.castTo(canvasElement, HTMLCanvasElementExtension.class);
		this.config= config;
	}

	public boolean init()
	{
		ScriptHelper.put("canvas", canvas, this);
		
		WebGLContextAttributes attributes= WebGLContextAttributes.create();
		attributes.set_antialias(config.antialiasing);
		attributes.set_stencil(config.stencil);
		attributes.set_alpha(config.alpha);
		attributes.set_premultipliedAlpha(config.premultipliedAlpha);
		attributes.set_preserveDrawingBuffer(config.preserveDrawingBuffer);

		WebGLRenderingContext context= findWebGLContext();
		if (context == null)
			return false;

		if (config.useDebugGL)
			gl= new DragomeGL20Debug(context);
		else
			gl= new DragomeGL20(context);

		String versionString= gl.glGetString(GL20.GL_VERSION);
		String vendorString= gl.glGetString(GL20.GL_VENDOR);
		String rendererString= gl.glGetString(GL20.GL_RENDERER);
//		glVersion = new GLVersion(Application.ApplicationType.WebGL, versionString, vendorString, rendererString); //FIXME needs fix
		return true;
	}

	private WebGLRenderingContext findWebGLContext()
	{
		String[] contextNames= new String[] { "moz-webgl", "webgl", "experimental-webgl", "webkit-webgl", "webkit-3d" };
		for (String contextName : contextNames)
		{
			Object context= canvas.getContext(contextName);
			if (context != null)
				return (WebGLRenderingContext) context;
		}

		return null;
	}

	public void update()
	{
		long currTimeStamp= System.currentTimeMillis();
		deltaTime= (currTimeStamp - lastTimeStamp) / 1000.0f;
		lastTimeStamp= currTimeStamp;
		time+= deltaTime;
		frames++;
		if (time > 1)
		{
			this.fps= frames;
			time= 0;
			frames= 0;
		}
	}

	@Override
	public boolean isGL30Available()
	{
		return false;
	}

	@Override
	public GL20 getGL20()
	{
		return gl;
	}

	@Override
	public GL30 getGL30()
	{
		return null;
	}

	@Override
	public int getWidth()
	{
		return canvas.getWidth();
	}

	@Override
	public int getHeight()
	{
		return canvas.getHeight();
	}

	@Override
	public int getBackBufferWidth()
	{
		return canvas.getWidth();
	}

	@Override
	public int getBackBufferHeight()
	{
		return canvas.getHeight();
	}

	@Override
	public long getFrameId()
	{
		return frameId;
	}

	@Override
	public float getDeltaTime()
	{
		return deltaTime;
	}

	@Override
	public float getRawDeltaTime()
	{
		return deltaTime;
	}

	@Override
	public int getFramesPerSecond()
	{
		return (int) fps;
	}

	@Override
	public GraphicsType getType()
	{
		return GraphicsType.WebGL;
	}

	@Override
	public float getPpiX()
	{
		return 96;
	}

	@Override
	public float getPpiY()
	{
		return 96;
	}

	@Override
	public float getPpcX()
	{
		return 96 / 2.54f;
	}

	@Override
	public float getPpcY()
	{
		return 96 / 2.54f;
	}

	@Override
	public float getDensity()
	{
		return 96.0f / 160;
	}

	@Override
	public boolean supportsDisplayModeChange()
	{
		return false;
	}

	@Override
	public Monitor getPrimaryMonitor()
	{
		return null;
	}

	@Override
	public Monitor getMonitor()
	{
		return null;
	}

	@Override
	public Monitor[] getMonitors()
	{
		return null;
	}

	@Override
	public DisplayMode[] getDisplayModes()
	{
		return null;
	}

	@Override
	public DisplayMode[] getDisplayModes(Monitor monitor)
	{
		return null;
	}

	@Override
	public DisplayMode getDisplayMode()
	{
		return null;
	}

	@Override
	public DisplayMode getDisplayMode(Monitor monitor)
	{
		return null;
	}

	@Override
	public boolean setFullscreenMode(DisplayMode displayMode)
	{
		return false;
	}

	@Override
	public boolean setWindowedMode(int width, int height)
	{
		return false;
	}

	@Override
	public void setTitle(String title)
	{
	}

	@Override
	public void setVSync(boolean vsync)
	{
	}

	@Override
	public BufferFormat getBufferFormat()
	{
		return new BufferFormat(8, 8, 8, 0, 16, config.stencil ? 8 : 0, 0, false);
	}

	@Override
	public boolean supportsExtension(String extension)
	{
		if (extensions == null)
			extensions= Gdx.gl.glGetString(GL20.GL_EXTENSIONS);
		return extensions.contains(extension);
	}

	@Override
	public void setContinuousRendering(boolean isContinuous)
	{
	}

	@Override
	public boolean isContinuousRendering()
	{
		return true;
	}

	@Override
	public void requestRendering()
	{
	}

	@Override
	public boolean isFullscreen()
	{
		return false;
	}

	@Override
	public Cursor newCursor(Pixmap pixmap, int xHotspot, int yHotspot)
	{
		return null;
	}

	@Override
	public void setCursor(Cursor cursor)
	{
	}

	@Override
	public void setSystemCursor(SystemCursor systemCursor)
	{
	}

	public GLVersion getGLVersion()
	{
		return glVersion;
	}

	public void setUndecorated(boolean undecorated) {
	}

	public void setResizable(boolean resizable) {
	}
}
