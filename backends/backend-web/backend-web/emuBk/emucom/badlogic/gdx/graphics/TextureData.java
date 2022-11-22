package emucom.badlogic.gdx.graphics;

import emucom.badlogic.gdx.files.FileHandle;
import emucom.badlogic.gdx.graphics.Pixmap.Format;
import emucom.badlogic.gdx.graphics.glutils.FileTextureData;

public interface TextureData {
    public enum TextureDataType {
        Pixmap, Custom
    }

    public TextureDataType getType();

    public boolean isPrepared();

    public void prepare();

    public emucom.badlogic.gdx.graphics.Pixmap consumePixmap();

    public boolean disposePixmap();

    public void consumeCustomData(int target);

    public int getWidth();

    public int getHeight();

    public Format getFormat();

    public boolean useMipMaps();

    public boolean isManaged();

    public static class Factory {

        public static TextureData loadFromFile(FileHandle file, boolean useMipMaps) {
            return loadFromFile(file, null, useMipMaps);
        }

        public static TextureData loadFromFile(FileHandle file, Format format, boolean useMipMaps) {
            if(file == null) return null;
            return new FileTextureData(file, new Pixmap(file), format, useMipMaps);
        }
    }
}