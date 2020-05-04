package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.browser.AnimationFrameCallback;
import org.teavm.jso.browser.Location;
import org.teavm.jso.browser.TimerHandler;
import org.teavm.jso.browser.Window;
import com.github.xpenatan.gdx.backend.web.dom.HTMLDocumentWrapper;
import com.github.xpenatan.gdx.backend.web.dom.LocationWrapper;
import com.github.xpenatan.gdx.backend.web.dom.WindowWrapper;

/**
 * @author xpenatan
 */
public class TeaWindow implements WindowWrapper, AnimationFrameCallback {

	private Window window;
	private Runnable runnable;

	public TeaWindow() {
		this.window = Window.current();
	}

	@Override
	public HTMLDocumentWrapper getDocument() {
		HTMLDocumentWrapper document = (HTMLDocumentWrapper)window.getDocument();
		return document;
	}

	@Override
	public void requestAnimationFrame(Runnable runnable) {
		this.runnable = runnable;
		Window.requestAnimationFrame(this);
	}

	@Override
	public void onAnimationFrame(double arg0) {
		Runnable toRun = runnable;
		runnable = null;
		toRun.run();
	}

	@Override
	public int setTimeout(Runnable run, int delay) {
		TimerHandler handler = new TimerHandler() {
			@Override
			public void onTimer() {
				run.run();
			}
		};
		return Window.setTimeout(handler, delay);
	}

	@Override
	public LocationWrapper getLocation() {
		Location location = window.getLocation();
		return (LocationWrapper)location;
	}
}
