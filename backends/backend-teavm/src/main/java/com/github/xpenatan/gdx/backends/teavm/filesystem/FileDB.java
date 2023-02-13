package com.github.xpenatan.gdx.backends.teavm.filesystem;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.teavm.TeaFileHandle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DB to handle local files (load and save). E.g. via JS local storage (ca. 5MB) or the Indexed DB (ca. 50MB)
 *
 * @author noblemaster
 */
public abstract class FileDB {

  /** The singleton instance as we only have one file system DB. */
  private static FileDB INSTANCE = null;


  protected FileDB() {
    // hiding the constructor
  }

  public static FileDB getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new FileDBManager();
    }
    return INSTANCE;
  }

  public abstract InputStream read(TeaFileHandle file);

  public final OutputStream write(TeaFileHandle file, boolean append, int bufferSize) {
    // buffer for writing
    int bufferSizeMax = 8192;
    ByteArrayOutputStream buffer = new ByteArrayOutputStream(Math.min(bufferSize, bufferSizeMax));

    // wrap output stream so we get notified when we are done writing
    return new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        buffer.write(b);
      }
      @Override
      public void close() throws IOException {
        super.close();

        // store the data now
        byte[] data = buffer.toByteArray();
        writeInternal(file, data, append, Math.max(data.length, bufferSize));
      }
    };
  }

  /** Notifies when data has been written for a file. */
  protected abstract void writeInternal(TeaFileHandle file, byte[] data, boolean append, int expectedLength);

  public final FileHandle[] list(TeaFileHandle file) {
    // convert paths to file handles
    String[] paths = paths(file);
    FileHandle[] files = new FileHandle[paths.length];
    for (int i = 0; i < paths.length; i++) {
      String path = paths[i];
      if ((path.length() > 0) && (path.charAt(path.length() - 1) == '/')) {
        path = path.substring(0, path.length() - 1);
      }
      files[i] = new TeaFileHandle(null, path, Files.FileType.Local);
    }
    return files;
  }

  /** Returns all the paths. */
  protected abstract String[] paths(TeaFileHandle file);

  public final FileHandle[] list(TeaFileHandle file, FileFilter filter) {
    // TeaVM: doesn't support 'File'
    throw new GdxRuntimeException("File filtering not supported.");
  }

  public final FileHandle[] list(TeaFileHandle file, FilenameFilter filter) {
    // TeaVM: doesn't support 'File'
    throw new GdxRuntimeException("File filtering not supported.");
  }

  public final FileHandle[] list(TeaFileHandle file, String suffix) {
    FileHandle[] list = list(file);
    List<FileHandle> filtered = new ArrayList<FileHandle>(list.length);
    for (FileHandle f: list) {
      if (file.path().endsWith(suffix)) {
        filtered.add(f);
      }
    }
    return filtered.toArray(new FileHandle[filtered.size()]);
  }

  public abstract boolean isDirectory(TeaFileHandle file);

  public abstract void mkdirs(TeaFileHandle file);

  public abstract boolean exists(TeaFileHandle file);

  public abstract boolean delete(TeaFileHandle file);

  public abstract long length(TeaFileHandle file);

  public abstract void rename(TeaFileHandle source, TeaFileHandle target);
}
