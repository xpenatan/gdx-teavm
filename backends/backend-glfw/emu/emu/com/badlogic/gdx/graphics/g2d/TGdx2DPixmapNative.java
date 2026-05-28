package emu.com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.ByteBuffer;
import org.teavm.interop.Address;
import org.teavm.interop.Import;
import org.teavm.interop.Structure;
import org.teavm.interop.c.Include;

@Include("gdx2d.h")
public class TGdx2DPixmapNative implements Disposable {
    int width;
    int height;
    int format;

    private ByteBuffer buffer;
    private byte[] pixels;

    private GDX2D_pixmap gdx2DPixmap;

    public TGdx2DPixmapNative(byte[] encodedData, int offset, int len, int requestedFormat) {
        int[] compactData = new int[len];
        for (int i = 0; i < len; i++) {
            compactData[i] = encodedData[offset + i] & 0xff;
        }
        gdx2DPixmap = gdx2d_load_teavm_bytes(Address.ofData(compactData), 0, len);
        if (gdx2DPixmap == null) {
            throw new GdxRuntimeException("Couldn't load pixmap: " + gdx2d_get_failure_reason());
        }
        this.width = gdx2DPixmap.width;
        this.height = gdx2DPixmap.height;
        this.format = gdx2DPixmap.format;
        createBuffer();
        copyHeapToBuffer();
        if (requestedFormat != 0 && requestedFormat != format) {
            convert(requestedFormat);
        }
    }

    /**
     * @throws GdxRuntimeException if allocation failed.
     */
    public TGdx2DPixmapNative(int width, int height, int format) throws GdxRuntimeException {
        gdx2DPixmap = gdx2d_new(width, height, format);
        if (gdx2DPixmap == null) {
            throw new GdxRuntimeException("Couldn't allocate pixmap");
        }
        this.width = gdx2DPixmap.width;
        this.height = gdx2DPixmap.height;
        this.format = format;
        createBuffer();
        copyHeapToBuffer();
    }

    private void convert (int requestedFormat) {
        TGdx2DPixmapNative pixmap = new TGdx2DPixmapNative(width, height, requestedFormat);
        pixmap.setBlend(TGdx2DPixmap.GDX2D_BLEND_NONE);
        pixmap.drawPixmap(this, 0, 0, 0, 0, width, height);

        GDX2D_pixmap oldPixmap = gdx2DPixmap;
        gdx2DPixmap = pixmap.gdx2DPixmap;
        this.format = pixmap.format;
        this.height = pixmap.height;
        this.width = pixmap.width;
        this.pixels = pixmap.pixels;
        this.buffer = pixmap.buffer;

        pixmap.gdx2DPixmap = null;
        pixmap.pixels = null;
        pixmap.buffer = null;

        gdx2d_free(oldPixmap);
    }

    private void createBuffer() {
        int size = width * height * gdx2d_bytes_per_pixel(format);
        pixels = new byte[size];
        buffer = ByteBuffer.wrap(pixels);
    }

    private void copyHeapToBuffer() {
        if (gdx2DPixmap == null || pixels == null) {
            return;
        }
        gdx2d_copy_pixels(gdx2DPixmap, Address.ofData(pixels));
        buffer.position(0);
        buffer.limit(pixels.length);
    }

    public void copyToHeap() {
        if (gdx2DPixmap == null || pixels == null) {
            return;
        }
        gdx2d_set_pixels(gdx2DPixmap, Address.ofData(pixels));
    }

    @Override
    public void dispose() {
        if (gdx2DPixmap != null) {
            gdx2d_free(gdx2DPixmap);
            gdx2DPixmap = null;
        }
        buffer = null;
        pixels = null;
    }

    public void clear(int color) {
        gdx2d_clear(gdx2DPixmap, color);
        copyHeapToBuffer();
    }

    public void setPixel(int x, int y, int color) {
        copyToHeap();
        gdx2d_set_pixel(gdx2DPixmap, x, y, color);
        copyHeapToBuffer();
    }

    public int getPixel(int x, int y) {
        copyToHeap();
        return gdx2d_get_pixel(gdx2DPixmap, x, y);
    }

    public void drawLine(int x, int y, int x2, int y2, int color) {
        copyToHeap();
        gdx2d_draw_line(gdx2DPixmap, x, y, x2, y2, color);
        copyHeapToBuffer();
    }

    public void drawRect(int x, int y, int width, int height, int color) {
        copyToHeap();
        gdx2d_draw_rect(gdx2DPixmap, x, y, width, height, color);
        copyHeapToBuffer();
    }

    public void drawCircle(int x, int y, int radius, int color) {
        copyToHeap();
        gdx2d_draw_circle(gdx2DPixmap, x, y, radius, color);
        copyHeapToBuffer();
    }

