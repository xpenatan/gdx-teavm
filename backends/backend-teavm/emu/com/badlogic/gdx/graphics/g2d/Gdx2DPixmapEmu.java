package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int32ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Uint8ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;
import java.io.InputStream;
import org.teavm.jso.JSBody;

@Emulate(Gdx2DPixmap.class)
public class Gdx2DPixmapEmu implements Disposable {
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

    int basePtr;
    int width;
    int height;
    int format;
    int heapStartIndex;
    int heapEndIndex;

    Int32ArrayWrapper nativeData;

    public Gdx2DPixmapEmu(byte[] encodedData, int offset, int len, int requestedFormat) {
        nativeData = loadNative(encodedData, offset, len);
        updateNativeData();

        if(requestedFormat != 0 && requestedFormat != format) {
            convert(requestedFormat);
        }
    }

    /**
     * @throws GdxRuntimeException if allocation failed.
     */
    public Gdx2DPixmapEmu(int width, int height, int format) throws GdxRuntimeException {
        nativeData = newPixmapNative(width, height, format);
        updateNativeData();
    }

    private void updateNativeData() {
        this.basePtr = nativeData.get(0);
        this.width = nativeData.get(1);
        this.height = nativeData.get(2);
        this.format = nativeData.get(3);
        this.heapStartIndex = nativeData.get(4);
        this.heapEndIndex = nativeData.get(5);
    }

//    public Gdx2DPixmapEmu(ByteBuffer encodedData, int offset, int len, int requestedFormat) throws IOException {
////        if(!encodedData.isDirect()) throw new IOException("Couldn't load pixmap from non-direct ByteBuffer");
////        pixelPtr = loadByteBuffer(nativeData, encodedData, offset, len);
////        if(pixelPtr == null) throw new IOException("Error loading pixmap: " + getFailureReason());
////
////        basePtr = nativeData[0];
////        width = (int)nativeData[1];
////        height = (int)nativeData[2];
////        format = (int)nativeData[3];
////
////        if(requestedFormat != 0 && requestedFormat != format) {
////            convert(requestedFormat);
////        }
//    }
//
//    public Gdx2DPixmapEmu(InputStream in, int requestedFormat) throws IOException {
////        ByteArrayOutputStream bytes = new ByteArrayOutputStream(1024);
////        byte[] buffer = new byte[1024];
////        int readBytes = 0;
////
////        while((readBytes = in.read(buffer)) != -1) {
////            bytes.write(buffer, 0, readBytes);
////        }
////
////        buffer = bytes.toByteArray();
////        pixelPtr = load(nativeData, buffer, 0, buffer.length);
////        load(nativeData, buffer, 0, buffer.length);
////        if(pixelPtr == null) throw new IOException("Error loading pixmap: " + getFailureReason());
////
////        basePtr = nativeData[0];
////        width = (int)nativeData[1];
////        height = (int)nativeData[2];
////        format = (int)nativeData[3];
////
////        if(requestedFormat != 0 && requestedFormat != format) {
////            convert(requestedFormat);
////        }
//    }
//
//    public Gdx2DPixmapEmu(ByteBuffer pixelPtr, int[] nativeData) {
////        this.pixelPtr = pixelPtr;
////        this.basePtr = nativeData[0];
////        this.width = (int)nativeData[1];
////        this.height = (int)nativeData[2];
////        this.format = (int)nativeData[3];
//    }

    private void convert(int requestedFormat) {
        Gdx2DPixmapEmu pixmap = new Gdx2DPixmapEmu(width, height, requestedFormat);
        pixmap.setBlend(GDX2D_BLEND_NONE);
        pixmap.drawPixmap(this, 0, 0, 0, 0, width, height);
        dispose();
        this.basePtr = pixmap.basePtr;
        this.format = pixmap.format;
        this.width = pixmap.width;
        this.height = pixmap.height;
        this.nativeData = pixmap.nativeData;
        this.heapStartIndex = pixmap.heapStartIndex;
        this.heapEndIndex = pixmap.heapEndIndex;
    }

    @Override
    public void dispose() {
        free(basePtr);
    }

    public void clear(int color) {
        clear(basePtr, color);
    }

    public void setPixel(int x, int y, int color) {
        setPixel(basePtr, x, y, color);
    }

