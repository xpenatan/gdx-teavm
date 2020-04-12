package com.github.xpenatan.gdx.backend.web;

import com.github.xpenatan.gdx.backend.web.dom.HTMLCanvasElementWrapper;

/**
 * @author xpenatan
 */
public class WebApplicationConfiguration {
	public HTMLCanvasElementWrapper canvas;
	/** whether to use a stencil buffer **/
	public boolean stencil = false;
	/** whether to enable antialiasing **/
	public boolean antialiasing = false;
	/** whether to include an alpha channel in the color buffer to combine the color buffer with the rest of the webpage
	 * effectively allows transparent backgrounds in Dragome, at a performance cost. **/
	public boolean alpha = false;
	/** whether to use premultipliedalpha, may have performance impact  **/
	public boolean premultipliedAlpha = false;
	/** preserve the back buffer, needed if you fetch a screenshot via canvas#toDataUrl, may have performance impact **/
	public boolean preserveDrawingBuffer = false;
	/** whether to use debugging mode for OpenGL calls. Errors will result in a RuntimeException being thrown. */
	public boolean useDebugGL = false;
	/** whether SoundManager2 should prefer to use flash instead of html5 audio (it should fall back if not available) */
	public boolean preferFlash = false;
}
