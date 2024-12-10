package emu.com.badlogic.gdx.graphics.g2d.freetype;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.StreamUtils;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.teavm.jso.JSBody;

public class FreeType {
    private static void nativeFree(int address) {
        nativeFreeInternal(address);
    }

    @JSBody(params = {"address"}, script = "Module._free(address);")
    private static native void nativeFreeInternal(int address);

    /**
     * @return returns the last error code FreeType reported
     */
    @JSBody(script = "return Module._c_FreeType_getLastErrorCode();")
    static native int getLastErrorCode();

    private static class Pointer {
        int address;

        Pointer(int address) {
            this.address = address;
        }
    }

    public static class Library extends Pointer implements Disposable {
        LongMap<Integer> fontData = new LongMap<Integer>();

        Library(int address) {
            super(address);
        }

        @Override
        public void dispose() {
            doneFreeType(address);
            for(Integer address : fontData.values()) {
                nativeFree(address);
            }
        }

        @JSBody(params = {"library"}, script = "Module._c_Library_doneFreeType(library);")
        private static native void doneFreeType(int library);

        public Face newFace(FileHandle fontFile, int faceIndex) {
            ByteBuffer buffer = null;
            try {
                //buffer = fontFile.map(); //method missing in gwt emulation
            }
            catch(GdxRuntimeException ignored) {
                // OK to ignore, some platforms do not support file mapping.
            }
            if(buffer == null) {
                InputStream input = fontFile.read();
                try {
                    int fileSize = (int)fontFile.length();
                    if(fileSize == 0) {
                        // Copy to a byte[] to get the size, then copy to the buffer.
                        byte[] data = StreamUtils.copyStreamToByteArray(input, 1024 * 16);
                        buffer = BufferUtils.newByteBuffer(data.length);
                        BufferUtils.copy(data, 0, buffer, data.length);
                    }
                    else {
                        // Trust the specified file size.
                        buffer = BufferUtils.newByteBuffer(fileSize);
                        StreamUtils.copyStream(input, buffer);
                    }
                }
                catch(IOException ex) {
                    throw new GdxRuntimeException(ex);
                }
                finally {
                    StreamUtils.closeQuietly(input);
                }
            }
            return newMemoryFace(buffer, faceIndex);
        }

        public Face newMemoryFace(byte[] data, int dataSize, int faceIndex) {
            ByteBuffer buffer = BufferUtils.newByteBuffer(data.length);
            BufferUtils.copy(data, 0, buffer, data.length);
            return newMemoryFace(buffer, faceIndex);
        }

        public Face newMemoryFace(ByteBuffer buffer, int faceIndex) {
            ArrayBufferViewWrapper buf = TypedArrays.getTypedArray(buffer);
            int[] addressToFree = new int[]{0}; // Hacky way to get two return values

            int face = newMemoryFace(address, buf, buffer.remaining(), faceIndex, addressToFree);
            if(face == 0) {
                if(addressToFree[0] != 0) { // 'Zero' would mean allocating the buffer failed
                    nativeFree(addressToFree[0]);
                }
                throw new GdxRuntimeException("Couldn't load font, FreeType error code: " + getLastErrorCode());
            }
            else {
                fontData.put(face, addressToFree[0]);
                return new Face(face, this);
            }
        }

        @JSBody(params = {"library", "data", "dataSize", "faceIndex", "outAddressToFree"}, script = "" +
                "var address = Module._malloc(data.length);" +
                "outAddressToFree[0] = address;" +
                "Module.writeArrayToMemory(data, address);" +
                "var ret = Module._c_Library_newMemoryFace(library, address, dataSize, faceIndex);" +
                "return ret")
        private static native int newMemoryFace(int library, ArrayBufferViewWrapper data, int dataSize, int faceIndex, int[] outAddressToFree);

        public Stroker createStroker() {
            int stroker = strokerNew(address);
            if(stroker == 0)
                throw new GdxRuntimeException("Couldn't create FreeType stroker, FreeType error code: " + getLastErrorCode());
            return new Stroker(stroker);
        }

        @JSBody(params = {"library"}, script = "return Module._c_Library_strokerNew(library);")
        private static native int strokerNew(int library);
    }

    public static class Face extends Pointer implements Disposable {
        Library library;

        public Face(int address, Library library) {
            super(address);
            this.library = library;
        }

