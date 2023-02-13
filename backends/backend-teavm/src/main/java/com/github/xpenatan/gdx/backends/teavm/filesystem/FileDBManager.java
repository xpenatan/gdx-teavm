package com.github.xpenatan.gdx.backends.teavm.filesystem;

import com.github.xpenatan.gdx.backends.teavm.WebFileHandle;
import com.github.xpenatan.gdx.backends.teavm.util.WebJSHelper;

import java.io.InputStream;

/**
 * Stores data either in persistent storage or memory.
 *
 * @author noblemaster
 */
public final class FileDBManager extends FileDB {

  /** Persistent local store for smaller files. */
  private final FileDBStorage localStorage;
  /** Memory store for larger files. Will be wiped when the page is newly loaded. */
  private final FileDBStorage memory;


  FileDBManager() {
    localStorage = new FileDBStorage(WebJSHelper.get().getStorage());
    memory = new FileDBStorage(new MemoryStorage());
  }

  @Override
  public InputStream read(WebFileHandle file) {
    if (memory.exists(file)) {
      return memory.read(file);
    }
    else {
      return localStorage.read(file);
    }
  }

  @Override
  protected void writeInternal(WebFileHandle file, byte[] data, boolean append, int expectedLength) {
    // write larger files into memory: up to 16.384kb into local storage (permanent)
    if (expectedLength >= 16384) {
      // data is large: store in memory
      if ((!append) || (!memory.exists(file))) {
        ((MemoryStorage) memory.storage()).cleanup();  // <-- removes old data as needed (to make sure we have enough space)
      }

      // write to memory...
      memory.writeInternal(file, data, append, expectedLength);
    }
    else {
      localStorage.writeInternal(file, data, append, expectedLength);
    }
  }

  @Override
  protected String[] paths(WebFileHandle file) {
    // combine & return the paths of memory & local storage
    String[] pathsMemory = memory.paths(file);
    String[] pathsLocalStorage = localStorage.paths(file);
    String[] paths = new String[pathsMemory.length + pathsLocalStorage.length];
    System.arraycopy(pathsMemory, 0, paths, 0, pathsMemory.length);
    System.arraycopy(pathsLocalStorage, 0, paths, pathsMemory.length, pathsLocalStorage.length);
    return paths;
  }

  @Override
  public boolean isDirectory(WebFileHandle file) {
    if (memory.exists(file)) {
      return memory.isDirectory(file);
    }
    else {
      return localStorage.isDirectory(file);
    }
  }

  @Override
  public void mkdirs(WebFileHandle file) {
    localStorage.mkdirs(file);
  }

  @Override
  public boolean exists(WebFileHandle file) {
    return memory.exists(file) || localStorage.exists(file);
  }

  @Override
  public boolean delete(WebFileHandle file) {
    if (memory.exists(file)) {
      return memory.delete(file);
    }
    else {
      return localStorage.delete(file);
    }
  }

  @Override
  public long length(WebFileHandle file) {
    if (memory.exists(file)) {
      return memory.length(file);
    }
    else {
      return localStorage.length(file);
    }
  }

  @Override
  public void rename(WebFileHandle source, WebFileHandle target) {
    if (memory.exists(source)) {
      memory.rename(source, target);
    }
    else {
      localStorage.rename(source, target);
    }
  }
}
