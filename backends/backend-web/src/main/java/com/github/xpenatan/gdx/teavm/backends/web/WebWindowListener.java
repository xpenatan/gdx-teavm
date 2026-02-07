package com.github.xpenatan.gdx.teavm.backends.web;

import com.github.xpenatan.gdx.teavm.backends.web.filesystem.FileData;

public interface WebWindowListener {
    /**
     * Called when external files are dropped into the html canvas, e.g from the Desktop.
     *
     * @param files array with absolute paths to the files.
     */
    default void filesDropped(FileData[] files) {}

    /**
     * Called to accept or not external files are dropped into the html canvas, e.g from the Desktop.
     */
    default boolean acceptFileDropped(String file) { return true; }
}