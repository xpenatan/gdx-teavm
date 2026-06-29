package emu.c.com.badlogic.gdx.graphics.g2d.freetype;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.StreamUtils;
import com.github.xpenatan.gdx.teavm.backends.shared.utils.BufferAddressUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.teavm.interop.Address;
import org.teavm.interop.Import;
import org.teavm.interop.c.Include;

@Include("teavm_freetype.h")
public class FreeType {

    static int getLastErrorCode() {
        return c_FreeType_getLastErrorCode();
    }

    @Import(name = "c_FreeType_getLastErrorCode")
    private static native int c_FreeType_getLastErrorCode();

    private static class Pointer {
        long address;

        Pointer(long address) {
            this.address = address;
        }
    }

    public static class Library extends Pointer implements Disposable {
        LongMap<FontBuffer> fontData = new LongMap<FontBuffer>();

        Library(long address) {
            super(address);
        }

        @Override
        public void dispose() {
            doneFreeType(address);
            for(FontBuffer fontBuffer : fontData.values()) {
                disposeFontBuffer(fontBuffer);
            }
            fontData.clear();
        }

        private static void disposeFontBuffer(FontBuffer fontBuffer) {
            if(fontBuffer != null && fontBuffer.owned && fontBuffer.buffer.isDirect()) {
                BufferUtils.disposeUnsafeByteBuffer(fontBuffer.buffer);
            }
        }

        private static class FontBuffer {
            final ByteBuffer buffer;
            final boolean owned;

            FontBuffer(ByteBuffer buffer, boolean owned) {
                this.buffer = buffer;
                this.owned = owned;
            }
        }

        private static void doneFreeType(long library) {
            c_Library_doneFreeType(library);
        }

        @Import(name = "c_Library_doneFreeType")
        private static native void c_Library_doneFreeType(long library);

        public Face newFace(FileHandle fontFile, int faceIndex) {
            InputStream input = fontFile.read();
            try {
                int fileSize = (int)fontFile.length();
                int estimatedSize = fileSize == 0 ? 1024 * 16 : fileSize;
                byte[] data = StreamUtils.copyStreamToByteArray(input, estimatedSize);
                return newMemoryFace(ByteBuffer.wrap(data), faceIndex, false);
            } catch(IOException ex) {
                throw new GdxRuntimeException(ex);
            } finally {
                StreamUtils.closeQuietly(input);
            }
        }

        public Face newMemoryFace(byte[] data, int dataSize, int faceIndex) {
            int size = Math.min(data.length, dataSize);
            return newMemoryFace(ByteBuffer.wrap(data, 0, size), faceIndex, false);
        }

        public Face newMemoryFace(ByteBuffer buffer, int faceIndex) {
            return newMemoryFace(buffer, faceIndex, false);
        }

        private Face newMemoryFace(ByteBuffer buffer, int faceIndex, boolean owned) {
            if(buffer.isDirect() || !buffer.hasArray()) {
                buffer = copyToHeapBuffer(buffer);
                owned = false;
            }
            long face = newMemoryFace(address, BufferAddressUtils.ofInternal(buffer), buffer.remaining(), faceIndex);
            if(face == 0) {
                if(owned) {
                    BufferUtils.disposeUnsafeByteBuffer(buffer);
                }
                throw new GdxRuntimeException("Couldn't load font, FreeType error code: " + getLastErrorCode());
            }
            fontData.put(face, new FontBuffer(buffer, owned));
            return new Face(face, this);
        }

        private static ByteBuffer copyToHeapBuffer(ByteBuffer source) {
            int position = source.position();
            byte[] data = new byte[source.remaining()];
            source.get(data);
            ((Buffer)source).position(position);
            return ByteBuffer.wrap(data);
        }

        private static long newMemoryFace(long library, Address data, int dataSize, int faceIndex) {
            return c_Library_newMemoryFace(library, data, dataSize, faceIndex);
        }

        @Import(name = "c_Library_newMemoryFace")
        private static native long c_Library_newMemoryFace(long library, Address data, int dataSize, int faceIndex);

