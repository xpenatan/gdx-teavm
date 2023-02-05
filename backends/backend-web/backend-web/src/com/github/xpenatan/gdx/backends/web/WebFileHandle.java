package com.github.xpenatan.gdx.backends.web;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.github.xpenatan.gdx.backends.web.preloader.Preloader;

import java.io.*;

/**
 * @author xpenatan
 */
public class WebFileHandle extends FileHandle {
    public final Preloader preloader;
    private final String file;
    private final FileType type;

    public WebFileHandle(Preloader preloader, String fileName, FileType type) {
        if ((type != FileType.Internal) && (type != FileType.Classpath) && (type != FileType.Local)) {
          throw new GdxRuntimeException("FileType '" + type + "' Not supported in web backend");
        }
        this.preloader = preloader;
        this.file = fixSlashes(fileName);
        this.type = type;
    }

    public WebFileHandle(String path) {
        this.type = FileType.Internal;
        this.preloader = ((WebApplication)Gdx.app).getPreloader();
        this.file = fixSlashes(path);
    }

    public String path() {
        return file;
    }

    public String name() {
        int index = file.lastIndexOf('/');
        if(index < 0) return file;
        return file.substring(index + 1);
    }

    public String extension() {
        String name = name();
        int dotIndex = name.lastIndexOf('.');
        if(dotIndex == -1) return "";
        return name.substring(dotIndex + 1);
    }

    public String nameWithoutExtension() {
        String name = name();
        int dotIndex = name.lastIndexOf('.');
        if(dotIndex == -1) return name;
        return name.substring(0, dotIndex);
    }

    /**
     * @return the path and filename without the extension, e.g. dir/dir2/file.png -> dir/dir2/file
     */
    public String pathWithoutExtension() {
        String path = file;
        int dotIndex = path.lastIndexOf('.');
        if(dotIndex == -1) return path;
        return path.substring(0, dotIndex);
    }

    public FileType type() {
        return type;
    }

    /**
     * Returns a java.io.File that represents this file handle. Note the returned file will only be usable for
     * {@link FileType#Absolute} and {@link FileType#External} file handles.
     */
    public File file() {
        throw new GdxRuntimeException("Not supported in web backend");
    }

    /**
     * Returns a stream for reading this file as bytes.
     *
     * @throws GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    public InputStream read() {
        if (type == FileType.Local) {
            return WebFileHandleDB.getInstance().read(this);
        }
        else {
            InputStream in = preloader.read(file);
            if (in == null) throw new GdxRuntimeException(file + " does not exist");
            return in;
        }
    }

    /**
     * Reads the entire file into a string using the specified charset.
     *
     * @throws GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    public String readString(String charset) {
        if (type == FileType.Local) {
            // obtain via reader
            return super.readString(charset);
        }
        else {
            // obtain text directly from preloader
            if(preloader.isText(file)) {
              return preloader.texts.get(file);
            }
            else {
              return super.readString(charset);
            }
        }
    }

    /**
     * Returns a stream for writing to this file. Parent directories will be created if necessary.
     *
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws GdxRuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *                             {@link FileType#Internal} file, or if it could not be written.
     */
    public OutputStream write(boolean append) {
        if (type == FileType.Local) {
            return WebFileHandleDB.getInstance().write(this, append);
        }
        else {
            throw new GdxRuntimeException("Cannot write to the given file.");
        }
    }

    /**
     * Returns a writer for writing to this file. Parent directories will be created if necessary.
     *
     * @param append  If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @param charset May be null to use the default charset.
     * @throws GdxRuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *                             {@link FileType#Internal} file, or if it could not be written.
     */
    public Writer writer(boolean append, String charset) {
        try {
            return new BufferedWriter(new OutputStreamWriter(write(append), "UTF-8"));
        }
        catch (Exception e) {
            throw new GdxRuntimeException("Error obtaining writer.", e);
        }
    }

    /**
     * Returns the paths to the children of this directory. Returns an empty list if this file handle represents a file and not a
     * directory. On the desktop, an {@link FileType#Internal} handle to a directory on the classpath will return a zero length
     * array.
     *
     * @throws GdxRuntimeException if this file is an {@link FileType#Classpath} file.
     */
    public FileHandle[] list() {
      if (type == FileType.Local) {
          return WebFileHandleDB.getInstance().list(this);
      }
      else {
          return preloader.list(file);
      }
    }

    /**
     * Returns the paths to the children of this directory that satisfy the specified filter. Returns an empty list if this file
     * handle represents a file and not a directory. On the desktop, an {@link FileType#Internal} handle to a directory on the
     * classpath will return a zero length array.
     *
     * @throws GdxRuntimeException if this file is an {@link FileType#Classpath} file.
     */
    public FileHandle[] list(FileFilter filter) {
        if (type == FileType.Local) {
            return WebFileHandleDB.getInstance().list(this, filter);
        }
        else {
            return preloader.list(file, filter);
        }
    }

