package com.github.xpenatan.gdx.teavm.backends.web.filesystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.teavm.backends.web.WebFileHandle;
import java.io.ByteArrayOutputStream;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * DB to handle local files (load and save). E.g. via JS local storage (ca. 5MB) or the Indexed DB (ca. 50MB)
 *
 * @author noblemaster
 */
public abstract class FileDB {

    public abstract InputStream read(WebFileHandle file);

    public abstract byte[] readBytes(WebFileHandle file);

    public final OutputStream write(WebFileHandle file, boolean append, int bufferSize) {
        // buffer for writing
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(Math.max(512, Math.min(bufferSize, 8192)));

        // wrap output stream so we get notified when we are done writing
        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                buffer.write(b);
            }

            @Override
            public void write(byte[] b) throws IOException {
                buffer.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                buffer.write(b, off, len);
            }

            @Override
            public void close() throws IOException {
                // store the data now
                byte[] data = buffer.toByteArray();
                writeInternal(file, data, append, Math.max(data.length, bufferSize));
            }
        };
    }

    /**
     * Notifies when data has been written for a file.
     */
    protected abstract void writeInternal(WebFileHandle file, byte[] data, boolean append, int expectedLength);

    public final FileHandle[] list(WebFileHandle file) {
        // convert paths to file handles
        String[] paths = paths(file);
        FileHandle[] files = new WebFileHandle[paths.length];

        for(int i = 0; i < paths.length; i++) {
            String path = paths[i];
            if((path.length() > 0) && (path.charAt(path.length() - 1) == '/')) {
                path = path.substring(0, path.length() - 1);
            }

            files[i] = Gdx.files.getFileHandle(path, file.type());
        }
        return files;
    }

    /**
     * Returns all the paths.
     */
    protected abstract String[] paths(WebFileHandle file);

    public final FileHandle[] list(WebFileHandle file, FileFilter filter) {
        // TeaVM: doesn't support 'File'
        throw new GdxRuntimeException("File filtering not supported.");
    }

    public final FileHandle[] list(WebFileHandle file, FilenameFilter filter) {
        // TeaVM: doesn't support 'File'
        throw new GdxRuntimeException("File filtering not supported.");
    }

    public final FileHandle[] list(WebFileHandle file, String suffix) {
        FileHandle[] list = list(file);
        List<FileHandle> filtered = new ArrayList<FileHandle>(list.length);
        for(FileHandle f : list) {
            if(file.path().endsWith(suffix)) {
                filtered.add(f);
            }
        }
        return filtered.toArray(new FileHandle[filtered.size()]);
    }

    public abstract boolean isDirectory(WebFileHandle file);

    public abstract void mkdirs(WebFileHandle file);

    public abstract boolean exists(WebFileHandle file);

    public abstract boolean delete(WebFileHandle file);

    public abstract boolean deleteDirectory(WebFileHandle file);

    public abstract long length(WebFileHandle file);

    public abstract void rename(WebFileHandle source, WebFileHandle target);
}