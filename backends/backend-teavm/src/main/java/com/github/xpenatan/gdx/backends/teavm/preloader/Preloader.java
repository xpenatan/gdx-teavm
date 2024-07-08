package com.github.xpenatan.gdx.backends.teavm.preloader;

import com.badlogic.gdx.Files.FileType;
import com.github.xpenatan.gdx.backends.teavm.AssetLoaderListener;

/**
 * @author xpenatan
 */
public class Preloader {

    private static Preload instance;

    private Preloader() {
    }

    public static Preload getInstance() {
        return Preloader.instance;
    }

    public static void setInstance(Preload instance) {
        Preloader.instance = instance;
    }

    public interface Preload {
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