        @Override
        public void dispose() {
            doneFace(address);
            Integer freeAddress = library.fontData.get(address);
            if(freeAddress != 0) { // Don't free 'zero' address
                library.fontData.remove(address);
                nativeFree(freeAddress);
            }
        }

        @JSBody(params = {"face"}, script = "Module._c_Face_doneFace(face);")
        private static native void doneFace(int face);

        public int getFaceFlags() {
            return getFaceFlags(address);
        }

        @JSBody(params = {"face"}, script = "return Module._c_Face_getFaceFlags(face);")
        private static native int getFaceFlags(int face);

        public int getStyleFlags() {
            return getStyleFlags(address);
        }

        @JSBody(params = {"face"}, script = "return Module._c_Face_getStyleFlags(face);")
        private static native int getStyleFlags(int face);

        public int getNumGlyphs() {
            return getNumGlyphs(address);
        }

        @JSBody(params = {"face"}, script = "return Module._c_Face_getNumGlyphs(face);")
        private static native int getNumGlyphs(int face);

        public int getAscender() {
            return getAscender(address);
        }

        @JSBody(params = {"face"}, script = "return Module._c_Face_getAscender(face);")
        private static native int getAscender(int face);

        public int getDescender() {
            return getDescender(address);
        }

        @JSBody(params = {"face"}, script = "return Module._c_Face_getDescender(face);")
        private static native int getDescender(int face);

        public int getHeight() {
            return getHeight(address);
        }

        @JSBody(params = {"face"}, script = "return Module._c_Face_getHeight(face);")
        private static native int getHeight(int face);

        public int getMaxAdvanceWidth() {
            return getMaxAdvanceWidth(address);
        }

        @JSBody(params = {"face"}, script = "return Module._c_Face_getMaxAdvanceWidth(face);")
        private static native int getMaxAdvanceWidth(int face);

        public int getMaxAdvanceHeight() {
            return getMaxAdvanceHeight(address);
        }

        @JSBody(params = {"face"}, script = "return Module._c_Face_getMaxAdvanceHeight(face);")
        private static native int getMaxAdvanceHeight(int face);

        public int getUnderlinePosition() {
            return getUnderlinePosition(address);
        }

        @JSBody(params = {"face"}, script = "return Module._c_Face_getUnderlinePosition(face);")
        private static native int getUnderlinePosition(int face);

        public int getUnderlineThickness() {
            return getUnderlineThickness(address);
        }

        @JSBody(params = {"face"}, script = "return Module._c_Face_getUnderlineThickness(face);")
        private static native int getUnderlineThickness(int face);

        public boolean selectSize(int strikeIndex) {
            return selectSize(address, strikeIndex);
        }

        @JSBody(params = {"face", "strike_index"}, script = "return !!Module._c_Face_selectSize(face, strike_index);")
        private static native boolean selectSize(int face, int strike_index);

        public boolean setCharSize(int charWidth, int charHeight, int horzResolution, int vertResolution) {
            return setCharSize(address, charWidth, charHeight, horzResolution, vertResolution);
        }

        @JSBody(params = {"face", "charWidth", "charHeight", "horzResolution", "vertResolution"}, script = "return !!Module._c_Face_setCharSize(face, charWidth, charHeight, horzResolution, vertResolution);")
        private static native boolean setCharSize(int face, int charWidth, int charHeight, int horzResolution, int vertResolution);

        public boolean setPixelSizes(int pixelWidth, int pixelHeight) {
            return setPixelSizes(address, pixelWidth, pixelHeight);
        }

        @JSBody(params = {"face", "pixelWidth", "pixelHeight"}, script = "return !!Module._c_Face_setPixelSizes(face, pixelWidth, pixelHeight);")
        private static native boolean setPixelSizes(int face, int pixelWidth, int pixelHeight);

        public boolean loadGlyph(int glyphIndex, int loadFlags) {
            return loadGlyph(address, glyphIndex, loadFlags);
        }

        @JSBody(params = {"face", "glyphIndex", "loadFlags"}, script = "return !!Module._c_Face_loadGlyph(face, glyphIndex, loadFlags);")
        private static native boolean loadGlyph(int face, int glyphIndex, int loadFlags);

        public boolean loadChar(int charCode, int loadFlags) {
            return loadChar(address, charCode, loadFlags);
        }

        @JSBody(params = {"face", "charCode", "loadFlags"}, script = "return !!Module._c_Face_loadChar(face, charCode, loadFlags);")
        private static native boolean loadChar(int face, int charCode, int loadFlags);

