package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.teavm.filesystem.FileDB;
import com.github.xpenatan.gdx.backends.teavm.filesystem.MemoryFileStorage;
import com.github.xpenatan.gdx.backends.teavm.filesystem.types.ClasspathStorage;
import com.github.xpenatan.gdx.backends.teavm.filesystem.types.InternalStorage;
import com.github.xpenatan.gdx.backends.teavm.filesystem.types.LocalDBStorage;

/**
 * @author xpenatan
 */
public class TeaFiles implements Files {

    public MemoryFileStorage internalStorage;
    public MemoryFileStorage classpathStorage;
    public MemoryFileStorage localStorage;
    public String storagePath;

    public TeaFiles(TeaApplicationConfiguration config, TeaApplication teaApplication) {
        this.internalStorage = new InternalStorage();
        this.classpathStorage = new ClasspathStorage();
        this.localStorage = new LocalDBStorage(teaApplication);
        storagePath = config.storagePrefix;
    }

    public FileDB getFileDB(FileType type) {
        if(type == FileType.Internal) {
            return internalStorage;
        }
        else if(type == FileType.Classpath) {
            return classpathStorage;
        }
        else if(type == FileType.Local) {
            return localStorage;
        }
        return null;
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
        throw new GdxRuntimeException("Type " + type + " is not supported");
    }

    @Override
    public FileHandle classpath(String path) {
        return new TeaFileHandle(this, path, FileType.Classpath);
    }

    @Override
    public FileHandle internal(String path) {
        return new TeaFileHandle(this, path, FileType.Internal);
    }

    @Override
    public FileHandle local(String path) {
        return new TeaFileHandle(this, path, FileType.Local);
    }

    @Override
    public FileHandle external(String path) {
        throw new GdxRuntimeException("Type external is not supported");
    }

    @Override
    public FileHandle absolute(String path) {
        throw new GdxRuntimeException("Type absolute is not supported");
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
        return storagePath;
    }

    @Override
    public boolean isLocalStorageAvailable() {
        return true;
    }
}
