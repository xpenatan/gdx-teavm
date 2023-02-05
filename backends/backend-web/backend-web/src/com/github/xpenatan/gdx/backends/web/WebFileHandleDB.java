package com.github.xpenatan.gdx.backends.web;

import com.badlogic.gdx.files.FileHandle;

import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * DB to handle local files (load and save). E.g. via JS local storage (ca. 5MB) or the Indexed DB (ca. 50MB)
 *
 * @author noblemaster
 */
public abstract class WebFileHandleDB {

  /** The singleton instance as we only have one file system DB. */
  private static final WebFileHandleDB INSTANCE = new WebFileHandleDBIndexed();


  protected WebFileHandleDB() {
    // hiding the constructor
  }

  public static WebFileHandleDB getInstance() {
    return INSTANCE;
  }

  public abstract InputStream read(WebFileHandle file);

  public abstract OutputStream write(WebFileHandle file, boolean append);

  public abstract FileHandle[] list(WebFileHandle file);

  public abstract FileHandle[] list(WebFileHandle file, FileFilter filter);

  public abstract FileHandle[] list(WebFileHandle file, FilenameFilter filter);

  public abstract FileHandle[] list(WebFileHandle file, String suffix);

  public abstract boolean isDirectory(WebFileHandle file);

  public abstract void mkdirs(WebFileHandle file);

  public abstract boolean exists(WebFileHandle file);

  public abstract boolean delete(WebFileHandle file);

  public abstract long length(WebFileHandle file);
}
