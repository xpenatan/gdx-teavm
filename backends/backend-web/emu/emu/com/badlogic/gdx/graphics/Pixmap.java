package emu.com.badlogic.gdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import emu.com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import emu.com.badlogic.gdx.graphics.g2d.Gdx2DPixmapNative;
import emu.com.badlogic.gdx.graphics.g2d.PixmapNativeInterface;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.teavm.backends.web.assetloader.AssetInstance;
import com.github.xpenatan.gdx.teavm.backends.web.assetloader.AssetLoaderListener;
import com.github.xpenatan.gdx.teavm.backends.web.TeaWebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.backends.web.dom.typedarray.TypedArrays;
import com.github.xpenatan.gdx.teavm.backends.web.assetloader.AssetType;
import com.github.xpenatan.gdx.teavm.backends.web.assetloader.TeaBlob;
import java.nio.ByteBuffer;
import org.teavm.jso.typedarrays.TypedArray;

public class Pixmap implements Disposable, PixmapNativeInterface {

    public static Pixmap createFromFrameBuffer(int x, int y, int w, int h) {
        Gdx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);
        final Pixmap pixmap = new Pixmap(w, h, Format.RGBA8888);
        ByteBuffer pixels = pixmap.getPixels();
        Gdx.gl.glReadPixels(x, y, w, h, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixels);
        pixmap.getNative().copyToHeap();
        return pixmap;
    }

    public enum Format {
        Alpha, Intensity, LuminanceAlpha, RGB565, RGBA4444, RGB888, RGBA8888;

        public static int toGdx2DPixmapFormat (Format format) {
            if (format == Alpha) return Gdx2DPixmap.GDX2D_FORMAT_ALPHA;
            if (format == Intensity) return Gdx2DPixmap.GDX2D_FORMAT_ALPHA;
            if (format == LuminanceAlpha) return Gdx2DPixmap.GDX2D_FORMAT_LUMINANCE_ALPHA;
            if (format == RGB565) return Gdx2DPixmap.GDX2D_FORMAT_RGB565;
            if (format == RGBA4444) return Gdx2DPixmap.GDX2D_FORMAT_RGBA4444;
            if (format == RGB888) return Gdx2DPixmap.GDX2D_FORMAT_RGB888;
            if (format == RGBA8888) return Gdx2DPixmap.GDX2D_FORMAT_RGBA8888;
            throw new GdxRuntimeException("Unknown Format: " + format);
        }

        public static Format fromGdx2DPixmapFormat (int format) {
            if (format == Gdx2DPixmap.GDX2D_FORMAT_ALPHA) return Alpha;
            if (format == Gdx2DPixmap.GDX2D_FORMAT_LUMINANCE_ALPHA) return LuminanceAlpha;
            if (format == Gdx2DPixmap.GDX2D_FORMAT_RGB565) return RGB565;
            if (format == Gdx2DPixmap.GDX2D_FORMAT_RGBA4444) return RGBA4444;
            if (format == Gdx2DPixmap.GDX2D_FORMAT_RGB888) return RGB888;
            if (format == Gdx2DPixmap.GDX2D_FORMAT_RGBA8888) return RGBA8888;
            throw new GdxRuntimeException("Unknown Gdx2DPixmap Format: " + format);
        }

        public static int toGlFormat (Format format) {
            return Gdx2DPixmap.toGlFormat(toGdx2DPixmapFormat(format));
        }

        public static int toGlType (Format format) {
            return Gdx2DPixmap.toGlType(toGdx2DPixmapFormat(format));
        }
    }

    Blending blending = Blending.SourceOver;
    Filter filter = Filter.BiLinear;

    private int color = 0;
    private Gdx2DPixmap nativePixmap;
    private boolean disposed;

    public static void downloadFromUrl(String url, final DownloadPixmapResponseListener responseListener) {
        AssetLoaderListener<TeaBlob> listener = new AssetLoaderListener<>() {
            @Override
            public void onFailure(String url) {
                Throwable t = new Exception("Failed to download image");
                responseListener.downloadFailed(t);
            }

            @Override
            public void onSuccess(String url, TeaBlob result) {
                TypedArray data = result.getData();
                byte[] byteArray = TypedArrays.toByteArray(data);
                Pixmap pixmapEmu = new Pixmap(byteArray, 0, byteArray.length);
                responseListener.downloadComplete(pixmapEmu);
            }
        };
        AssetInstance.getDownloaderInstance().load(true, url, AssetType.Binary, listener);
    }

    public Pixmap(FileHandle file) {
        String path = file.path();
        TeaApplicationConfiguration config = ((TeaWebApplication)Gdx.app).getConfig();
        if(!file.exists()) {
            // Add a way to debug when assets was not loaded in preloader.
            throw new GdxRuntimeException("File is null, it does not exist: " + path);
        }
        byte[] bytes = file.readBytes();
        nativePixmap = new Gdx2DPixmap(bytes, 0, bytes.length, 0);
    }

    public Pixmap(byte[] encodedData, int offset, int len) {
        nativePixmap = new Gdx2DPixmap(encodedData, offset, len, 0);
    }

    public Pixmap(ByteBuffer encodedData, int offset, int len) {
        throw new GdxRuntimeException("Pixmap constructor not supported");
//        if (!encodedData.isDirect()) throw new GdxRuntimeException("Couldn't load pixmap from non-direct ByteBuffer");
//        try {
//            nativePixmap = new Gdx2DPixmapEmu(encodedData, offset, len, 0);
//            initPixmapEmu(-1, -1, null, null);
//        } catch (IOException e) {
//            throw new GdxRuntimeException("Couldn't load pixmap from image data", e);
//        }
    }

    public Pixmap(int width, int height, Format format) {
        nativePixmap = new Gdx2DPixmap(width, height, Format.toGdx2DPixmapFormat(format));
        setColor(0, 0, 0, 0);
        fill();
    }

    @Override
    public Gdx2DPixmapNative getNative() {
        return nativePixmap.getNative();
    }

    public Pixmap(Gdx2DPixmap pixmap) {
        nativePixmap = pixmap;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setColor(float r, float g, float b, float a) {
        this.color = Color.rgba8888(r, g, b, a);
    }

    public void setColor(Color color) {
        setColor(color.r, color.g, color.b, color.a);
    }

    public void fill() {
        nativePixmap.clear(color);
    }

    public void drawLine(int x, int y, int x2, int y2) {
        nativePixmap.drawLine(x, y, x2, y2, color);
    }

    public void drawRectangle(int x, int y, int width, int height) {
        nativePixmap.drawRect(x, y, width, height, color);
    }

    public void drawPixmap(Pixmap pixmap, int x, int y) {
        drawPixmap(pixmap, x, y, 0, 0, pixmap.getWidth(), pixmap.getHeight());
    }

    public void drawPixmap(Pixmap pixmap, int x, int y, int srcx, int srcy, int srcWidth, int srcHeight) {
        nativePixmap.drawPixmap(pixmap.nativePixmap, srcx, srcy, x, y, srcWidth, srcHeight);
    }

    public void drawPixmap(Pixmap pixmap, int srcx, int srcy, int srcWidth, int srcHeight, int dstx, int dsty, int dstWidth, int dstHeight) {
        nativePixmap.drawPixmap(pixmap.nativePixmap, srcx, srcy, srcWidth, srcHeight, dstx, dsty, dstWidth, dstHeight);
    }

    public void fillRectangle(int x, int y, int width, int height) {
        nativePixmap.fillRect(x, y, width, height, color);
    }

    public void drawCircle(int x, int y, int radius) {
        nativePixmap.drawCircle(x, y, radius, color);
    }

    public void fillCircle(int x, int y, int radius) {
        nativePixmap.fillCircle(x, y, radius, color);
    }

    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        nativePixmap.fillTriangle(x1, y1, x2, y2, x3, y3, color);
    }

    public int getWidth() {
        return nativePixmap.getWidth();
    }

    public int getHeight() {
        return nativePixmap.getHeight();
    }

    @Override
    public void dispose() {
        if (disposed) throw new GdxRuntimeException("Pixmap already disposed!");
        nativePixmap.dispose();
        disposed = true;
    }

    public boolean isDisposed () {
        return disposed;
    }

    public int getGLFormat () {
        return nativePixmap.getGLFormat();
    }

    public int getGLInternalFormat () {
        return nativePixmap.getGLInternalFormat();
    }

    public int getGLType () {
        return nativePixmap.getGLType();
    }

    public ByteBuffer getPixels() {
        return nativePixmap.getBuffer();
    }

    public void setPixels(ByteBuffer pixels) {
        if (!pixels.isDirect())
            throw new GdxRuntimeException("Couldn't setPixels from non-direct ByteBuffer");
        ByteBuffer buffer = nativePixmap.getBuffer();
        BufferUtils.copy(pixels, buffer, buffer.limit());
    }

    public int getPixel(int x, int y) {
        return nativePixmap.getPixel(x, y);
    }

    public void drawPixel(int x, int y) {
        nativePixmap.setPixel(x, y, color);
    }

    public void drawPixel(int x, int y, int color) {
        nativePixmap.setPixel(x, y, color);
    }

    public Format getFormat () {
        return Format.fromGdx2DPixmapFormat(nativePixmap.getFormat());
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
        nativePixmap.setScale(filter == Filter.NearestNeighbour ? Gdx2DPixmap.GDX2D_SCALE_NEAREST : Gdx2DPixmap.GDX2D_SCALE_LINEAR);
    }

    public Filter getFilter() {
        return filter;
    }

    public void setBlending(Blending blending) {
        this.blending = blending;
        nativePixmap.setBlend(blending == Blending.None ? 0 : 1);
    }

    public Blending getBlending () {
        return blending;
    }

    public enum Blending {
        None, SourceOver
    }

    public enum Filter {
        NearestNeighbour, BiLinear
    }

    public interface DownloadPixmapResponseListener {

        /** Called on the render thread when image was downloaded successfully.
         * @param pixmap */
        void downloadComplete (Pixmap pixmap);

        /** Called when image download failed. This might get called on a background thread. */
        void downloadFailed (Throwable t);
    }
}