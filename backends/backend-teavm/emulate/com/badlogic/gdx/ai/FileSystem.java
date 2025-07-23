package com.badlogic.gdx.ai;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;

public interface FileSystem {
    public FileHandleResolver newResolver(Files.FileType fileType);

    public FileHandle newFileHandle(String fileName);

    public FileHandle newFileHandle(File file);

    public FileHandle newFileHandle(String fileName, Files.FileType type);

    public FileHandle newFileHandle(File file, Files.FileType type);
}