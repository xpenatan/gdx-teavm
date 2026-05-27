package com.github.xpenatan.gdx.teavm.backends.ios;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.File;

public final class IOSFileHandle extends FileHandle {
    public IOSFileHandle(String fileName, FileType type) {
        super(fileName, type);
    }

    public IOSFileHandle(File file, FileType type) {
        super(file, type);
    }

    @Override
    public FileHandle child(String name) {
        if(file.getPath().isEmpty()) {
            return new IOSFileHandle(new File(name), type);
        }
        return new IOSFileHandle(new File(file, name), type);
    }

    @Override
    public FileHandle sibling(String name) {
        if(file.getPath().isEmpty()) {
            throw new GdxRuntimeException("Cannot get the sibling of the root.");
        }
        return new IOSFileHandle(new File(file.getParent(), name), type);
    }

    @Override
    public FileHandle parent() {
        File parent = file.getParentFile();
        if(parent == null) {
            parent = type == FileType.Absolute ? new File("/") : new File("");
        }
        return new IOSFileHandle(parent, type);
    }

    @Override
    public File file() {
        if(type == FileType.External) {
            return new File(IOSFiles.externalPath, file.getPath());
        }
        if(type == FileType.Local) {
            return new File(IOSFiles.localPath, file.getPath());
        }
        return file;
    }
}
