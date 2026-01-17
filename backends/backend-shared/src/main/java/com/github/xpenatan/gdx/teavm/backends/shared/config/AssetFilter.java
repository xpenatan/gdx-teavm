package com.github.xpenatan.gdx.teavm.backends.shared.config;

/**
 * @author xpenatan
 */
public interface AssetFilter {
    /**
     * @param file        the file to filter
     * @return whether to include the asset or resources in the assets folder or not.
     */
    boolean accept(String file);
}