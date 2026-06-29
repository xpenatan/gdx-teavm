#include "teavm_freetype.h"

#include <limits.h>
#include <stddef.h>
#include <stdlib.h>
#include <string.h>

#include <ft2build.h>
#include FT_FREETYPE_H
#include FT_GLYPH_H
#include FT_STROKER_H

#define TEAVM_FT_PTR(type, value) ((type)(intptr_t)(value))
#define TEAVM_FT_ADDR(value) ((int64_t)(intptr_t)(value))

static int32_t teavm_freetype_last_error = 0;

static int32_t teavm_freetype_success(FT_Error error) {
    if (error != 0) {
        teavm_freetype_last_error = (int32_t)error;
        return 0;
    }
    return 1;
}

static int32_t teavm_freetype_abs_int(int32_t value) {
    return value < 0 ? -value : value;
}

int32_t c_FreeType_getLastErrorCode(void) {
    return teavm_freetype_last_error;
}

int64_t c_FreeType_initFreeTypeJni(void) {
    FT_Library library = NULL;
    FT_Error error = FT_Init_FreeType(&library);
    if (error != 0) {
        teavm_freetype_last_error = (int32_t)error;
        return 0;
    }
    return TEAVM_FT_ADDR(library);
}

void c_Library_doneFreeType(int64_t library) {
    if (library != 0) {
        FT_Done_FreeType(TEAVM_FT_PTR(FT_Library, library));
    }
}

int64_t c_Library_newMemoryFace(int64_t library, void* data, int32_t dataSize, int32_t faceIndex) {
    FT_Face face = NULL;
    FT_Error error = FT_New_Memory_Face(TEAVM_FT_PTR(FT_Library, library),
            (const FT_Byte*)data, (FT_Long)dataSize, (FT_Long)faceIndex, &face);
    if (error != 0) {
        teavm_freetype_last_error = (int32_t)error;
        return 0;
    }
    return TEAVM_FT_ADDR(face);
}

int64_t c_Library_strokerNew(int64_t library) {
    FT_Stroker stroker = NULL;
    FT_Error error = FT_Stroker_New(TEAVM_FT_PTR(FT_Library, library), &stroker);
    if (error != 0) {
        teavm_freetype_last_error = (int32_t)error;
        return 0;
    }
    return TEAVM_FT_ADDR(stroker);
}

void c_Face_doneFace(int64_t face) {
    if (face != 0) {
        FT_Done_Face(TEAVM_FT_PTR(FT_Face, face));
    }
}

int32_t c_Face_getFaceFlags(int64_t face) {
    return (int32_t)TEAVM_FT_PTR(FT_Face, face)->face_flags;
}

int32_t c_Face_getStyleFlags(int64_t face) {
    return (int32_t)TEAVM_FT_PTR(FT_Face, face)->style_flags;
}

int32_t c_Face_getNumGlyphs(int64_t face) {
    return (int32_t)TEAVM_FT_PTR(FT_Face, face)->num_glyphs;
}

int32_t c_Face_getAscender(int64_t face) {
    return (int32_t)TEAVM_FT_PTR(FT_Face, face)->ascender;
}

int32_t c_Face_getDescender(int64_t face) {
    return (int32_t)TEAVM_FT_PTR(FT_Face, face)->descender;
}

int32_t c_Face_getHeight(int64_t face) {
    return (int32_t)TEAVM_FT_PTR(FT_Face, face)->height;
}

int32_t c_Face_getMaxAdvanceWidth(int64_t face) {
    return (int32_t)TEAVM_FT_PTR(FT_Face, face)->max_advance_width;
}

int32_t c_Face_getMaxAdvanceHeight(int64_t face) {
    return (int32_t)TEAVM_FT_PTR(FT_Face, face)->max_advance_height;
}

int32_t c_Face_getUnderlinePosition(int64_t face) {
    return (int32_t)TEAVM_FT_PTR(FT_Face, face)->underline_position;
}

int32_t c_Face_getUnderlineThickness(int64_t face) {
    return (int32_t)TEAVM_FT_PTR(FT_Face, face)->underline_thickness;
}

int32_t c_Face_selectSize(int64_t face, int32_t strikeIndex) {
    return teavm_freetype_success(FT_Select_Size(TEAVM_FT_PTR(FT_Face, face), strikeIndex));
}

