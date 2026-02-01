package com.github.xpenatan.gdx.teavm.backends.psp;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.File;

/**
 * @author mzechner
 * @author Nathan Sweet
 */
public final class PSPFileHandle extends FileHandle {
    public PSPFileHandle(String fileName, FileType type) {
        super(fileName, type);
    }

    public PSPFileHandle(File file, FileType type) {
        super(file, type);
    }

    public FileHandle child(String name) {
        if (file.getPath().isEmpty()) return new PSPFileHandle(new File(name), type);
        return new PSPFileHandle(new File(file, name), type);
    }

    public FileHandle sibling(String name) {
        if (file.getPath().isEmpty()) throw new GdxRuntimeException("Cannot get the sibling of the root.");
        return new PSPFileHandle(new File(file.getParent(), name), type);
    }

    public FileHandle parent() {
        File parent = file.getParentFile();
        if (parent == null) {
            if (type == FileType.Absolute)
                parent = new File("/");
            else
                parent = new File("");
        }
        return new PSPFileHandle(parent, type);
    }

    public File file() {
        if (type == FileType.External) return new File(PSPFiles.externalPath, file.getPath());
        if (type == FileType.Local) return new File(PSPFiles.localPath, file.getPath());
        return file;
    }
}
