package emucom.badlogic.gdx.graphics;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import emucom.badlogic.gdx.Gdx;
import emucom.badlogic.gdx.files.FileHandle;
import java.util.HashMap;
import java.util.Map;

public class Texture extends GLTexture {
    private static AssetManager assetManager;
    final static Map<Application, Array<com.badlogic.gdx.graphics.Texture>> managedTextures = new HashMap<Application, Array<com.badlogic.gdx.graphics.Texture>>();

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

    com.badlogic.gdx.graphics.TextureData data;

    public Texture (String internalPath) {
        this(Gdx.files.internal(internalPath));
    }

    public Texture (FileHandle file) {
        this(file, null, false);
    }

    public Texture (FileHandle file, boolean useMipMaps) {
        this(file, null, useMipMaps);
    }

    public Texture (FileHandle file, Pixmap.Format format, boolean useMipMaps) {
        this(TextureData.Factory.loadFromFile(file, format, useMipMaps));
    }

    public Texture (Pixmap pixmap) {
        this(new PixmapTextureData(pixmap, null, false, false));
    }

    public Texture (Pixmap pixmap, boolean useMipMaps) {
        this(new PixmapTextureData(pixmap, null, useMipMaps, false));
    }

    public Texture (com.badlogic.gdx.graphics.Pixmap pixmap, com.badlogic.gdx.graphics.Pixmap.Format format, boolean useMipMaps) {
        this(new PixmapTextureData(pixmap, format, useMipMaps, false));
    }

    public Texture (int width, int height, com.badlogic.gdx.graphics.Pixmap.Format format) {
        this(new PixmapTextureData(new com.badlogic.gdx.graphics.Pixmap(width, height, format), null, false, true));
    }

    public Texture (com.badlogic.gdx.graphics.TextureData data) {
        this(GL20.GL_TEXTURE_2D, Gdx.gl.glGenTexture(), data);
    }

    protected Texture (int glTarget, int glHandle, com.badlogic.gdx.graphics.TextureData data) {
        super(glTarget, glHandle);
        load(data);
        if (data.isManaged()) addManagedTexture(Gdx.app, this);
    }

    public void load (com.badlogic.gdx.graphics.TextureData data) {
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

    private static void addManagedTexture (Application app, com.badlogic.gdx.graphics.Texture texture) {
        Array<com.badlogic.gdx.graphics.Texture> managedTextureArray = managedTextures.get(app);
        if (managedTextureArray == null) managedTextureArray = new Array<com.badlogic.gdx.graphics.Texture>();
        managedTextureArray.add(texture);
        managedTextures.put(app, managedTextureArray);
    }

    public static void clearAllTextures (Application app) {
        managedTextures.remove(app);
    }

    public static void invalidateAllTextures (Application app) {
        Array<com.badlogic.gdx.graphics.Texture> managedTextureArray = managedTextures.get(app);
        if (managedTextureArray == null) return;

        if (assetManager == null) {
            for (int i = 0; i < managedTextureArray.size; i++) {
                com.badlogic.gdx.graphics.Texture texture = managedTextureArray.get(i);
                texture.reload();
            }
        } else {
            // first we have to make sure the AssetManager isn't loading anything anymore,
            // otherwise the ref counting trick below wouldn't work (when a texture is
            // currently on the task stack of the manager.)
            assetManager.finishLoading();

            // next we go through each texture and reload either directly or via the
            // asset manager.
            Array<com.badlogic.gdx.graphics.Texture> textures = new Array<com.badlogic.gdx.graphics.Texture>(managedTextureArray);
            for (com.badlogic.gdx.graphics.Texture texture : textures) {
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
                    TextureLoader.TextureParameter params = new TextureLoader.TextureParameter();
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
                    assetManager.load(fileName, com.badlogic.gdx.graphics.Texture.class, params);
                }
            }
            managedTextureArray.clear();
            managedTextureArray.addAll(textures);
        }
    }
    public static void setAssetManager (AssetManager manager) {
        com.badlogic.gdx.graphics.Texture.assetManager = manager;
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