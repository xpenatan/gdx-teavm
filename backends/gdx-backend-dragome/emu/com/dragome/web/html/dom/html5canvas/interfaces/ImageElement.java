package com.dragome.web.html.dom.html5canvas.interfaces;

import com.dragome.web.html.dom.html5canvas.interfaces.CanvasImageSource;

public interface ImageElement extends CanvasImageSource{

	int getWidth();
	int getHeight();
	void setSrc(String src);
}
