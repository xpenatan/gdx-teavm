package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.github.xpenatan.gdx.backends.teavm.filesystem.FileDB;
import com.github.xpenatan.gdx.backends.teavm.preloader.Preloader;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * @author xpenatan
 */
public class TeaFileHandle extends FileHandle {
    public final Preloader preloader;
    private final String file;
    private final FileType type;

    public TeaFileHandle(Preloader preloader, String fileName, FileType type) {
        if((type != FileType.Internal) && (type != FileType.Classpath) && (type != FileType.Local)) {
            throw new GdxRuntimeException("FileType '" + type + "' Not supported in web backend");
        }
        this.preloader = preloader;
        this.file = fixSlashes(fileName);
        this.type = type;
    }

    public TeaFileHandle(String path) {
        this(((TeaApplication)Gdx.app).getPreloader(), path, FileType.Internal);
    }

    /** @return The full url to an asset, e.g. http://localhost:8080/assets/data/shotgun.ogg */
    public String getAssetUrl () {
        return preloader.getAssetUrl() + preloader.assetNames.get(file, file);
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
        if(type == FileType.Local) {
            return FileDB.getInstance().read(this);
        }
        else {
            InputStream in = preloader.read(file);
            if(in == null) throw new GdxRuntimeException(file + " does not exist");
            return in;
        }
    }

    /**
     * Returns a buffered stream for reading this file as bytes.
     *
     * @throws GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    public BufferedInputStream read(int bufferSize) {
        return new BufferedInputStream(read(), bufferSize);
    }

    /**
     * Returns a reader for reading this file as characters.
     *
     * @throws GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    public Reader reader() {
        return new InputStreamReader(read());
    }

    /**
     * Returns a reader for reading this file as characters.
     *
     * @throws GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    public Reader reader(String charset) {
        try {
            return new InputStreamReader(read(), charset);
        }
        catch(UnsupportedEncodingException e) {
            throw new GdxRuntimeException("Encoding '" + charset + "' not supported", e);
        }
    }

    /**
     * Returns a buffered reader for reading this file as characters.
     *
     * @throws GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    public BufferedReader reader(int bufferSize) {
        return new BufferedReader(reader(), bufferSize);
    }

    /**
     * Returns a buffered reader for reading this file as characters.
     *
     * @throws GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    public BufferedReader reader(int bufferSize, String charset) {
        return new BufferedReader(reader(charset), bufferSize);
    }

    /**
     * Reads the entire file into a string using the platform's default charset.
     *
     * @throws GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    public String readString() {
        return readString(null);
    }

    /**
     * Reads the entire file into a string using the specified charset.
     *
     * @throws GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    public String readString(String charset) {
        if(type == FileType.Local) {
            // obtain via reader
            return super.readString(charset);
        }
        else {
            if(preloader.isText(file)) return preloader.texts.get(file);
            try {
                return new String(readBytes(), "UTF-8");
            }
            catch(UnsupportedEncodingException e) {
                return null;
            }
        }
    }

    /**
     * Reads the entire file into a byte array.
     *
     * @throws GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    public byte[] readBytes() {
        int length = (int)length();
        if(length == 0) length = 512;
        byte[] buffer = new byte[length];
        int position = 0;
        InputStream input = read();
        try {
            while(true) {
                int count = input.read(buffer, position, buffer.length - position);
                if(count == -1) break;
                position += count;
                if(position == buffer.length) {
                    // Grow buffer.
                    byte[] newBuffer = new byte[buffer.length * 2];
                    System.arraycopy(buffer, 0, newBuffer, 0, position);
                    buffer = newBuffer;
                }
            }
        }
        catch(IOException ex) {
            throw new GdxRuntimeException("Error reading file: " + this, ex);
        }
        finally {
            try {
                if(input != null) input.close();
            }
            catch(IOException ignored) {
            }
        }
        if(position < buffer.length) {
            // Shrink buffer.
            byte[] newBuffer = new byte[position];
            System.arraycopy(buffer, 0, newBuffer, 0, position);
            buffer = newBuffer;
        }
        return buffer;
    }

    /**
     * Reads the entire file into the byte array. The byte array must be big enough to hold the file's data.
     *
     * @param bytes  the array to load the file into
     * @param offset the offset to start writing bytes
     * @param size   the number of bytes to read, see {@link #length()}
     * @return the number of read bytes
     */
    public int readBytes(byte[] bytes, int offset, int size) {
        InputStream input = read();
        int position = 0;
        try {
            while(true) {
                int count = input.read(bytes, offset + position, size - position);
                if(count <= 0) break;
                position += count;
            }
        }
        catch(IOException ex) {
            throw new GdxRuntimeException("Error reading file: " + this, ex);
        }
        finally {
            try {
                if(input != null) input.close();
            }
            catch(IOException ignored) {
            }
        }
        return position - offset;
    }

