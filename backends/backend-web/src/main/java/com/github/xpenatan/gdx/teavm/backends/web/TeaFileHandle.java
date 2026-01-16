package com.github.xpenatan.gdx.teavm.backends.web;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
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
    private final String file;
    private final FileType type;

    private TeaFiles teaFiles;

    public TeaFileHandle(TeaFiles teaFiles, String fileName, FileType type) {
        if((type != FileType.Internal) && (type != FileType.Classpath) && (type != FileType.Local)) {
            throw new GdxRuntimeException("FileType '" + type + "' Not supported in web backend");
        }
        this.file = fixSlashes(fileName);
        this.type = type;
        this.teaFiles = teaFiles;
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

    /**
     * @return the path and filename without the extension, e.g. dir/dir2/file.png -> dir/dir2/file
     */
    @Override
    public String pathWithoutExtension() {
        String path = file;
        int dotIndex = path.lastIndexOf('.');
        if(dotIndex == -1) return path;
        return path.substring(0, dotIndex);
    }

    @Override
    public FileType type() {
        return type;
    }

    /**
     * Returns a java.io.File that represents this file handle. Note the returned file will only be usable for
     * {@link FileType#Absolute} and {@link FileType#External} file handles.
     */
    @Override
    public File file() {
        throw new GdxRuntimeException("Not supported in web backend");
    }

    /**
     * Returns a stream for reading this file as bytes.
     *
     * @throws GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public InputStream read() {
        boolean exists = teaFiles.getFileDB(type).exists(this);
        if (type == FileType.Classpath || (type == FileType.Internal && !exists) || (type == FileType.Local && !exists)) {
            InputStream input = teaFiles.getFileDB(FileType.Classpath).read(this);
            if (input == null) throw new GdxRuntimeException("File not found: " + file + " (" + type + ")");
            return input;
        }
        return teaFiles.getFileDB(type).read(this);
    }

    /**
     * Returns a buffered stream for reading this file as bytes.
     *
     * @throws GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public BufferedInputStream read(int bufferSize) {
        return new BufferedInputStream(read(), bufferSize);
    }

    /**
     * Returns a reader for reading this file as characters.
     *
     * @throws GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public Reader reader() {
        return new InputStreamReader(read());
    }

    /**
     * Returns a reader for reading this file as characters.
     *
     * @throws GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
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
    @Override
    public BufferedReader reader(int bufferSize) {
        return new BufferedReader(reader(), bufferSize);
    }

    /**
     * Returns a buffered reader for reading this file as characters.
     *
     * @throws GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public BufferedReader reader(int bufferSize, String charset) {
        return new BufferedReader(reader(charset), bufferSize);
    }

    /**
     * Reads the entire file into a string using the platform's default charset.
     *
     * @throws GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public String readString() {
        return readString(null);
    }

    /**
     * Reads the entire file into a string using the specified charset.
     *
     * @throws GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
     */
    @Override
    public String readString(String charset) {
        return super.readString(charset);
    }

    /**
     * Reads the entire file into the byte array. The byte array must be big enough to hold the file's data.
     *
     * @param bytes  the array to load the file into
     * @param offset the offset to start writing bytes
     * @param size   the number of bytes to read, see {@link #length()}
     * @return the number of read bytes
     */
    @Override
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
    @Override
    public OutputStream write(boolean append) {
        if(type == FileType.Classpath) throw new GdxRuntimeException("Cannot write to a classpath file: " + file);
        if(type == FileType.Internal) throw new GdxRuntimeException("Cannot write to an internal file: " + file);
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
    @Override
    public OutputStream write(boolean append, int bufferSize) {
        return teaFiles.getFileDB(type).write(this, append, bufferSize);
    }

    /**
     * Reads the remaining bytes from the specified stream and writes them to this file. The stream is closed. Parent directories
     * will be created if necessary.
     *
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws GdxRuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *                             {@link FileType#Internal} file, or if it could not be written.
     */
    @Override
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
    @Override
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
    @Override
    public Writer writer(boolean append, String charset) {
        if(type == FileType.Classpath) throw new GdxRuntimeException("Cannot write to a classpath file: " + file);
        if(type == FileType.Internal) throw new GdxRuntimeException("Cannot write to an internal file: " + file);
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public FileHandle[] list() {
        if(type == FileType.Classpath) throw new GdxRuntimeException("Cannot list a classpath directory: " + file);
        return teaFiles.getFileDB(type).list(this);
    }

    /**
     * Returns the paths to the children of this directory that satisfy the specified filter. Returns an empty list if this file
     * handle represents a file and not a directory. On the desktop, an {@link FileType#Internal} handle to a directory on the
     * classpath will return a zero length array.
     *
     * @throws GdxRuntimeException if this file is an {@link FileType#Classpath} file.
     */
    @Override
    public FileHandle[] list(FileFilter filter) {
        if(type == FileType.Classpath) throw new GdxRuntimeException("Cannot list a classpath directory: " + file);
        return teaFiles.getFileDB(type).list(this, filter);
    }

    /**
     * Returns the paths to the children of this directory that satisfy the specified filter. Returns an empty list if this file
     * handle represents a file and not a directory. On the desktop, an {@link FileType#Internal} handle to a directory on the
     * classpath will return a zero length array.
     *
     * @throws GdxRuntimeException if this file is an {@link FileType#Classpath} file.
     */
    @Override
    public FileHandle[] list(FilenameFilter filter) {
        if(type == FileType.Classpath) throw new GdxRuntimeException("Cannot list a classpath directory: " + file);
        return teaFiles.getFileDB(type).list(this, filter);
    }

    /**
     * Returns the paths to the children of this directory with the specified suffix. Returns an empty list if this file handle
     * represents a file and not a directory. On the desktop, an {@link FileType#Internal} handle to a directory on the classpath
     * will return a zero length array.
     *
     * @throws GdxRuntimeException if this file is an {@link FileType#Classpath} file.
     */
    @Override
    public FileHandle[] list(String suffix) {
        if(type == FileType.Classpath) throw new GdxRuntimeException("Cannot list a classpath directory: " + file);
        return teaFiles.getFileDB(type).list(this, suffix);
    }

    /**
     * Returns true if this file is a directory. Always returns false for classpath files. On Android, an
     * {@link FileType#Internal} handle to an empty directory will return false. On the desktop, an {@link FileType#Internal}
     * handle to a directory on the classpath will return false.
     */
    @Override
    public boolean isDirectory() {
        if (type == FileType.Classpath) return false;
        return teaFiles.getFileDB(type).isDirectory(this);
    }

    /**
     * Returns a handle to the child with the specified name.
     *
     * @throws GdxRuntimeException if this file handle is a {@link FileType#Classpath} or {@link FileType#Internal} and the child
     *                             doesn't exist.
     */
    @Override
    public FileHandle child(String name) {
        return new TeaFileHandle(teaFiles, (file.isEmpty() ? "" : (file + (file.endsWith("/") ? "" : "/"))) + name,
                type);
    }

    @Override
    public FileHandle parent() {
        int index = file.lastIndexOf("/");
        String dir = "";
        if(index > 0) dir = file.substring(0, index);
        return new TeaFileHandle(teaFiles, dir, type);
    }

    @Override
    public FileHandle sibling(String name) {
        return parent().child(fixSlashes(name));
    }

    /**
     * @throws GdxRuntimeException if this file handle is a {@link FileType#Classpath} or {@link FileType#Internal} file.
     */
    @Override
    public void mkdirs() {
        if(type == FileType.Classpath) throw new GdxRuntimeException("Cannot mkdirs with a classpath file: " + file);
        if(type == FileType.Internal) throw new GdxRuntimeException("Cannot mkdirs with an internal file: " + file);
        teaFiles.getFileDB(type).mkdirs(this);
    }

    public void mkdirsInternal() {
        teaFiles.getFileDB(type).mkdirs(this);
    }

    /**
     * Returns true if the file exists. On Android, a {@link FileType#Classpath} or {@link FileType#Internal} handle to a
     * directory will always return false.
     */
    @Override
    public boolean exists() {
        boolean exists = teaFiles.getFileDB(type).exists(this);
        switch (type) {
            case Internal:
                if (exists) return true;
                // Fall through.
            case Classpath:
                return teaFiles.getFileDB(FileType.Classpath).exists(this);
        }
        return exists;
    }

    /**
     * Deletes this file or empty directory and returns success. Will not delete a directory that has children.
     *
     * @throws GdxRuntimeException if this file handle is a {@link FileType#Classpath} or {@link FileType#Internal} file.
     */
    @Override
    public boolean delete() {
        if(type == FileType.Classpath) throw new GdxRuntimeException("Cannot delete a classpath file: " + file);
        if(type == FileType.Internal) throw new GdxRuntimeException("Cannot delete an internal file: " + file);
        return teaFiles.getFileDB(type).delete(this);
    }

    /**
     * Deletes this file or directory and all children, recursively.
     *
     * @throws GdxRuntimeException if this file handle is a {@link FileType#Classpath} or {@link FileType#Internal} file.
     */
    @Override
    public boolean deleteDirectory() {
        if(type == FileType.Classpath) throw new GdxRuntimeException("Cannot delete a classpath file: " + file);
        if(type == FileType.Internal) throw new GdxRuntimeException("Cannot delete an internal file: " + file);
        return teaFiles.getFileDB(type).deleteDirectory(this);
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
    @Override
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
    @Override
    public void moveTo(FileHandle dest) {
        switch(type) {
            case Classpath: {
                throw new GdxRuntimeException("Cannot move a classpath file: " + file);
            }
            case Internal: {
                throw new GdxRuntimeException("Cannot move an internal file: " + file);
            }
        }
        if(!dest.exists()) {
            FileHandle destParent = dest.parent();
            FileHandle thisParent = parent();
            if(thisParent.equals(destParent)) {
                // Rename if same parent. Similar to absolute from desktop
                teaFiles.getFileDB(type).rename(this, (TeaFileHandle)dest);
                return;
            }
        }

        copyTo(dest);
        delete();
        if(exists() && isDirectory()) deleteDirectory();
    }

    /**
     * Returns the length in bytes of this file, or 0 if this file is a directory, does not exist, or the size cannot otherwise be
     * determined.
     */
    @Override
    public long length() {
        return teaFiles.getFileDB(type).length(this);
    }

    /**
     * Returns the last modified time in milliseconds for this file. Zero is returned if the file doesn't exist. Zero is returned
     * for {@link FileType#Classpath} files. On Android, zero is returned for {@link FileType#Internal} files. On the desktop, zero
     * is returned for {@link FileType#Internal} files on the classpath.
     */
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