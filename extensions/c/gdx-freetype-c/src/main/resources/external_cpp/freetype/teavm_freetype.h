#ifndef TEAVM_FREETYPE_H
#define TEAVM_FREETYPE_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

int32_t c_FreeType_getLastErrorCode(void);
int64_t c_FreeType_initFreeTypeJni(void);

void c_Library_doneFreeType(int64_t library);
int64_t c_Library_newMemoryFace(int64_t library, void* data, int32_t dataSize, int32_t faceIndex);
int64_t c_Library_strokerNew(int64_t library);

void c_Face_doneFace(int64_t face);
int32_t c_Face_getFaceFlags(int64_t face);
int32_t c_Face_getStyleFlags(int64_t face);
int32_t c_Face_getNumGlyphs(int64_t face);
int32_t c_Face_getAscender(int64_t face);
int32_t c_Face_getDescender(int64_t face);
int32_t c_Face_getHeight(int64_t face);
int32_t c_Face_getMaxAdvanceWidth(int64_t face);
int32_t c_Face_getMaxAdvanceHeight(int64_t face);
int32_t c_Face_getUnderlinePosition(int64_t face);
int32_t c_Face_getUnderlineThickness(int64_t face);
int32_t c_Face_selectSize(int64_t face, int32_t strikeIndex);
int32_t c_Face_setCharSize(int64_t face, int32_t charWidth, int32_t charHeight, int32_t horzResolution, int32_t vertResolution);
int32_t c_Face_setPixelSizes(int64_t face, int32_t pixelWidth, int32_t pixelHeight);
int32_t c_Face_loadGlyph(int64_t face, int32_t glyphIndex, int32_t loadFlags);
int32_t c_Face_loadChar(int64_t face, int32_t charCode, int32_t loadFlags);
int64_t c_Face_getGlyph(int64_t face);
int64_t c_Face_getSize(int64_t face);
int32_t c_Face_hasKerning(int64_t face);
int32_t c_Face_getKerning(int64_t face, int32_t leftGlyph, int32_t rightGlyph, int32_t kernMode);
int32_t c_Face_getCharIndex(int64_t face, int32_t charCode);

int64_t c_Size_getMetrics(int64_t size);
int32_t c_SizeMetrics_getXppem(int64_t metrics);
int32_t c_SizeMetrics_getYppem(int64_t metrics);
int32_t c_SizeMetrics_getXscale(int64_t metrics);
int32_t c_SizeMetrics_getYscale(int64_t metrics);
int32_t c_SizeMetrics_getAscender(int64_t metrics);
int32_t c_SizeMetrics_getDescender(int64_t metrics);
int32_t c_SizeMetrics_getHeight(int64_t metrics);
int32_t c_SizeMetrics_getMaxAdvance(int64_t metrics);

int64_t c_GlyphSlot_getMetrics(int64_t slot);
int32_t c_GlyphSlot_getLinearHoriAdvance(int64_t slot);
int32_t c_GlyphSlot_getLinearVertAdvance(int64_t slot);
int32_t c_GlyphSlot_getAdvanceX(int64_t slot);
int32_t c_GlyphSlot_getAdvanceY(int64_t slot);
int32_t c_GlyphSlot_getFormat(int64_t slot);
int64_t c_GlyphSlot_getBitmap(int64_t slot);
int32_t c_GlyphSlot_getBitmapLeft(int64_t slot);
int32_t c_GlyphSlot_getBitmapTop(int64_t slot);
int32_t c_GlyphSlot_renderGlyph(int64_t slot, int32_t renderMode);
int64_t c_GlyphSlot_getGlyph(int64_t glyphSlot);

int32_t c_GlyphMetrics_getWidth(int64_t metrics);
int32_t c_GlyphMetrics_getHeight(int64_t metrics);
int32_t c_GlyphMetrics_getHoriBearingX(int64_t metrics);
int32_t c_GlyphMetrics_getHoriBearingY(int64_t metrics);
int32_t c_GlyphMetrics_getHoriAdvance(int64_t metrics);
int32_t c_GlyphMetrics_getVertBearingX(int64_t metrics);
int32_t c_GlyphMetrics_getVertBearingY(int64_t metrics);
int32_t c_GlyphMetrics_getVertAdvance(int64_t metrics);

void c_Glyph_done(int64_t glyph);
int64_t c_Glyph_strokeBorder(int64_t glyph, int64_t stroker, int32_t inside);
int64_t c_Glyph_toBitmap(int64_t glyph, int32_t renderMode);
int64_t c_Glyph_getBitmap(int64_t glyph);
int32_t c_Glyph_getLeft(int64_t glyph);
int32_t c_Glyph_getTop(int64_t glyph);

int32_t c_Bitmap_getRows(int64_t bitmap);
int32_t c_Bitmap_getWidth(int64_t bitmap);
int32_t c_Bitmap_getPitch(int64_t bitmap);
int32_t c_Bitmap_getBufferSize(int64_t bitmap);
void c_Bitmap_copyBuffer(int64_t bitmap, void* target, int32_t targetSize);
int32_t c_Bitmap_getNumGray(int64_t bitmap);
int32_t c_Bitmap_getPixelMode(int64_t bitmap);

void c_Stroker_set(int64_t stroker, int32_t radius, int32_t lineCap, int32_t lineJoin, int32_t miterLimit);
void c_Stroker_done(int64_t stroker);

#ifdef __cplusplus
}
#endif

#endif
