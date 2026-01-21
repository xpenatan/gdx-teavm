package emu.com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.teavm.backends.glfw.utils.GLFW;
import java.nio.ByteBuffer;
import org.teavm.backend.c.runtime.Memory;
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

    private GDX2D_pixmap gdx2DPixmap;

    public TGdx2DPixmapNative(byte[] encodedData, int offset, int len, int requestedFormat) {
        Address address = Address.ofData(encodedData);
        address.add(offset);
        gdx2DPixmap = gdx2d_load(address, len);
        this.width = gdx2DPixmap.width;
        this.height = gdx2DPixmap.height;
        this.buffer = ByteBuffer.wrap(gdx2DPixmap.pixels);
        this.format = gdx2DPixmap.format;
        if (requestedFormat != 0 && requestedFormat != format) {
            convert(requestedFormat);
        }
    }

    /**
     * @throws GdxRuntimeException if allocation failed.
     */
    public TGdx2DPixmapNative(int width, int height, int format) throws GdxRuntimeException {
        gdx2DPixmap = gdx2d_new(width, height, format);
        this.width = gdx2DPixmap.width;
        this.height = gdx2DPixmap.height;
        this.buffer = ByteBuffer.allocate(width * height * 4);
        this.format = format;
    }

    private void convert (int requestedFormat) {
        TGdx2DPixmapNative pixmap = new TGdx2DPixmapNative(width, height, requestedFormat);
        pixmap.setBlend(TGdx2DPixmap.GDX2D_BLEND_NONE);
        pixmap.drawPixmap(this, 0, 0, 0, 0, width, height);
        dispose();
        this.format = pixmap.format;
        this.height = pixmap.height;
        this.width = pixmap.width;
    }

    private void copyHeapToBuffer() {

    }

    public void copyToHeap() {

    }

    @Override
    public void dispose() {
        if (buffer != null) {
            buffer = null;
        }

        Memory.free(gdx2DPixmap.toAddress());
    }

    public void clear(int color) {
    }

    public void setPixel(int x, int y, int color) {
    }

    public int getPixel(int x, int y) {
        return 0;
    }

    public void drawLine(int x, int y, int x2, int y2, int color) {
    }

    public void drawRect(int x, int y, int width, int height, int color) {
    }

    public void drawCircle(int x, int y, int radius, int color) {
    }

    public void fillRect(int x, int y, int width, int height, int color) {
    }

    public void fillCircle(int x, int y, int radius, int color) {
    }

    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
    }

    public void drawPixmap(TGdx2DPixmapNative src, int srcX, int srcY, int dstX, int dstY, int width, int height) {
        gdx2d_draw_pixmap(src.gdx2DPixmap, gdx2DPixmap, srcX, srcY, width, height, dstX, dstY, width, height);
    }

    public void drawPixmap(TGdx2DPixmapNative src, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth, int dstHeight) {
        gdx2d_draw_pixmap(src.gdx2DPixmap, gdx2DPixmap, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);
    }

    public void setBlend(int blend) {
    }

    public void setScale(int scale) {
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
        public byte[] pixels;
    }
//
//    @Import(name = "gdx2d_load")
//    private static native GDX2D_pixmap gdx2d_load(ByteBuffer buffer, int len);

    @Import(name = "gdx2d_load")
    private static native GDX2D_pixmap gdx2d_load(Address address, int len);

    @Import(name = "gdx2d_new")
    private static native GDX2D_pixmap gdx2d_new(int width, int height, int format);

    @Import(name = "gdx2d_draw_pixmap")
    private static native GDX2D_pixmap gdx2d_draw_pixmap(
            GDX2D_pixmap src_pixmap, GDX2D_pixmap dst_pixmap,
            int src_x, int src_y, int src_width, int src_height,
            int dst_x, int dst_y, int dst_width, int dst_height
    );
}