int32_t c_Face_setCharSize(int64_t face, int32_t charWidth, int32_t charHeight, int32_t horzResolution, int32_t vertResolution) {
    return teavm_freetype_success(FT_Set_Char_Size(TEAVM_FT_PTR(FT_Face, face), charWidth, charHeight, horzResolution, vertResolution));
}

int32_t c_Face_setPixelSizes(int64_t face, int32_t pixelWidth, int32_t pixelHeight) {
    return teavm_freetype_success(FT_Set_Pixel_Sizes(TEAVM_FT_PTR(FT_Face, face), pixelWidth, pixelHeight));
}

int32_t c_Face_loadGlyph(int64_t face, int32_t glyphIndex, int32_t loadFlags) {
    return teavm_freetype_success(FT_Load_Glyph(TEAVM_FT_PTR(FT_Face, face), (FT_UInt)glyphIndex, (FT_Int32)loadFlags));
}

int32_t c_Face_loadChar(int64_t face, int32_t charCode, int32_t loadFlags) {
    return teavm_freetype_success(FT_Load_Char(TEAVM_FT_PTR(FT_Face, face), (FT_ULong)charCode, (FT_Int32)loadFlags));
}

int64_t c_Face_getGlyph(int64_t face) {
    return TEAVM_FT_ADDR(TEAVM_FT_PTR(FT_Face, face)->glyph);
}

int64_t c_Face_getSize(int64_t face) {
    return TEAVM_FT_ADDR(TEAVM_FT_PTR(FT_Face, face)->size);
}

int32_t c_Face_hasKerning(int64_t face) {
    return FT_HAS_KERNING(TEAVM_FT_PTR(FT_Face, face)) ? 1 : 0;
}

int32_t c_Face_getKerning(int64_t face, int32_t leftGlyph, int32_t rightGlyph, int32_t kernMode) {
    FT_Vector kerning;
    FT_Error error = FT_Get_Kerning(TEAVM_FT_PTR(FT_Face, face), (FT_UInt)leftGlyph, (FT_UInt)rightGlyph, (FT_UInt)kernMode, &kerning);
    if (error != 0) {
        teavm_freetype_last_error = (int32_t)error;
        return 0;
    }
    return (int32_t)kerning.x;
}

int32_t c_Face_getCharIndex(int64_t face, int32_t charCode) {
    return (int32_t)FT_Get_Char_Index(TEAVM_FT_PTR(FT_Face, face), (FT_ULong)charCode);
}

int64_t c_Size_getMetrics(int64_t size) {
    return TEAVM_FT_ADDR(&TEAVM_FT_PTR(FT_Size, size)->metrics);
}

int32_t c_SizeMetrics_getXppem(int64_t metrics) {
    return (int32_t)TEAVM_FT_PTR(FT_Size_Metrics*, metrics)->x_ppem;
}

int32_t c_SizeMetrics_getYppem(int64_t metrics) {
    return (int32_t)TEAVM_FT_PTR(FT_Size_Metrics*, metrics)->y_ppem;
}

int32_t c_SizeMetrics_getXscale(int64_t metrics) {
    return (int32_t)TEAVM_FT_PTR(FT_Size_Metrics*, metrics)->x_scale;
}

int32_t c_SizeMetrics_getYscale(int64_t metrics) {
    return (int32_t)TEAVM_FT_PTR(FT_Size_Metrics*, metrics)->y_scale;
}

int32_t c_SizeMetrics_getAscender(int64_t metrics) {
    return (int32_t)TEAVM_FT_PTR(FT_Size_Metrics*, metrics)->ascender;
}

int32_t c_SizeMetrics_getDescender(int64_t metrics) {
    return (int32_t)TEAVM_FT_PTR(FT_Size_Metrics*, metrics)->descender;
}

int32_t c_SizeMetrics_getHeight(int64_t metrics) {
    return (int32_t)TEAVM_FT_PTR(FT_Size_Metrics*, metrics)->height;
}

int32_t c_SizeMetrics_getMaxAdvance(int64_t metrics) {
    return (int32_t)TEAVM_FT_PTR(FT_Size_Metrics*, metrics)->max_advance;
}

