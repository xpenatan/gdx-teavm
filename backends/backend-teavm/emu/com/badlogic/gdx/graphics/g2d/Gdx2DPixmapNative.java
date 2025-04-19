package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;
import java.nio.ByteBuffer;
import org.teavm.jso.JSBody;
import org.teavm.jso.typedarrays.Int32Array;
import org.teavm.jso.typedarrays.Int8Array;

public class Gdx2DPixmapNative implements Disposable {

    int basePtr;
    int width;
    int height;
    int format;
    int heapStartIndex;
    int heapEndIndex;

    private Int32Array nativeData;
    private ByteBuffer buffer;

    public Gdx2DPixmapNative(byte[] encodedData, int offset, int len, int requestedFormat) {
        nativeData = loadNative(encodedData, offset, len);
        updateNativeData();

        if(requestedFormat != 0 && requestedFormat != format) {
            convert(requestedFormat);
        }
    }

    /**
     * @throws GdxRuntimeException if allocation failed.
     */
    public Gdx2DPixmapNative(int width, int height, int format) throws GdxRuntimeException {
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
        copyHeapToBuffer();
    }

    private void copyHeapToBuffer() {
        Int8Array heapData = getHeapData(false);
        if(buffer == null) {
            int length = heapData.getLength();
            buffer = ByteBuffer.allocateDirect(length);
        }
        TypedArrays.copy(heapData, buffer);
    }

    private void convert(int requestedFormat) {
        Gdx2DPixmapNative pixmap = new Gdx2DPixmapNative(width, height, requestedFormat);
        pixmap.setBlend(Gdx2DPixmap.GDX2D_BLEND_NONE);
        pixmap.drawPixmap(basePtr, 0, 0, 0, 0, width, height);
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
        buffer = null;
        nativeData = null;
    }

    public void clear(int color) {
        clear(basePtr, color);
        copyHeapToBuffer();
    }

    public void setPixel(int x, int y, int color) {
        setPixel(basePtr, x, y, color);
        copyHeapToBuffer();
    }

    public int getPixel(int x, int y) {
        return getPixel(basePtr, x, y);
    }

    public void drawLine(int x, int y, int x2, int y2, int color) {
        drawLine(basePtr, x, y, x2, y2, color);
        copyHeapToBuffer();
    }

    public void drawRect(int x, int y, int width, int height, int color) {
        drawRect(basePtr, x, y, width, height, color);
        copyHeapToBuffer();
    }

    public void drawCircle(int x, int y, int radius, int color) {
        drawCircle(basePtr, x, y, radius, color);
        copyHeapToBuffer();
    }

    public void fillRect(int x, int y, int width, int height, int color) {
        fillRect(basePtr, x, y, width, height, color);
        copyHeapToBuffer();
    }