        public GlyphSlot getGlyph() {
            return new GlyphSlot(getGlyph(address));
        }

        @JSBody(params = {"face"}, script = "return Module._c_Face_getGlyph(face);")
        private static native int getGlyph(int face);

        public Size getSize() {
            return new Size(getSize(address));
        }

        @JSBody(params = {"face"}, script = "return Module._c_Face_getSize(face);")
        private static native int getSize(int face);

        public boolean hasKerning() {
            return hasKerning(address);
        }

        @JSBody(params = {"face"}, script = "return !!Module._c_Face_hasKerning(face);")
        private static native boolean hasKerning(int face);

        public int getKerning(int leftGlyph, int rightGlyph, int kernMode) {
            return getKerning(address, leftGlyph, rightGlyph, kernMode);
        }

        @JSBody(params = {"face", "leftGlyph", "rightGlyph", "kernMode"}, script = "return Module._c_Face_getKerning(face, leftGlyph, rightGlyph, kernMode);")
        private static native int getKerning(int face, int leftGlyph, int rightGlyph, int kernMode);

        public int getCharIndex(int charCode) {
            return getCharIndex(address, charCode);
        }

        @JSBody(params = {"face", "charCode"}, script = "return Module._c_Face_getCharIndex(face, charCode);")
        private static native int getCharIndex(int face, int charCode);
    }

    public static class Size extends Pointer {
        Size(int address) {
            super(address);
        }

        public SizeMetrics getMetrics() {
            return new SizeMetrics(getMetrics(address));
        }

        @JSBody(params = {"address"}, script = "return Module._c_Size_getMetrics(address);")
        private static native int getMetrics(int address);
    }

    public static class SizeMetrics extends Pointer {
        SizeMetrics(int address) {
            super(address);
        }

        public int getXppem() {
            return getXppem(address);
        }

        @JSBody(params = {"metrics"}, script = "return Module._c_SizeMetrics_getXppem(metrics);")
        private static native int getXppem(int metrics);

        public int getYppem() {
            return getYppem(address);
        }

        @JSBody(params = {"metrics"}, script = "return Module._c_SizeMetrics_getYppem(metrics);")
        private static native int getYppem(int metrics);

        public int getXScale() {
            return getXscale(address);
        }

        @JSBody(params = {"metrics"}, script = "return Module._c_SizeMetrics_getXscale(metrics);")
        private static native int getXscale(int metrics);

        public int getYscale() {
            return getYscale(address);
        }

        @JSBody(params = {"metrics"}, script = "return Module._c_SizeMetrics_getYscale(metrics);")
        private static native int getYscale(int metrics);

        public int getAscender() {
            return getAscender(address);
        }

        @JSBody(params = {"metrics"}, script = "return Module._c_SizeMetrics_getAscender(metrics);")
        private static native int getAscender(int metrics);

        public int getDescender() {
            return getDescender(address);
        }

        @JSBody(params = {"metrics"}, script = "return Module._c_SizeMetrics_getDescender(metrics);")
        private static native int getDescender(int metrics);

        public int getHeight() {
            return getHeight(address);
        }

        @JSBody(params = {"metrics"}, script = "return Module._c_SizeMetrics_getHeight(metrics);")
        private static native int getHeight(int metrics);

        public int getMaxAdvance() {
            return getMaxAdvance(address);
        }

        @JSBody(params = {"metrics"}, script = "return Module._c_SizeMetrics_getMaxAdvance(metrics);")
        private static native int getMaxAdvance(int metrics);
    }

    public static class GlyphSlot extends Pointer {
        GlyphSlot(int address) {
            super(address);
        }

        public GlyphMetrics getMetrics() {
            return new GlyphMetrics(getMetrics(address));
        }

        @JSBody(params = {"slot"}, script = "return Module._c_GlyphSlot_getMetrics(slot);")
        private static native int getMetrics(int slot);

        public int getLinearHoriAdvance() {
            return getLinearHoriAdvance(address);
        }

        @JSBody(params = {"slot"}, script = "return Module._c_GlyphSlot_getLinearHoriAdvance(slot);")
        private static native int getLinearHoriAdvance(int slot);

        public int getLinearVertAdvance() {
            return getLinearVertAdvance(address);
        }

