package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoaderEmu;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;
import java.util.HashMap;
import java.util.Map;

@Emulate(Texture.class)
public class TextureEmu extends GLTexture {
    private static AssetManager assetManager;
    final static Map<Application, Array<TextureEmu>> managedTextures = new HashMap<Application, Array<TextureEmu>>();

    public enum TextureFilter {
        Nearest(GL20.GL_NEAREST),

        Linear(GL20.GL_LINEAR),

        MipMap(GL20.GL_LINEAR_MIPMAP_LINEAR),

        MipMapNearestNearest(GL20.GL_NEAREST_MIPMAP_NEAREST),

        MipMapLinearNearest(GL20.GL_LINEAR_MIPMAP_NEAREST),

        MipMapNearestLinear(GL20.GL_NEAREST_MIPMAP_LINEAR),

        MipMapLinearLinear(GL20.GL_LINEAR_MIPMAP_LINEAR);

        final int glEnum;

        TextureFilter (int glEnum) {
            this.glEnum = glEnum;
        }

        public boolean isMipMap () {
            return glEnum != GL20.GL_NEAREST && glEnum != GL20.GL_LINEAR;
        }

        public int getGLEnum () {
            return glEnum;
        }
    }

    public enum TextureWrap {
        MirroredRepeat(GL20.GL_MIRRORED_REPEAT), ClampToEdge(GL20.GL_CLAMP_TO_EDGE), Repeat(GL20.GL_REPEAT);

        final int glEnum;

        TextureWrap (int glEnum) {
            this.glEnum = glEnum;
        }

        public int getGLEnum () {
            return glEnum;
        }
    }

    TextureData data;

    public TextureEmu (String internalPath) {
        this(Gdx.files.internal(internalPath));
    }

    public TextureEmu (FileHandle file) {
        this(file, null, false);
    }

    public TextureEmu (FileHandle file, boolean useMipMaps) {
        this(file, null, useMipMaps);
    }

    public TextureEmu (FileHandle file, Pixmap.Format format, boolean useMipMaps) {
        this(TextureData.Factory.loadFromFile(file, format, useMipMaps));
    }

    public TextureEmu (Pixmap pixmap) {
        this(new PixmapTextureData(pixmap, null, false, false));
    }

    public TextureEmu (Pixmap pixmap, boolean useMipMaps) {
        this(new PixmapTextureData(pixmap, null, useMipMaps, false));
    }

    public TextureEmu (Pixmap pixmap, Pixmap.Format format, boolean useMipMaps) {
        this(new PixmapTextureData(pixmap, format, useMipMaps, false));
    }

    public TextureEmu (int width, int height, Pixmap.Format format) {
        this(new PixmapTextureData(new Pixmap(width, height, format), null, false, true));
    }

    public TextureEmu (TextureData data) {
        this(GL20.GL_TEXTURE_2D, Gdx.gl.glGenTexture(), data);
    }

    protected TextureEmu (int glTarget, int glHandle, TextureData data) {
        super(glTarget, glHandle);
        load(data);
        if (data.isManaged()) addManagedTexture(Gdx.app, this);
    }

    public void load (TextureData data) {
        if (this.data != null && data.isManaged() != this.data.isManaged())
            throw new GdxRuntimeException("New data must have the same managed status as the old data");
        this.data = data;

        if (!data.isPrepared()) data.prepare();

        bind();
        uploadImageData(GL20.GL_TEXTURE_2D, data);

        unsafeSetFilter(minFilter, magFilter, true);
        unsafeSetWrap(uWrap, vWrap, true);
        unsafeSetAnisotropicFilter(anisotropicFilterLevel, true);
        Gdx.gl.glBindTexture(glTarget, 0);
    }

    @Override
    protected void reload () {
        if (!isManaged()) throw new GdxRuntimeException("Tried to reload unmanaged Texture");
        glHandle = Gdx.gl.glGenTexture();
        load(data);
    }

    public void draw (Pixmap pixmap, int x, int y) {
        if (data.isManaged()) throw new GdxRuntimeException("can't draw to a managed texture");

        bind();
        Gdx.gl.glTexSubImage2D(glTarget, 0, x, y, pixmap.getWidth(), pixmap.getHeight(), pixmap.getGLFormat(), pixmap.getGLType(),
                pixmap.getPixels());
    }

