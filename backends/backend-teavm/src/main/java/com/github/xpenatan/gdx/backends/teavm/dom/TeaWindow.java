package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.browser.AnimationFrameCallback;
import org.teavm.jso.browser.Location;
import org.teavm.jso.browser.TimerHandler;
import org.teavm.jso.browser.Window;
import com.github.xpenatan.gdx.backend.web.dom.DocumentWrapper;
import com.github.xpenatan.gdx.backend.web.dom.LocationWrapper;
import com.github.xpenatan.gdx.backend.web.dom.TimerWrapper;
import com.github.xpenatan.gdx.backend.web.dom.WindowWrapper;

/**
 * @author xpenatan
 */
public class TeaWindow implements WindowWrapper, AnimationFrameCallback {

	private Window window;
	private Runnable runnable;
	private TeaTimer timer;

	public TeaWindow() {
		this.window = Window.current();
		timer = new TeaTimer();
	}

	@Override
	public DocumentWrapper getDocument() {
		DocumentWrapper document = (DocumentWrapper)window.getDocument();
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
	public TimerWrapper getTimer() {
		return timer;
	}

	@Override
	public LocationWrapper getLocation() {
		Location location = window.getLocation();
		return (LocationWrapper)location;
	}
}
