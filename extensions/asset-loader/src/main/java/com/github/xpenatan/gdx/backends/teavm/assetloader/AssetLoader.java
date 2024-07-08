package com.github.xpenatan.gdx.backends.teavm.assetloader;

import com.badlogic.gdx.Files.FileType;

/**
 * @author xpenatan
 */
public class AssetLoader {

    private static AssetLoad instance;

    private AssetLoader() {
    }

    public static AssetLoad getInstance() {
        return AssetLoader.instance;
    }

    public static void setInstance(AssetLoad instance) {
        AssetLoader.instance = instance;
    }

    public interface AssetLoad {
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
}