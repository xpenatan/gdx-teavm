package com.github.xpenatan.gdx.backends.web.emu.com.badlogic.gdx.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.github.xpenatan.gdx.backends.web.emu.Emulate;
import com.github.xpenatan.gdx.backends.web.emu.com.badlogic.gdx.graphics.glutils.FileTextureDataEmu;
import com.github.xpenatan.gdx.backends.web.emu.graphics.PixmapEmu;

@Emulate(TextureData.class)
public interface TextureDataEmu {
    @Emulate(TextureData.TextureDataType.class)
    public enum TextureDataTypeEmu {

        Pixmap, Custom
    }

    public TextureDataTypeEmu getType();

    public boolean isPrepared();

    public void prepare();

    public PixmapEmu consumePixmap();

    public boolean disposePixmap();

    public void consumeCustomData(int target);

    public int getWidth();

    public int getHeight();

    public Pixmap.Format getFormat();

    public boolean useMipMaps();

    public boolean isManaged();

    @Emulate(TextureData.Factory.class)
    public static class FactoryEmu {

        public static TextureDataEmu loadFromFile(FileHandle file, boolean useMipMaps) {
            return loadFromFile(file, null, useMipMaps);
        }

        public static TextureDataEmu loadFromFile(FileHandle file, Pixmap.Format format, boolean useMipMaps) {
            if(file == null) return null;
            FileTextureDataEmu fileTextureDataEmu = new FileTextureDataEmu(file, new PixmapEmu(file), format, useMipMaps);
            return fileTextureDataEmu;
        }
    }
}