        public Stroker createStroker() {
            long stroker = strokerNew(address);
            if(stroker == 0) {
                throw new GdxRuntimeException("Couldn't create FreeType stroker, FreeType error code: " + getLastErrorCode());
            }
            return new Stroker(stroker);
        }

        private static long strokerNew(long library) {
            return c_Library_strokerNew(library);
        }

        @Import(name = "c_Library_strokerNew")
        private static native long c_Library_strokerNew(long library);
    }

    public static class Face extends Pointer implements Disposable {
        Library library;

        public Face(long address, Library library) {
            super(address);
            this.library = library;
        }

        @Override
        public void dispose() {
            doneFace(address);
            Library.FontBuffer fontBuffer = library.fontData.remove(address);
            Library.disposeFontBuffer(fontBuffer);
        }

        private static void doneFace(long face) {
            c_Face_doneFace(face);
        }

        @Import(name = "c_Face_doneFace")
        private static native void c_Face_doneFace(long face);

        public int getFaceFlags() {
            return getFaceFlags(address);
        }

        private static int getFaceFlags(long face) {
            return c_Face_getFaceFlags(face);
        }

        @Import(name = "c_Face_getFaceFlags")
        private static native int c_Face_getFaceFlags(long face);

        public int getStyleFlags() {
            return getStyleFlags(address);
        }

        private static int getStyleFlags(long face) {
            return c_Face_getStyleFlags(face);
        }

        @Import(name = "c_Face_getStyleFlags")
        private static native int c_Face_getStyleFlags(long face);

        public int getNumGlyphs() {
            return getNumGlyphs(address);
        }

        private static int getNumGlyphs(long face) {
            return c_Face_getNumGlyphs(face);
        }

        @Import(name = "c_Face_getNumGlyphs")
        private static native int c_Face_getNumGlyphs(long face);

        public int getAscender() {
            return getAscender(address);
        }

        private static int getAscender(long face) {
            return c_Face_getAscender(face);
        }

        @Import(name = "c_Face_getAscender")
        private static native int c_Face_getAscender(long face);

        public int getDescender() {
            return getDescender(address);
        }

        private static int getDescender(long face) {
            return c_Face_getDescender(face);
        }

        @Import(name = "c_Face_getDescender")
        private static native int c_Face_getDescender(long face);

        public int getHeight() {
            return getHeight(address);
        }

        private static int getHeight(long face) {
            return c_Face_getHeight(face);
        }

        @Import(name = "c_Face_getHeight")
        private static native int c_Face_getHeight(long face);

        public int getMaxAdvanceWidth() {
            return getMaxAdvanceWidth(address);
        }

        private static int getMaxAdvanceWidth(long face) {
            return c_Face_getMaxAdvanceWidth(face);
        }

        @Import(name = "c_Face_getMaxAdvanceWidth")
        private static native int c_Face_getMaxAdvanceWidth(long face);

        public int getMaxAdvanceHeight() {
            return getMaxAdvanceHeight(address);
        }

        private static int getMaxAdvanceHeight(long face) {
            return c_Face_getMaxAdvanceHeight(face);
        }

        @Import(name = "c_Face_getMaxAdvanceHeight")
        private static native int c_Face_getMaxAdvanceHeight(long face);

        public int getUnderlinePosition() {
            return getUnderlinePosition(address);
        }

        private static int getUnderlinePosition(long face) {
            return c_Face_getUnderlinePosition(face);
        }

        @Import(name = "c_Face_getUnderlinePosition")
        private static native int c_Face_getUnderlinePosition(long face);

        public int getUnderlineThickness() {
            return getUnderlineThickness(address);
        }

        private static int getUnderlineThickness(long face) {
            return c_Face_getUnderlineThickness(face);
        }

        @Import(name = "c_Face_getUnderlineThickness")
        private static native int c_Face_getUnderlineThickness(long face);

        public boolean selectSize(int strikeIndex) {
            return selectSize(address, strikeIndex);
        }

        private static boolean selectSize(long face, int strikeIndex) {
            return c_Face_selectSize(face, strikeIndex);
        }

        @Import(name = "c_Face_selectSize")
        private static native boolean c_Face_selectSize(long face, int strikeIndex);

