package com.github.xpenatan.gdx.teavm.backends.shared.config;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;

public class AssetFileHandle extends FileHandle {

    FileType copyType;

    public String assetsChildDir = "";
    public AssetFilter filter;

    public AssetFileHandle(String fileName) {
        this(new File(fileName), FileType.Absolute, FileType.Internal);
    }

    /**
     * Create an asset handle.
     *
     * <ul>
     *   <li>For {@link FileType#Classpath}, {@code pathOrResource} is treated as a
     *       <b>classpath resource path</b> (e.g. {@code "com/kotcrab/vis/ui/skin/x1"}
     *       or the dotted equivalent {@code "com.kotcrab.vis.ui.skin.x1"}). Its
     *       contents are looked up via the build classloader and copied to
     *       {@code assets/<resource-path>/...} so that
     *       {@code Gdx.files.classpath(...)} resolves them at runtime.</li>
     *   <li>For any other {@link FileType}, {@code pathOrResource} is treated as
     *       a path on disk (existing behavior).</li>
     * </ul>
     */
    public AssetFileHandle(String pathOrResource, FileType type) {
        this(buildFile(pathOrResource, type), FileType.Absolute, type);
    }

    public AssetFileHandle(String fileName, FileType type, FileType copyType) {
        this(new File(fileName), type, copyType);
    }

    public AssetFileHandle(File file, FileType type, FileType copyType) {
        this.type = type;
        this.copyType = copyType;
        this.file = file;
    }

    public FileHandle child(String name) {
        if(file.getPath().isEmpty()) return new AssetFileHandle(new File(name), super.type(), copyType);
        return new AssetFileHandle(new File(file, name), super.type(), copyType);
    }

    @Override
    public FileType type() {
        return copyType;
    }

    /** True if this handle resolves through the build classloader instead of disk. */
    public boolean isClasspathResource() {
        return copyType == FileType.Classpath;
    }

    /**
     * Returns the classpath resource path stored in {@link #file()}, normalized to
     * forward slashes and without a leading slash. Only meaningful when
     * {@link #isClasspathResource()} is {@code true}.
     */
    public String getClasspathResource() {
        String s = file.getPath().replace('\\', '/');
        while(s.startsWith("/")) s = s.substring(1);
        return s;
    }

    private static File buildFile(String pathOrResource, FileType type) {
        if(type == FileType.Classpath) {
            return new File(normalizeClasspath(pathOrResource));
        }
        return new File(pathOrResource);
    }

    private static String normalizeClasspath(String pathOrResource) {
        if(pathOrResource == null) throw new IllegalArgumentException("resource path must not be null");
        String s = pathOrResource.replace('\\', '/');
        // Strip leading slash for classloader lookups.
        while(s.startsWith("/")) s = s.substring(1);
        // Ergonomic: if user wrote "com.kotcrab.vis.ui.skin.x1" (package-style, no slashes),
        // convert dots to slashes. We deliberately don't touch paths that already use slashes
        // so that "com/foo/bar.json" keeps its file extension.
        if(!s.contains("/") && s.contains(".")) {
            s = s.replace('.', '/');
        }
        return s;
    }
}