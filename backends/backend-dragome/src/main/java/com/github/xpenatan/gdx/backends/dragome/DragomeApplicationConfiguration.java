package com.github.xpenatan.gdx.backends.dragome;

import org.w3c.dom.Element;
import org.w3c.dom.html.HTMLCanvasElement;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.w3c.BrowserDomHandler;
import com.github.xpenatan.gdx.backend.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backends.dragome.dom.DragomeCanvas;
import com.github.xpenatan.gdx.backends.dragome.dom.DragomeTypedArrays;
import com.github.xpenatan.gdx.backends.dragome.dom.DragomeWindow;

public class DragomeApplicationConfiguration extends WebApplicationConfiguration {

	public DragomeApplicationConfiguration(String canvasID) {
		window = new DragomeWindow();
		BrowserDomHandler elementBySelector = new BrowserDomHandler();
		Element canvasElem = elementBySelector.getElementBySelector(canvasID);
		HTMLCanvasElement canvasElement = JsCast.castTo(canvasElem, HTMLCanvasElement.class);
		canvas = new DragomeCanvas(canvasElement);

		new DragomeTypedArrays();
	}
}
