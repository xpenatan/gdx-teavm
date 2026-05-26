package com.github.xpenatan.gdx.teavm.backends.android;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.File;

public final class AndroidFileHandle extends FileHandle {
    public AndroidFileHandle(String fileName, FileType type) {
        super(fileName, type);
    }

    public AndroidFileHandle(File file, FileType type) {
        super(file, type);
    }

    @Override
    public FileHandle child(String name) {
        if(file.getPath().isEmpty()) {
            return new AndroidFileHandle(new File(name), type);
        }
        return new AndroidFileHandle(new File(file, name), type);
    }

    @Override
    public FileHandle sibling(String name) {
        if(file.getPath().isEmpty()) {
            throw new GdxRuntimeException("Cannot get the sibling of the root.");
        }
        return new AndroidFileHandle(new File(file.getParent(), name), type);
    }

    @Override
    public FileHandle parent() {
        File parent = file.getParentFile();
        if(parent == null) {
            parent = type == FileType.Absolute ? new File("/") : new File("");
        }
        return new AndroidFileHandle(parent, type);
    }

    @Override
    public File file() {
        if(type == FileType.External) {
            return new File(AndroidFiles.externalPath, file.getPath());
        }
        if(type == FileType.Local) {
            return new File(AndroidFiles.localPath, file.getPath());
        }
        return file;
    }
}
