/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.graphics.g2d.freetype;

import java.nio.ByteBuffer;
import java.nio.FreeTypeUtil;
import java.nio.HasArrayBufferView;
import java.nio.IntBuffer;

import org.w3c.dom.typedarray.ArrayBuffer;
import org.w3c.dom.typedarray.ArrayBufferView;
import org.w3c.dom.typedarray.Int8Array;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FreeTypePixmap;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.LongMap;
import com.dragome.commons.javascript.ScriptHelper;

/**
 * Port from Intrigus GWT FreeType
 *
 * @author Simon Gerst
 * */
public class FreeType {
	private static void nativeFree (int address) {
		ScriptHelper.put("a", address, null);
		ScriptHelper.evalNoResult("Module._free(a)", null);
	}

	private static class Pointer {
		public int address;

		Pointer (int address) {
			this.address = address;
		}
	}

	public static class Library extends Pointer implements Disposable {
		LongMap<Integer> fontData = new LongMap<Integer>();

		Library (int address) {
			super(address);
		}

		@Override
		public void dispose () {
			ScriptHelper.put("a", address, null);
			ScriptHelper.evalNoResult("Module._c_Library_doneFreeType(a)", null);
			for (Integer address : fontData.values()) {
				nativeFree(address);
			}
		}

		public Face newFace (FileHandle font, int faceIndex) {
			byte[] data = font.readBytes();
			return newMemoryFace(data, data.length, faceIndex);
		}

		public Face newMemoryFace (byte[] data, int dataSize, int faceIndex) {
			ByteBuffer buffer = BufferUtils.newByteBuffer(data.length);
			BufferUtils.copy(data, 0, buffer, data.length);
			return newMemoryFace(buffer, faceIndex);
		}

		public Face newMemoryFace (ByteBuffer buffer, int faceIndex) {
			ArrayBufferView buf = ((HasArrayBufferView)buffer).getTypedArray();
			int[] addressToFree = new int[] {0}; // Hacky way to get two return values
			int face = newMemoryFace(address, buf, buffer.remaining(), faceIndex, addressToFree);
			if (face == 0) {
				if (addressToFree[0] != 0) { // 'Zero' would mean allocating the buffer failed
					nativeFree(addressToFree[0]);
				}
				throw new GdxRuntimeException("Couldn't load font");
			} else {
				fontData.put(face, addressToFree[0]);
				return new Face(face, this);
			}
		}

		private static int newMemoryFace (int library, ArrayBufferView data, int dataSize, int faceIndex, int[] outAddressToFree) {
			ScriptHelper.put("outAddressToFree", outAddressToFree, null);
			ScriptHelper.put("faceIndex", faceIndex, null);
			ScriptHelper.put("dataSize", dataSize, null);
			ScriptHelper.put("library", library, null);
			ScriptHelper.put("data", data, null);
			ScriptHelper.evalNoResult("data=data.node", null);
			ScriptHelper.evalNoResult("var address = Module._malloc(data.length);", null);
			ScriptHelper.evalNoResult("outAddressToFree[0] = address;", null);
			ScriptHelper.evalNoResult("Module.writeArrayToMemory(data, address);", null);
			return ScriptHelper.evalInt("Module._c_Library_newMemoryFace(library, address, dataSize, faceIndex)", null);
		}

		public Stroker createStroker () {
			ScriptHelper.put("a", address, null);
			int stroker = ScriptHelper.evalInt("Module._c_Library_strokerNew(a);", null);
			if (stroker == 0) throw new GdxRuntimeException("Couldn't create FreeType stroker");
			return new Stroker(stroker);
		}
	}

	public static class Face extends Pointer implements Disposable {
		Library library;

		public Face (int address, Library library) {
			super(address);
			this.library = library;
		}

		@Override
		public void dispose () {
			ScriptHelper.put("a", address, null);
			ScriptHelper.evalNoResult("Module._c_Face_doneFace(a)", null);
			Integer freeAddress = library.fontData.get(address);
			if (freeAddress != 0) { // Don't free 'zero' address
				library.fontData.remove(address);
				nativeFree(freeAddress);
			}
		}

		public int getFaceFlags () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_Face_getFaceFlags(a);", null);
		}

