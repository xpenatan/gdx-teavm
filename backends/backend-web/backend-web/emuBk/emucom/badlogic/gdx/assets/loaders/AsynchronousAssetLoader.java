package emucom.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import emucom.badlogic.gdx.files.FileHandle;

public abstract class AsynchronousAssetLoader<T, P extends AssetLoaderParameters<T>> extends AssetLoader<T, P> {

    public AsynchronousAssetLoader (FileHandleResolver resolver) {
        super(resolver);
    }

    public abstract void loadAsync (AssetManager manager, String fileName, FileHandle file, P parameter);

    public void unloadAsync (AssetManager manager, String fileName, FileHandle file, P parameter) {
    }

    public abstract T loadSync (AssetManager manager, String fileName, FileHandle file, P parameter);
}