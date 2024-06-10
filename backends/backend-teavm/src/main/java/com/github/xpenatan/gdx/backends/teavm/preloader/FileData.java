package com.github.xpenatan.gdx.backends.teavm.preloader;

import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;

public class FileData {
    private final String name;
    private final Int8ArrayWrapper data;

    public FileData(String name, Int8ArrayWrapper data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public Int8ArrayWrapper getData() {
        return data;
    }

    public byte[] getBytes() {
        return TypedArrays.toByteArray(data);
    }
}