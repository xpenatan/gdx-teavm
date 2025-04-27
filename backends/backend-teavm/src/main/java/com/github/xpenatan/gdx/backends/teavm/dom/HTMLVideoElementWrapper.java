package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSObject;

/**
 * @author xpenatan
 */
public interface HTMLVideoElementWrapper extends JSObject {
    // HTMLVideoElement
    int getWidth();
    void setWidth(int width);
    int getHeight();
    void setHeight(int height);
    int getVideoWidth();
    int getVideoHeight();
    String getPoster();
    void setPoster(String poster);
}