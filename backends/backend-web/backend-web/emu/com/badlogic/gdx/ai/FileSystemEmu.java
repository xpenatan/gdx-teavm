package com.badlogic.gdx.ai;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.backends.web.emu.Emulate;
import java.io.File;

@Emulate(valueStr = "com.badlogic.gdx.ai.FileSystem")
public interface FileSystemEmu {
    public FileHandleResolver newResolver(Files.FileType fileType);

    public FileHandle newFileHandle(String fileName);

    public FileHandle newFileHandle(File file);

    public FileHandle newFileHandle(String fileName, Files.FileType type);

    public FileHandle newFileHandle(File file, Files.FileType type);
}