    public void fillCircle(int x, int y, int radius, int color) {
        fillCircle(basePtr, x, y, radius, color);
        copyHeapToBuffer();
    }

    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        fillTriangle(basePtr, x1, y1, x2, y2, x3, y3, color);
        copyHeapToBuffer();
    }

    public void drawPixmap(int basePtr, int srcX, int srcY, int dstX, int dstY, int width, int height) {
        drawPixmap(basePtr, basePtr, srcX, srcY, width, height, dstX, dstY, width, height);
        copyHeapToBuffer();
    }

    public void drawPixmap(int basePtr, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth, int dstHeight) {
        drawPixmap(basePtr, basePtr, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);
        copyHeapToBuffer();
    }

    public void setBlend(int blend) {
        setBlend(basePtr, blend);
        copyHeapToBuffer();
    }

    public void setScale(int scale) {
        setScale(basePtr, scale);
        copyHeapToBuffer();
    }

    public ByteBuffer getBuffer() {
        return buffer;
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

    public Int8Array getHeapData(boolean shouldCopy) {
        if(heapStartIndex == 0 && heapEndIndex == 0) {
            return null;
        }
        return getHeapDataNative(shouldCopy, heapStartIndex, heapEndIndex);
    }

    @JSBody(params = {"shouldCopy", "heapStartIndex", "heapEndIndex"}, script = "" +
            "var heapArray = Gdx.HEAP8.subarray(heapStartIndex, heapEndIndex);" +
            "if(shouldCopy) {" +
            "   var newArray = new Int8Array(heapArray);" +
            "   return newArray;" +
            "}" +
            "return heapArray;"
    )
    public static native Int8Array getHeapDataNative(boolean shouldCopy, int heapStartIndex, int heapEndIndex);

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
    public static native Int32Array loadNative(byte[] buffer, int offset, int len); /*MANUAL
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
    public static native Int32Array newPixmapNative(int width, int height, int format); /*MANUAL
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
    public static native void free(int pixmap); /*
        gdx2d_free((gdx2d_pixmap*)pixmap);
     */

    @JSBody(params = { "pixmap", "color" }, script = "" +
            "Gdx.Gdx.prototype.g2d_clear(pixmap, color);")
    public static native void clear(int pixmap, int color); /*
        gdx2d_clear((gdx2d_pixmap*)pixmap, color);
     */

    @JSBody(params = { "pixmap", "x", "y", "color" }, script = "" +
            "Gdx.Gdx.prototype.g2d_set_pixel(pixmap, x, y, color);")
    public static native void setPixel(int pixmap, int x, int y, int color); /*
        gdx2d_set_pixel((gdx2d_pixmap*)pixmap, x, y, color);
     */

    @JSBody(params = { "pixmap", "x", "y" }, script = "" +
            "return Gdx.Gdx.prototype.g2d_get_pixel(pixmap, x, y);")
    public static native int getPixel(int pixmap, int x, int y); /*
        return gdx2d_get_pixel((gdx2d_pixmap*)pixmap, x, y);
     */

    @JSBody(params = { "pixmap", "x", "y", "x2", "y2", "color" }, script = "" +
            "Gdx.Gdx.prototype.g2d_draw_line(pixmap, x, y, x2, y2, color);")
    public static native void drawLine(int pixmap, int x, int y, int x2, int y2, int color); /*
        gdx2d_draw_line((gdx2d_pixmap*)pixmap, x, y, x2, y2, color);
     */

    @JSBody(params = { "pixmap", "x", "y", "width", "height", "color" }, script = "" +
            "Gdx.Gdx.prototype.g2d_draw_rect(pixmap, x, y, width, height, color);")
    public static native void drawRect(int pixmap, int x, int y, int width, int height, int color); /*
        gdx2d_draw_rect((gdx2d_pixmap*)pixmap, x, y, width, height, color);
     */

    @JSBody(params = { "pixmap", "x", "y", "radius", "color" }, script = "" +
            "Gdx.Gdx.prototype.g2d_draw_circle(pixmap, x, y, radius, color);")
    public static native void drawCircle(int pixmap, int x, int y, int radius, int color); /*
        gdx2d_draw_circle((gdx2d_pixmap*)pixmap, x, y, radius, color);
     */

    @JSBody(params = { "pixmap", "x", "y", "width", "height", "color" }, script = "" +
            "Gdx.Gdx.prototype.g2d_fill_rect(pixmap, x, y, width, height, color);")
    public static native void fillRect(int pixmap, int x, int y, int width, int height, int color); /*
        gdx2d_fill_rect((gdx2d_pixmap*)pixmap, x, y, width, height, color);
     */

    @JSBody(params = { "pixmap", "x", "y", "radius", "color" }, script = "" +
            "Gdx.Gdx.prototype.g2d_fill_circle(pixmap, x, y, radius, color);")
    public static native void fillCircle(int pixmap, int x, int y, int radius, int color); /*
        gdx2d_fill_circle((gdx2d_pixmap*)pixmap, x, y, radius, color);
     */

    @JSBody(params = { "pixmap", "x1", "y1", "x2", "y2", "x3", "y3", "color" }, script = "" +
            "Gdx.Gdx.prototype.g2d_fill_triangle(pixmap, x1, y1, x2, y2, x3, y3, color);")
    public static native void fillTriangle(int pixmap, int x1, int y1, int x2, int y2, int x3, int y3, int color); /*
        gdx2d_fill_triangle((gdx2d_pixmap*)pixmap, x1, y1, x2, y2, x3, y3, color);
     */

    @JSBody(params = { "src", "dst", "srcX", "srcY", "srcWidth", "srcHeight", "dstX", "dstY", "dstWidth", "dstHeight" }, script = "" +
            "Gdx.Gdx.prototype.g2d_draw_pixmap(src, dst, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);")
    public static native void drawPixmap(int src, int dst, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth, int dstHeight); /*
        gdx2d_draw_pixmap((gdx2d_pixmap*)src, (gdx2d_pixmap*)dst, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);
         */

    @JSBody(params = { "src", "blend" }, script = "" +
            "Gdx.Gdx.prototype.g2d_set_blend(src, blend);")
    public static native void setBlend(int src, int blend); /*
        gdx2d_set_blend((gdx2d_pixmap*)src, blend);
     */

    @JSBody(params = { "src", "scale" }, script = "" +
            "Gdx.Gdx.prototype.g2d_set_scale(src, scale);")
    public static native void setScale(int src, int scale); /*
        gdx2d_set_scale((gdx2d_pixmap*)src, scale);
     */

    public static native String getFailureReason(); /*
     return env->NewStringUTF(gdx2d_get_failure_reason());
     */

    @JSBody(params = { "msg" }, script = "" +
            "console.log(msg);")
    public static native void print(String msg);
}