        @JSBody(params = {"slot"}, script = "return Module._c_GlyphSlot_getLinearVertAdvance(slot);")
        private static native int getLinearVertAdvance(int slot);

        public int getAdvanceX() {
            return getAdvanceX(address);
        }

        @JSBody(params = {"slot"}, script = "return Module._c_GlyphSlot_getAdvanceX(slot);")
        private static native int getAdvanceX(int slot);

        public int getAdvanceY() {
            return getAdvanceY(address);
        }

        @JSBody(params = {"slot"}, script = "return Module._c_GlyphSlot_getAdvanceY(slot);")
        private static native int getAdvanceY(int slot);

        public int getFormat() {
            return getFormat(address);
        }

        @JSBody(params = {"slot"}, script = "return Module._c_GlyphSlot_getFormat(slot);")
        private static native int getFormat(int slot);

        public Bitmap getBitmap() {
            return new Bitmap(getBitmap(address));
        }

        @JSBody(params = {"slot"}, script = "return Module._c_GlyphSlot_getBitmap(slot);")
        private static native int getBitmap(int slot);

        public int getBitmapLeft() {
            return getBitmapLeft(address);
        }

        @JSBody(params = {"slot"}, script = "return Module._c_GlyphSlot_getBitmapLeft(slot);")
        private static native int getBitmapLeft(int slot);

        public int getBitmapTop() {
            return getBitmapTop(address);
        }

        @JSBody(params = {"slot"}, script = "return Module._c_GlyphSlot_getBitmapTop(slot);")
        private static native int getBitmapTop(int slot);

        public boolean renderGlyph(int renderMode) {
            return renderGlyph(address, renderMode);
        }

        @JSBody(params = {"slot", "renderMode"}, script = "return !!Module._c_GlyphSlot_renderGlyph(slot, renderMode);")
        private static native boolean renderGlyph(int slot, int renderMode);

        public Glyph getGlyph() {
            int glyph = getGlyph(address);
            if(glyph == 0)
                throw new GdxRuntimeException("Couldn't get glyph, FreeType error code: " + getLastErrorCode());
            return new Glyph(glyph);
        }

        @JSBody(params = {"glyphSlot"}, script = "return Module._c_GlyphSlot_getGlyph(glyphSlot);")
        private static native int getGlyph(int glyphSlot);
    }

    public static class Glyph extends Pointer implements Disposable {
        private boolean rendered;

        Glyph(int address) {
            super(address);
        }

        @Override
        public void dispose() {
            done(address);
        }

        @JSBody(params = {"glyph"}, script = "Module._c_Glyph_done(glyph);")
        private static native void done(int glyph);

        private int bTI(boolean bool) {
            return bool == true ? 1 : 0;
        }

        public void strokeBorder(Stroker stroker, boolean inside) {
            address = strokeBorder(address, stroker.address, bTI(inside));
        }

        @JSBody(params = {"glyph", "stroker", "inside"}, script = "return Module._c_Glyph_strokeBorder(glyph, stroker, inside);")
        private static native int strokeBorder(int glyph, int stroker, int inside);

        public void toBitmap(int renderMode) {
            int bitmap = toBitmap(address, renderMode);
            if(bitmap == 0)
                throw new GdxRuntimeException("Couldn't render glyph, FreeType error code: " + getLastErrorCode());
            address = bitmap;
            rendered = true;
        }

        @JSBody(params = {"glyph", "renderMode"}, script = "return Module._c_Glyph_toBitmap(glyph, renderMode);")
        private static native int toBitmap(int glyph, int renderMode);

        public Bitmap getBitmap() {
            if(!rendered) {
                throw new GdxRuntimeException("Glyph is not yet rendered");
            }
            return new Bitmap(getBitmap(address));
        }

        @JSBody(params = {"glyph"}, script = "return Module._c_Glyph_getBitmap(glyph);")
        private static native int getBitmap(int glyph);

        public int getLeft() {
            if(!rendered) {
                throw new GdxRuntimeException("Glyph is not yet rendered");
            }
            return getLeft(address);
        }

        @JSBody(params = {"glyph"}, script = "return Module._c_Glyph_getLeft(glyph);")
        private static native int getLeft(int glyph);

        public int getTop() {
            if(!rendered) {
                throw new GdxRuntimeException("Glyph is not yet rendered");
            }
            return getTop(address);
        }

        @JSBody(params = {"glyph"}, script = "return Module._c_Glyph_getTop(glyph);")
        private static native int getTop(int glyph);
    }

