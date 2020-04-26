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

/**
 * @author xpenatan
 */
public class WebApplication implements Application, Runnable {

	private static WebAgentInfo agentInfo;

	private WebGraphics graphics;
	private WebInput input;
	private HTMLCanvasElementWrapper canvas;
	private WebApplicationConfiguration config;
	private ApplicationListener appListener;
	private WindowWrapper window;

	private AppState initState = AppState.IDLE;

	private int lastWidth = -1;
	private int lastHeight = 1;

	private WebApplicationLogger logger;

	public static WebAgentInfo getAgentInfo () {
		return agentInfo;
	}

	public WebApplication(ApplicationListener appListener, WebApplicationConfiguration config) {
		WebJSHelper jSHelper = WebApplicationConfiguration.JSHelper;
		this.window = jSHelper.getCurrentWindow();
		this.appListener = appListener;
		this.config = config;
		this.canvas = config.JSHelper.getCanvas();
		WebApplication.agentInfo = WebApplicationConfiguration.JSHelper.getAgentInfo();
		init();
	}

	private void init() {
		graphics = new WebGraphics(config);
		input = new WebInput(this.canvas);
		logger = new WebApplicationLogger();


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
		return graphics;
	}

	@Override
	public Audio getAudio() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Input getInput() {
		return input;
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
		logger.log(tag, message);
	}

	@Override
	public void log(String tag, String message, Throwable exception) {
		logger.log(tag, message, exception);
	}

	@Override
	public void error(String tag, String message) {
		logger.error(tag, message);
	}

	@Override
	public void error(String tag, String message, Throwable exception) {
		logger.error(tag, message, exception);
	}

	@Override
	public void debug(String tag, String message) {
		logger.debug(tag, message);
	}

	@Override
	public void debug(String tag, String message, Throwable exception) {
		logger.debug(tag, message, exception);
	}

	@Override
	public void setLogLevel(int logLevel) {
		logger.setLogLevel(logLevel);
	}

	@Override
	public int getLogLevel() {
		return logger.getLogLevel();
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

//	applicationLogger =  new ApplicationLogger() {
//		@Override
//		public void log (String tag, String message) {
//			System.out.println(tag + ": " + message);
//		}
//
//		@Override
//		public void log (String tag, String message, Throwable exception) {
//			System.out.println(tag + ": " + message);
//			exception.printStackTrace(System.out);
//		}
//
//		@Override
//		public void error (String tag, String message) {
//			ScriptHelper.put("tag", tag, this);
//			ScriptHelper.put("message", message, this);
//			ScriptHelper.evalNoResult("console.error(tag + ': ' + message)", this);
//		}
//
//		@Override
//		public void error (String tag, String message, Throwable exception) {
//			ScriptHelper.put("tag", tag, this);
//			ScriptHelper.put("message", message, this);
//			ScriptHelper.evalNoResult("console.error(tag + ': ' + message)", this);
//			exception.printStackTrace(System.err);
//		}
//
//		@Override
//		public void debug (String tag, String message) {
//			ScriptHelper.put("tag", tag, this);
//			ScriptHelper.put("message", message, this);
//			ScriptHelper.evalNoResult("console.warn(tag + ': ' + message)", this);
//		}
//
//		@Override
//		public void debug (String tag, String message, Throwable exception) {
//			ScriptHelper.put("tag", tag, this);
//			ScriptHelper.put("message", message, this);
//			ScriptHelper.evalNoResult("console.warn(tag + ': ' + message)", this);
//			exception.printStackTrace(System.out);
//		}
//	};

	public enum AppState {
		IDLE,
		QUEUE_ASSETS_LOADED,
		INIT_SOUND,
		APP_CREATE,
		APP_READY
	}

}
