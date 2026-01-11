package emu.com.badlogic.gdx.utils;

import com.github.xpenatan.gdx.teavm.backend.web.assetloader.AssetInstance;
import com.github.xpenatan.gdx.teavm.backend.web.assetloader.AssetLoader;

public class SharedLibraryLoader {

    public void load (String libraryName) {
        AssetLoader assetLoader = AssetInstance.getLoaderInstance();
        assetLoader.loadScript(libraryName + ".js", null);
    }
}