        public boolean setCharSize(int charWidth, int charHeight, int horzResolution, int vertResolution) {
            return setCharSize(address, charWidth, charHeight, horzResolution, vertResolution);
        }

        private static boolean setCharSize(long face, int charWidth, int charHeight, int horzResolution, int vertResolution) {
            return c_Face_setCharSize(face, charWidth, charHeight, horzResolution, vertResolution);
        }

        @Import(name = "c_Face_setCharSize")
        private static native boolean c_Face_setCharSize(long face, int charWidth, int charHeight, int horzResolution, int vertResolution);

        public boolean setPixelSizes(int pixelWidth, int pixelHeight) {
            return setPixelSizes(address, pixelWidth, pixelHeight);
        }

        private static boolean setPixelSizes(long face, int pixelWidth, int pixelHeight) {
            return c_Face_setPixelSizes(face, pixelWidth, pixelHeight);
        }

        @Import(name = "c_Face_setPixelSizes")
        private static native boolean c_Face_setPixelSizes(long face, int pixelWidth, int pixelHeight);

        public boolean loadGlyph(int glyphIndex, int loadFlags) {
            return loadGlyph(address, glyphIndex, loadFlags);
        }

        private static boolean loadGlyph(long face, int glyphIndex, int loadFlags) {
            return c_Face_loadGlyph(face, glyphIndex, loadFlags);
        }

        @Import(name = "c_Face_loadGlyph")
        private static native boolean c_Face_loadGlyph(long face, int glyphIndex, int loadFlags);

        public boolean loadChar(int charCode, int loadFlags) {
            return loadChar(address, charCode, loadFlags);
        }

        private static boolean loadChar(long face, int charCode, int loadFlags) {
            return c_Face_loadChar(face, charCode, loadFlags);
        }

        @Import(name = "c_Face_loadChar")
        private static native boolean c_Face_loadChar(long face, int charCode, int loadFlags);

        public GlyphSlot getGlyph() {
            return new GlyphSlot(getGlyph(address));
        }

        private static long getGlyph(long face) {
            return c_Face_getGlyph(face);
        }

        @Import(name = "c_Face_getGlyph")
        private static native long c_Face_getGlyph(long face);

        public Size getSize() {
            return new Size(getSize(address));
        }

        private static long getSize(long face) {
            return c_Face_getSize(face);
        }

        @Import(name = "c_Face_getSize")
        private static native long c_Face_getSize(long face);

        public boolean hasKerning() {
            return hasKerning(address);
        }

        private static boolean hasKerning(long face) {
            return c_Face_hasKerning(face);
        }

        @Import(name = "c_Face_hasKerning")
        private static native boolean c_Face_hasKerning(long face);

        public int getKerning(int leftGlyph, int rightGlyph, int kernMode) {
            return getKerning(address, leftGlyph, rightGlyph, kernMode);
        }

        private static int getKerning(long face, int leftGlyph, int rightGlyph, int kernMode) {
            return c_Face_getKerning(face, leftGlyph, rightGlyph, kernMode);
        }

        @Import(name = "c_Face_getKerning")
        private static native int c_Face_getKerning(long face, int leftGlyph, int rightGlyph, int kernMode);

        public int getCharIndex(int charCode) {
            return getCharIndex(address, charCode);
        }

        private static int getCharIndex(long face, int charCode) {
            return c_Face_getCharIndex(face, charCode);
        }

        @Import(name = "c_Face_getCharIndex")
        private static native int c_Face_getCharIndex(long face, int charCode);
    }

    public static class Size extends Pointer {
        Size(long address) {
            super(address);
        }

        public SizeMetrics getMetrics() {
            return new SizeMetrics(getMetrics(address));
        }

        private static long getMetrics(long address) {
            return c_Size_getMetrics(address);
        }

        @Import(name = "c_Size_getMetrics")
        private static native long c_Size_getMetrics(long address);
    }

    public static class SizeMetrics extends Pointer {
        SizeMetrics(long address) {
            super(address);
        }

        public int getXppem() {
            return c_SizeMetrics_getXppem(address);
        }

        @Import(name = "c_SizeMetrics_getXppem")
        private static native int c_SizeMetrics_getXppem(long metrics);

