package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.Pixmap;

public class ETC1TextureData implements TextureData {
    public ETC1TextureData(FileHandle file) {
        throw new GdxRuntimeException("ETC1TextureData not supported in web backend");
    }

    public ETC1TextureData(FileHandle file, boolean useMipMaps) {
        throw new GdxRuntimeException("ETC1TextureData not supported in web backend");
    }

    @Override
    public TextureDataType getType() {
        return null;
    }

    @Override
    public boolean isPrepared() {
        return false;
    }

    @Override
    public void prepare() {
    }

    @Override
    public Pixmap consumePixmap() {
        return null;
    }

    @Override
    public boolean disposePixmap() {
        return false;
    }

    @Override
    public void consumeCustomData(int target) {

    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public Pixmap.Format getFormat() {
        return null;
    }

    @Override
    public boolean useMipMaps() {
        return false;
    }

    @Override
    public boolean isManaged() {
        return false;
    }
}