int64_t c_GlyphSlot_getMetrics(int64_t slot) {
    return TEAVM_FT_ADDR(&TEAVM_FT_PTR(FT_GlyphSlot, slot)->metrics);
}

int32_t c_GlyphSlot_getLinearHoriAdvance(int64_t slot) {
    return (int32_t)TEAVM_FT_PTR(FT_GlyphSlot, slot)->linearHoriAdvance;
}

int32_t c_GlyphSlot_getLinearVertAdvance(int64_t slot) {
    return (int32_t)TEAVM_FT_PTR(FT_GlyphSlot, slot)->linearVertAdvance;
}

int32_t c_GlyphSlot_getAdvanceX(int64_t slot) {
    return (int32_t)TEAVM_FT_PTR(FT_GlyphSlot, slot)->advance.x;
}

int32_t c_GlyphSlot_getAdvanceY(int64_t slot) {
    return (int32_t)TEAVM_FT_PTR(FT_GlyphSlot, slot)->advance.y;
}

int32_t c_GlyphSlot_getFormat(int64_t slot) {
    return (int32_t)TEAVM_FT_PTR(FT_GlyphSlot, slot)->format;
}

int64_t c_GlyphSlot_getBitmap(int64_t slot) {
    return TEAVM_FT_ADDR(&TEAVM_FT_PTR(FT_GlyphSlot, slot)->bitmap);
}

int32_t c_GlyphSlot_getBitmapLeft(int64_t slot) {
    return TEAVM_FT_PTR(FT_GlyphSlot, slot)->bitmap_left;
}

int32_t c_GlyphSlot_getBitmapTop(int64_t slot) {
    return TEAVM_FT_PTR(FT_GlyphSlot, slot)->bitmap_top;
}

int32_t c_GlyphSlot_renderGlyph(int64_t slot, int32_t renderMode) {
    return teavm_freetype_success(FT_Render_Glyph(TEAVM_FT_PTR(FT_GlyphSlot, slot), (FT_Render_Mode)renderMode));
}

int64_t c_GlyphSlot_getGlyph(int64_t glyphSlot) {
    FT_Glyph glyph = NULL;
    FT_Error error = FT_Get_Glyph(TEAVM_FT_PTR(FT_GlyphSlot, glyphSlot), &glyph);
    if (error != 0) {
        teavm_freetype_last_error = (int32_t)error;
        return 0;
    }
    return TEAVM_FT_ADDR(glyph);
}

int32_t c_GlyphMetrics_getWidth(int64_t metrics) {
    return (int32_t)TEAVM_FT_PTR(FT_Glyph_Metrics*, metrics)->width;
}

int32_t c_GlyphMetrics_getHeight(int64_t metrics) {
    return (int32_t)TEAVM_FT_PTR(FT_Glyph_Metrics*, metrics)->height;
}

int32_t c_GlyphMetrics_getHoriBearingX(int64_t metrics) {
    return (int32_t)TEAVM_FT_PTR(FT_Glyph_Metrics*, metrics)->horiBearingX;
}

int32_t c_GlyphMetrics_getHoriBearingY(int64_t metrics) {
    return (int32_t)TEAVM_FT_PTR(FT_Glyph_Metrics*, metrics)->horiBearingY;
}

int32_t c_GlyphMetrics_getHoriAdvance(int64_t metrics) {
    return (int32_t)TEAVM_FT_PTR(FT_Glyph_Metrics*, metrics)->horiAdvance;
}

int32_t c_GlyphMetrics_getVertBearingX(int64_t metrics) {
    return (int32_t)TEAVM_FT_PTR(FT_Glyph_Metrics*, metrics)->vertBearingX;
}

int32_t c_GlyphMetrics_getVertBearingY(int64_t metrics) {
    return (int32_t)TEAVM_FT_PTR(FT_Glyph_Metrics*, metrics)->vertBearingY;
}

int32_t c_GlyphMetrics_getVertAdvance(int64_t metrics) {
    return (int32_t)TEAVM_FT_PTR(FT_Glyph_Metrics*, metrics)->vertAdvance;
}

void c_Glyph_done(int64_t glyph) {
    if (glyph != 0) {
        FT_Done_Glyph(TEAVM_FT_PTR(FT_Glyph, glyph));
    }
}

