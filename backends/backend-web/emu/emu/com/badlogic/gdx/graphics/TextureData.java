package emu.com.badlogic.gdx.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.FileTextureData;

public interface TextureData {

    public enum TextureDataType {
        Pixmap, Custom
    }

    public TextureDataType getType();

    public boolean isPrepared();

    public void prepare();

    public com.badlogic.gdx.graphics.Pixmap consumePixmap();

    public boolean disposePixmap();

    public void consumeCustomData(int target);

    public int getWidth();

    public int getHeight();

    public com.badlogic.gdx.graphics.Pixmap.Format getFormat();

    public boolean useMipMaps();

    public boolean isManaged();

    public static class Factory {

        public static com.badlogic.gdx.graphics.TextureData loadFromFile(FileHandle file, boolean useMipMaps) {
            return loadFromFile(file, null, useMipMaps);
        }

        public static com.badlogic.gdx.graphics.TextureData loadFromFile(FileHandle file, com.badlogic.gdx.graphics.Pixmap.Format format, boolean useMipMaps) {
            if(file == null) return null;
            FileTextureData fileTextureDataEmu = new FileTextureData(file, new com.badlogic.gdx.graphics.Pixmap(file), format, useMipMaps);
            return fileTextureDataEmu;
        }
    }
}