    public static class Bitmap extends Pointer {
        Bitmap(int address) {
            super(address);
        }

        public int getRows() {
            return getRows(address);
        }

        @JSBody(params = {"bitmap"}, script = "return Module._c_Bitmap_getRows(bitmap);")
        private static native int getRows(int bitmap);

        public int getWidth() {
            return getWidth(address);
        }

        @JSBody(params = {"bitmap"}, script = "return Module._c_Bitmap_getWidth(bitmap);")
        private static native int getWidth(int bitmap);

        public int getPitch() {
            return getPitch(address);
        }

        @JSBody(params = {"bitmap"}, script = "return Module._c_Bitmap_getPitch(bitmap);")
        private static native int getPitch(int bitmap);

        public ByteBuffer getBuffer() {
            if(getRows() == 0)
                // Issue #768 - CheckJNI frowns upon env->NewDirectByteBuffer with NULL buffer or capacity 0
                // "JNI WARNING: invalid values for address (0x0) or capacity (0)"
                // FreeType sets FT_Bitmap::buffer to NULL when the bitmap is empty (e.g. for ' ')
                // JNICheck is on by default on emulators and might have a point anyway...
                // So let's avoid this and just return a dummy non-null non-zero buffer
                return BufferUtils.newByteBuffer(1);
            int offset = getBufferAddress(address);
            int length = getBufferSize(address);
            Int8ArrayWrapper int8ArrayWrapper = getBuffer(address, offset, length);

            byte[] byteArray = TypedArrays.toByteArray(int8ArrayWrapper);
            ByteBuffer buf = ByteBuffer.wrap(byteArray, 0, length);
            return buf;
        }

        @JSBody(params = {"bitmap"}, script = "return Module._c_Bitmap_getBufferAddress(bitmap);")
        private static native int getBufferAddress(int bitmap);

        @JSBody(params = {"bitmap"}, script = "return Module._c_Bitmap_getBufferSize(bitmap);")
        private static native int getBufferSize(int bitmap);

        @JSBody(params = {"bitmap", "offset", "length"}, script = "" +
                "var buff = Module.HEAP8.subarray(offset, offset + length);" +
                "return buff;")
        private static native Int8ArrayWrapper getBuffer(int bitmap, int offset, int length);

