package com.github.xpenatan.gdx.backends.teavm.preloader;

/**
 * @author xpenatan
 */
public interface AssetFilter {
    /**
     * @param file        the file to filter
     * @param isDirectory whether the file is a directory
     * @return whether to include the file in the war/ folder or not.
     */
    public boolean accept(String file, boolean isDirectory);

    /**
     * @param file the file to get the type for
     * @return the type of the file, one of {@link AssetType}
     */
    public AssetType getType(String file);

    /**
     * @param file the file to get the bundle name for
     * @return the name of the bundle to which this file should be added
     */
    public String getBundleName(String file);
}
