package com.github.xpenatan.gdx.backends.teavm.preloader;

/**
 * @author xpenatan
 */
public class DefaultAssetFilter implements AssetFilter {
    @Override
    public boolean accept(String file, boolean isDirectory) {
        if(isDirectory && file.endsWith(".svn")) return false;
        if(file.endsWith(".jar")) return false;
        return true;
    }
}