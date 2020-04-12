package com.github.xpenatan.gdx.backend.web;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.github.xpenatan.gdx.backend.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLRenderingContextWrapper;

public class WebGraphics implements Graphics {

	private WebGLRenderingContextWrapper context;
	protected HTMLCanvasElementWrapper canvas;
	protected WebApplicationConfiguration config;
	protected GL20 gl20;
	protected GLVersion glVersion;

	public WebGraphics(WebApplicationConfiguration config) {
		this.config = config;
		this.canvas = config.canvas;
		context = canvas.getGLContext(config);
		gl20 = new WebGL20(context);

		gl20.glViewport(0, 0, canvas.getWidth(), canvas.getHeight());
		gl20.glClearColor(1, 1, 0f, 1f);
		gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public boolean isGL30Available() {
		return false;
	}

	@Override
	public GL20 getGL20() {
		return gl20;
	}

	@Override
	public GL30 getGL30() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGL20(GL20 gl20) {
		this.gl20 = gl20;
		Gdx.gl = gl20;
		Gdx.gl20 = gl20;
	}

	@Override
	public void setGL30(GL30 gl30) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBackBufferWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBackBufferHeight() {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getDeltaTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getRawDeltaTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFramesPerSecond() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public GraphicsType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GLVersion getGLVersion() {
		return glVersion;
	}

	@Override
	public float getPpiX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getPpiY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getPpcX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getPpcY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getDensity() {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DisplayMode[] getDisplayModes(Monitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DisplayMode getDisplayMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DisplayMode getDisplayMode(Monitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setFullscreenMode(DisplayMode displayMode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setWindowedMode(int width, int height) {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supportsExtension(String extension) {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return false;
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

}
