package com.github.xpenatan.gdx.backends.teavm.filesystem;

public class FileData {

    public static final int TYPE_DIRECTORY = 1;
    public static final int TYPE_FILE = 2;

    private final String name;
    private byte[] bytes;
    private int type;

    public FileData(String name, byte[] bytes) {
        this(name, TYPE_FILE, bytes);
    }

    public FileData(String name, int type, byte[] bytes) {
        this.name = name;
        this.bytes = bytes;
        this.type = type;
    }

    public FileData(String name) {
        this.name = name;
        this.type = TYPE_DIRECTORY;
    }

    public String getName() {
        return name;
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
}