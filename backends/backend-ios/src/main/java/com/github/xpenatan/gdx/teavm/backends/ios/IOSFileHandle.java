package com.github.xpenatan.gdx.teavm.backends.ios;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class IOSFileHandle extends FileHandle {
    public IOSFileHandle(String fileName, FileType type) {
        super(fileName, type);
    }

    public IOSFileHandle(File file, FileType type) {
        super(file, type);
    }

    @Override
    public FileHandle child(String name) {
        if(file.getPath().isEmpty()) {
            return new IOSFileHandle(new File(name), type);
        }
        return new IOSFileHandle(new File(file, name), type);
    }

    @Override
    public FileHandle sibling(String name) {
        if(file.getPath().isEmpty()) {
            throw new GdxRuntimeException("Cannot get the sibling of the root.");
        }
        return new IOSFileHandle(new File(file.getParent(), name), type);
    }

    @Override
    public FileHandle parent() {
        File parent = file.getParentFile();
        if(parent == null) {
            parent = type == FileType.Absolute ? new File("/") : new File("");
        }
        return new IOSFileHandle(parent, type);
    }

    @Override
    public File file() {
        if(type == FileType.Internal || type == FileType.Classpath) {
            return new File(IOSFiles.localPath, file.getPath());
        }
        if(type == FileType.External) {
            return new File(IOSFiles.externalPath, file.getPath());
        }
        if(type == FileType.Local) {
            return new File(IOSFiles.localPath, file.getPath());
        }
        return file;
    }

    @Override
    public InputStream read() {
        File resolvedFile = file();
        try {
            return new FileInputStream(resolvedFile);
        }
        catch(IOException exception) {
            throw new GdxRuntimeException("Error reading file: " + resolvedFile, exception);
        }
    }

    @Override
    public Reader reader() {
        return reader(null);
    }

    @Override
    public Reader reader(String charset) {
        Charset resolvedCharset = charset == null ? StandardCharsets.UTF_8 : Charset.forName(charset);
        return new InputStreamReader(read(), resolvedCharset);
    }

    @Override
    public BufferedReader reader(int bufferSize) {
        return new BufferedReader(reader(), bufferSize);
    }

    @Override
    public BufferedReader reader(int bufferSize, String charset) {
        return new BufferedReader(reader(charset), bufferSize);
    }

    @Override
    public String readString() {
        return readString(null);
    }

    @Override
    public String readString(String charset) {
        Charset resolvedCharset = charset == null ? StandardCharsets.UTF_8 : Charset.forName(charset);
        return new String(readBytes(), resolvedCharset);
    }

    @Override
    public byte[] readBytes() {
        File resolvedFile = file();
        try(FileInputStream input = new FileInputStream(resolvedFile);
            ByteArrayOutputStream output = new ByteArrayOutputStream((int)Math.max(0, resolvedFile.length()))) {
            byte[] buffer = new byte[4096];
            int read;
            while((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            return output.toByteArray();
        }
        catch(IOException exception) {
            throw new GdxRuntimeException("Error reading file: " + resolvedFile, exception);
        }
    }
}
