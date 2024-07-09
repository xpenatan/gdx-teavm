package com.github.xpenatan.gdx.backends.teavm.filesystem;

public class FileData {

    public static final int TYPE_DIRECTORY = 1;
    public static final int TYPE_FILE = 2;

    private final String path;
    private final byte[] bytes;
    private final int type;

    public FileData(String path) {
        this(path, TYPE_DIRECTORY, null);
    }

    public FileData(String path, byte[] bytes) {
        this(path, TYPE_FILE, bytes);
    }

    public FileData(String path, int type, byte[] bytes) {
        if(bytes != null && path.endsWith("/")) {
            int length = path.length();
            path = path.substring(0, length-1);
        }
        this.path = path;
        this.bytes = bytes;
        this.type = type;
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
}