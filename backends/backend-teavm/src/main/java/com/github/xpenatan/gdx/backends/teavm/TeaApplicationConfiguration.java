package com.github.xpenatan.gdx.backends.teavm;

import com.github.xpenatan.gdx.backends.teavm.dom.TeaTypedArrays;

/**
 * @author xpenatan
 */
public class TeaApplicationConfiguration {

    /**
     * Experimental webassembly pixmap
     */
    public boolean useNativePixmap = false;

    /** Sets the {@link TeaWindowListener} which will be informed about teavm events. */
    public TeaWindowListener windowListener;

    /**
     * Load assets before starting the game. For custom loading, change to false.
     */
    public boolean preloadAssets = true;

    /**
     * The prefix for the browser storage. If you have multiple apps on the same server and want to keep the
     * data separate for those applications, you will need to set unique prefixes. This is useful if you are
     * e.g. uploading multiple webapps to itch.io and want to keep the data separate for each application.
     * <p>
     * For example use "app1_" for one, and "app2_" for the other application, so the data that is stored in the
     * browser is not shared between the applications. If you leave the storage prefix at "", all the data
     * and files stored will be shared between the applications.
     */
    public String storagePrefix = "";

    /**
     * Show download logs.
     */
    public boolean showDownloadLogs = false;

    public String canvasID;

    /**
     * the width of the drawing area in pixels, 0 for using the available space or -1 to use html canvas size
     **/
    public int width = -1;
    /**
     * the height of the drawing area in pixels, 0 for using the available space or -1 to use html canvas size
     **/
    public int height = -1;
    /**
     * Padding to use for resizing the game content in the browser window, for resizable applications only. Defaults to 0. The
     * padding is necessary to prevent the browser from showing scrollbars. This can happen if the game content is of the same size
     * than the browser window. The padding is given in logical pixels, not affected by {@link #usePhysicalPixels}.
     */
    public int padHorizontal = 0, padVertical = 0;
    /**
     * whether to use a stencil buffer
     **/
    public boolean stencil = false;
    /**
     * whether to enable antialiasing
     **/
    public boolean antialiasing = false;
    /**
     * whether to include an alpha channel in the color buffer
     **/
    public boolean alpha = false;
    /**
     * whether to use premultipliedalpha, may have performance impact
     **/
    public boolean premultipliedAlpha = false;
    /**
     * preserve the back buffer, needed if you fetch a screenshot via canvas#toDataUrl, may have performance impact
     **/
    public boolean preserveDrawingBuffer = false;
    /**
     * whether to use debugging mode for OpenGL calls. Errors will result in a RuntimeException being thrown.
     */
    public boolean useDebugGL = false;

    public boolean usePhysicalPixels;

    public boolean isFixedSizeApplication() {
        return width != 0 && height != 0;
    }

    public boolean isAutoSizeApplication() {
        return width == 0 && height == 0;
    }

    public TeaApplicationConfiguration(String canvasID) {
        this.canvasID = canvasID;
        new TeaTypedArrays();
    }
}