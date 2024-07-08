package com.github.xpenatan.gdx.backends.teavm.preloader;

import com.badlogic.gdx.Files.FileType;
import com.github.xpenatan.gdx.backends.teavm.AssetLoaderListener;

/**
 * @author xpenatan
 */
public interface Preloader {

    String getAssetUrl();

    String getScriptUrl();

    boolean isAssetInQueue(String path);

    boolean isAssetLoaded(FileType fileType, String path);

    /**
     * Load asset and add to FileHandle system
     */
    void loadAsset(boolean async, String path, AssetType assetType, FileType fileType, AssetLoaderListener<Blob> listener);

    /**
     * Load script and attach to html document
     */
    void loadScript(boolean async, String path, AssetLoaderListener<String> listener);

    int getQueue();
}