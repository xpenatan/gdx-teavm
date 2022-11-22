package emucom.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.utils.Array;
import emucom.badlogic.gdx.files.FileHandle;

public abstract class AssetLoader<T, P extends AssetLoaderParameters<T>> {
    private FileHandleResolver resolver;

    public AssetLoader (FileHandleResolver resolver) {
        this.resolver = resolver;
    }

    public FileHandle resolve (String fileName) {
        return resolver.resolve(fileName);
    }

    public abstract Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, P parameter);
}