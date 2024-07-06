package com.github.xpenatan.gdx.backends.teavm.config;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;

public class AssetFileHandle extends FileHandle {

    FileType copyType;

    public static AssetFileHandle createHandle(String fileName, FileType type) {
        return new AssetFileHandle(fileName, type, type);
    }

    public static AssetFileHandle createCopyHandle(File file, FileType type) {
        return new AssetFileHandle(file, FileType.Absolute, type);
    }

    public AssetFileHandle(String fileName) {
        this(new File(fileName), FileType.Absolute, FileType.Internal);
    }

    private AssetFileHandle(String fileName, FileType type, FileType copyType) {
        this(new File(fileName), type, copyType);
    }

    private AssetFileHandle(File file, FileType type, FileType copyType) {
        this.type = type;
        this.copyType = copyType;
        this.file = file;
    }

    public FileHandle child (String name) {
        if (file.getPath().isEmpty()) return new AssetFileHandle(new File(name), super.type(), copyType);
        return new AssetFileHandle(new File(file, name), super.type(), copyType);
    }

    @Override
    public FileType type() {
        return copyType;
    }
}