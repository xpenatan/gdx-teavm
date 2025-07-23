package com.badlogic.gdx.utils;

import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetInstance;
import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetLoader;

public class SharedLibraryLoader {

    public void load (String libraryName) {
        AssetLoader assetLoader = AssetInstance.getLoaderInstance();
        assetLoader.loadScript(libraryName + ".js", null);
    }
}
