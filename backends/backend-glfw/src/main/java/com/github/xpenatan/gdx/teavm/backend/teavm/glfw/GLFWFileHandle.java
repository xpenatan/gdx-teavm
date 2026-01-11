package com.github.xpenatan.gdx.teavm.backend.teavm.glfw;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.File;

/**
 * @author mzechner
 * @author Nathan Sweet
 */
public final class GLFWFileHandle extends FileHandle {
    public GLFWFileHandle(String fileName, FileType type) {
        super(fileName, type);
    }

    public GLFWFileHandle(File file, FileType type) {
        super(file, type);
    }

    public FileHandle child(String name) {
        if (file.getPath().isEmpty()) return new GLFWFileHandle(new File(name), type);
        return new GLFWFileHandle(new File(file, name), type);
    }

    public FileHandle sibling(String name) {
        if (file.getPath().isEmpty()) throw new GdxRuntimeException("Cannot get the sibling of the root.");
        return new GLFWFileHandle(new File(file.getParent(), name), type);
    }

    public FileHandle parent() {
        File parent = file.getParentFile();
        if (parent == null) {
            if (type == FileType.Absolute)
                parent = new File("/");
            else
                parent = new File("");
        }
        return new GLFWFileHandle(parent, type);
    }

    public File file() {
        if (type == FileType.External) return new File(GLFWFiles.externalPath, file.getPath());
        if (type == FileType.Local) return new File(GLFWFiles.localPath, file.getPath());
        return file;
    }
}
