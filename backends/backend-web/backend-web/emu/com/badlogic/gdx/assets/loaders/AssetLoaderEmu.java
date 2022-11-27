package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.github.xpenatan.gdx.backends.web.emu.Emulate;

@Emulate(AssetLoader.class)
public abstract class AssetLoaderEmu<T, P extends AssetLoaderParameters<T>> {
    private FileHandleResolver resolver;

    public AssetLoaderEmu (FileHandleResolver resolver) {
        this.resolver = resolver;
    }

    public FileHandle resolve (String fileName) {
        return resolver.resolve(fileName);
    }

    public abstract Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, P parameter);
}