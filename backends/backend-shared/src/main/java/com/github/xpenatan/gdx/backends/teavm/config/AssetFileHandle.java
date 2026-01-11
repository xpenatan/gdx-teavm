package com.github.xpenatan.gdx.backends.teavm.config;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;

public class AssetFileHandle extends FileHandle {

    FileType copyType;

    public String assetsChildDir = "";
    public AssetFilter filter;

    public static AssetFileHandle createHandle(String fileName, FileType type) {
        return new AssetFileHandle(fileName, FileType.Absolute, type);
    }

    public static AssetFileHandle createHandle(File file, FileType type) {
        return new AssetFileHandle(file, FileType.Absolute, type);
    }

    public static AssetFileHandle createCopyHandle(String fileName, FileType type, String assetsChildDir) {
        return createCopyHandle(fileName, type, assetsChildDir, null);
    }

    public static AssetFileHandle createCopyHandle(String fileName, FileType type, String assetsChildDir, AssetFilter filter) {
        AssetFileHandle assetFileHandle = new AssetFileHandle(fileName, FileType.Absolute, type);
        assetFileHandle.assetsChildDir = assetsChildDir;
        assetFileHandle.filter = filter;
        return assetFileHandle;
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