        public int getYppem() {
            return c_SizeMetrics_getYppem(address);
        }

        @Import(name = "c_SizeMetrics_getYppem")
        private static native int c_SizeMetrics_getYppem(long metrics);

        public int getXScale() {
            return c_SizeMetrics_getXscale(address);
        }

        @Import(name = "c_SizeMetrics_getXscale")
        private static native int c_SizeMetrics_getXscale(long metrics);

        public int getYscale() {
            return c_SizeMetrics_getYscale(address);
        }

        @Import(name = "c_SizeMetrics_getYscale")
        private static native int c_SizeMetrics_getYscale(long metrics);

        public int getAscender() {
            return c_SizeMetrics_getAscender(address);
        }

        @Import(name = "c_SizeMetrics_getAscender")
        private static native int c_SizeMetrics_getAscender(long metrics);

        public int getDescender() {
            return c_SizeMetrics_getDescender(address);
        }

        @Import(name = "c_SizeMetrics_getDescender")
        private static native int c_SizeMetrics_getDescender(long metrics);

        public int getHeight() {
            return c_SizeMetrics_getHeight(address);
        }

        @Import(name = "c_SizeMetrics_getHeight")
        private static native int c_SizeMetrics_getHeight(long metrics);

        public int getMaxAdvance() {
            return c_SizeMetrics_getMaxAdvance(address);
        }

        @Import(name = "c_SizeMetrics_getMaxAdvance")
        private static native int c_SizeMetrics_getMaxAdvance(long metrics);
    }

    public static class GlyphSlot extends Pointer {
        GlyphSlot(long address) {
            super(address);
        }

        public GlyphMetrics getMetrics() {
            return new GlyphMetrics(getMetrics(address));
        }

        private static long getMetrics(long slot) {
            return c_GlyphSlot_getMetrics(slot);
        }

        @Import(name = "c_GlyphSlot_getMetrics")
        private static native long c_GlyphSlot_getMetrics(long slot);

        public int getLinearHoriAdvance() {
            return c_GlyphSlot_getLinearHoriAdvance(address);
        }

        @Import(name = "c_GlyphSlot_getLinearHoriAdvance")
        private static native int c_GlyphSlot_getLinearHoriAdvance(long slot);

        public int getLinearVertAdvance() {
            return c_GlyphSlot_getLinearVertAdvance(address);
        }

        @Import(name = "c_GlyphSlot_getLinearVertAdvance")
        private static native int c_GlyphSlot_getLinearVertAdvance(long slot);

        public int getAdvanceX() {
            return c_GlyphSlot_getAdvanceX(address);
        }

        @Import(name = "c_GlyphSlot_getAdvanceX")
        private static native int c_GlyphSlot_getAdvanceX(long slot);

        public int getAdvanceY() {
            return c_GlyphSlot_getAdvanceY(address);
        }

        @Import(name = "c_GlyphSlot_getAdvanceY")
        private static native int c_GlyphSlot_getAdvanceY(long slot);

        public int getFormat() {
            return c_GlyphSlot_getFormat(address);
        }

        @Import(name = "c_GlyphSlot_getFormat")
        private static native int c_GlyphSlot_getFormat(long slot);

        public Bitmap getBitmap() {
            return new Bitmap(getBitmap(address));
        }

        private static long getBitmap(long slot) {
            return c_GlyphSlot_getBitmap(slot);
        }

        @Import(name = "c_GlyphSlot_getBitmap")
        private static native long c_GlyphSlot_getBitmap(long slot);

        public int getBitmapLeft() {
            return c_GlyphSlot_getBitmapLeft(address);
        }

        @Import(name = "c_GlyphSlot_getBitmapLeft")
        private static native int c_GlyphSlot_getBitmapLeft(long slot);

        public int getBitmapTop() {
            return c_GlyphSlot_getBitmapTop(address);
        }

        @Import(name = "c_GlyphSlot_getBitmapTop")
        private static native int c_GlyphSlot_getBitmapTop(long slot);

        public boolean renderGlyph(int renderMode) {
            return c_GlyphSlot_renderGlyph(address, renderMode);
        }

