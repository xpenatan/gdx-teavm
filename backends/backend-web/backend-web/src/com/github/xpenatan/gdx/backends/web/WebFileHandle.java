package com.github.xpenatan.gdx.backends.web;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.github.xpenatan.gdx.backends.web.filesystem.FileDB;
import com.github.xpenatan.gdx.backends.web.preloader.Preloader;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

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

    @Override
    public String path() {
        return file;
    }

    @Override
    public FileType type() {
        return type;
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
    public WebFileHandle child(String name) {
        return new WebFileHandle(preloader, (file.isEmpty() ? "" : (file + (file.endsWith("/") ? "" : "/"))) + name,
                FileType.Internal);
    }

    @Override
    public WebFileHandle parent() {
        int index = file.lastIndexOf("/");
        String dir = "";
        if(index > 0) dir = file.substring(0, index);
        return new WebFileHandle(preloader, dir, type);
    }

    @Override
    public WebFileHandle sibling(String name) {
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

    @Override
    public BufferedInputStream read(int bufferSize) {
      return new BufferedInputStream(read(), bufferSize);
    }

    @Override
    public Reader reader() {
      return new InputStreamReader(read());
    }

    @Override
    public Reader reader(String charset) {
      InputStream stream = read();
      try {
        return new InputStreamReader(stream, charset);
      } catch (UnsupportedEncodingException ex) {
        StreamUtils.closeQuietly(stream);
        throw new GdxRuntimeException("Error reading file: " + this, ex);
      }
    }

    @Override
    public BufferedReader reader(int bufferSize) {
      return new BufferedReader(new InputStreamReader(read()), bufferSize);
    }

    @Override
    public BufferedReader reader(int bufferSize, String charset) {
      try {
        return new BufferedReader(new InputStreamReader(read(), charset), bufferSize);
      } catch (UnsupportedEncodingException ex) {
        throw new GdxRuntimeException("Error reading file: " + this, ex);
      }
    }

    @Override
    public String readString() {
      return readString(null);
    }

    @Override
    public byte[] readBytes() {
      InputStream input = read();
      try {
        return StreamUtils.copyStreamToByteArray(input, estimateLength());
      } catch (IOException ex) {
        throw new GdxRuntimeException("Error reading file: " + this, ex);
      } finally {
        StreamUtils.closeQuietly(input);
      }
    }

    private int estimateLength () {
      int length = (int)length();
      return length != 0 ? length : 512;
    }

    @Override
    public int readBytes(byte[] bytes, int offset, int size) {
      InputStream input = read();
      int position = 0;
      try {
        while (true) {
          int count = input.read(bytes, offset + position, size - position);
          if (count <= 0) break;
          position += count;
        }
      } catch (IOException ex) {
        throw new GdxRuntimeException("Error reading file: " + this, ex);
      } finally {
        StreamUtils.closeQuietly(input);
      }
      return position - offset;
    }

    @Override
    public ByteBuffer map() {
      return map(FileChannel.MapMode.READ_ONLY);
    }

    @Override
    public ByteBuffer map(FileChannel.MapMode mode) {
      if (type == FileType.Classpath) throw new GdxRuntimeException("Cannot map a classpath file: " + this);
      RandomAccessFile raf = null;
      try {
        File f = file();
        raf = new RandomAccessFile(f, mode == FileChannel.MapMode.READ_ONLY ? "r" : "rw");
        FileChannel fileChannel = raf.getChannel();
        ByteBuffer map = fileChannel.map(mode, 0, f.length());
        map.order(ByteOrder.nativeOrder());
        return map;
      } catch (Exception ex) {
        throw new GdxRuntimeException("Error memory mapping file: " + this + " (" + type + ")", ex);
      } finally {
        StreamUtils.closeQuietly(raf);
      }
    }

    @Override
    public OutputStream write(boolean append, int bufferSize) {
      return new BufferedOutputStream(write(append), bufferSize);
    }

    @Override
    public void write(InputStream input, boolean append) {
      OutputStream output = null;
      try {
        output = write(append);
        StreamUtils.copyStream(input, output);
      } catch (Exception ex) {
        throw new GdxRuntimeException("Error stream writing to file: " + file + " (" + type + ")", ex);
      } finally {
        StreamUtils.closeQuietly(input);
        StreamUtils.closeQuietly(output);
      }
    }

    @Override
    public Writer writer(boolean append) {
      return writer(append, null);
    }

    @Override
    public void writeString(String string, boolean append) {
      writeString(string, append, null);
    }

    @Override
    public void writeString(String string, boolean append, String charset) {
      Writer writer = null;
      try {
        writer = writer(append, charset);
        writer.write(string);
      } catch (Exception ex) {
        throw new GdxRuntimeException("Error writing file: " + file + " (" + type + ")", ex);
      } finally {
        StreamUtils.closeQuietly(writer);
      }
    }

    @Override
    public void writeBytes(byte[] bytes, boolean append) {
      OutputStream output = write(append);
      try {
        output.write(bytes);
      } catch (IOException ex) {
        throw new GdxRuntimeException("Error writing file: " + file + " (" + type + ")", ex);
      } finally {
        StreamUtils.closeQuietly(output);
      }
    }

    @Override
    public void writeBytes(byte[] bytes, int offset, int length, boolean append) {
      OutputStream output = write(append);
      try {
        output.write(bytes, offset, length);
      } catch (IOException ex) {
        throw new GdxRuntimeException("Error writing file: " + file + " (" + type + ")", ex);
      } finally {
        StreamUtils.closeQuietly(output);
      }
    }

    @Override
    public void emptyDirectory() {
      emptyDirectory(false);
    }

    @Override
    public void emptyDirectory(boolean preserveTree) {
      if (type == FileType.Classpath) throw new GdxRuntimeException("Cannot delete a classpath file: " + file);
      if (type == FileType.Internal) throw new GdxRuntimeException("Cannot delete an internal file: " + file);
      emptyDirectory(file(), preserveTree);
    }

    public void copyTo(WebFileHandle dest) {
      if (!isDirectory()) {
        if (dest.isDirectory()) dest = dest.child(name());
        copyFile(this, dest);
        return;
      }
      if (dest.exists()) {
        if (!dest.isDirectory()) throw new GdxRuntimeException("Destination exists but is not a directory: " + dest);
      } else {
        dest.mkdirs();
        if (!dest.isDirectory()) throw new GdxRuntimeException("Destination directory cannot be created: " + dest);
      }
      copyDirectory(this, dest.child(name()));
    }

    @Override
    public void moveTo(FileHandle dest) {
      switch (type) {
        case Classpath:
          throw new GdxRuntimeException("Cannot move a classpath file: " + file);
        case Internal:
          throw new GdxRuntimeException("Cannot move an internal file: " + file);
        case Absolute:
        case External:
          // Try rename for efficiency and to change case on case-insensitive file systems.
          if (file().renameTo(dest.file())) return;
      }
      copyTo(dest);
      delete();
      if (exists() && isDirectory()) deleteDirectory();
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof WebFileHandle)) return false;
      WebFileHandle other = (WebFileHandle)obj;
      return type == other.type && path().equals(other.path());
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 37 + type.hashCode();
      hash = hash * 67 + path().hashCode();
      return hash;
    }

  static private void emptyDirectory (File file, boolean preserveTree) {
    if (file.exists()) {
      File[] files = file.listFiles();
      if (files != null) {
        for (int i = 0, n = files.length; i < n; i++) {
          if (!files[i].isDirectory())
            files[i].delete();
          else if (preserveTree)
            emptyDirectory(files[i], true);
          else
            deleteDirectory(files[i]);
        }
      }
    }
  }

  static private boolean deleteDirectory (File file) {
    emptyDirectory(file, false);
    return file.delete();
  }

  static private void copyFile (WebFileHandle source, WebFileHandle dest) {
    try {
      dest.write(source.read(), false);
    } catch (Exception ex) {
      throw new GdxRuntimeException("Error copying source file: " + source.file + " (" + source.type + ")\n" //
              + "To destination: " + dest.file + " (" + dest.type + ")", ex);
    }
  }

  static private void copyDirectory (WebFileHandle sourceDir, WebFileHandle destDir) {
    destDir.mkdirs();
    WebFileHandle[] files = (WebFileHandle[])sourceDir.list();
    for (int i = 0, n = files.length; i < n; i++) {
      WebFileHandle srcFile = files[i];
      WebFileHandle destFile = destDir.child(srcFile.name());
      if (srcFile.isDirectory())
        copyDirectory(srcFile, destFile);
      else
        copyFile(srcFile, destFile);
    }
  }
}
