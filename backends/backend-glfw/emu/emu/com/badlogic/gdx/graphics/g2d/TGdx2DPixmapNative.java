package emu.com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.teavm.interop.Address;
import org.teavm.interop.Function;
import org.teavm.interop.Import;
import org.teavm.interop.Structure;

public class TGdx2DPixmapNative implements Disposable {
    int width;
    int height;
    int format;

    private ByteBuffer buffer;

    public TGdx2DPixmapNative(ByteBuffer encodedData, int offset, int len, int requestedFormat) {
        PixmapInfo pixmapInfo = loadNative(encodedData, offset, len);
        this.width = pixmapInfo.width;
        this.height = pixmapInfo.height;
        this.buffer = pixmapInfo.pixels;
        this.format = pixmapInfo.format;

        if (requestedFormat != format && requestedFormat != 0) {
            convertTo(requestedFormat);
        }
    }

    private void convertTo(int requestedFormat) {
        switch (requestedFormat) {
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA8888:
                convertToRGBA8888();
                break;
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA4444:
                convertToRGBA4444();
                break;
            case Gdx2DPixmap.GDX2D_FORMAT_RGB888:
                convertToRGB888();
                break;
            case Gdx2DPixmap.GDX2D_FORMAT_RGB565:
                convertToRGB565();
                break;
            default:
                throw new GdxRuntimeException("Unsupported conversion to " + requestedFormat + "!");
        }
    }

