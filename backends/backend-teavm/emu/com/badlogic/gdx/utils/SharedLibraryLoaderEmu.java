package com.badlogic.gdx.utils;

import com.badlogic.gdx.Gdx;
import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetInstance;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;
import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetLoader;

@Emulate(SharedLibraryLoader.class)
public class SharedLibraryLoaderEmu {

    public void load (String libraryName) {
        TeaApplication app = (TeaApplication)Gdx.app;
        AssetLoader assetLoader = AssetInstance.getLoaderInstance();
        assetLoader.loadScript(libraryName + ".js", null);
    }
}