    public int getPixel(int x, int y) {
        return getPixel(basePtr, x, y);
    }

    public void drawLine(int x, int y, int x2, int y2, int color) {
        drawLine(basePtr, x, y, x2, y2, color);
    }

    public void drawRect(int x, int y, int width, int height, int color) {
        drawRect(basePtr, x, y, width, height, color);
    }

    public void drawCircle(int x, int y, int radius, int color) {
        drawCircle(basePtr, x, y, radius, color);
    }

    public void fillRect(int x, int y, int width, int height, int color) {
        fillRect(basePtr, x, y, width, height, color);
    }

    public void fillCircle(int x, int y, int radius, int color) {
        fillCircle(basePtr, x, y, radius, color);
    }

    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        fillTriangle(basePtr, x1, y1, x2, y2, x3, y3, color);
    }

    public void drawPixmap(Gdx2DPixmapEmu src, int srcX, int srcY, int dstX, int dstY, int width, int height) {
        drawPixmap(src.basePtr, basePtr, srcX, srcY, width, height, dstX, dstY, width, height);
    }

    public void drawPixmap(Gdx2DPixmapEmu src, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth, int dstHeight) {
        drawPixmap(src.basePtr, basePtr, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);
    }

    public void setBlend(int blend) {
        setBlend(basePtr, blend);
    }

    public void setScale(int scale) {
        setScale(basePtr, scale);
    }

    public static Gdx2DPixmapEmu newPixmap(InputStream in, int requestedFormat) {
        throw new GdxRuntimeException("newPixmap not supported 1");
        //        try {
//            return new Gdx2DPixmapEmu(in, requestedFormat);
//        } catch(IOException e) {
//            return null;
//        }
    }

    public static Gdx2DPixmapEmu newPixmap(int width, int height, int format) {
        try {
            return new Gdx2DPixmapEmu(width, height, format);
        } catch(IllegalArgumentException e) {
            return null;
        }
    }

    public Uint8ArrayWrapper getPixels(boolean shouldCopy) {
        return getHeapData(shouldCopy);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getFormat() {
        return format;
    }

    public int getGLInternalFormat() {
        return toGlFormat(format);
    }

    public int getGLFormat() {
        return getGLInternalFormat();
    }

    public int getGLType() {
        return toGlType(format);
    }

    public String getFormatString() {
        return getFormatString(format);
    }

    public Uint8ArrayWrapper getHeapData(boolean shouldCopy) {
        if(heapStartIndex == 0 && heapEndIndex == 0) {
            return null;
        }
        return getHeapDataNative(shouldCopy, heapStartIndex, heapEndIndex);
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

    @JSBody(params = {"shouldCopy", "heapStartIndex", "heapEndIndex"}, script = "" +
            "var heapArray = Gdx.HEAPU8.subarray(heapStartIndex, heapEndIndex);" +
            "if(shouldCopy) {" +
            "   var newArray = new Uint8Array(heapArray);" +
            "   return newArray;" +
            "}" +
            "return heapArray;"
    )
    private static native Uint8ArrayWrapper getHeapDataNative(boolean shouldCopy, int heapStartIndex, int heapEndIndex);

    // @off
    /*JNI
    #include <gdx2d/gdx2d.h>
    #include <stdlib.h>
     */

    @JSBody(params = {"buffer", "offset", "len"}, script = "" +
            "var cBufferSize = buffer.length * Uint8Array.BYTES_PER_ELEMENT;" +
            "var cBuffer = Gdx._malloc(cBufferSize);" +
            "Gdx.writeArrayToMemory(buffer, cBuffer);" +
            "var pixmap = Gdx.Gdx.prototype.g2d_load(cBuffer, offset, len);" +
            "Gdx._free(cBuffer);" +
            "var pixels = Gdx.Gdx.prototype.g2d_get_pixels(pixmap);" +
            "var pixmapAddr = Gdx.getPointer(pixmap);" +
            "var format = pixmap.get_format();" +
            "var width = pixmap.get_width();" +
            "var height = pixmap.get_height();" +
            "var bytesPerPixel = Gdx.Gdx.prototype.g2d_bytes_per_pixel(format);" +
            "var bytesSize = width * height * bytesPerPixel;" +
            "var startIndex = pixels;" +
            "var endIndex = startIndex + bytesSize;" +
            "var nativeData = new Int32Array(6);" +
            "nativeData[0] = pixmapAddr;" +
            "nativeData[1] = width;" +
            "nativeData[2] = height;" +
            "nativeData[3] = format;" +
            "nativeData[4] = startIndex;" +
            "nativeData[5] = endIndex;" +
            "return nativeData;"
    )
    private static native Int32ArrayWrapper loadNative(byte[] buffer, int offset, int len); /*MANUAL
        const unsigned char* p_buffer = (const unsigned char*)env->GetPrimitiveArrayCritical(buffer, 0);
        gdx2d_pixmap* pixmap = gdx2d_load(p_buffer + offset, len);
        env->ReleasePrimitiveArrayCritical(buffer, (char*)p_buffer, 0);

        if(pixmap==0)
            return 0;

        jobject pixel_buffer = env->NewDirectByteBuffer((void*)pixmap->pixels, pixmap->width * pixmap->height * gdx2d_bytes_per_pixel(pixmap->format));
        jlong* p_native_data = (jlong*)env->GetPrimitiveArrayCritical(nativeData, 0);
        p_native_data[0] = (jlong)pixmap;
        p_native_data[1] = pixmap->width;
        p_native_data[2] = pixmap->height;
        p_native_data[3] = pixmap->format;
        env->ReleasePrimitiveArrayCritical(nativeData, p_native_data, 0);

        return pixel_buffer;
     */

    @JSBody(params = {"width", "height", "format"}, script = "" +
            "var pixmap = Gdx.Gdx.prototype.g2d_new(width, height, format);" +
            "var pixels = Gdx.Gdx.prototype.g2d_get_pixels(pixmap);" +
            "var pixmapAddr = Gdx.getPointer(pixmap);" +
            "var format = pixmap.get_format();" +
            "var width = pixmap.get_width();" +
            "var height = pixmap.get_height();" +
            "var bytesPerPixel = Gdx.Gdx.prototype.g2d_bytes_per_pixel(format);" +
            "var bytesSize = width * height * bytesPerPixel;" +
            "var startIndex = pixels;" +
            "var endIndex = startIndex + bytesSize;" +
            "var nativeData = new Int32Array(6);" +
            "nativeData[0] = pixmapAddr;" +
            "nativeData[1] = width;" +
            "nativeData[2] = height;" +
            "nativeData[3] = format;" +
            "nativeData[4] = startIndex;" +
            "nativeData[5] = endIndex;" +
            "return nativeData;"
    )
    private static native Int32ArrayWrapper newPixmapNative(int width, int height, int format); /*MANUAL
        gdx2d_pixmap* pixmap = gdx2d_new(width, height, format);
        if(pixmap==0)
            return 0;

        jobject pixel_buffer = env->NewDirectByteBuffer((void*)pixmap->pixels, pixmap->width * pixmap->height * gdx2d_bytes_per_pixel(pixmap->format));
        jlong* p_native_data = (jlong*)env->GetPrimitiveArrayCritical(nativeData, 0);
        p_native_data[0] = (jlong)pixmap;
        p_native_data[1] = pixmap->width;
        p_native_data[2] = pixmap->height;
        p_native_data[3] = pixmap->format;
        env->ReleasePrimitiveArrayCritical(nativeData, p_native_data, 0);

        return pixel_buffer;
     */

    @JSBody(params = { "pixmap" }, script = "" +
            "Gdx.Gdx.prototype.g2d_free(pixmap);")
    private static native void free(int pixmap); /*
        gdx2d_free((gdx2d_pixmap*)pixmap);
     */

    @JSBody(params = { "pixmap", "color" }, script = "" +
            "Gdx.Gdx.prototype.g2d_clear(pixmap, color);")
    private static native void clear(int pixmap, int color); /*
        gdx2d_clear((gdx2d_pixmap*)pixmap, color);
     */

    @JSBody(params = { "pixmap", "x", "y", "color" }, script = "" +
            "Gdx.Gdx.prototype.g2d_set_pixel(pixmap, x, y, color);")
    private static native void setPixel(int pixmap, int x, int y, int color); /*
        gdx2d_set_pixel((gdx2d_pixmap*)pixmap, x, y, color);
     */

    @JSBody(params = { "pixmap", "x", "y" }, script = "" +
            "return Gdx.Gdx.prototype.g2d_get_pixel(pixmap, x, y);")
    private static native int getPixel(int pixmap, int x, int y); /*
        return gdx2d_get_pixel((gdx2d_pixmap*)pixmap, x, y);
     */

    @JSBody(params = { "pixmap", "x", "y", "x2", "y2", "color" }, script = "" +
            "Gdx.Gdx.prototype.g2d_draw_line(pixmap, x, y, x2, y2, color);")
    private static native void drawLine(int pixmap, int x, int y, int x2, int y2, int color); /*
        gdx2d_draw_line((gdx2d_pixmap*)pixmap, x, y, x2, y2, color);
     */

    @JSBody(params = { "pixmap", "x", "y", "width", "height", "color" }, script = "" +
            "Gdx.Gdx.prototype.g2d_draw_rect(pixmap, x, y, width, height, color);")
    private static native void drawRect(int pixmap, int x, int y, int width, int height, int color); /*
        gdx2d_draw_rect((gdx2d_pixmap*)pixmap, x, y, width, height, color);
     */

    @JSBody(params = { "pixmap", "x", "y", "radius", "color" }, script = "" +
            "Gdx.Gdx.prototype.g2d_draw_circle(pixmap, x, y, radius, color);")
    private static native void drawCircle(int pixmap, int x, int y, int radius, int color); /*
        gdx2d_draw_circle((gdx2d_pixmap*)pixmap, x, y, radius, color);
     */

    @JSBody(params = { "pixmap", "x", "y", "width", "height", "color" }, script = "" +
            "Gdx.Gdx.prototype.g2d_fill_rect(pixmap, x, y, width, height, color);")
    private static native void fillRect(int pixmap, int x, int y, int width, int height, int color); /*
        gdx2d_fill_rect((gdx2d_pixmap*)pixmap, x, y, width, height, color);
     */

    @JSBody(params = { "pixmap", "x", "y", "radius", "color" }, script = "" +
            "Gdx.Gdx.prototype.g2d_fill_circle(pixmap, x, y, radius, color);")
    private static native void fillCircle(int pixmap, int x, int y, int radius, int color); /*
        gdx2d_fill_circle((gdx2d_pixmap*)pixmap, x, y, radius, color);
     */

    @JSBody(params = { "pixmap", "x1", "y1", "x2", "y2", "x3", "y3", "color" }, script = "" +
            "Gdx.Gdx.prototype.g2d_fill_triangle(pixmap, x1, y1, x2, y2, x3, y3, color);")
    private static native void fillTriangle(int pixmap, int x1, int y1, int x2, int y2, int x3, int y3, int color); /*
        gdx2d_fill_triangle((gdx2d_pixmap*)pixmap, x1, y1, x2, y2, x3, y3, color);
     */

    @JSBody(params = { "src", "dst", "srcX", "srcY", "srcWidth", "srcHeight", "dstX", "dstY", "dstWidth", "dstHeight" }, script = "" +
            "Gdx.Gdx.prototype.g2d_draw_pixmap(src, dst, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);")
    private static native void drawPixmap(int src, int dst, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth, int dstHeight); /*
        gdx2d_draw_pixmap((gdx2d_pixmap*)src, (gdx2d_pixmap*)dst, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);
         */

    @JSBody(params = { "src", "blend" }, script = "" +
            "Gdx.Gdx.prototype.g2d_set_blend(src, blend);")
    private static native void setBlend(int src, int blend); /*
        gdx2d_set_blend((gdx2d_pixmap*)src, blend);
     */

    @JSBody(params = { "src", "scale" }, script = "" +
            "Gdx.Gdx.prototype.g2d_set_scale(src, scale);")
    private static native void setScale(int src, int scale); /*
        gdx2d_set_scale((gdx2d_pixmap*)src, scale);
     */

    public static native String getFailureReason(); /*
     return env->NewStringUTF(gdx2d_get_failure_reason());
     */

    @JSBody(params = { "msg" }, script = "" +
            "console.log(msg);")
    public static native void print(String msg);
}