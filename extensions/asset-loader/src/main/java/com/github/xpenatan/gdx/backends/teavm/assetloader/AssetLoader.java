package com.github.xpenatan.gdx.backends.teavm.assetloader;

import com.badlogic.gdx.Files.FileType;

/**
 * AssetLoader is a wrapper on AssetDownloader that adds additional functionalities
 * like "[website]/assets" as default asset path.
 *
 * @author xpenatan
 */
public interface AssetLoader {

    String getAssetUrl();

    String getScriptUrl();

    boolean isAssetInQueue(String path);

    boolean isAssetLoaded(FileType fileType, String path);

    /**
     * Load asset and add to FileHandle system. Skip downloading if file already exists
     */
    void loadAsset(String path, AssetType assetType, FileType fileType);

    /**
     * Load asset and add to FileHandle system. Skip downloading if file already exists
     */
    void loadAsset(String path, AssetType assetType, FileType fileType, AssetLoaderListener<Blob> listener);

    /**
     * Load asset and add to FileHandle system. Overwrite true will update the file.
     */
    void loadAsset(String path, AssetType assetType, FileType fileType, AssetLoaderListener<Blob> listener, boolean overwrite);

    /**
     * Load script and attach to html document
     */
    void loadScript(String path);

    /**
     * Load script and attach to html document
     */
    void loadScript(String path, AssetLoaderListener<String> listener);

    int getQueue();
}