package com.github.xpenatan.gdx.teavm.backends.android;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;

public final class AndroidFiles implements Files {
    static public final String externalPath = System.getProperty("user.home") + File.separator;
    static public final String localPath = new File("").getAbsolutePath() + File.separator;

    @Override
    public FileHandle getFileHandle(String fileName, FileType type) {
        return new AndroidFileHandle(fileName, type);
    }

    @Override
    public FileHandle classpath(String path) {
        return new AndroidFileHandle(path, FileType.Classpath);
    }

    @Override
    public FileHandle internal(String path) {
        return new AndroidFileHandle(path, FileType.Internal);
    }

    @Override
    public FileHandle external(String path) {
        return new AndroidFileHandle(path, FileType.External);
    }

    @Override
    public FileHandle absolute(String path) {
        return new AndroidFileHandle(path, FileType.Absolute);
    }

    @Override
    public FileHandle local(String path) {
        return new AndroidFileHandle(path, FileType.Local);
    }

    @Override
    public String getExternalStoragePath() {
        return externalPath;
    }

    @Override
    public boolean isExternalStorageAvailable() {
        return true;
    }

    @Override
    public String getLocalStoragePath() {
        return localPath;
    }

    @Override
    public boolean isLocalStorageAvailable() {
        return true;
    }
}
