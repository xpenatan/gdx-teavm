package emu.com.badlogic.gdx.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.FileTextureData;

public interface TTextureData {
    public enum TextureDataType {

        Pixmap, Custom
    }

    public TextureDataType getType();

    public boolean isPrepared();

    public void prepare();

    public Pixmap consumePixmap();

    public boolean disposePixmap();

    public void consumeCustomData(int target);

    public int getWidth();

    public int getHeight();

    public Pixmap.Format getFormat();

    public boolean useMipMaps();

    public boolean isManaged();

    public static class Factory {

        public static TextureData loadFromFile(FileHandle file, boolean useMipMaps) {
            return loadFromFile(file, null, useMipMaps);
        }

        public static TextureData loadFromFile(FileHandle file, Pixmap.Format format, boolean useMipMaps) {
            if(file == null) return null;
            FileTextureData fileTextureDataEmu = new FileTextureData(file, new Pixmap(file), format, useMipMaps);
            return fileTextureDataEmu;
        }
    }
}