    /**
     * Returns a stream for writing to this file. Parent directories will be created if necessary.
     *
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws GdxRuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *                             {@link FileType#Internal} file, or if it could not be written.
     */
    public OutputStream write(boolean append) {
        //TODO remove fixed size
        return write(append, 4096);
    }

    /**
     * Returns a buffered stream for writing to this file. Parent directories will be created if necessary.
     *
     * @param append     If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @param bufferSize The size of the buffer.
     * @throws GdxRuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *                             {@link FileType#Internal} file, or if it could not be written.
     */
    public OutputStream write(boolean append, int bufferSize) {
        if(type == FileType.Local) {
            return FileDB.getInstance().write(this, append, bufferSize);
        }
        else {
            throw new GdxRuntimeException("Cannot write to the given file.");
        }
    }

    /**
     * Reads the remaining bytes from the specified stream and writes them to this file. The stream is closed. Parent directories
     * will be created if necessary.
     *
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws GdxRuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *                             {@link FileType#Internal} file, or if it could not be written.
     */
    public void write(InputStream input, boolean append) {
        OutputStream output = null;
        try {
            int available = input.available();
            output = write(append, available);
            StreamUtils.copyStream(input, output);
        }
        catch(Exception ex) {
            throw new GdxRuntimeException("Error stream writing to file: " + file + " (" + type + ")", ex);
        }
        finally {
            StreamUtils.closeQuietly(input);
            StreamUtils.closeQuietly(output);
        }
    }

    /**
     * Returns a writer for writing to this file using the default charset. Parent directories will be created if necessary.
     *
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws GdxRuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *                             {@link FileType#Internal} file, or if it could not be written.
     */
    public Writer writer(boolean append) {
        return writer(append, null);
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
        catch(Exception e) {
            throw new GdxRuntimeException("Error obtaining writer.", e);
        }
    }

    /**
     * Writes the specified string to the file using the default charset. Parent directories will be created if necessary.
     *
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws GdxRuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *                             {@link FileType#Internal} file, or if it could not be written.
     */
    public void writeString(String string, boolean append) {
        writeString(string, append, null);
    }

    /**
     * Writes the specified string to the file as UTF-8. Parent directories will be created if necessary.
     *
     * @param append  If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @param charset May be null to use the default charset.
     * @throws GdxRuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *                             {@link FileType#Internal} file, or if it could not be written.
     */
    public void writeString(String string, boolean append, String charset) {
        Writer writer = null;
        try {
            writer = writer(append, charset);
            writer.write(string);
        }
        catch(Exception ex) {
            throw new GdxRuntimeException("Error writing file: " + file + " (" + type + ")", ex);
        }
        finally {
            StreamUtils.closeQuietly(writer);
        }
    }

    /**
     * Writes the specified bytes to the file. Parent directories will be created if necessary.
     *
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws GdxRuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *                             {@link FileType#Internal} file, or if it could not be written.
     */
    public void writeBytes(byte[] bytes, boolean append) {
        OutputStream output = write(append);
        try {
            output.write(bytes);
        }
        catch(IOException ex) {
            throw new GdxRuntimeException("Error writing file: " + file + " (" + type + ")", ex);
        }
        finally {
            StreamUtils.closeQuietly(output);
        }
    }

