package com.badlogic.gdx.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(TextureData.class)
public interface TextureDataEmu {
    @Emulate(TextureData.TextureDataType.class)
    public enum TextureDataTypeEmu {

        Pixmap, Custom
    }

    public TextureDataTypeEmu getType();

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

    @Emulate(TextureData.Factory.class)
    public static class FactoryEmu {

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