        // @on
        public Pixmap getPixmap (Pixmap.Format format, Color color, float gamma) {
            int width = getWidth(), rows = getRows();
            ByteBuffer src = getBuffer();
            Pixmap pixmap;
            int pixelMode = getPixelMode();
            int rowBytes = Math.abs(getPitch()); // We currently ignore negative pitch.
            if (color == Color.WHITE && pixelMode == FT_PIXEL_MODE_GRAY && rowBytes == width && gamma == 1) {
                pixmap = new Pixmap(width, rows, Pixmap.Format.Alpha);
                BufferUtils.copy(src, pixmap.getPixels(), pixmap.getPixels().capacity());
            } else {
                pixmap = new Pixmap(width, rows, Pixmap.Format.RGBA8888);
                int rgba = Color.rgba8888(color);
                byte[] srcRow = new byte[rowBytes];
                int[] dstRow = new int[width];
                IntBuffer dst = pixmap.getPixels().asIntBuffer();
                if (pixelMode == FT_PIXEL_MODE_MONO) {
                    // Use the specified color for each set bit.
                    for (int y = 0; y < rows; y++) {
                        src.get(srcRow);
                        for (int i = 0, x = 0; x < width; i++, x += 8) {
                            byte b = srcRow[i];
                            for (int ii = 0, n = Math.min(8, width - x); ii < n; ii++) {
                                if ((b & (1 << (7 - ii))) != 0)
                                    dstRow[x + ii] = rgba;
                                else
                                    dstRow[x + ii] = 0;
                            }
                        }
                        dst.put(dstRow);
                    }
                } else {
                    // Use the specified color for RGB, blend the FreeType bitmap with alpha.
                    int rgb = rgba & 0xffffff00;
                    int a = rgba & 0xff;
                    for (int y = 0; y < rows; y++) {
                        src.get(srcRow);
                        for (int x = 0; x < width; x++) {
                            // Zero raised to any power is always zero.
                            // 255 (=one) raised to any power is always one.
                            // We only need Math.pow() when alpha is NOT zero and NOT one.
                            int alpha = srcRow[x] & 0xff;
                            if (alpha == 0)
                                dstRow[x] = rgb;
                            else if (alpha == 255)
                                dstRow[x] = rgb | a;
                            else
                                dstRow[x] = rgb | (int)(a * (float)Math.pow(alpha / 255f, gamma)); // Inverse gamma.
                        }
                        dst.put(dstRow);
                    }
                }
            }

            Pixmap converted = pixmap;
            if (format != pixmap.getFormat()) {
                converted = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), format);
                converted.setBlending(Pixmap.Blending.None);
                converted.drawPixmap(pixmap, 0, 0);
                converted.setBlending(Pixmap.Blending.SourceOver);
                pixmap.dispose();
            }
            return converted;
        }
        // @off

        public int getNumGray() {
            return getNumGray(address);
        }

        @JSBody(params = {"bitmap"}, script = "return Module._c_Bitmap_getNumGray(bitmap);")
        private static native int getNumGray(int bitmap);

        public int getPixelMode() {
            return getPixelMode(address);
        }

        @JSBody(params = {"bitmap"}, script = "return Module._c_Bitmap_getPixelMode(bitmap);")
        private static native int getPixelMode(int bitmap);
    }

    public static class GlyphMetrics extends Pointer {
        GlyphMetrics(int address) {
            super(address);
        }

        public int getWidth() {
            return getWidth(address);
        }

        @JSBody(params = {"metrics"}, script = "return Module._c_GlyphMetrics_getWidth(metrics);")
        private static native int getWidth(int metrics);

        public int getHeight() {
            return getHeight(address);
        }

        @JSBody(params = {"metrics"}, script = "return Module._c_GlyphMetrics_getHeight(metrics);")
        private static native int getHeight(int metrics);

        public int getHoriBearingX() {
            return getHoriBearingX(address);
        }

        @JSBody(params = {"metrics"}, script = "return Module._c_GlyphMetrics_getHoriBearingX(metrics);")
        private static native int getHoriBearingX(int metrics);

        public int getHoriBearingY() {
            return getHoriBearingY(address);
        }

        @JSBody(params = {"metrics"}, script = "return Module._c_GlyphMetrics_getHoriBearingY(metrics);")
        private static native int getHoriBearingY(int metrics);

        public int getHoriAdvance() {
            return getHoriAdvance(address);
        }

        @JSBody(params = {"metrics"}, script = "return Module._c_GlyphMetrics_getHoriAdvance(metrics);")
        private static native int getHoriAdvance(int metrics);

        public int getVertBearingX() {
            return getVertBearingX(address);
        }

        @JSBody(params = {"metrics"}, script = "return Module._c_GlyphMetrics_getVertBearingX(metrics);")
        private static native int getVertBearingX(int metrics);

        public int getVertBearingY() {
            return getVertBearingY(address);
        }

        @JSBody(params = {"metrics"}, script = "return Module._c_GlyphMetrics_getVertBearingY(metrics);")
        private static native int getVertBearingY(int metrics);

        public int getVertAdvance() {
            return getVertAdvance(address);
        }

        @JSBody(params = {"metrics"}, script = "return Module._c_GlyphMetrics_getVertAdvance(metrics);")
        private static native int getVertAdvance(int metrics);
    }

    public static class Stroker extends Pointer implements Disposable {
        Stroker(int address) {
            super(address);
        }

        public void set(int radius, int lineCap, int lineJoin, int miterLimit) {
            set(address, radius, lineCap, lineJoin, miterLimit);
        }

        @JSBody(params = {"stroker", "radius", "lineCap", "lineJoin", "miterLimit"}, script = "Module._c_Stroker_set(stroker, radius, lineCap, lineJoin, miterLimit);")
        private static native void set(int stroker, int radius, int lineCap, int lineJoin, int miterLimit);

        @Override
        public void dispose() {
            done(address);
        }

        @JSBody(params = {"stroker"}, script = "Module._c_Stroker_done(stroker);")
        private static native void done(int stroker);
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
        // Javascript library should be already loaded when calling this

        int address = initFreeTypeJni();
        if(address == 0)
            throw new GdxRuntimeException("Couldn't initialize FreeType library");
        else
            return new Library(address);
    }

    @JSBody(script = "return Module._c_FreeType_initFreeTypeJni();")
    private static native int initFreeTypeJni();

    public static int toInt(int value) {
        return ((value + 63) & -64) >> 6;
    }

}