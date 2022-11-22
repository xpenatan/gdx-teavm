package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.backends.web.emu.Emulate;

@Emulate(AsynchronousAssetLoader.class)
public abstract class AsynchronousAssetLoaderEmu<T, P extends AssetLoaderParameters<T>> extends AssetLoader<T, P> {

    public AsynchronousAssetLoaderEmu (FileHandleResolver resolver) {
        super(resolver);
    }

    public abstract void loadAsync (AssetManager manager, String fileName, FileHandle file, P parameter);

    public void unloadAsync (AssetManager manager, String fileName, FileHandle file, P parameter) {
    }

    public abstract T loadSync (AssetManager manager, String fileName, FileHandle file, P parameter);
}