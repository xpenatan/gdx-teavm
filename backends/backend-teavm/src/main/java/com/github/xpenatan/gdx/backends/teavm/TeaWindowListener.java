package com.github.xpenatan.gdx.backends.teavm;

public interface TeaWindowListener {
    /**
     * Called when external files are dropped into the html canvas, e.g from the Desktop.
     *
     * @param files array with absolute paths to the files
     */
    void filesDropped(String[] files);
}