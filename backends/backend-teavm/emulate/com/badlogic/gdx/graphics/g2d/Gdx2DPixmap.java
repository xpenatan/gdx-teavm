package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Gdx2DPixmap implements Disposable, PixmapNativeInterface {
    public static final int GDX2D_FORMAT_ALPHA = 1;
    public static final int GDX2D_FORMAT_LUMINANCE_ALPHA = 2;
    public static final int GDX2D_FORMAT_RGB888 = 3;
    public static final int GDX2D_FORMAT_RGBA8888 = 4;
    public static final int GDX2D_FORMAT_RGB565 = 5;
    public static final int GDX2D_FORMAT_RGBA4444 = 6;

    public static final int GDX2D_SCALE_NEAREST = 0;
    public static final int GDX2D_SCALE_LINEAR = 1;

    public static final int GDX2D_BLEND_NONE = 0;
    public static final int GDX2D_BLEND_SRC_OVER = 1;

    public static int toGlFormat(int format) {
        switch(format) {
            case GDX2D_FORMAT_ALPHA:
                return GL20.GL_ALPHA;
            case GDX2D_FORMAT_LUMINANCE_ALPHA:
                return GL20.GL_LUMINANCE_ALPHA;
            case GDX2D_FORMAT_RGB888:
            case GDX2D_FORMAT_RGB565:
                return GL20.GL_RGB;
            case GDX2D_FORMAT_RGBA8888:
            case GDX2D_FORMAT_RGBA4444:
                return GL20.GL_RGBA;
            default:
                throw new GdxRuntimeException("unknown format: " + format);
        }
    }

    public static int toGlType(int format) {
        switch(format) {
            case GDX2D_FORMAT_ALPHA:
            case GDX2D_FORMAT_LUMINANCE_ALPHA:
            case GDX2D_FORMAT_RGB888:
            case GDX2D_FORMAT_RGBA8888:
                return GL20.GL_UNSIGNED_BYTE;
            case GDX2D_FORMAT_RGB565:
                return GL20.GL_UNSIGNED_SHORT_5_6_5;
            case GDX2D_FORMAT_RGBA4444:
                return GL20.GL_UNSIGNED_SHORT_4_4_4_4;
            default:
                throw new GdxRuntimeException("unknown format: " + format);
        }
    }

    private Gdx2DPixmapNative nativePixmap;

    public Gdx2DPixmap(byte[] encodedData, int offset, int len, int requestedFormat) {
        nativePixmap = new Gdx2DPixmapNative(encodedData, offset, len, requestedFormat);
    }

    /**
     * @throws GdxRuntimeException if allocation failed.
     */
    public Gdx2DPixmap(int width, int height, int format) throws GdxRuntimeException {
        nativePixmap = new Gdx2DPixmapNative(width, height, format);
    }

    @Override
    public void dispose() {
        nativePixmap.dispose();
    }

    public void clear(int color) {
        nativePixmap.clear(color);
    }

    public void setPixel(int x, int y, int color) {
        nativePixmap.setPixel(x, y, color);
    }

    public int getPixel(int x, int y) {
        return nativePixmap.getPixel(x, y);
    }

    public void drawLine(int x, int y, int x2, int y2, int color) {
        nativePixmap.drawLine(x, y, x2, y2, color);
    }

    public void drawRect(int x, int y, int width, int height, int color) {
        nativePixmap.drawRect(x, y, width, height, color);
    }

    public void drawCircle(int x, int y, int radius, int color) {
        nativePixmap.drawCircle(x, y, radius, color);
    }

    public void fillRect(int x, int y, int width, int height, int color) {
        nativePixmap.fillRect(x, y, width, height, color);
    }

    public void fillCircle(int x, int y, int radius, int color) {
        nativePixmap.fillCircle(x, y, radius, color);
    }

    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        nativePixmap.fillTriangle(x1, y1, x2, y2, x3, y3, color);
    }

    public void drawPixmap(Gdx2DPixmap src, int srcX, int srcY, int dstX, int dstY, int width, int height) {
        nativePixmap.drawPixmap(src.nativePixmap.basePtr, srcX, srcY, dstX, dstY, width, height);
    }

    public void drawPixmap(Gdx2DPixmap src, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth, int dstHeight) {
        nativePixmap.drawPixmap(src.nativePixmap.basePtr, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);
    }

    public void setBlend(int blend) {
        nativePixmap.setBlend(blend);
    }

    public void setScale(int scale) {
        nativePixmap.setScale(scale);
    }

    public static Gdx2DPixmap newPixmap(InputStream in, int requestedFormat) {
        throw new GdxRuntimeException("newPixmap not supported 1");
        //        try {
//            return new Gdx2DPixmapEmu(in, requestedFormat);
//        } catch(IOException e) {
//            return null;
//        }
    }

    public static Gdx2DPixmap newPixmap(int width, int height, int format) {
        try {
            return new Gdx2DPixmap(width, height, format);
        } catch(IllegalArgumentException e) {
            return null;
        }
    }

    public ByteBuffer getBuffer() {
        return nativePixmap.getBuffer();
    }

    public int getHeight() {
        return nativePixmap.height;
    }

    public int getWidth() {
        return nativePixmap.width;
    }

    public int getFormat() {
        return nativePixmap.format;
    }

    public int getGLInternalFormat() {
        return toGlFormat(nativePixmap.format);
    }

    public int getGLFormat() {
        return getGLInternalFormat();
    }

    public int getGLType() {
        return toGlType(nativePixmap.format);
    }

    public String getFormatString() {
        return getFormatString(nativePixmap.format);
    }

    static private String getFormatString(int format) {
        switch(format) {
            case GDX2D_FORMAT_ALPHA:
                return "alpha";
            case GDX2D_FORMAT_LUMINANCE_ALPHA:
                return "luminance alpha";
            case GDX2D_FORMAT_RGB888:
                return "rgb888";
            case GDX2D_FORMAT_RGBA8888:
                return "rgba8888";
            case GDX2D_FORMAT_RGB565:
                return "rgb565";
            case GDX2D_FORMAT_RGBA4444:
                return "rgba4444";
            default:
                return "unknown";
        }
    }

    @Override
    public Gdx2DPixmapNative getNative() {
        return nativePixmap;
    }
}