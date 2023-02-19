package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.backends.teavm.preloader.Preloader;

/**
 * @author xpenatan
 */
public class TeaFiles implements Files {

    final Preloader preloader;

    public TeaFiles(Preloader preloader) {
        this.preloader = preloader;
    }

    @Override
    public FileHandle getFileHandle(String path, FileType type) {
        return new TeaFileHandle(preloader, path, type);
    }

    @Override
    public FileHandle classpath(String path) {
        return new TeaFileHandle(preloader, path, FileType.Classpath);
    }

    @Override
    public FileHandle internal(String path) {
        return new TeaFileHandle(preloader, path, FileType.Internal);
    }

    @Override
    public FileHandle external(String path) {
        return new TeaFileHandle(preloader, path, FileType.External);
    }

    @Override
    public FileHandle absolute(String path) {
        return new TeaFileHandle(preloader, path, FileType.Absolute);
    }

    @Override
    public FileHandle local(String path) {
        return new TeaFileHandle(preloader, path, FileType.Local);
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
        return null;
    }

    @Override
    public boolean isLocalStorageAvailable() {
        return true;
    }
}
