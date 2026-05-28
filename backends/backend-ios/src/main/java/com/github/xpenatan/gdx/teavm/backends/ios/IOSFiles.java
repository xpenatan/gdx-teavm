package com.github.xpenatan.gdx.teavm.backends.ios;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;

public final class IOSFiles implements Files {
    static public final String externalPath = System.getProperty("user.home") + File.separator;
    static public String localPath = normalizePath(new File("").getAbsolutePath());

    public static void setLocalPath(String path) {
        if(path != null && !path.isEmpty()) {
            localPath = normalizePath(path);
        }
    }

    private static String normalizePath(String path) {
        return path.endsWith(File.separator) ? path : path + File.separator;
    }

    @Override
    public FileHandle getFileHandle(String fileName, FileType type) {
        return new IOSFileHandle(fileName, type);
    }

    @Override
    public FileHandle classpath(String path) {
        return new IOSFileHandle(path, FileType.Classpath);
    }

    @Override
    public FileHandle internal(String path) {
        return new IOSFileHandle(path, FileType.Internal);
    }

    @Override
    public FileHandle external(String path) {
        return new IOSFileHandle(path, FileType.External);
    }

    @Override
    public FileHandle absolute(String path) {
        return new IOSFileHandle(path, FileType.Absolute);
    }

    @Override
    public FileHandle local(String path) {
        return new IOSFileHandle(path, FileType.Local);
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
