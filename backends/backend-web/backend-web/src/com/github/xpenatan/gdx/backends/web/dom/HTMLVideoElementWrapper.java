package com.github.xpenatan.gdx.backends.web.dom;

/**
 * @author xpenatan
 */
public interface HTMLVideoElementWrapper {
    // HTMLVideoElement
    public int getWidth();

    public void setWidth(int width);

    public int getHeight();

    public void setHeight(int height);

    public int getVideoWidth();

    public int getVideoHeight();

    public String getPoster();

    public void setPoster(String poster);
}
