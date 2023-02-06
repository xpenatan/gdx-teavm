package com.github.xpenatan.gdx.backends.web;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.web.filesystem.FileDB;
import com.github.xpenatan.gdx.backends.web.preloader.Preloader;

import java.io.*;

/**
 * @author xpenatan
 */
public class WebFileHandle extends FileHandle {
    public final Preloader preloader;
    private final String file;

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

    @Override
    public String path() {
        return file;
    }

    @Override
    public String name() {
        int index = file.lastIndexOf('/');
        if(index < 0) return file;
        return file.substring(index + 1);
    }

    @Override
    public String extension() {
        String name = name();
        int dotIndex = name.lastIndexOf('.');
        if(dotIndex == -1) return "";
        return name.substring(dotIndex + 1);
    }

    @Override
    public String nameWithoutExtension() {
        String name = name();
        int dotIndex = name.lastIndexOf('.');
        if(dotIndex == -1) return name;
        return name.substring(0, dotIndex);
    }

    @Override
    public String pathWithoutExtension() {
        String path = file;
        int dotIndex = path.lastIndexOf('.');
        if(dotIndex == -1) return path;
        return path.substring(0, dotIndex);
    }

    @Override
    public File file() {
        throw new GdxRuntimeException("Not supported in web backend");
    }

    @Override
    public InputStream read() {
        if (type == FileType.Local) {
            return FileDB.getInstance().read(this);
        }
        else {
            InputStream in = preloader.read(file);
            if (in == null) throw new GdxRuntimeException(file + " does not exist");
            return in;
        }
    }

    @Override
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

    @Override
    public OutputStream write(boolean append) {
        if (type == FileType.Local) {
            return FileDB.getInstance().write(this, append);
        }
        else {
            throw new GdxRuntimeException("Cannot write to the given file.");
        }
    }

    @Override
    public Writer writer(boolean append, String charset) {
        try {
            return new BufferedWriter(new OutputStreamWriter(write(append), "UTF-8"));
        }
        catch (Exception e) {
            throw new GdxRuntimeException("Error obtaining writer.", e);
        }
    }

    @Override
    public FileHandle[] list() {
      if (type == FileType.Local) {
          return FileDB.getInstance().list(this);
      }
      else {
          return preloader.list(file);
      }
    }

    @Override
    public FileHandle[] list(FileFilter filter) {
        if (type == FileType.Local) {
            return FileDB.getInstance().list(this, filter);
        }
        else {
            return preloader.list(file, filter);
        }
    }

    @Override
    public FileHandle[] list(FilenameFilter filter) {
        if (type == FileType.Local) {
            return FileDB.getInstance().list(this, filter);
        }
        else {
            return preloader.list(file, filter);
        }
    }

    @Override
    public FileHandle[] list(String suffix) {
        if (type == FileType.Local) {
            return FileDB.getInstance().list(this, suffix);
        }
        else {
            return preloader.list(file, suffix);
        }
    }

    @Override
    public boolean isDirectory() {
        if (type == FileType.Local) {
            return FileDB.getInstance().isDirectory(this);
        }
        else {
            return preloader.isDirectory(file);
        }
    }

    @Override
    public FileHandle child(String name) {
        return new WebFileHandle(preloader, (file.isEmpty() ? "" : (file + (file.endsWith("/") ? "" : "/"))) + name,
                FileType.Internal);
    }

    @Override
    public FileHandle parent() {
        int index = file.lastIndexOf("/");
        String dir = "";
        if(index > 0) dir = file.substring(0, index);
        return new WebFileHandle(preloader, dir, type);
    }

    @Override
    public FileHandle sibling(String name) {
        return parent().child(fixSlashes(name));
    }

    /**
     * @throws GdxRuntimeException if this file handle is a {@link FileType#Classpath} or {@link FileType#Internal} file.
     */
    public void mkdirs() {
        if (type == FileType.Local) {
            FileDB.getInstance().mkdirs(this);
        }
        else {
            throw new GdxRuntimeException("Cannot mkdirs for non-local file: " + file);
        }
    }

    @Override
    public boolean exists() {
        if (type == FileType.Local) {
            return FileDB.getInstance().exists(this);
        }
        else {
            return preloader.contains(file);
        }
    }

    @Override
    public boolean delete() {
        if (type == FileType.Local) {
            return FileDB.getInstance().delete(this);
        }
        else {
            throw new GdxRuntimeException("Cannot delete a non-local file: " + file);
        }
    }

    @Override
    public boolean deleteDirectory() {
        throw new GdxRuntimeException("Cannot delete directory (missing implementation): " + file);
    }

    @Override
    public long length() {
        if (type == FileType.Local) {
            return FileDB.getInstance().length(this);
        }
        else {
            return preloader.length(file);
        }
    }

    @Override
    public long lastModified() {
        return 0;
    }

    @Override
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
