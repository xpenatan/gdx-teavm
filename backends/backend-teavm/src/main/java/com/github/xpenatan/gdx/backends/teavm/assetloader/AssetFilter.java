package com.github.xpenatan.gdx.backends.teavm.assetloader;

/**
 * @author xpenatan
 */
public interface AssetFilter {
    /**
     * @param file        the file to filter
     * @param isDirectory whether the file is a directory
     * @return whether to include the asset or resources in the assets folder or not.
     */
    boolean accept(String file, boolean isDirectory);
}