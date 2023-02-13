package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.CubemapData;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.utils.Array;
import com.github.xpenatan.gdx.backends.web.gen.Emulate;

@Emulate(CubemapLoader.class)
public class CubemapLoaderEmu extends AsynchronousAssetLoader<Cubemap, CubemapLoaderEmu.CubemapParameterEmu> {
    static public class CubemapLoaderInfo {
        String filename;
        CubemapData data;
        Cubemap cubemap;
    }

    CubemapLoaderInfo info = new CubemapLoaderInfo();

    public CubemapLoaderEmu(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, CubemapParameterEmu parameter) {
        info.filename = fileName;
        if(parameter == null || parameter.cubemapData == null) {
            Pixmap pixmap = null;
            Pixmap.Format format = null;
            boolean genMipMaps = false;
            info.cubemap = null;

            if(parameter != null) {
                format = parameter.format;
                info.cubemap = parameter.cubemap;
            }
        }
        else {
            info.data = parameter.cubemapData;
            info.cubemap = parameter.cubemap;
        }
        if(!info.data.isPrepared()) info.data.prepare();
    }

    @Override
    public Cubemap loadSync(AssetManager manager, String fileName, FileHandle file, CubemapParameterEmu parameter) {
        if(info == null) return null;
        Cubemap cubemap = info.cubemap;
        if(cubemap != null) {
            cubemap.load(info.data);
        }
        else {
            cubemap = new Cubemap(info.data);
        }
        if(parameter != null) {
            cubemap.setFilter(parameter.minFilter, parameter.magFilter);
            cubemap.setWrap(parameter.wrapU, parameter.wrapV);
        }
        return cubemap;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, CubemapParameterEmu parameter) {
        return null;
    }

    @Emulate(CubemapLoader.CubemapParameter.class)
    static public class CubemapParameterEmu extends AssetLoaderParameters<Cubemap> {
        public Pixmap.Format format = null;
        public Cubemap cubemap = null;
        public CubemapData cubemapData = null;
        public TextureFilter minFilter = TextureFilter.Nearest;
        public TextureFilter magFilter = TextureFilter.Nearest;
        public TextureWrap wrapU = TextureWrap.ClampToEdge;
        public TextureWrap wrapV = TextureWrap.ClampToEdge;
    }
}
