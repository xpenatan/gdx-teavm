package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.backends.teavm.filesystem.FileDB;
import com.github.xpenatan.gdx.backends.teavm.filesystem.types.InternalDBStorage;
import com.github.xpenatan.gdx.backends.teavm.filesystem.types.LocalDBStorage;
import com.github.xpenatan.gdx.backends.teavm.preloader.Preloader;

/**
 * @author xpenatan
 */
public class TeaFiles implements Files {

    final Preloader preloader;

    public InternalDBStorage internalStorage;
    public LocalDBStorage localStorage;

    public TeaFiles(TeaApplicationConfiguration config, TeaApplication teaApplication, Preloader preloader) {
        this.preloader = preloader;
        if(config.useNewFileHandle) {
            this.internalStorage = new InternalDBStorage();
            this.localStorage = new LocalDBStorage(teaApplication);
        }
    }

    @Override
    public FileHandle getFileHandle(String path, FileType type) {
        FileDB fileDB = null;
        if(type == FileType.Internal) {
            return internal(path);
        }
        else if(type == FileType.Classpath) {
            return classpath(path);
        }
        else if(type == FileType.Local) {
            return local(path);
        }
        return new TeaFileHandle(preloader, null, path, type);
    }

    @Override
    public FileHandle classpath(String path) {
        return new TeaFileHandle(preloader, internalStorage, path, FileType.Classpath);
    }

    @Override
    public FileHandle internal(String path) {
        return new TeaFileHandle(preloader, internalStorage, path, FileType.Internal);
    }

    @Override
    public FileHandle external(String path) {
        return new TeaFileHandle(preloader, null, path, FileType.External);
    }

    @Override
    public FileHandle absolute(String path) {
        return new TeaFileHandle(preloader, null, path, FileType.Absolute);
    }

    @Override
    public FileHandle local(String path) {
        return new TeaFileHandle(preloader, localStorage, path, FileType.Local);
    }

    @Override
    public String getExternalStoragePath() {
        return null;
    }

    @Override
    public boolean isExternalStorageAvailable() {
        return false;
    }

    @Override
    public String getLocalStoragePath() {
        return FileDB.getInstance().getPath();
    }

    @Override
    public boolean isLocalStorageAvailable() {
        return true;
    }
}
