package com.github.xpenatan.gdx.backends.dragome.dom;

import org.w3c.dom.Document;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.Window;
import com.github.xpenatan.gdx.backend.web.dom.HTMLDocumentWrapper;
import com.github.xpenatan.gdx.backend.web.dom.WindowWrapper;

/**
 * @author xpenatan
 */
public class DragomeWindow implements WindowWrapper {

	Window window;

	public DragomeWindow() {
		window = Window.getInstance();
	}

	@Override
	public HTMLDocumentWrapper getDocument() {
		Document document = Window.getDocument();
		return JsCast.castTo(document, HTMLDocumentWrapper.class);
	}

	@Override
	public void requestAnimationFrame(Runnable runnable) {
		window.requestAnimationFrame(runnable);
	}

	@Override
	public int setTimeout(Runnable run, int delay) {
		//TODO needs to impl
		return 0;
	}
}
