package com.github.xpenatan.gdx.teavm.backend.teavm.glfw;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;

/**
 * @author mzechner
 * @author Nathan Sweet
 */
public final class GLFWFiles implements Files {
    static public final String externalPath = System.getProperty("user.home") + File.separator;
    static public final String localPath = new File("").getAbsolutePath() + File.separator;

    @Override
    public FileHandle getFileHandle(String fileName, FileType type) {
        return new GLFWFileHandle(fileName, type);
    }

    @Override
    public FileHandle classpath(String path) {
        return new GLFWFileHandle(path, FileType.Classpath);
    }

    @Override
    public FileHandle internal(String path) {
        return new GLFWFileHandle(path, FileType.Internal);
    }

    @Override
    public FileHandle external(String path) {
        return new GLFWFileHandle(path, FileType.External);
    }

    @Override
    public FileHandle absolute(String path) {
        return new GLFWFileHandle(path, FileType.Absolute);
    }

    @Override
    public FileHandle local(String path) {
        return new GLFWFileHandle(path, FileType.Local);
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