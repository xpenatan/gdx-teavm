package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.browser.AnimationFrameCallback;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLDocument;

import com.github.xpenatan.gdx.backend.web.dom.HTMLDocumentWrapper;
import com.github.xpenatan.gdx.backend.web.dom.WindowWrapper;

public class TeaWindow implements WindowWrapper, AnimationFrameCallback {

	private Window window;
	private Runnable runnable;

	public TeaWindow() {
		this.window = Window.current();
	}

	@Override
	public HTMLDocumentWrapper getDocument() {
		HTMLDocument document = window.getDocument();
		return new TeaDocument(document);
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

}
