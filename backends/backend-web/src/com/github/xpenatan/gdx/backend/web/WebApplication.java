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
	private HTMLCanvasElementWrapper canvas;
	private WebApplicationConfiguration config;
	private ApplicationListener appListener;
	private WindowWrapper window;

//	private AppState initState = AppState.IDLE;
	private AppState initState = AppState.IS_READY;

	public WebApplication(ApplicationListener appListener, WebApplicationConfiguration config) {
		this.window = config.window;
		this.appListener = appListener;
		this.config = config;
		this.canvas = config.canvas;
		init();
	}

	private void init() {

		graphics = new WebGraphics(config);


		Gdx.app = this;
		Gdx.graphics = graphics;
		Gdx.gl = graphics.getGL20();
		Gdx.gl20 = graphics.getGL20();


		window.requestAnimationFrame(this);
	}

	@Override
	public void run() {
		switch (initState) {
			case IDLE:
				break;
			case IS_READY:
				step();
				break;
			case APP_CREATE:
				break;
			case ASSETS_LOADED:
				break;
			case INIT_SOUND:
				break;
		}
	}

	private void step() {
		System.out.println("TEST");
		window.requestAnimationFrame(this);
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
		ASSETS_LOADED,
		INIT_SOUND,
		APP_CREATE,
		IS_READY
	}

}
