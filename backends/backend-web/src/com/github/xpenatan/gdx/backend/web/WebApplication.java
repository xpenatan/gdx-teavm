package com.github.xpenatan.gdx.backend.web;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Clipboard;
import com.github.xpenatan.gdx.backend.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.WindowWrapper;
import com.github.xpenatan.gdx.backend.web.preloader.Preloader;

public class WebApplication implements Application, Runnable {

	private WebGraphics graphics;
	private WebInput input;
	private HTMLCanvasElementWrapper canvas;
	private WebApplicationConfiguration config;
	private ApplicationListener appListener;
	private WindowWrapper window;

	private AppState initState = AppState.IDLE;

	private int lastWidth = -1;
	private int lastHeight = 1;

	public WebApplication(ApplicationListener appListener, WebApplicationConfiguration config) {
		this.window = config.window;
		this.appListener = appListener;
		this.config = config;
		this.canvas = config.canvas;
		init();
	}

	private void init() {

		graphics = new WebGraphics(config);
		input = new WebInput();


		Gdx.app = this;
		Gdx.graphics = graphics;
		Gdx.gl = graphics.getGL20();
		Gdx.gl20 = graphics.getGL20();
		Gdx.input = input;

		window.requestAnimationFrame(this);
	}

	@Override
	public void run() {
		AppState state = initState;
		switch (state) {
			case IDLE:
				initState = AppState.QUEUE_ASSETS_LOADED;
				break;
			case APP_CREATE:
			case APP_READY:
				step(appListener);
				break;
			case QUEUE_ASSETS_LOADED:
//				initState = AppState.INIT_SOUND;
				initState = AppState.APP_CREATE;
				break;
			default:
				break;
		}

		window.requestAnimationFrame(this);
	}

	private void step(ApplicationListener appListener) {
		graphics.update();
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		if (width != lastWidth || height != lastHeight) {
			lastWidth = width;
			lastHeight = height;
			Gdx.gl.glViewport(0, 0, width, height);

			if(initState == AppState.APP_CREATE) {
				initState = AppState.APP_READY;
				appListener.create();
			}
			appListener.resize(width, height);
		}
		graphics.frameId++;
		appListener.render();
		input.reset();
	}

	public Preloader getPreloader() {

		return null;
	}

	@Override
	public ApplicationListener getApplicationListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graphics getGraphics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Audio getAudio() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Input getInput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Files getFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Net getNet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void log(String tag, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void log(String tag, String message, Throwable exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void error(String tag, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void error(String tag, String message, Throwable exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void debug(String tag, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void debug(String tag, String message, Throwable exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLogLevel(int logLevel) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLogLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setApplicationLogger(ApplicationLogger applicationLogger) {
		// TODO Auto-generated method stub

	}

	@Override
	public ApplicationLogger getApplicationLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getJavaHeap() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNativeHeap() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Preferences getPreferences(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Clipboard getClipboard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void postRunnable(Runnable runnable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		// TODO Auto-generated method stub

	}

	public enum AppState {
		IDLE,
		QUEUE_ASSETS_LOADED,
		INIT_SOUND,
		APP_CREATE,
		APP_READY
	}

}
