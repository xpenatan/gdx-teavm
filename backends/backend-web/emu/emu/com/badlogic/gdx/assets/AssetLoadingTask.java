package emu.com.badlogic.gdx.assets;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.EMU_AssetManagerUtils;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.TimeUtils;
import emu.com.badlogic.gdx.utils.async.AsyncExecutor;
import emu.com.badlogic.gdx.utils.async.AsyncResult;
import emu.com.badlogic.gdx.utils.async.AsyncTask;
import com.github.xpenatan.gdx.teavm.backend.web.assetloader.AssetInstance;
import com.github.xpenatan.gdx.teavm.backend.web.assetloader.AssetType;
import com.github.xpenatan.gdx.teavm.backend.web.assetloader.AssetLoader;

public class AssetLoadingTask implements AsyncTask<Void> {
    AssetManager manager;
    final AssetDescriptor assetDesc;
    final com.badlogic.gdx.assets.loaders.AssetLoader loader;
    final AsyncExecutor executor;
    final long startTime;

    volatile boolean asyncDone;
    volatile boolean dependenciesLoaded;
    volatile Array<AssetDescriptor> dependencies;
    volatile AsyncResult<Void> depsFuture;
    volatile AsyncResult<Void> loadFuture;
    volatile Object asset;

    volatile boolean cancel;

    int count = 0;

    public AssetLoadingTask(AssetManager manager, AssetDescriptor assetDesc, com.badlogic.gdx.assets.loaders.AssetLoader loader, AsyncExecutor threadPool) {
        this.manager = manager;
        this.assetDesc = assetDesc;
        this.loader = loader;
        this.executor = threadPool;
        startTime = manager.getLogger().getLevel() == Logger.DEBUG ? TimeUtils.nanoTime() : 0;
    }

    @Override
    public Void call() throws Exception {
        if(cancel) return null;
        AsynchronousAssetLoader asyncLoader = (AsynchronousAssetLoader)loader;
        if(!dependenciesLoaded) {
            FileHandle resolve = resolve(loader, assetDesc);
            dependencies = asyncLoader.getDependencies(assetDesc.fileName, resolve, assetDesc.params);
            if(dependencies != null) {
                removeDuplicates(dependencies);
                EMU_AssetManagerUtils.injectDependencies(manager, assetDesc.fileName, dependencies);
            }
            else {
                // if we have no dependencies, we load the async part of the task immediately.
                asyncLoader.loadAsync(manager, assetDesc.fileName, resolve, assetDesc.params);
                asyncDone = true;
            }
        }
        else {
            FileHandle resolve = resolve(loader, assetDesc);
            asyncLoader.loadAsync(manager, assetDesc.fileName, resolve, assetDesc.params);
            asyncDone = true;
        }
        return null;
    }

    public boolean update() {
        // GTW: check if we have a file that was not preloaded and is not done loading yet
        AssetLoader assetLoader = AssetInstance.getLoaderInstance();
        FileHandle fileHandle = resolve(loader, assetDesc);
        String path = fileHandle.path();
        Files.FileType type = fileHandle.type();
        if(!fileHandle.extension().isEmpty() && !assetLoader.isAssetLoaded(type, path)) {
            // Only try to download if contains extension and is not in queue or downloading.
            if(!assetLoader.isAssetInQueueOrDownloading(path)) {
                count++;
                if(count == 2) {
                    cancel = true;
                }
                else {
                    assetLoader.loadAsset(path, AssetType.Binary, type, null);
                }
            }
        }
        else {
            if(loader instanceof SynchronousAssetLoader)
                handleSyncLoader();
            else
                handleAsyncLoader();
        }
        return asset != null;
    }

    private void handleSyncLoader() {
        SynchronousAssetLoader syncLoader = (SynchronousAssetLoader)loader;
        if(!dependenciesLoaded) {
            dependenciesLoaded = true;
            dependencies = syncLoader.getDependencies(assetDesc.fileName, resolve(loader, assetDesc), assetDesc.params);
            if(dependencies == null) {
                asset = syncLoader.load(manager, assetDesc.fileName, resolve(loader, assetDesc), assetDesc.params);
                return;
            }
            removeDuplicates(dependencies);
            EMU_AssetManagerUtils.injectDependencies(manager, assetDesc.fileName, dependencies);
        }
        else
            asset = syncLoader.load(manager, assetDesc.fileName, resolve(loader, assetDesc), assetDesc.params);
    }

    private void handleAsyncLoader() {
        AsynchronousAssetLoader asyncLoader = (AsynchronousAssetLoader)loader;
        if(!dependenciesLoaded) {
            if(depsFuture == null)
                depsFuture = executor.submit(this);
            else if(depsFuture.isDone()) {
                try {
                    depsFuture.get();
                } catch(Exception e) {
                    throw new GdxRuntimeException("Couldn't load dependencies of asset: " + assetDesc.fileName, e);
                }
                dependenciesLoaded = true;
                if(asyncDone)
                    asset = asyncLoader.loadSync(manager, assetDesc.fileName, resolve(loader, assetDesc), assetDesc.params);
            }
        }
        else if(loadFuture == null && !asyncDone)
            loadFuture = executor.submit(this);
        else if(asyncDone)
            asset = asyncLoader.loadSync(manager, assetDesc.fileName, resolve(loader, assetDesc), assetDesc.params);
        else if(loadFuture.isDone()) {
            try {
                loadFuture.get();
            } catch(Exception e) {
                throw new GdxRuntimeException("Couldn't load asset: " + assetDesc.fileName, e);
            }
            asset = asyncLoader.loadSync(manager, assetDesc.fileName, resolve(loader, assetDesc), assetDesc.params);
        }
    }

    public void unload() {
        if(loader instanceof AsynchronousAssetLoader)
            ((AsynchronousAssetLoader)loader).unloadAsync(manager, assetDesc.fileName, resolve(loader, assetDesc), assetDesc.params);
    }

    private FileHandle resolve(com.badlogic.gdx.assets.loaders.AssetLoader loader, AssetDescriptor assetDesc) {
        if(assetDesc.file == null) assetDesc.file = loader.resolve(assetDesc.fileName);
        return assetDesc.file;
    }

    private void removeDuplicates(Array<AssetDescriptor> array) {
        boolean ordered = array.ordered;
        array.ordered = true;
        for(int i = 0; i < array.size; ++i) {
            final String fn = array.get(i).fileName;
            final Class type = array.get(i).type;
            for(int j = array.size - 1; j > i; --j)
                if(type == array.get(j).type && fn.equals(array.get(j).fileName)) array.removeIndex(j);
        }
        array.ordered = ordered;
    }
}