    private void convertToRGBA8888() {
        ByteBuffer newBuffer = ByteBuffer.allocateDirect(width * height * 4);
        byte[] tmp;
        switch (format) {
            case Gdx2DPixmap.GDX2D_FORMAT_RGB888:
                tmp = new byte[3];
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        buffer.get(tmp);
                        newBuffer.put(tmp);
                        newBuffer.put((byte) 0xFF);
                    }
                }
                break;
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA4444:
                tmp = new byte[2];
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        buffer.get(tmp);
                        newBuffer.put((byte) (tmp[0] & 0xF * 255 / 15));
                        newBuffer.put((byte) (tmp[0] << 4 & 0xF * 255 / 15));
                        newBuffer.put((byte) (tmp[1] & 0xF * 255 / 15));
                        newBuffer.put((byte) (tmp[1] << 4 & 0xF * 255 / 15));
                    }
                }
                break;
            case Gdx2DPixmap.GDX2D_FORMAT_RGB565:
                tmp = new byte[2];
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        buffer.get(tmp);
                        int rgb565 = ((tmp[0] & 0xFF) << 8) | (tmp[1] & 0xFF);
                        newBuffer.put((byte) (((rgb565 >> 11) & 0x1F) * 255 / 31));
                        newBuffer.put((byte) (((rgb565 >> 5) & 0x3F) * 255 / 63));
                        newBuffer.put((byte) ((rgb565 & 0x1F) * 255 / 31));
                        newBuffer.put((byte) 0xFF);
                    }
                }
                break;
            default:
                throw new GdxRuntimeException("Unsupported conversion to RGBA8888!");
        }
    }

    private void convertToRGBA4444() {
        ByteBuffer newBuffer = ByteBuffer.allocateDirect(width * height * 2);
        byte[] tmp;
        switch (format) {
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA8888:
                tmp = new byte[4];
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        buffer.get(tmp);
                        newBuffer.put((byte) ((tmp[0] >> 4) | ((tmp[1] >> 4) << 4)));
                        newBuffer.put((byte) ((tmp[2] >> 4) | ((tmp[3] >> 4) << 4)));
                    }
                }
                break;
            case Gdx2DPixmap.GDX2D_FORMAT_RGB888:
                tmp = new byte[3];
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        buffer.get(tmp);
                        newBuffer.put((byte) ((tmp[0] >> 4) | ((tmp[1] >> 4) << 4)));
                        newBuffer.put((byte) ((tmp[2] >> 4) | (0xF << 4)));
                    }
                }
                break;
            case Gdx2DPixmap.GDX2D_FORMAT_RGB565:
                tmp = new byte[2];
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        buffer.get(tmp);
                        int rgb565 = ((tmp[0] & 0xFF) << 8) | (tmp[1] & 0xFF);
                        newBuffer.put((byte) (((rgb565 >> 12) & 0xF) | ((rgb565 >> 8) & 0xF0)));
                        newBuffer.put((byte) (((rgb565 >> 4) & 0xF) | (0xF << 4)));
                    }
                }
                break;
            default:
                throw new GdxRuntimeException("Unsupported conversion to RGBA4444!");
        }
        buffer = newBuffer;
        format = Gdx2DPixmap.GDX2D_FORMAT_RGBA4444;
    }

    private void convertToRGB565() {
        ByteBuffer newBuffer = ByteBuffer.allocateDirect(width * height * 2);
        byte[] tmp;
        switch (format) {
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA8888:
                tmp = new byte[4];
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        buffer.get(tmp);
                        int rgb565 = ((tmp[0] & 0xF8) << 8) | ((tmp[1] & 0xFC) << 3) | (tmp[2] >> 3);
                        newBuffer.put((byte) (rgb565 >> 8));
                        newBuffer.put((byte) rgb565);
                    }
                }
                break;
            case Gdx2DPixmap.GDX2D_FORMAT_RGB888:
                tmp = new byte[3];
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        buffer.get(tmp);
                        int rgb565 = ((tmp[0] & 0xF8) << 8) | ((tmp[1] & 0xFC) << 3) | (tmp[2] >> 3);
                        newBuffer.put((byte) (rgb565 >> 8));
                        newBuffer.put((byte) rgb565);
                    }
                }
                break;
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA4444:
                tmp = new byte[2];
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        buffer.get(tmp);
                        int rgba4444 = ((tmp[0] & 0xFF) << 8) | (tmp[1] & 0xFF);
                        int rgb565 = ((rgba4444 & 0xF000) << 4) | ((rgba4444 & 0x0F00) << 3) | ((rgba4444 & 0x00F0) << 1);
                        newBuffer.put((byte) (rgb565 >> 8));
                        newBuffer.put((byte) rgb565);
                    }
                }
                break;
            default:
                throw new GdxRuntimeException("Unsupported conversion to RGB565!");
        }
        buffer = newBuffer;
        format = Gdx2DPixmap.GDX2D_FORMAT_RGB565;
    }

    private void convertToRGB888() {
        ByteBuffer newBuffer = ByteBuffer.allocateDirect(width * height * 4);
        byte[] tmp;
        switch (format) {
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA8888:
                tmp = new byte[4];
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        buffer.get(tmp);
                        newBuffer.put(tmp, 0, 3);
                    }
                }
                break;
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA4444:
                tmp = new byte[2];
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        buffer.get(tmp);
                        newBuffer.put((byte) (tmp[0] & 0xF * 255 / 15));
                        newBuffer.put((byte) (tmp[0] << 4 & 0xF * 255 / 15));
                        newBuffer.put((byte) (tmp[1] & 0xF * 255 / 15));
                    }
                }
                break;
            case Gdx2DPixmap.GDX2D_FORMAT_RGB565:
                tmp = new byte[2];
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        buffer.get(tmp);
                        int rgb565 = ((tmp[0] & 0xFF) << 8) | (tmp[1] & 0xFF);
                        newBuffer.put((byte) (((rgb565 >> 11) & 0x1F) * 255 / 31));
                        newBuffer.put((byte) (((rgb565 >> 5) & 0x3F) * 255 / 63));
                        newBuffer.put((byte) ((rgb565 & 0x1F) * 255 / 31));
                    }
                }
                break;
            default:
                throw new GdxRuntimeException("Unsupported conversion to RGBA8888!");
        }
    }



    /**
     * @throws GdxRuntimeException if allocation failed.
     */
    public TGdx2DPixmapNative(int width, int height, int format) throws GdxRuntimeException {
        PixmapInfo pixmapInfo = newPixmapNative(width, height, format);
        this.width = pixmapInfo.width;
        this.height = pixmapInfo.height;
        this.buffer = pixmapInfo.pixels;
        this.format = format;
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
    }

    public void clear(int color) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                buffer.position(x + y * width * bytesPerPixel(format));
                writeColor(format, buffer, color);
            }
        }
    }

    public void setPixel(int x, int y, int color) {
        buffer.position(x + y * width * bytesPerPixel(format));
        writeColor(format, buffer, color);
    }

    public int getPixel(int x, int y) {
        buffer.position(x + y * width * bytesPerPixel(format));
        return readColor(format, buffer);
    }

    public void drawLine(int x, int y, int x2, int y2, int color) {
        throw new UnsupportedOperationException("drawLine is not supported in TeaVM C backend");
    }

    public void drawRect(int x, int y, int width, int height, int color) {
        for (int dx = x; dx < x + width; dx++) {
            if (y == 0 || y == height - 1 || dx == x || dx == x + width - 1) {
                setPixel(dx, y, color);
            }
        }
    }

    public void drawCircle(int x, int y, int radius, int color) {
        throw new UnsupportedOperationException("drawCircle is not supported in TeaVM C backend");
    }

    public void fillRect(int x, int y, int width, int height, int color) {
        for (int dx = x; dx < x + width; dx++) {
            for (int dy = y; dy < y + height; dy++) {
                setPixel(dx, dy, color);
            }
        }
    }

    public void fillCircle(int x, int y, int radius, int color) {
        throw new UnsupportedOperationException("fillCircle is not supported in TeaVM C backend");
    }

    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        throw new UnsupportedOperationException("fillTriangle is not supported in TeaVM C backend");
    }

    public void drawPixmap(ByteBuffer buffer, int srcX, int srcY, int dstX, int dstY, int width, int height) {
        for (int dy = 0; dy < height; dy++) {
            for (int dx = 0; dx < width; dx++) {
                int color = readColor(format, buffer.position(srcX + dx + srcY * width * bytesPerPixel(format)));
                setPixel(dstX + dx, dstY + dy, color);
            }
        }
    }

    public void drawPixmap(ByteBuffer buffer, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth, int dstHeight) {
        for (int dy = 0; dy < dstHeight; dy++) {
            for (int dx = 0; dx < dstWidth; dx++) {
                int color = readColor(format, buffer.position(srcX + dx + srcY * srcWidth * bytesPerPixel(format)));
                setPixel(dstX + dx, dstY + dy, color);
            }
        }
    }

    public void setBlend(int blend) {
        throw new UnsupportedOperationException("setBlend is not supported in TeaVM C backend");
    }

    public void setScale(int scale) {
        throw new UnsupportedOperationException("setScale is not supported in TeaVM C backend");
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

    // @off
    /*JNI
    #include <gdx2d/gdx2d.h>
    #include <stdlib.h>
     */

    public static class STBIIOCallbacks extends Structure {
        ReadCallback read;
        SkipCallback skip;
        EofCallback eof;
    }

    public static class ReadCallback extends Function {
        public void read(Address user, Address data, int size) {
        }
    }

    public static class SkipCallback extends Function {
        public void skip(Address user, int n) {
        }
    }

    public static class STBIContext extends Structure {
        public int img_x;
        public int img_y;
        public int img_n;
        public int img_out_n;

        public STBIIOCallbacks io; // stbi_io_callbacks mapped to generic Object
        public Address io_user_data;

        public int read_from_callbacks;
        public int buflen;
        public byte[] buffer_start = new byte[128];
        public int callback_already_read;

        public Address img_buffer;
        public Address img_buffer_end;
        public Address img_buffer_original;
        public Address img_buffer_original_end;
    }

    @Import(name = "stbi__refill_buffer")
    private static native void stbiRefillBuffer(STBIContext s);

    // initialize a memory-decode context
    @Import(name = "stbi__start_mem")
    private static void stbiStartMem(STBIContext s, Address buffer, int len) {
        s.io.read = null;
        s.read_from_callbacks = 0;
        s.callback_already_read = 0;
        s.img_buffer = s.img_buffer_original = buffer;
        s.img_buffer_end = s.img_buffer_original_end = buffer.add(len);
    }

    @Import(name = "stbi__start_callbacks")
    private static native void stbiStartCallbacks(STBIContext s, STBIIOCallbacks io, Address user);

    @Import(name = "stbi__stdio_read")
    private static native int stbiStdioRead(Address user, Address data, int size);

    @Import(name = "stbi__stdio_skip")
    private static native int stbiStdioSkip(Address user, int n);

    @Import(name = "stbi__stdio_eof")
    private static native int stbiStdioEof(Address user);

    @Import(name = "stbi__start_file")
    private static native void stbiStartFile(STBIContext s, String name);

    @Import(name = "stbi__rewind")
    private static native void stbiRewind(STBIContext s);

    public enum STBIOrder {
        STBI_ORDER_RGB,
        STBI_ORDER_BGR;

        public static STBIOrder fromInt(int i) {
            switch (i) {
                case 0:
                    return STBI_ORDER_RGB;
                case 1:
                    return STBI_ORDER_BGR;
                default:
                    throw new GdxRuntimeException("Unknown STBIOrder: " + i);
            }
        }

        public int toInt() {
            switch (this) {
                case STBI_ORDER_RGB:
                    return 0;
                case STBI_ORDER_BGR:
                    return 1;
                default:
                    throw new GdxRuntimeException("Unknown STBIOrder: " + this);
            }
        }
    }

    public static class STBIResultInfo extends Structure {
        public int bits_per_pixel;
        public int num_hannels;
        public int order;
    }

    public static class EofCallback extends Function {

        public int eof(Address user) {
            return 0;
        }
    }

    public static class STBIPng extends Structure {
        STBIContext s;
        byte[] idata;
        byte[] expanded;
        byte[] out;
        int depth;
    }

    @Import(name = "stbi__jpeg_test")
    private static native int stbiJpegTest(STBIContext s);

    @Import(name = "stbi__jpeg_load")
    private static native Address stbiJpegLoad(STBIContext s, Address x, Address y, Address comp, int reqComp, STBIResultInfo ri);

    @Import(name = "stbi__jpeg_info")
    private static native int stbiJpegInfo(STBIContext s, Address x, Address y, Address comp);

    @Import(name = "stbi__png_test")
    private static native int stbiPngTest(STBIContext s);

    @Import(name = "stbi__png_load")
    private static native Address stbiPngLoad(STBIContext s, Address x, Address y, Address comp, int reqComp, STBIResultInfo ri);

    @Import(name = "stbi__png_info")
    private static native int stbiPngInfo(STBIContext s, Address x, Address y, Address comp);

    @Import(name = "stbi__png_is16")
    private static native int stbiPngIs16(STBIContext s);

    @Import(name = "stbi__bmp_test")
    private static native int stbiBmpTest(STBIContext s);

    @Import(name = "stbi__bmp_load")
    private static native Address stbiBmpLoad(STBIContext s, Address x, Address y, Address comp, int reqComp, STBIResultInfo ri);

    @Import(name = "stbi__bmp_info")
    private static native int stbiBmpInfo(STBIContext s, Address x, Address y, Address comp);

    @Import(name = "stbi__tga_test")
    private static native int stbiTgaTest(STBIContext s);

    @Import(name = "stbi__tga_load")
    private static native Address stbiTgaLoad(STBIContext s, Address x, Address y, Address comp, int reqComp, STBIResultInfo ri);

    @Import(name = "stbi__tga_info")
    private static native int stbiTgaInfo(STBIContext s, Address x, Address y, Address comp);

    @Import(name = "stbi__psd_test")
    private static native int stbiPsdTest(STBIContext s);

    @Import(name = "stbi__psd_load")
    private static native Address stbiPsdLoad(STBIContext s, Address x, Address y, Address comp, int reqComp, STBIResultInfo ri, int bpc);

    @Import(name = "stbi__psd_info")
    private static native int stbiPsdInfo(STBIContext s, Address x, Address y, Address comp);

    @Import(name = "stbi__psd_is16")
    private static native int stbiPsdIs16(STBIContext s);

    @Import(name = "stbi__hdr_test")
    private static native int stbiHdrTest(STBIContext s);

    @Import(name = "stbi__hdr_load")
    private static native Address stbiHdrLoad(STBIContext s, Address x, Address y, Address comp, int reqComp, STBIResultInfo ri);

    @Import(name = "stbi__hdr_info")
    private static native int stbiHdrInfo(STBIContext s, Address x, Address y, Address comp);

    @Import(name = "stbi__pic_test")
    private static native int stbiPicTest(STBIContext s);

    @Import(name = "stbi__pic_load")
    private static native Address stbiPicLoad(STBIContext s, Address x, Address y, Address comp, int reqComp, STBIResultInfo ri);

    @Import(name = "stbi__pic_info")
    private static native int stbiPicInfo(STBIContext s, Address x, Address y, Address comp);

    @Import(name = "stbi__gif_test")
    private static native int stbiGifTest(STBIContext s);

    @Import(name = "stbi__gif_load")
    private static native Address stbiGifLoad(STBIContext s, Address x, Address y, Address comp, int reqComp, STBIResultInfo ri);

    @Import(name = "stbi__load_gif_main")
    private static native Address stbiLoadGifMain(STBIContext s, Address delays, Address x, Address y, Address z, Address comp, int reqComp);

    @Import(name = "stbi__gif_info")
    private static native int stbiGifInfo(STBIContext s, Address x, Address y, Address comp);

    @Import(name = "stbi__pnm_test")
    private static native int stbiPnmTest(STBIContext s);

    @Import(name = "stbi__pnm_load")
    private static native Address stbiPnmLoad(STBIContext s, Address x, Address y, Address comp, int reqComp, STBIResultInfo ri);

    @Import(name = "stbi__pnm_info")
    private static native int stbiPnmInfo(STBIContext s, Address x, Address y, Address comp);

    @Import(name = "stbi__pnm_is16")
    private static native int stbiPnmIs16(STBIContext s);

    @Import(name = "stbi_load")
    private static native ByteBuffer stbiLoad(String filename, IntBuffer x, IntBuffer y, IntBuffer comp, int reqComp);

    @Import(name = "stbi_load_from_file")
    private static native ByteBuffer stbiLoadFromFile(String file, IntBuffer x, IntBuffer y, IntBuffer comp, int reqComp);

    @Import(name = "stbi_load_from_file_16")
    private static native ByteBuffer stbiLoadFromFile16(String file, IntBuffer x, IntBuffer y, IntBuffer comp, int reqComp);

    @Import(name = "stbi_load_from_memory")
    private static native ByteBuffer stbiLoadFromMemory(ByteBuffer buffer, int len, IntBuffer x, IntBuffer y, IntBuffer comp, int reqComp);

    @Import(name = "stbi_load_16")
    private static native ByteBuffer stbiLoad16(String filename, IntBuffer x, IntBuffer y, IntBuffer comp, int reqComp);

    public static PixmapInfo loadNative(ByteBuffer buffer, int offset, int len) {
        IntBuffer x = IntBuffer.allocate(1);
        IntBuffer y = IntBuffer.allocate(1);
        IntBuffer comp = IntBuffer.allocate(1);

        ByteBuffer pixels = stbiLoadFromMemory(buffer, len, x, y, comp, 0);
        if (pixels == null || x.get() == 0 || y.get() == 0) {
            throw new GdxRuntimeException("Failed to load image from memory");
        }
        pixels.flip();

        int components = comp.get();
        if (components == 0) {
            throw new GdxRuntimeException("Failed to load image from memory: No components");
        }

        if (components > 4) {
            throw new GdxRuntimeException("Failed to load image from memory: Too many components");
        }

        int format = Gdx2DPixmap.GDX2D_FORMAT_RGBA8888;
        switch (components) {
            case 1: {
                format = Gdx2DPixmap.GDX2D_FORMAT_ALPHA;
                break;
            }
            case 2: {
                format = Gdx2DPixmap.GDX2D_FORMAT_RGBA4444;
                break;
            }
            case 3: {
                format = Gdx2DPixmap.GDX2D_FORMAT_RGB565;
                break;
            }
            case 4: {
                break;
            }
            default: {
                throw new GdxRuntimeException("Failed to load image from memory: Unsupported number of components");
            }
        }

        return new PixmapInfo(x.get(0), y.get(0), format, pixels);
    } /*MANUAL
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

    public static PixmapInfo newPixmapNative(int width, int height, int format) {
        ByteBuffer pixmapData = ByteBuffer.allocate(Address.sizeOf() + Long.SIZE * bytesPerPixel(format));
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixmapData = writeColor(format, pixmapData, 0);
            }
        }

        return new PixmapInfo(width, height, format, pixmapData);
    }

    private static int bytesPerPixel(int format) {
        switch (format) {
            case Gdx2DPixmap.GDX2D_FORMAT_ALPHA:
                return 1;
            case Gdx2DPixmap.GDX2D_FORMAT_LUMINANCE_ALPHA:
                return 2;
            case Gdx2DPixmap.GDX2D_FORMAT_RGB565:
                return 2;
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA4444:
                return 2;
            case Gdx2DPixmap.GDX2D_FORMAT_RGB888:
                return 3;
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA8888:
                return 4;
            default:
                throw new GdxRuntimeException("Unsupported format: " + format);
        }
    }

    private static int toArgb8888(int color, int format) {
        switch (format) {
            case Gdx2DPixmap.GDX2D_FORMAT_ALPHA:
                return color << 24;
            case Gdx2DPixmap.GDX2D_FORMAT_LUMINANCE_ALPHA:
                return (color & 0xFF) << 24 | (color & 0xFF) << 16 | (color & 0xFF) << 8 | 0xFF;
            case Gdx2DPixmap.GDX2D_FORMAT_RGB565:
                return (color & 0xF800) << 8 | (color & 0x07E0) << 3 | (color & 0x001F) << 11 | 0xFF;
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA4444:
                return (color & 0xF000) << 12 | (color & 0x0F00) << 8 | (color & 0x00F0) << 4 | (color & 0x000F) << 12 | 0xFF;
            case Gdx2DPixmap.GDX2D_FORMAT_RGB888:
                return (color & 0xFF0000) >> 16 | (color & 0xFF00) >> 8 | (color & 0xFF) | 0xFF;
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA8888:
                return color;
            default:
                throw new GdxRuntimeException("Unsupported format: " + format);
        }
    }

    private static int toFormat(int color, int format) {
        switch (format) {
            case Gdx2DPixmap.GDX2D_FORMAT_ALPHA:
                return color >> 24;
            case Gdx2DPixmap.GDX2D_FORMAT_LUMINANCE_ALPHA:
                return (color >> 24) & 0xFF;
            case Gdx2DPixmap.GDX2D_FORMAT_RGB565:
                return (color >> 11) & 0x1F | ((color >> 5) & 0x3F) << 5 | ((color) & 0x1F) << 11;
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA4444:
                return (color >> 12) & 0xF | ((color >> 8) & 0xF) << 4 | ((color >> 4) & 0xF) << 8 | ((color) & 0xF) << 12;
            case Gdx2DPixmap.GDX2D_FORMAT_RGB888:
                return (color >> 16) & 0xFF | ((color >> 8) & 0xFF) << 8 | (color & 0xFF) << 16;
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA8888:
                return color;
            default:
                throw new GdxRuntimeException("Unsupported format: " + format);
        }
    }

    public static void free(Address pixmap) {
        long aLong = pixmap.getLong();
        if (aLong != 0L) {
            Address pixmapData = Address.fromLong(aLong);
//            Memory.free(pixmapData);
        }

//        Memory.free(pixmap);
    }

    public void clear(ByteBuffer pixmap, int color) {
    }

    private static ByteBuffer writeColor(long format, ByteBuffer addr, int colorArgb8888) {
        switch ((int) format) {
            case Gdx2DPixmap.GDX2D_FORMAT_ALPHA:
                addr.put((byte) (colorArgb8888 >> 24));
                return addr;
            case Gdx2DPixmap.GDX2D_FORMAT_LUMINANCE_ALPHA:
                addr.put(convertToLuminance(colorArgb8888 >> 8));
                addr.put((byte) (colorArgb8888 & 0xFF));
                return addr;
            case Gdx2DPixmap.GDX2D_FORMAT_RGB565:
                int r = (colorArgb8888 >> 16) & 0xFF;
                int g = (colorArgb8888 >> 8) & 0xFF;
                int b = colorArgb8888 & 0xFF;
                addr.putShort((short) ((r + g * 587 + b * 114) / 1000));
                return addr;
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA4444:
                addr.put((byte) (colorArgb8888 >> 4 & 0xF));
                addr.put((byte) (colorArgb8888 & 0xF));
                return addr;
            case Gdx2DPixmap.GDX2D_FORMAT_RGB888:
                addr.put((byte) (colorArgb8888 >> 16));
                addr.put((byte) (colorArgb8888 >> 8));
                addr.put((byte) (colorArgb8888));
                return addr;
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA8888:
                addr.putInt(colorArgb8888);
                return addr;
            default:
                throw new GdxRuntimeException("Unsupported format: " + format);
        }
    }

    private static int readColor(int format, ByteBuffer addr) {
        switch (format) {
            case Gdx2DPixmap.GDX2D_FORMAT_ALPHA:
                return addr.get() & 0xFF;
            case Gdx2DPixmap.GDX2D_FORMAT_LUMINANCE_ALPHA:
                int luminance = addr.get() & 0xFF;
                int alpha = addr.get() & 0xFF;
                return alpha << 24 | luminance << 16 | luminance << 8 | luminance;
            case Gdx2DPixmap.GDX2D_FORMAT_RGB565:
                int rgb565 = addr.getShort() & 0xFFFF;
                return (rgb565 * 255 / 31) << 16 | (rgb565 * 255 / 63) << 8 | rgb565 * 255 / 31;
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA4444:
                int rgba4444 = addr.get() & 0xF;
                rgba4444 = rgba4444 << 4 | addr.get() & 0xF;
                return rgba4444 << 16 | rgba4444 << 8 | rgba4444;
            case Gdx2DPixmap.GDX2D_FORMAT_RGB888:
                int rgb888 = addr.get() & 0xFF;
                rgb888 = rgb888 << 8 | addr.get() & 0xFF;
                rgb888 = rgb888 << 8 | addr.get() & 0xFF;
                return rgb888;
            case Gdx2DPixmap.GDX2D_FORMAT_RGBA8888:
                return addr.getInt();
            default:
                throw new GdxRuntimeException("Unsupported format: " + format);
        }
    }

    private static byte convertToLuminance(int rgb) {
        return (byte) (rgb * 0.299f + rgb * 0.587f + rgb * 0.114f);
    }

    private static class PixmapInfo {
        private final int width;
        private final int height;
        private final int format;
        private final ByteBuffer pixels;

        public PixmapInfo(int width, int height, int format, ByteBuffer pixels) {
            this.width = width;
            this.height = height;
            this.format = format;
            this.pixels = pixels;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getFormat() {
            return format;
        }

        public ByteBuffer getPixels() {
            return pixels;
        }
    }
}