        @Import(name = "c_GlyphSlot_renderGlyph")
        private static native boolean c_GlyphSlot_renderGlyph(long slot, int renderMode);

        public Glyph getGlyph() {
            long glyph = getGlyph(address);
            if(glyph == 0) {
                throw new GdxRuntimeException("Couldn't get glyph, FreeType error code: " + getLastErrorCode());
            }
            return new Glyph(glyph);
        }

        private static long getGlyph(long glyphSlot) {
            return c_GlyphSlot_getGlyph(glyphSlot);
        }

        @Import(name = "c_GlyphSlot_getGlyph")
        private static native long c_GlyphSlot_getGlyph(long glyphSlot);
    }

    public static class Glyph extends Pointer implements Disposable {
        private boolean rendered;

        Glyph(long address) {
            super(address);
        }

        @Override
        public void dispose() {
            done(address);
        }

        private static void done(long glyph) {
            c_Glyph_done(glyph);
        }

        @Import(name = "c_Glyph_done")
        private static native void c_Glyph_done(long glyph);

        public void strokeBorder(Stroker stroker, boolean inside) {
            address = strokeBorder(address, stroker.address, inside);
            if(address == 0) {
                throw new GdxRuntimeException("Couldn't stroke glyph, FreeType error code: " + getLastErrorCode());
            }
        }

        private static long strokeBorder(long glyph, long stroker, boolean inside) {
            return c_Glyph_strokeBorder(glyph, stroker, inside);
        }

        @Import(name = "c_Glyph_strokeBorder")
        private static native long c_Glyph_strokeBorder(long glyph, long stroker, boolean inside);

        public void toBitmap(int renderMode) {
            long bitmap = toBitmap(address, renderMode);
            if(bitmap == 0) {
                throw new GdxRuntimeException("Couldn't render glyph, FreeType error code: " + getLastErrorCode());
            }
            address = bitmap;
            rendered = true;
        }

        private static long toBitmap(long glyph, int renderMode) {
            return c_Glyph_toBitmap(glyph, renderMode);
        }

        @Import(name = "c_Glyph_toBitmap")
        private static native long c_Glyph_toBitmap(long glyph, int renderMode);

        public Bitmap getBitmap() {
            if(!rendered) {
                throw new GdxRuntimeException("Glyph is not yet rendered");
            }
            return new Bitmap(getBitmap(address));
        }

        private static long getBitmap(long glyph) {
            return c_Glyph_getBitmap(glyph);
        }

        @Import(name = "c_Glyph_getBitmap")
        private static native long c_Glyph_getBitmap(long glyph);

        public int getLeft() {
            if(!rendered) {
                throw new GdxRuntimeException("Glyph is not yet rendered");
            }
            return c_Glyph_getLeft(address);
        }

        @Import(name = "c_Glyph_getLeft")
        private static native int c_Glyph_getLeft(long glyph);

        public int getTop() {
            if(!rendered) {
                throw new GdxRuntimeException("Glyph is not yet rendered");
            }
            return c_Glyph_getTop(address);
        }

        @Import(name = "c_Glyph_getTop")
        private static native int c_Glyph_getTop(long glyph);
    }

    public static class Bitmap extends Pointer {
        Bitmap(long address) {
            super(address);
        }

        public int getRows() {
            return c_Bitmap_getRows(address);
        }

        @Import(name = "c_Bitmap_getRows")
        private static native int c_Bitmap_getRows(long bitmap);

        public int getWidth() {
            return c_Bitmap_getWidth(address);
        }

        @Import(name = "c_Bitmap_getWidth")
        private static native int c_Bitmap_getWidth(long bitmap);

        public int getPitch() {
            return c_Bitmap_getPitch(address);
        }

        @Import(name = "c_Bitmap_getPitch")
        private static native int c_Bitmap_getPitch(long bitmap);

        public ByteBuffer getBuffer() {
            if(getRows() == 0) {
                return ByteBuffer.allocate(1);
            }
            int length = c_Bitmap_getBufferSize(address);
            if(length <= 0) {
                return ByteBuffer.allocate(1);
            }
            ByteBuffer copy = ByteBuffer.allocate(length);
            c_Bitmap_copyBuffer(address, BufferAddressUtils.ofInternal(copy), length);
            return copy;
        }

