package com.github.xpenatan.gdx.backends.web.filesystem;

import com.badlogic.gdx.utils.Base64Coder;
import com.github.xpenatan.gdx.backends.web.WebFileHandle;
import com.github.xpenatan.gdx.backends.web.dom.StorageWrapper;
import com.github.xpenatan.gdx.backends.web.utils.Storage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Local storage based file system. Stays persistent but is limited to about 2.5-5MB in general.
 *
 * @author noblemaster
 */
final class FileDBStorage extends FileDB {

  /** Our files stored and encoded as Base64. Directories with "d:..." and regular files stored as "f:...". */
  private final StorageWrapper storage;

  private static final String ID_FILE = "f:";
  private static final String ID_DIR = "d:";


  FileDBStorage(StorageWrapper storage) {
    this.storage = storage;
  }

  StorageWrapper storage() {
    return storage;
  }

  @Override
  public InputStream read(WebFileHandle file) {
    // we obtain the stored byte array
    String data = storage.getItem(ID_FILE + file.path());
    return new ByteArrayInputStream(Base64Coder.decode(data));
  }

  @Override
  public void writeInternal(WebFileHandle file, byte[] data, int expectedLength) {
    storage.setItem(ID_FILE + file.path(), new String(Base64Coder.encode(data)));
  }

  @Override
  public String[] paths(WebFileHandle file) {
    String[] paths = new String[storage.getLength()];
    for (int i = 0; i < storage.getLength(); i++) {
      // cut the identifier for files and directories and add to path list
      paths[i] = storage.key(i).substring(2);
    }
    return paths;
  }

  @Override
  public boolean isDirectory(WebFileHandle file) {
    return storage.getItem(ID_DIR + file.path()) != null;
  }

  @Override
  public void mkdirs(WebFileHandle file) {
    // add for current path if not already a file!
    if (storage.getItem(ID_FILE + file.path()) == null) {
      storage.setItem(ID_DIR + file.path(), "");
    }

    // do for parent directories also
    file = (WebFileHandle)file.parent();
    while (file.path().length() > 0) {
      storage.setItem(ID_DIR + file.path(), "");
      file = (WebFileHandle)file.parent();
    }
  }

  @Override
  public boolean exists(WebFileHandle file) {
    // check if either a file or directory entry exists
    return (storage.getItem(ID_DIR + file.path()) != null) ||
           (storage.getItem(ID_FILE + file.path()) != null);
  }

  @Override
  public boolean delete(WebFileHandle file) {
    storage.removeItem(ID_DIR + file.path());
    storage.removeItem(ID_FILE + file.path());
    return true;
  }

  @Override
  public long length(WebFileHandle file) {
    // this is somewhat slow
    String data = storage.getItem(ID_FILE + file.path());
    return Base64Coder.decode(data).length;
  }
}
