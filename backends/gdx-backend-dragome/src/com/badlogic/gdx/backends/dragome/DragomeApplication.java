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
import org.w3c.dom.events.EventListener;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.dragome.preloader.AssetDownloader;
import com.badlogic.gdx.backends.dragome.preloader.Preloader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.dragome.view.DefaultVisualActivity;
import com.dragome.web.dispatcher.EventDispatcherImpl;
import com.dragome.web.html.dom.w3c.BrowserDomHandler;
import com.dragome.web.html.dom.w3c.HTMLCanvasElementExtension;

/** @author xpenatan */
public abstract class DragomeApplication extends DefaultVisualActivity implements Application {

	private ApplicationListener listener;
	BrowserDomHandler elementBySelector;

	DragomeGraphics graphics;
	DragomeInput input;
	DragomeNet net;
	DragomeFiles files;
	DragomeAudio audio;
	Clipboard clipboard;
	boolean init;
	Preloader preloader;

	private Array<Runnable> runnables = new Array<Runnable>();
	private Array<Runnable> runnablesHelper = new Array<Runnable>();
	private Array<LifecycleListener> lifecycleListeners = new Array<LifecycleListener>(); // FIXME need a proper impl
	private int lastWidth;
	private int lastHeight;

	@Override
	public void build () {

		elementBySelector = new BrowserDomHandler();

// Element createElement = document.createElement("canvas");
// Element elementById = document.getElementById("body");
// elementById.appendChild(createElement);

		prepate();
	}

	private void prepate () {
		listener = createApplicationListener();
		if (listener == null) return;
		DragomeApplicationConfiguration config = getConfig();
		if(config == null)
			config = new DragomeApplicationConfiguration();
		graphics = new DragomeGraphics(this, config);
		if (!graphics.init()) {
			error("DragomeApplication", "WebGL not supported.");
			return;
		}

		preloader = new Preloader(AssetDownloader.getHostPageBaseURL());
		input = new DragomeInput(this);
		net = new DragomeNet();
		files = new DragomeFiles(preloader);
		audio = new DragomeAudio();
		Gdx.app = this;
		Gdx.graphics = graphics;
		Gdx.gl20 = graphics.getGL20();
		Gdx.gl = Gdx.gl20;
		Gdx.input = input;
		Gdx.net = net;
		Gdx.files = files;
		Gdx.audio = audio;
		this.clipboard = new DragomeClipboard();
		lastWidth = graphics.getWidth();
		lastHeight = graphics.getHeight();

		try {
			listener.create();
			onResize();
		} catch (Throwable t) {
			error("DragomeApplication", "exception: " + t.getMessage(), t);
			t.printStackTrace();
			throw new RuntimeException(t);
		}

		DragomeWindow.onResize(new Runnable() {

			@Override
			public void run () {
				onResize();
			}
		});

//		new Timer().setInterval(new Runnable() {
//			public void run () {
//				try {
//					mainLoop();
//				} catch (Throwable t) {
//					error("DragomeApplication", "exception: " + t.getMessage(), t);
//					throw new RuntimeException(t);
//				}
//			}
//		}, 0);

		DragomeWindow.requestAnimationFrame(new Runnable() {

			@Override
			public void run () {
				try {
					mainLoop();
				} catch (Throwable t) {
					error("DragomeApplication", "exception: " + t.getMessage(), t);
					throw new RuntimeException(t);
				}
				DragomeWindow.requestAnimationFrame(this, graphics.canvas);
			}
		}, graphics.canvas);


		init = true;
	}

	protected void onResize () {
		if (init == false) return;
		try {
			mainLoop();
		} catch (Throwable t) {
			error("DragomeApplication", "exception: " + t.getMessage(), t);
			throw new RuntimeException(t);
		}
	}

	private void mainLoop () {
		graphics.update();
		if (Gdx.graphics.getWidth() != lastWidth || Gdx.graphics.getHeight() != lastHeight) {
			DragomeApplication.this.listener.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			lastWidth = graphics.getWidth();
			lastHeight = graphics.getHeight();
			Gdx.gl.glViewport(0, 0, lastWidth, lastHeight);
		}
		runnablesHelper.addAll(runnables);
		runnables.clear();
		for (int i = 0; i < runnablesHelper.size; i++) {
			runnablesHelper.get(i).run();
		}
		runnablesHelper.clear();
		graphics.frameId++;
		listener.render();
		input.reset();
	}

	public abstract ApplicationListener createApplicationListener ();

	public abstract DragomeApplicationConfiguration getConfig ();

	@Override
	public ApplicationListener getApplicationListener () {
		return null;
	}

	@Override
	public Graphics getGraphics () {
		return graphics;
	}

	@Override
	public Audio getAudio () {
		return audio;
	}

	@Override
	public Input getInput () {
		return input;
	}

	@Override
	public Files getFiles () {
		return files;
	}

	@Override
	public Net getNet () {
		return net;
	}

	@Override
	public void log (String tag, String message) {
	}

	@Override
	public void log (String tag, String message, Throwable exception) {
	}

	@Override
	public void error (String tag, String message) {
	}

	@Override
	public void error (String tag, String message, Throwable exception) {
	}

	@Override
	public void debug (String tag, String message) {
	}

	@Override
	public void debug (String tag, String message, Throwable exception) {
	}

	@Override
	public void setLogLevel (int logLevel) {
	}

	@Override
	public int getLogLevel () {
		return 0;
	}

	@Override
	public ApplicationType getType () {
		return null;
	}

	@Override
	public int getVersion () {
		return 0;
	}

	@Override
	public long getJavaHeap () {
		return 0;
	}

	@Override
	public long getNativeHeap () {
		return 0;
	}

	@Override
	public Preferences getPreferences (String name) {
		return null;
	}

	@Override
	public Clipboard getClipboard () {
		return clipboard;
	}

	@Override
	public void postRunnable (Runnable runnable) {
	}

	@Override
	public void exit () {
	}

	@Override
	public void addLifecycleListener (LifecycleListener listener) {
		synchronized (lifecycleListeners) {
			lifecycleListeners.add(listener);
		}
	}

	@Override
	public void removeLifecycleListener (LifecycleListener listener) {
		synchronized (lifecycleListeners) {
			lifecycleListeners.removeValue(listener, true);
		}
	}

	public HTMLCanvasElementExtension getCanvas () {
		return graphics.canvas;
	}

	public Preloader getPreloader () {
		return preloader;
	}

	public void addEventListener (EventListener aEventListener, String... aEvent) {
		Element theElement = elementBySelector.getElementBySelector("body");
		EventDispatcherImpl.setEventListener(theElement, aEventListener, aEvent);
	}
}