        @Import(name = "c_Bitmap_getBufferSize")
        private static native int c_Bitmap_getBufferSize(long bitmap);

        @Import(name = "c_Bitmap_copyBuffer")
        private static native void c_Bitmap_copyBuffer(long bitmap, Address target, int targetSize);

        public Pixmap getPixmap(Format format, Color color, float gamma) {
            int width = getWidth();
            int rows = getRows();
            ByteBuffer src = getBuffer();
            Pixmap pixmap;
            int pixelMode = getPixelMode();
            int rowBytes = Math.abs(getPitch());
            if(color == Color.WHITE && pixelMode == FT_PIXEL_MODE_GRAY && rowBytes == width && gamma == 1) {
                pixmap = new Pixmap(width, rows, Format.Alpha);
                ByteBuffer pixels = pixmap.getPixels();
                int length = pixels.capacity();
                for(int i = 0; i < length; i++) {
                    pixels.put(i, src.get(i));
                }
            }
            else {
                pixmap = new Pixmap(width, rows, Format.RGBA8888);
                int rgba = Color.rgba8888(color);
                byte[] srcRow = new byte[rowBytes];
                int[] dstRow = new int[width];
                IntBuffer dst = pixmap.getPixels().asIntBuffer();
                if(pixelMode == FT_PIXEL_MODE_MONO) {
                    for(int y = 0; y < rows; y++) {
                        src.get(srcRow);
                        for(int i = 0, x = 0; x < width; i++, x += 8) {
                            byte b = srcRow[i];
                            for(int ii = 0, n = Math.min(8, width - x); ii < n; ii++) {
                                dstRow[x + ii] = (b & (1 << (7 - ii))) != 0 ? rgba : 0;
                            }
                        }
                        dst.put(dstRow);
                    }
                }
                else {
                    int rgb = rgba & 0xffffff00;
                    int a = rgba & 0xff;
                    for(int y = 0; y < rows; y++) {
                        src.get(srcRow);
                        for(int x = 0; x < width; x++) {
                            int alpha = srcRow[x] & 0xff;
                            if(alpha == 0) {
                                dstRow[x] = rgb;
                            }
                            else if(alpha == 255) {
                                dstRow[x] = rgb | a;
                            }
                            else {
                                dstRow[x] = rgb | (int)(a * (float)Math.pow(alpha / 255f, gamma));
                            }
                        }
                        dst.put(dstRow);
                    }
                }
            }

            Pixmap converted = pixmap;
            if(format != pixmap.getFormat()) {
                converted = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), format);
                converted.setBlending(Blending.None);
                converted.drawPixmap(pixmap, 0, 0);
                converted.setBlending(Blending.SourceOver);
                pixmap.dispose();
            }
            return converted;
        }

        public int getNumGray() {
            return c_Bitmap_getNumGray(address);
        }

        @Import(name = "c_Bitmap_getNumGray")
        private static native int c_Bitmap_getNumGray(long bitmap);

        public int getPixelMode() {
            return c_Bitmap_getPixelMode(address);
        }

        @Import(name = "c_Bitmap_getPixelMode")
        private static native int c_Bitmap_getPixelMode(long bitmap);
    }

    public static class GlyphMetrics extends Pointer {
        GlyphMetrics(long address) {
            super(address);
        }

        public int getWidth() {
            return c_GlyphMetrics_getWidth(address);
        }

        @Import(name = "c_GlyphMetrics_getWidth")
        private static native int c_GlyphMetrics_getWidth(long metrics);

        public int getHeight() {
            return c_GlyphMetrics_getHeight(address);
        }

        @Import(name = "c_GlyphMetrics_getHeight")
        private static native int c_GlyphMetrics_getHeight(long metrics);

        public int getHoriBearingX() {
            return c_GlyphMetrics_getHoriBearingX(address);
        }

        @Import(name = "c_GlyphMetrics_getHoriBearingX")
        private static native int c_GlyphMetrics_getHoriBearingX(long metrics);

        public int getHoriBearingY() {
            return c_GlyphMetrics_getHoriBearingY(address);
        }

        @Import(name = "c_GlyphMetrics_getHoriBearingY")
        private static native int c_GlyphMetrics_getHoriBearingY(long metrics);

        public int getHoriAdvance() {
            return c_GlyphMetrics_getHoriAdvance(address);
        }

        @Import(name = "c_GlyphMetrics_getHoriAdvance")
        private static native int c_GlyphMetrics_getHoriAdvance(long metrics);

        public int getVertBearingX() {
            return c_GlyphMetrics_getVertBearingX(address);
        }

        @Import(name = "c_GlyphMetrics_getVertBearingX")
        private static native int c_GlyphMetrics_getVertBearingX(long metrics);

        public int getVertBearingY() {
            return c_GlyphMetrics_getVertBearingY(address);
        }

        @Import(name = "c_GlyphMetrics_getVertBearingY")
        private static native int c_GlyphMetrics_getVertBearingY(long metrics);

        public int getVertAdvance() {
            return c_GlyphMetrics_getVertAdvance(address);
        }

        @Import(name = "c_GlyphMetrics_getVertAdvance")
        private static native int c_GlyphMetrics_getVertAdvance(long metrics);
    }

    public static class Stroker extends Pointer implements Disposable {
        Stroker(long address) {
            super(address);
        }

        public void set(int radius, int lineCap, int lineJoin, int miterLimit) {
            c_Stroker_set(address, radius, lineCap, lineJoin, miterLimit);
        }

        @Import(name = "c_Stroker_set")
        private static native void c_Stroker_set(long stroker, int radius, int lineCap, int lineJoin, int miterLimit);

        @Override
        public void dispose() {
            c_Stroker_done(address);
        }

        @Import(name = "c_Stroker_done")
        private static native void c_Stroker_done(long stroker);
    }

    public static int FT_PIXEL_MODE_NONE = 0;
    public static int FT_PIXEL_MODE_MONO = 1;
    public static int FT_PIXEL_MODE_GRAY = 2;
    public static int FT_PIXEL_MODE_GRAY2 = 3;
    public static int FT_PIXEL_MODE_GRAY4 = 4;
    public static int FT_PIXEL_MODE_LCD = 5;
    public static int FT_PIXEL_MODE_LCD_V = 6;

    private static int encode(char a, char b, char c, char d) {
        return (a << 24) | (b << 16) | (c << 8) | d;
    }

    public static int FT_ENCODING_NONE = 0;
    public static int FT_ENCODING_MS_SYMBOL = encode('s', 'y', 'm', 'b');
    public static int FT_ENCODING_UNICODE = encode('u', 'n', 'i', 'c');
    public static int FT_ENCODING_SJIS = encode('s', 'j', 'i', 's');
    public static int FT_ENCODING_GB2312 = encode('g', 'b', ' ', ' ');
    public static int FT_ENCODING_BIG5 = encode('b', 'i', 'g', '5');
    public static int FT_ENCODING_WANSUNG = encode('w', 'a', 'n', 's');
    public static int FT_ENCODING_JOHAB = encode('j', 'o', 'h', 'a');
    public static int FT_ENCODING_ADOBE_STANDARD = encode('A', 'D', 'O', 'B');
    public static int FT_ENCODING_ADOBE_EXPERT = encode('A', 'D', 'B', 'E');
    public static int FT_ENCODING_ADOBE_CUSTOM = encode('A', 'D', 'B', 'C');
    public static int FT_ENCODING_ADOBE_LATIN_1 = encode('l', 'a', 't', '1');
    public static int FT_ENCODING_OLD_LATIN_2 = encode('l', 'a', 't', '2');
    public static int FT_ENCODING_APPLE_ROMAN = encode('a', 'r', 'm', 'n');

    public static int FT_FACE_FLAG_SCALABLE = (1 << 0);
    public static int FT_FACE_FLAG_FIXED_SIZES = (1 << 1);
    public static int FT_FACE_FLAG_FIXED_WIDTH = (1 << 2);
    public static int FT_FACE_FLAG_SFNT = (1 << 3);
    public static int FT_FACE_FLAG_HORIZONTAL = (1 << 4);
    public static int FT_FACE_FLAG_VERTICAL = (1 << 5);
    public static int FT_FACE_FLAG_KERNING = (1 << 6);
    public static int FT_FACE_FLAG_FAST_GLYPHS = (1 << 7);
    public static int FT_FACE_FLAG_MULTIPLE_MASTERS = (1 << 8);
    public static int FT_FACE_FLAG_GLYPH_NAMES = (1 << 9);
    public static int FT_FACE_FLAG_EXTERNAL_STREAM = (1 << 10);
    public static int FT_FACE_FLAG_HINTER = (1 << 11);
    public static int FT_FACE_FLAG_CID_KEYED = (1 << 12);
    public static int FT_FACE_FLAG_TRICKY = (1 << 13);

    public static int FT_STYLE_FLAG_ITALIC = (1 << 0);
    public static int FT_STYLE_FLAG_BOLD = (1 << 1);

    public static int FT_LOAD_DEFAULT = 0x0;
    public static int FT_LOAD_NO_SCALE = 0x1;
    public static int FT_LOAD_NO_HINTING = 0x2;
    public static int FT_LOAD_RENDER = 0x4;
    public static int FT_LOAD_NO_BITMAP = 0x8;
    public static int FT_LOAD_VERTICAL_LAYOUT = 0x10;
    public static int FT_LOAD_FORCE_AUTOHINT = 0x20;
    public static int FT_LOAD_CROP_BITMAP = 0x40;
    public static int FT_LOAD_PEDANTIC = 0x80;
    public static int FT_LOAD_IGNORE_GLOBAL_ADVANCE_WIDTH = 0x200;
    public static int FT_LOAD_NO_RECURSE = 0x400;
    public static int FT_LOAD_IGNORE_TRANSFORM = 0x800;
    public static int FT_LOAD_MONOCHROME = 0x1000;
    public static int FT_LOAD_LINEAR_DESIGN = 0x2000;
    public static int FT_LOAD_NO_AUTOHINT = 0x8000;

    public static int FT_LOAD_TARGET_NORMAL = 0x0;
    public static int FT_LOAD_TARGET_LIGHT = 0x10000;
    public static int FT_LOAD_TARGET_MONO = 0x20000;
    public static int FT_LOAD_TARGET_LCD = 0x30000;
    public static int FT_LOAD_TARGET_LCD_V = 0x40000;

    public static int FT_RENDER_MODE_NORMAL = 0;
    public static int FT_RENDER_MODE_LIGHT = 1;
    public static int FT_RENDER_MODE_MONO = 2;
    public static int FT_RENDER_MODE_LCD = 3;
    public static int FT_RENDER_MODE_LCD_V = 4;
    public static int FT_RENDER_MODE_MAX = 5;

    public static int FT_KERNING_DEFAULT = 0;
    public static int FT_KERNING_UNFITTED = 1;
    public static int FT_KERNING_UNSCALED = 2;

    public static int FT_STROKER_LINECAP_BUTT = 0;
    public static int FT_STROKER_LINECAP_ROUND = 1;
    public static int FT_STROKER_LINECAP_SQUARE = 2;

    public static int FT_STROKER_LINEJOIN_ROUND = 0;
    public static int FT_STROKER_LINEJOIN_BEVEL = 1;
    public static int FT_STROKER_LINEJOIN_MITER_VARIABLE = 2;
    public static int FT_STROKER_LINEJOIN_MITER = FT_STROKER_LINEJOIN_MITER_VARIABLE;
    public static int FT_STROKER_LINEJOIN_MITER_FIXED = 3;

    public static Library initFreeType() {
        long address = initFreeTypeJni();
        if(address == 0) {
            throw new GdxRuntimeException("Couldn't initialize FreeType library, FreeType error code: " + getLastErrorCode());
        }
        return new Library(address);
    }

    private static long initFreeTypeJni() {
        return c_FreeType_initFreeTypeJni();
    }

    @Import(name = "c_FreeType_initFreeTypeJni")
    private static native long c_FreeType_initFreeTypeJni();

    public static int toInt(int value) {
        return ((value + 63) & -64) >> 6;
    }
}
