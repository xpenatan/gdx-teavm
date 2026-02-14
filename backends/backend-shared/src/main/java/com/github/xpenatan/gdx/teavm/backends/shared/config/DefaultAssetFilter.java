package com.github.xpenatan.gdx.teavm.backends.shared.config;

/**
 * @author xpenatan
 */
public class DefaultAssetFilter implements AssetFilter {
    @Override
    public boolean accept(String file) {
        if(file.endsWith(".svn")) return false;
        if(file.endsWith(".jar")) return false;
        if(file.endsWith(TeaAssets.ASSETS_FILE_NAME)) return false;
        return true;
    }
}