		public int getStyleFlags () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_Face_getStyleFlags(a);", null);
		}

		public int getNumGlyphs () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_Face_getNumGlyphs(a);", null);
		}

		public int getAscender () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_Face_getAscender(a);", null);
		}

		public int getDescender () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_Face_getDescender(a);", null);
		}

		public int getHeight () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_Face_getHeight(a);", null);
		}

		public int getMaxAdvanceWidth () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_Face_getMaxAdvanceWidth(a);", null);
		}

		public int getMaxAdvanceHeight () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_Face_getMaxAdvanceHeight(a);", null);
		}

		public int getUnderlinePosition () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_Face_getUnderlinePosition(a);", null);
		}

		public int getUnderlineThickness () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_Face_getUnderlineThickness(a);", null);
		}

		public boolean selectSize (int strikeIndex) {
			ScriptHelper.put("a", address, null);
			ScriptHelper.put("b", strikeIndex, null);
			return ScriptHelper.evalBoolean("!!Module._c_Face_selectSize(a,b);", null);
		}

		public boolean setCharSize (int charWidth, int charHeight, int horzResolution, int vertResolution) {
			ScriptHelper.put("a", address, null);
			ScriptHelper.put("b", charWidth, null);
			ScriptHelper.put("c", charHeight, null);
			ScriptHelper.put("d", horzResolution, null);
			ScriptHelper.put("e", vertResolution, null);
			return ScriptHelper.evalBoolean("!!Module._c_Face_setCharSize(a,b,c,d,e);", null);
		}

		public boolean setPixelSizes (int pixelWidth, int pixelHeight) {
			ScriptHelper.put("a", address, null);
			ScriptHelper.put("b", pixelWidth, null);
			ScriptHelper.put("c", pixelHeight, null);
			return ScriptHelper.evalBoolean("!!Module._c_Face_setPixelSizes(a,b,c);", null);
		}

		public boolean loadGlyph (int glyphIndex, int loadFlags) {
			ScriptHelper.put("a", address, null);
			ScriptHelper.put("b", glyphIndex, null);
			ScriptHelper.put("c", loadFlags, null);
			return ScriptHelper.evalBoolean("!!Module._c_Face_loadGlyph(a,b,c);", null);
		}

		public boolean loadChar (int charCode, int loadFlags) {
			ScriptHelper.put("a", address, null);
			ScriptHelper.put("b", charCode, null);
			ScriptHelper.put("c", loadFlags, null);
			return ScriptHelper.evalBoolean("!!Module._c_Face_loadChar(a,b,c);", null);
		}

		public GlyphSlot getGlyph () {
			ScriptHelper.put("a", address, null);
			return new GlyphSlot(ScriptHelper.evalInt("Module._c_Face_getGlyph(a)", null));
		}

		public Size getSize () {
			ScriptHelper.put("a", address, null);
			return new Size(ScriptHelper.evalInt("Module._c_Face_getSize(a)", null));
		}

		public boolean hasKerning () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalBoolean("!!Module._c_Face_hasKerning(a);", null);
		}

		public int getKerning (int leftGlyph, int rightGlyph, int kernMode) {
			ScriptHelper.put("a", address, null);
			ScriptHelper.put("b", leftGlyph, null);
			ScriptHelper.put("c", rightGlyph, null);
			ScriptHelper.put("d", kernMode, null);
			return ScriptHelper.evalInt("Module._c_Face_getKerning(a,b,c,d);", null);
		}

		public int getCharIndex (int charCode) {
			ScriptHelper.put("a", address, null);
			ScriptHelper.put("b", charCode, null);
			return ScriptHelper.evalInt("Module._c_Face_getCharIndex(a,b);", null);
		}
	}

	public static class Size extends Pointer {
		Size (int address) {
			super(address);
		}

		public SizeMetrics getMetrics () {
			ScriptHelper.put("a", address, null);
			return new SizeMetrics(ScriptHelper.evalInt("Module._c_Size_getMetrics(a)", null));
		}
	}

	public static class SizeMetrics extends Pointer {
		SizeMetrics (int address) {
			super(address);
		}

		public int getXppem () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_SizeMetrics_getXppem(a);", null);
		}

		public int getYppem () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_SizeMetrics_getYppem(a);", null);
		}

		public int getXScale () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_SizeMetrics_getXscale(a);", null);
		}

		public int getYscale () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_SizeMetrics_getYscale(a);", null);
		}

		public int getAscender () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_SizeMetrics_getAscender(a);", null);
		}

		public int getDescender () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_SizeMetrics_getDescender(a);", null);
		}

		public int getHeight () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_SizeMetrics_getHeight(a);", null);
		}

		public int getMaxAdvance () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_SizeMetrics_getMaxAdvance(a);", null);
		}
	}

	public static class GlyphSlot extends Pointer {
		GlyphSlot (int address) {
			super(address);
		}

		public GlyphMetrics getMetrics () {
			ScriptHelper.put("a", address, null);
			return new GlyphMetrics(ScriptHelper.evalInt("Module._c_GlyphSlot_getMetrics(a)", null));
		}

		public int getLinearHoriAdvance () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_GlyphSlot_getLinearHoriAdvance(a);", null);
		}

		public int getLinearVertAdvance () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_GlyphSlot_getLinearVertAdvance(a);", null);
		}

		public int getAdvanceX () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_GlyphSlot_getAdvanceX(a);", null);
		}

		public int getAdvanceY () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_GlyphSlot_getAdvanceY(a);", null);
		}

		public int getFormat () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_GlyphSlot_getFormat(a);", null);
		}

		public Bitmap getBitmap () {
			ScriptHelper.put("a", address, null);
			return new Bitmap(ScriptHelper.evalInt("Module._c_GlyphSlot_getBitmap(a)", null));
		}

		public int getBitmapLeft () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_GlyphSlot_getBitmapLeft(a);", null);
		}

		public int getBitmapTop () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_GlyphSlot_getBitmapTop(a);", null);
		}

		public boolean renderGlyph (int renderMode) {
			ScriptHelper.put("a", address, null);
			ScriptHelper.put("b", renderMode, null);
			return ScriptHelper.evalBoolean("!!Module._c_GlyphSlot_renderGlyph(a,b);", null);
		}

		public Glyph getGlyph () {
			ScriptHelper.put("a", address, null);
			int glyph = ScriptHelper.evalInt("Module._c_GlyphSlot_getGlyph(a);", null);
			if (glyph == 0) throw new GdxRuntimeException("Couldn't get glyph");
			return new Glyph(glyph);
		}
	}

	public static class Glyph extends Pointer implements Disposable {
		private boolean rendered;

		Glyph (int address) {
			super(address);
		}

		@Override
		public void dispose () {
			ScriptHelper.put("a", address, null);
			ScriptHelper.evalNoResult("Module._c_Glyph_done(a);", null);
		}

		private int bTI (boolean bool) {
			return bool == true ? 1 : 0;
		}

		public void strokeBorder (Stroker stroker, boolean inside) {
			ScriptHelper.put("a", address, null);
			ScriptHelper.put("b", stroker, null);
			ScriptHelper.put("c", bTI(inside), null);
			address = ScriptHelper.evalInt("Module._c_Glyph_strokeBorder(a,b,c);", null);
		}

		public void toBitmap (int renderMode) {
			ScriptHelper.put("a", address, null);
			ScriptHelper.put("b", renderMode, null);
			int bitmap = ScriptHelper.evalInt("Module._c_Glyph_toBitmap(a,b);", null);
			if (bitmap == 0) throw new GdxRuntimeException("Couldn't render glyph");
			address = bitmap;
			rendered = true;
		}

		public Bitmap getBitmap () {
			if (!rendered) {
				throw new GdxRuntimeException("Glyph is not yet rendered");
			}
			ScriptHelper.put("a", address, null);
			return new Bitmap(ScriptHelper.evalInt("Module._c_Glyph_getBitmap(a)", null));
		}

		public int getLeft () {
			if (!rendered) {
				throw new GdxRuntimeException("Glyph is not yet rendered");
			}
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_Glyph_getLeft(a);", null);
		}

		public int getTop () {
			if (!rendered) {
				throw new GdxRuntimeException("Glyph is not yet rendered");
			}
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_Glyph_getTop(a);", null);
		}
	}

	public static class Bitmap extends Pointer {
		Bitmap (int address) {
			super(address);
		}

		public int getRows () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_Bitmap_getRows(a);", null);
		}

		public int getWidth () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_Bitmap_getWidth(a);", null);
		}

		public int getPitch () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_Bitmap_getPitch(a);", null);
		}

		public ByteBuffer getBuffer () {
			if (getRows() == 0)
				// Issue #768 - CheckJNI frowns upon env->NewDirectByteBuffer with NULL buffer or capacity 0
				// "JNI WARNING: invalid values for address (0x0) or capacity (0)"
				// FreeType sets FT_Bitmap::buffer to NULL when the bitmap is empty (e.g. for ' ')
				// JNICheck is on by default on emulators and might have a point anyway...
				// So let's avoid this and just return a dummy non-null non-zero buffer
				return BufferUtils.newByteBuffer(1);
			ScriptHelper.put("a", address, null);
			int offset = ScriptHelper.evalInt("Module._c_Bitmap_getBufferAddress(a);", null);
			int length = ScriptHelper.evalInt("Module._c_Bitmap_getBufferSize(a);", null);
			ScriptHelper.put("b", offset, null);
			ScriptHelper.put("c", length, null);
			Int8Array as = ScriptHelper.evalCasting("Module.HEAP8.subarray(b,b+c)", Int8Array.class, null);
			ArrayBuffer aBuf = as.getBuffer();
			ByteBuffer buf = FreeTypeUtil.newDirectReadWriteByteBuffer(aBuf, length, offset);

			return buf;
		}

		public Pixmap getPixmap (Format format, Color color, float gamma) {
			int width = getWidth(), rows = getRows();
			ByteBuffer src = getBuffer();
			FreeTypePixmap pixmap;
			ByteBuffer changedPixels;
			int pixelMode = getPixelMode();
			int rowBytes = Math.abs(getPitch()); // We currently ignore negative pitch.
			if (color == Color.WHITE && pixelMode == FT_PIXEL_MODE_GRAY && rowBytes == width && gamma == 1) {
				pixmap = new FreeTypePixmap(width, rows, Format.Alpha);
				changedPixels = pixmap.getRealPixels();
				BufferUtils.copy(src, changedPixels, pixmap.getRealPixels().capacity());
			} else {
				pixmap = new FreeTypePixmap(width, rows, Format.RGBA8888);
				int rgba = Color.rgba8888(color);
				byte[] srcRow = new byte[rowBytes];
				int[] dstRow = new int[width];
				changedPixels = pixmap.getRealPixels();
				IntBuffer dst = changedPixels.asIntBuffer();
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
							float alpha;

							// Zero raised to any power is always zero.
							// 255 (=one) raised to any power is always one
							// This means that we only have to calculate Math.pow() when alpha is NOT zero and NOT one
							if ((srcRow[x] & 0xff) == 0) {
								alpha = 0f;
							} else if ((srcRow[x] & 0xff) == 255) {
								alpha = 1f;
							} else {
								alpha = (float)Math.pow(((srcRow[x] & 0xff) / 255f), gamma); // Inverse gamma.
							}
							dstRow[x] = rgb | (int)(a * alpha);
						}
						dst.put(dstRow);
					}
				}
			}

			pixmap.putPixelsBack(changedPixels);
			pixmap.setPixelsNull();

			Pixmap converted = pixmap;
			if (pixmap.getFormat() != null && format != pixmap.getFormat()) {
				converted = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), format);
				pixmap.setBlending(Blending.None);
				converted.drawPixmap(pixmap, 0, 0);
				pixmap.dispose();
			}
			return converted;
		}

		public int getNumGray () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_Bitmap_getNumGray(a);", null);
		}


		public int getPixelMode () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_Bitmap_getPixelMode(a);", null);
		}
	}

	public static class GlyphMetrics extends Pointer {
		GlyphMetrics (int address) {
			super(address);
		}

		public int getWidth () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_GlyphMetrics_getWidth(a);", null);
		}

		public int getHeight () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_GlyphMetrics_getHeight(a);", null);
		}

		public int getHoriBearingX () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_GlyphMetrics_getHoriBearingX(a);", null);
		}

		public int getHoriBearingY () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_GlyphMetrics_getHoriBearingY(a);", null);
		}

		public int getHoriAdvance () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_GlyphMetrics_getHoriAdvance(a);", null);
		}

		public int getVertBearingX () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_GlyphMetrics_getVertBearingX(a);", null);
		}

		public int getVertBearingY () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_GlyphMetrics_getVertBearingY(a);", null);
		}

		public int getVertAdvance () {
			ScriptHelper.put("a", address, null);
			return ScriptHelper.evalInt("Module._c_GlyphMetrics_getVertAdvance(a);", null);
		}
	}

	public static class Stroker extends Pointer implements Disposable {
		Stroker (int address) {
			super(address);
		}

		public void set (int radius, int lineCap, int lineJoin, int miterLimit) {
			ScriptHelper.put("a", address, null);
			ScriptHelper.put("b", radius, null);
			ScriptHelper.put("c", lineCap, null);
			ScriptHelper.put("d", lineJoin, null);
			ScriptHelper.put("e", miterLimit, null);
			ScriptHelper.evalNoResult("Module._c_Stroker_set(a,b,c,d,e);", null);
		}

		@Override
		public void dispose () {
			ScriptHelper.put("a", address, null);
			ScriptHelper.evalNoResult("Module._c_Stroker_done(a);", null);
		}
	}

	public static int FT_PIXEL_MODE_NONE = 0;
	public static int FT_PIXEL_MODE_MONO = 1;
	public static int FT_PIXEL_MODE_GRAY = 2;
	public static int FT_PIXEL_MODE_GRAY2 = 3;
	public static int FT_PIXEL_MODE_GRAY4 = 4;
	public static int FT_PIXEL_MODE_LCD = 5;
	public static int FT_PIXEL_MODE_LCD_V = 6;

	private static int encode (char a, char b, char c, char d) {
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

	public static Library initFreeType () {
		int address = ScriptHelper.evalInt("Module._c_FreeType_initFreeTypeJni();", null);
		if (address == 0)
			throw new GdxRuntimeException("Couldn't initialize FreeType library");
		else
			return new Library(address);
	}

	public static int toInt (int value) {
		return ((value + 63) & -64) >> 6;
	}
}
