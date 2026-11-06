package com.github.xpenatan.gdx.teavm.backends.web.filesystem;

public class FileData {

    public static final int TYPE_DIRECTORY = 1;
    public static final int TYPE_FILE = 2;

    private final String path;
    private final byte[] bytes;
    private final int type;
    private final long lastModified;

    public FileData(String path) {
        this(path, TYPE_DIRECTORY, null, System.currentTimeMillis());
    }

    public FileData(String path, byte[] bytes) {
        this(path, TYPE_FILE, bytes, System.currentTimeMillis());
    }

    public FileData(String path, int type, byte[] bytes) {
        this(path, type, bytes, System.currentTimeMillis());
    }

    public FileData(String path, int type, byte[] bytes, long lastModified) {
        if(bytes != null && path.endsWith("/")) {
            int length = path.length();
            path = path.substring(0, length-1);
        }
        this.path = path;
        this.bytes = bytes;
        this.type = type;
        this.lastModified = lastModified;
    }

    public String getPath() {
        return path;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public boolean isDirectory() {
        return type == TYPE_DIRECTORY;
    }

    public int getType() {
        return type;
    }

    public int getBytesSize() {
        return bytes != null ? bytes.length : 0;
    }

    public long getLastModified() {
        return lastModified;
    }
}