int64_t c_Glyph_strokeBorder(int64_t glyph, int64_t stroker, int32_t inside) {
    FT_Glyph borderGlyph = TEAVM_FT_PTR(FT_Glyph, glyph);
    FT_Error error = FT_Glyph_StrokeBorder(&borderGlyph, TEAVM_FT_PTR(FT_Stroker, stroker), inside ? 1 : 0, 1);
    if (error != 0) {
        teavm_freetype_last_error = (int32_t)error;
        return 0;
    }
    return TEAVM_FT_ADDR(borderGlyph);
}

int64_t c_Glyph_toBitmap(int64_t glyph, int32_t renderMode) {
    FT_Glyph bitmap = TEAVM_FT_PTR(FT_Glyph, glyph);
    FT_Error error = FT_Glyph_To_Bitmap(&bitmap, (FT_Render_Mode)renderMode, NULL, 1);
    if (error != 0) {
        teavm_freetype_last_error = (int32_t)error;
        return 0;
    }
    return TEAVM_FT_ADDR(bitmap);
}

int64_t c_Glyph_getBitmap(int64_t glyph) {
    FT_BitmapGlyph bitmapGlyph = TEAVM_FT_PTR(FT_BitmapGlyph, glyph);
    return TEAVM_FT_ADDR(&bitmapGlyph->bitmap);
}

int32_t c_Glyph_getLeft(int64_t glyph) {
    FT_BitmapGlyph bitmapGlyph = TEAVM_FT_PTR(FT_BitmapGlyph, glyph);
    return bitmapGlyph->left;
}

int32_t c_Glyph_getTop(int64_t glyph) {
    FT_BitmapGlyph bitmapGlyph = TEAVM_FT_PTR(FT_BitmapGlyph, glyph);
    return bitmapGlyph->top;
}

int32_t c_Bitmap_getRows(int64_t bitmap) {
    return (int32_t)TEAVM_FT_PTR(FT_Bitmap*, bitmap)->rows;
}

int32_t c_Bitmap_getWidth(int64_t bitmap) {
    return (int32_t)TEAVM_FT_PTR(FT_Bitmap*, bitmap)->width;
}

int32_t c_Bitmap_getPitch(int64_t bitmap) {
    return TEAVM_FT_PTR(FT_Bitmap*, bitmap)->pitch;
}

int32_t c_Bitmap_getBufferSize(int64_t bitmap) {
    FT_Bitmap* bmp = TEAVM_FT_PTR(FT_Bitmap*, bitmap);
    int64_t size = (int64_t)bmp->rows * (int64_t)teavm_freetype_abs_int(bmp->pitch);
    if (size <= 0 || size > INT_MAX) {
        return 0;
    }
    return (int32_t)size;
}

void c_Bitmap_copyBuffer(int64_t bitmap, void* target, int32_t targetSize) {
    FT_Bitmap* bmp = TEAVM_FT_PTR(FT_Bitmap*, bitmap);
    int32_t copySize = c_Bitmap_getBufferSize(bitmap);
    if (target == NULL || targetSize <= 0 || copySize <= 0 || bmp->buffer == NULL) {
        return;
    }
    if (copySize > targetSize) {
        copySize = targetSize;
    }
    memcpy(target, bmp->buffer, (size_t)copySize);
}

int32_t c_Bitmap_getNumGray(int64_t bitmap) {
    return (int32_t)TEAVM_FT_PTR(FT_Bitmap*, bitmap)->num_grays;
}

int32_t c_Bitmap_getPixelMode(int64_t bitmap) {
    return (int32_t)TEAVM_FT_PTR(FT_Bitmap*, bitmap)->pixel_mode;
}

void c_Stroker_set(int64_t stroker, int32_t radius, int32_t lineCap, int32_t lineJoin, int32_t miterLimit) {
    FT_Stroker_Set(TEAVM_FT_PTR(FT_Stroker, stroker), radius,
            (FT_Stroker_LineCap)lineCap, (FT_Stroker_LineJoin)lineJoin, miterLimit);
}

void c_Stroker_done(int64_t stroker) {
    if (stroker != 0) {
        FT_Stroker_Done(TEAVM_FT_PTR(FT_Stroker, stroker));
    }
}
