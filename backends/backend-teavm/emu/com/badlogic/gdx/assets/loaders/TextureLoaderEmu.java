package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.utils.Array;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(TextureLoader.class)
public class TextureLoaderEmu extends AsynchronousAssetLoader<Texture, TextureLoaderEmu.TextureParameterEmu> {
    TextureData data;
    Texture texture;

    public TextureLoaderEmu(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle fileHandle, TextureParameterEmu parameter) {
        if(parameter == null || (parameter != null && parameter.textureData == null)) {
            Pixmap pixmap = null;
            Pixmap.Format format = null;
            boolean genMipMaps = false;
            texture = null;

            if(parameter != null) {
                format = parameter.format;
                genMipMaps = parameter.genMipMaps;
                texture = parameter.texture;
            }

            FileHandle handle = resolve(fileName);
            pixmap = new Pixmap(handle);
            data = new FileTextureData(handle, pixmap, format, genMipMaps);
        }
        else {
            data = parameter.textureData;
            if(!data.isPrepared()) data.prepare();
            texture = parameter.texture;
        }
    }

    @Override
    public Texture loadSync(AssetManager manager, String fileName, FileHandle fileHandle, TextureParameterEmu parameter) {
        Texture texture = this.texture;
        if(texture != null) {
            texture.load(data);
        }
        else {
            texture = new Texture(data);
        }
        if(parameter != null) {
            texture.setFilter(parameter.minFilter, parameter.magFilter);
            texture.setWrap(parameter.wrapU, parameter.wrapV);
        }
        return texture;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle fileHandle, TextureParameterEmu parameter) {
        return null;
    }

    @Emulate(TextureLoader.TextureParameter.class)
    static public class TextureParameterEmu extends AssetLoaderParameters<Texture> {
        /**
         * the format of the final Texture. Uses the source images format if null
         **/
        public Pixmap.Format format = null;
        /**
         * whether to generate mipmaps
         **/
        public boolean genMipMaps = false;
        public Texture texture = null;
        /**
         * TextureData for textures created on the fly, optional. When set, all format and genMipMaps are ignored
         */
        public TextureData textureData = null;
        public TextureFilter minFilter = TextureFilter.Nearest;
        public TextureFilter magFilter = TextureFilter.Nearest;
        public TextureWrap wrapU = TextureWrap.ClampToEdge;
        public TextureWrap wrapV = TextureWrap.ClampToEdge;
    }
}
