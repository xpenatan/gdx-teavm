package com.badlogic.gdx.assets;

import com.badlogic.gdx.utils.Array;

public class EMU_AssetManagerUtils {

    public static synchronized void injectDependencies(AssetManager manager, String parentAssetFilename, Array<AssetDescriptor> dependendAssetDescs) {
        manager.injectDependencies(parentAssetFilename, dependendAssetDescs);
    }
}