    public void fillRect(int x, int y, int width, int height, int color) {
        copyToHeap();
        gdx2d_fill_rect(gdx2DPixmap, x, y, width, height, color);
        copyHeapToBuffer();
    }

    public void fillCircle(int x, int y, int radius, int color) {
        copyToHeap();
        gdx2d_fill_circle(gdx2DPixmap, x, y, radius, color);
        copyHeapToBuffer();
    }

    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        copyToHeap();
        gdx2d_fill_triangle(gdx2DPixmap, x1, y1, x2, y2, x3, y3, color);
        copyHeapToBuffer();
    }

    public void drawPixmap(TGdx2DPixmapNative src, int srcX, int srcY, int dstX, int dstY, int width, int height) {
        copyToHeap();
        src.copyToHeap();
        gdx2d_draw_pixmap(src.gdx2DPixmap, gdx2DPixmap, srcX, srcY, width, height, dstX, dstY, width, height);
        copyHeapToBuffer();
    }

    public void drawPixmap(TGdx2DPixmapNative src, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth, int dstHeight) {
        copyToHeap();
        src.copyToHeap();
        gdx2d_draw_pixmap(src.gdx2DPixmap, gdx2DPixmap, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);
        copyHeapToBuffer();
    }

    public void setBlend(int blend) {
        gdx2d_set_blend(gdx2DPixmap, blend);
    }

    public void setScale(int scale) {
        gdx2d_set_scale(gdx2DPixmap, scale);
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

    public static class GDX2D_pixmap extends Structure {
        public int width;
        public int height;
        public int format;
        public int blend;
        public int scale;
        public Address pixels;
    }

    @Import(name = "gdx2d_load")
    private static native GDX2D_pixmap gdx2d_load(Address address, int len);

    @Import(name = "gdx2d_load_teavm_bytes")
    private static native GDX2D_pixmap gdx2d_load_teavm_bytes(Address address, int offset, int len);

    @Import(name = "gdx2d_new")
    private static native GDX2D_pixmap gdx2d_new(int width, int height, int format);

    @Import(name = "gdx2d_draw_pixmap")
    private static native GDX2D_pixmap gdx2d_draw_pixmap(
            GDX2D_pixmap src_pixmap, GDX2D_pixmap dst_pixmap,
            int src_x, int src_y, int src_width, int src_height,
            int dst_x, int dst_y, int dst_width, int dst_height
    );

    @Import(name = "gdx2d_free")
    private static native void gdx2d_free(GDX2D_pixmap pixmap);

    @Import(name = "gdx2d_copy_pixels")
    private static native void gdx2d_copy_pixels(GDX2D_pixmap pixmap, Address dst);

    @Import(name = "gdx2d_set_pixels")
    private static native void gdx2d_set_pixels(GDX2D_pixmap pixmap, Address src);

    @Import(name = "gdx2d_bytes_per_pixel")
    private static native int gdx2d_bytes_per_pixel(int format);

    @Import(name = "gdx2d_get_failure_reason")
    private static native String gdx2d_get_failure_reason();

    @Import(name = "gdx2d_clear")
    private static native void gdx2d_clear(GDX2D_pixmap pixmap, int color);

    @Import(name = "gdx2d_set_pixel")
    private static native void gdx2d_set_pixel(GDX2D_pixmap pixmap, int x, int y, int color);

    @Import(name = "gdx2d_get_pixel")
    private static native int gdx2d_get_pixel(GDX2D_pixmap pixmap, int x, int y);

    @Import(name = "gdx2d_draw_line")
    private static native void gdx2d_draw_line(GDX2D_pixmap pixmap, int x, int y, int x2, int y2, int color);

    @Import(name = "gdx2d_draw_rect")
    private static native void gdx2d_draw_rect(GDX2D_pixmap pixmap, int x, int y, int width, int height, int color);

    @Import(name = "gdx2d_draw_circle")
    private static native void gdx2d_draw_circle(GDX2D_pixmap pixmap, int x, int y, int radius, int color);

    @Import(name = "gdx2d_fill_rect")
    private static native void gdx2d_fill_rect(GDX2D_pixmap pixmap, int x, int y, int width, int height, int color);

    @Import(name = "gdx2d_fill_circle")
    private static native void gdx2d_fill_circle(GDX2D_pixmap pixmap, int x, int y, int radius, int color);

    @Import(name = "gdx2d_fill_triangle")
    private static native void gdx2d_fill_triangle(GDX2D_pixmap pixmap, int x1, int y1, int x2, int y2, int x3, int y3, int color);

    @Import(name = "gdx2d_set_blend")
    private static native void gdx2d_set_blend(GDX2D_pixmap pixmap, int blend);

    @Import(name = "gdx2d_set_scale")
    private static native void gdx2d_set_scale(GDX2D_pixmap pixmap, int scale);
}