    @Override
    public int getWidth () {
        return data.getWidth();
    }

    @Override
    public int getHeight () {
        return data.getHeight();
    }

    @Override
    public int getDepth () {
        return 0;
    }

    public TextureData getTextureData () {
        return data;
    }

    public boolean isManaged () {
        return data.isManaged();
    }

    public void dispose () {
        // this is a hack. reason: we have to set the glHandle to 0 for textures that are
        // reloaded through the asset manager as we first remove (and thus dispose) the texture
        // and then reload it. the glHandle is set to 0 in invalidateAllTextures prior to
        // removal from the asset manager.
        if (glHandle == 0) return;
        delete();
        if (data.isManaged()) if (managedTextures.get(Gdx.app) != null) managedTextures.get(Gdx.app).removeValue(this, true);
    }

    public String toString () {
        if (data instanceof FileTextureData) return data.toString();
        return super.toString();
    }

    private static void addManagedTexture (Application app, TextureEmu texture) {
        Array<TextureEmu> managedTextureArray = managedTextures.get(app);
        if (managedTextureArray == null) managedTextureArray = new Array<TextureEmu>();
        managedTextureArray.add(texture);
        managedTextures.put(app, managedTextureArray);
    }

    public static void clearAllTextures (Application app) {
        managedTextures.remove(app);
    }

    public static void invalidateAllTextures (Application app) {
        Array<TextureEmu> managedTextureArray = managedTextures.get(app);
        if (managedTextureArray == null) return;

        if (assetManager == null) {
            for (int i = 0; i < managedTextureArray.size; i++) {
                TextureEmu texture = managedTextureArray.get(i);
                texture.reload();
            }
        } else {
            // first we have to make sure the AssetManager isn't loading anything anymore,
            // otherwise the ref counting trick below wouldn't work (when a texture is
            // currently on the task stack of the manager.)
            assetManager.finishLoading();

            // next we go through each texture and reload either directly or via the
            // asset manager.
            Array<TextureEmu> textures = new Array<TextureEmu>(managedTextureArray);
            for (TextureEmu texture : textures) {
                String fileName = assetManager.getAssetFileName(texture);
                if (fileName == null) {
                    texture.reload();
                } else {
                    // get the ref count of the texture, then set it to 0 so we
                    // can actually remove it from the assetmanager. Also set the
                    // handle to zero, otherwise we might accidentially dispose
                    // already reloaded textures.
                    final int refCount = assetManager.getReferenceCount(fileName);
                    assetManager.setReferenceCount(fileName, 0);
                    texture.glHandle = 0;

                    // create the parameters, passing the reference to the texture as
                    // well as a callback that sets the ref count.
                    TextureLoaderEmu.TextureParameterEmu params = new TextureLoaderEmu.TextureParameterEmu();
                    params.textureData = texture.getTextureData();
                    params.minFilter = texture.getMinFilter();
                    params.magFilter = texture.getMagFilter();
                    params.wrapU = texture.getUWrap();
                    params.wrapV = texture.getVWrap();
                    params.genMipMaps = texture.data.useMipMaps(); // not sure about this?
                    params.texture = texture; // special parameter which will ensure that the references stay the same.
                    params.loadedCallback = new AssetLoaderParameters.LoadedCallback() {
                        @Override
                        public void finishedLoading (AssetManager assetManager, String fileName, Class type) {
                            assetManager.setReferenceCount(fileName, refCount);
                        }
                    };

                    // unload the texture, create a new gl handle then reload it.
                    assetManager.unload(fileName);
                    texture.glHandle = Gdx.gl.glGenTexture();
                    assetManager.load(fileName, TextureEmu.class, params);
                }
            }
            managedTextureArray.clear();
            managedTextureArray.addAll(textures);
        }
    }
    public static void setAssetManager (AssetManager manager) {
        TextureEmu.assetManager = manager;
    }

    public static String getManagedStatus () {
        StringBuilder builder = new StringBuilder();
        builder.append("Managed textures/app: { ");
        for (Application app : managedTextures.keySet()) {
            builder.append(managedTextures.get(app).size);
            builder.append(" ");
        }
        builder.append("}");
        return builder.toString();
    }

    public static int getNumManagedTextures () {
        return managedTextures.get(Gdx.app).size;
    }
}