    /**
     * Writes the specified bytes to the file. Parent directories will be created if necessary.
     *
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws GdxRuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *                             {@link FileType#Internal} file, or if it could not be written.
     */
    public void writeBytes(byte[] bytes, int offset, int length, boolean append) {
        OutputStream output = write(append);
        try {
            output.write(bytes, offset, length);
        }
        catch(IOException ex) {
            throw new GdxRuntimeException("Error writing file: " + file + " (" + type + ")", ex);
        }
        finally {
            StreamUtils.closeQuietly(output);
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
        if(type == FileType.Local) {
            return FileDB.getInstance().list(this);
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
        if(type == FileType.Local) {
            return FileDB.getInstance().list(this, filter);
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
    public FileHandle[] list(FilenameFilter filter) {
        if(type == FileType.Local) {
            return FileDB.getInstance().list(this, filter);
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
        if(type == FileType.Local) {
            return FileDB.getInstance().list(this, suffix);
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
        if(type == FileType.Local) {
            return FileDB.getInstance().isDirectory(this);
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
        return new TeaFileHandle(preloader, (file.isEmpty() ? "" : (file + (file.endsWith("/") ? "" : "/"))) + name,
                type);
    }

    public FileHandle parent() {
        int index = file.lastIndexOf("/");
        String dir = "";
        if(index > 0) dir = file.substring(0, index);
        return new TeaFileHandle(preloader, dir, type);
    }

    public FileHandle sibling(String name) {
        return parent().child(fixSlashes(name));
    }

    /**
     * @throws GdxRuntimeException if this file handle is a {@link FileType#Classpath} or {@link FileType#Internal} file.
     */
    public void mkdirs() {
        if(type == FileType.Local) {
            FileDB.getInstance().mkdirs(this);
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
        if(type == FileType.Local) {
            return FileDB.getInstance().exists(this);
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
        if(type == FileType.Local) {
            return FileDB.getInstance().delete(this);
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
        if(type == FileType.Local) {
            return FileDB.getInstance().deleteDirectory(this);
        }
        throw new GdxRuntimeException("Cannot delete directory (missing implementation): " + file);
    }

    /**
     * Copies this file or directory to the specified file or directory. If this handle is a file, then 1) if the destination is a
     * file, it is overwritten, or 2) if the destination is a directory, this file is copied into it, or 3) if the destination
     * doesn't exist, {@link #mkdirs()} is called on the destination's parent and this file is copied into it with a new name. If
     * this handle is a directory, then 1) if the destination is a file, GdxRuntimeException is thrown, or 2) if the destination is
     * a directory, this directory is copied into it recursively, overwriting existing files, or 3) if the destination doesn't
     * exist, {@link #mkdirs()} is called on the destination and this directory is copied into it recursively.
     *
     * @throws GdxRuntimeException if the destination file handle is a {@link FileType#Classpath} or {@link FileType#Internal} file,
     *                             or copying failed.
     */
    public void copyTo(FileHandle dest) {
        if(!isDirectory()) {
            if(dest.isDirectory()) dest = dest.child(name());
            copyFile(this, (TeaFileHandle)dest);
            return;
        }
        if(dest.exists()) {
            if(!dest.isDirectory()) throw new GdxRuntimeException("Destination exists but is not a directory: " + dest);
        }
        else {
            dest.mkdirs();
            if(!dest.isDirectory()) throw new GdxRuntimeException("Destination directory cannot be created: " + dest);
        }
        copyDirectory(this, (TeaFileHandle)dest.child(name()));
    }

    /**
     * Moves this file to the specified file, overwriting the file if it already exists.
     *
     * @throws GdxRuntimeException if the source or destination file handle is a {@link FileType#Classpath} or
     *                             {@link FileType#Internal} file.
     */
    public void moveTo(FileHandle dest) {
        switch(type) {
            case Classpath: {
                throw new GdxRuntimeException("Cannot move a classpath file: " + file);
            }
            case Internal: {
                throw new GdxRuntimeException("Cannot move an internal file: " + file);
            }
            case Local:
            case Absolute:
            case External:
            default:
                copyTo(dest);
                delete();
                if(exists() && isDirectory()) deleteDirectory();
        }
    }

    /**
     * Returns the length in bytes of this file, or 0 if this file is a directory, does not exist, or the size cannot otherwise be
     * determined.
     */
    public long length() {
        if(type == FileType.Local) {
            return FileDB.getInstance().length(this);
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

    static private void copyFile(TeaFileHandle source, TeaFileHandle dest) {
        try {
            dest.write(source.read(), false);
        }
        catch(Exception ex) {
            throw new GdxRuntimeException("Error copying source file: " + source.file + " (" + source.type + ")\n" //
                    + "To destination: " + dest.file + " (" + dest.type + ")", ex);
        }
    }

    static private void copyDirectory(TeaFileHandle sourceDir, TeaFileHandle destDir) {
        destDir.mkdirs();
        TeaFileHandle[] files = (TeaFileHandle[])sourceDir.list();
        for(TeaFileHandle srcFile : files) {
            TeaFileHandle destFile = (TeaFileHandle)destDir.child(srcFile.name());
            if(srcFile.isDirectory()) {
                copyDirectory(srcFile, destFile);
            }
            else {
                copyFile(srcFile, destFile);
            }
        }
    }
}