    /**
     * Returns the paths to the children of this directory that satisfy the specified filter. Returns an empty list if this file
     * handle represents a file and not a directory. On the desktop, an {@link FileType#Internal} handle to a directory on the
     * classpath will return a zero length array.
     *
     * @throws GdxRuntimeException if this file is an {@link FileType#Classpath} file.
     */
    public FileHandle[] list(FilenameFilter filter) {
        if (type == FileType.Local) {
            return WebFileHandleDB.getInstance().list(this, filter);
        }
        else {
            return preloader.list(file, filter);
        }
    }

    /**
     * Returns the paths to the children of this directory with the specified suffix. Returns an empty list if this file handle
     * represents a file and not a directory. On the desktop, an {@link FileType#Internal} handle to a directory on the classpath
     * will return a zero length array.
     *
     * @throws GdxRuntimeException if this file is an {@link FileType#Classpath} file.
     */
    public FileHandle[] list(String suffix) {
        if (type == FileType.Local) {
            return WebFileHandleDB.getInstance().list(this, suffix);
        }
        else {
            return preloader.list(file, suffix);
        }
    }

    /**
     * Returns true if this file is a directory. Always returns false for classpath files. On Android, an
     * {@link FileType#Internal} handle to an empty directory will return false. On the desktop, an {@link FileType#Internal}
     * handle to a directory on the classpath will return false.
     */
    public boolean isDirectory() {
        if (type == FileType.Local) {
            return WebFileHandleDB.getInstance().isDirectory(this);
        }
        else {
            return preloader.isDirectory(file);
        }
    }

    /**
     * Returns a handle to the child with the specified name.
     *
     * @throws GdxRuntimeException if this file handle is a {@link FileType#Classpath} or {@link FileType#Internal} and the child
     *                             doesn't exist.
     */
    public FileHandle child(String name) {
        return new WebFileHandle(preloader, (file.isEmpty() ? "" : (file + (file.endsWith("/") ? "" : "/"))) + name,
                FileType.Internal);
    }

    public FileHandle parent() {
        int index = file.lastIndexOf("/");
        String dir = "";
        if(index > 0) dir = file.substring(0, index);
        return new WebFileHandle(preloader, dir, type);
    }

    public FileHandle sibling(String name) {
        return parent().child(fixSlashes(name));
    }

    /**
     * @throws GdxRuntimeException if this file handle is a {@link FileType#Classpath} or {@link FileType#Internal} file.
     */
    public void mkdirs() {
        if (type == FileType.Local) {
            WebFileHandleDB.getInstance().mkdirs(this);
        }
        else {
            throw new GdxRuntimeException("Cannot mkdirs for non-local file: " + file);
        }
    }

    /**
     * Returns true if the file exists. On Android, a {@link FileType#Classpath} or {@link FileType#Internal} handle to a
     * directory will always return false.
     */
    public boolean exists() {
        if (type == FileType.Local) {
            return WebFileHandleDB.getInstance().exists(this);
        }
        else {
            return preloader.contains(file);
        }
    }

    /**
     * Deletes this file or empty directory and returns success. Will not delete a directory that has children.
     *
     * @throws GdxRuntimeException if this file handle is a {@link FileType#Classpath} or {@link FileType#Internal} file.
     */
    public boolean delete() {
        if (type == FileType.Local) {
            return WebFileHandleDB.getInstance().delete(this);
        }
        else {
            throw new GdxRuntimeException("Cannot delete a non-local file: " + file);
        }
    }

    /**
     * Deletes this file or directory and all children, recursively.
     *
     * @throws GdxRuntimeException if this file handle is a {@link FileType#Classpath} or {@link FileType#Internal} file.
     */
    public boolean deleteDirectory() {
        throw new GdxRuntimeException("Cannot delete directory (missing implementation): " + file);
    }

    /**
     * Returns the length in bytes of this file, or 0 if this file is a directory, does not exist, or the size cannot otherwise be
     * determined.
     */
    public long length() {
        if (type == FileType.Local) {
            return WebFileHandleDB.getInstance().length(this);
        }
        else {
            return preloader.length(file);
        }
    }

    /**
     * Returns the last modified time in milliseconds for this file. Zero is returned if the file doesn't exist. Zero is returned
     * for {@link FileType#Classpath} files. On Android, zero is returned for {@link FileType#Internal} files. On the desktop, zero
     * is returned for {@link FileType#Internal} files on the classpath.
     */
    public long lastModified() {
        return 0;
    }

    public String toString() {
        return file;
    }

    private static String fixSlashes(String path) {
        path = path.replace("\\", "/");
        if(path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
