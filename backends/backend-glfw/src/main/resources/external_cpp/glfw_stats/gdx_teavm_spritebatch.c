#include <stdint.h>
#include <math.h>
#include <string.h>
#include "../../src/core.h"
#include "../../src/exceptions.h"
#include "../../src/classes/com/badlogic/gdx/graphics/Texture.h"
#include "../../src/classes/com/badlogic/gdx/graphics/TextureData.h"
#include "../../src/classes/com/badlogic/gdx/graphics/g2d/Sprite.h"
#include "../../src/classes/com/badlogic/gdx/graphics/g2d/SpriteBatch.h"
#include "../../src/classes/java/lang/IllegalStateException.h"
#include "../../src/classes/java/lang/RuntimeException.h"
#include "../../src/classes/org/teavm/runtime/Allocator.h"
#include "../../src/classes/org/teavm/runtime/ExceptionHandling.h"

#define GDX_SPRITE_SIZE 20
#define GDX_SPRITE_WIDTH 32.0f
#define GDX_SPRITE_HALF_WIDTH 16.0f
#define GDX_SIN_MASK 16383
#define GDX_SIN_COUNT 16384
#define GDX_DEG_TO_INDEX 0x1.6c16c2p5f
#define GDX_RAD_FULL 6.28318530717958647692f

static float gdx_teavm_sin_table[GDX_SIN_COUNT];
static int32_t gdx_teavm_sin_table_ready;
static int32_t gdx_teavm_sin_cos_cache_ready;
static float gdx_teavm_sin_cos_cache_rotation;
static float gdx_teavm_sin_cos_cache_sin;
static float gdx_teavm_sin_cos_cache_cos;

static float* gdx_teavm_get_sin_table(void) {
    if (!gdx_teavm_sin_table_ready) {
        for (int32_t i = 0; i < GDX_SIN_COUNT; i++) {
            gdx_teavm_sin_table[i] = sinf(((float)i + 0.5f) / (float)GDX_SIN_COUNT * GDX_RAD_FULL);
        }
        for (int32_t i = 0; i < 360; i += 90) {
            gdx_teavm_sin_table[(int32_t)((float)i * GDX_DEG_TO_INDEX) & GDX_SIN_MASK] =
                    sinf((float)i * GDX_RAD_FULL / 360.0f);
        }
        gdx_teavm_sin_table_ready = 1;
    }
    return gdx_teavm_sin_table;
}

static void gdx_teavm_throw_spritebatch_not_drawing(void) {
    void* exception = meth_otr_Allocator_allocate(&jl_IllegalStateException_Cls);
    meth_jl_RuntimeException__init_(exception);
    meth_otr_ExceptionHandling_throwException(exception);
}

static void gdx_teavm_spritebatch_append_sprite(void* batch_obj, cls_cbggg_SpriteBatch* batch,
        cls_cbggg_Sprite* sprite, TeaVM_Array* batch_vertices_array, int32_t* current_idx_ptr) {
    void* texture = sprite->parent.fld_texture;
    int32_t current_idx = *current_idx_ptr;

    if (texture != batch->fld_lastTexture) {
        batch->fld_idx = current_idx;
        meth_cbggg_SpriteBatch_flush(batch_obj);
        teavm_gc_writeBarrier(batch_obj);
        batch->fld_lastTexture = texture;

        void* texture_data = TEAVM_FIELD(texture, cls_cbgg_Texture, fld_data);
        batch->fld_invTexWidth = 1.0f / (float) TEAVM_METHOD(texture_data, cbgg_TextureData_VT, virt_getWidth)(texture_data);
        batch->fld_invTexHeight = 1.0f / (float) TEAVM_METHOD(texture_data, cbgg_TextureData_VT, virt_getHeight)(texture_data);
        current_idx = batch->fld_idx;
    }

    if (current_idx > TEAVM_ARRAY_LENGTH(batch_vertices_array) - GDX_SPRITE_SIZE) {
        batch->fld_idx = current_idx;
        meth_cbggg_SpriteBatch_flush(batch_obj);
        current_idx = batch->fld_idx;
    }

    TeaVM_Array* sprite_vertices_array = sprite->fld_vertices;
    float* sprite_vertices = TEAVM_ARRAY_DATA(sprite_vertices_array, float);
    if (sprite->fld_dirty) {
        sprite->fld_dirty = 0;

        float local_x = -sprite->fld_originX;
        float local_y = -sprite->fld_originY;
        float local_x2 = local_x + sprite->fld_width;
        float local_y2 = local_y + sprite->fld_height;
        float world_origin_x = sprite->fld_x - local_x;
        float world_origin_y = sprite->fld_y - local_y;
        float scale_x = sprite->fld_scaleX;
        float scale_y = sprite->fld_scaleY;
        if (scale_x != 1.0f || scale_y != 1.0f) {
            local_x *= scale_x;
            local_y *= scale_y;
            local_x2 *= scale_x;
            local_y2 *= scale_y;
        }

        float x1;
        float y1;
        float x2;
        float y2;
        float x3;
        float y3;
        float x4;
        float y4;
        float rotation = sprite->fld_rotation;
        if (rotation != 0.0f) {
            float sin;
            float cos;
            if (gdx_teavm_sin_cos_cache_ready && rotation == gdx_teavm_sin_cos_cache_rotation) {
                sin = gdx_teavm_sin_cos_cache_sin;
                cos = gdx_teavm_sin_cos_cache_cos;
            }
            else {
                float* sin_table = gdx_teavm_get_sin_table();
                cos = sin_table[((int32_t) ((rotation + 90.0f) * GDX_DEG_TO_INDEX)) & GDX_SIN_MASK];
                sin = sin_table[((int32_t) (rotation * GDX_DEG_TO_INDEX)) & GDX_SIN_MASK];
                gdx_teavm_sin_cos_cache_rotation = rotation;
                gdx_teavm_sin_cos_cache_sin = sin;
                gdx_teavm_sin_cos_cache_cos = cos;
                gdx_teavm_sin_cos_cache_ready = 1;
            }
            float local_x_cos = local_x * cos;
            float local_x_sin = local_x * sin;
            float local_y_cos = local_y * cos;
            float local_y_sin = local_y * sin;
            float local_x2_cos = local_x2 * cos;
            float local_x2_sin = local_x2 * sin;
            float local_y2_cos = local_y2 * cos;
            float local_y2_sin = local_y2 * sin;

            x1 = local_x_cos - local_y_sin + world_origin_x;
            y1 = local_y_cos + local_x_sin + world_origin_y;
            x2 = local_x_cos - local_y2_sin + world_origin_x;
            y2 = local_y2_cos + local_x_sin + world_origin_y;
            x3 = local_x2_cos - local_y2_sin + world_origin_x;
            y3 = local_y2_cos + local_x2_sin + world_origin_y;
            x4 = x1 + (x3 - x2);
            y4 = y3 - (y2 - y1);
        }
        else {
            x1 = local_x + world_origin_x;
            y1 = local_y + world_origin_y;
            x2 = x1;
            y2 = local_y2 + world_origin_y;
            x3 = local_x2 + world_origin_x;
            y3 = y2;
            x4 = x3;
            y4 = y1;
        }

        sprite_vertices[0] = x1;
        sprite_vertices[1] = y1;
        sprite_vertices[5] = x2;
        sprite_vertices[6] = y2;
        sprite_vertices[10] = x3;
        sprite_vertices[11] = y3;
        sprite_vertices[15] = x4;
        sprite_vertices[16] = y4;
    }

    float* batch_vertices = TEAVM_ARRAY_DATA(batch_vertices_array, float) + current_idx;
    memcpy(batch_vertices, sprite_vertices, GDX_SPRITE_SIZE * sizeof(float));
    *current_idx_ptr = current_idx + GDX_SPRITE_SIZE;
}

void gdx_teavm_spritebatch_draw_sprite(void* batch_obj, void* sprite_obj) {
    batch_obj = teavm_nullCheck(batch_obj);
    sprite_obj = teavm_nullCheck(sprite_obj);
    cls_cbggg_SpriteBatch* batch = (cls_cbggg_SpriteBatch*) batch_obj;
    cls_cbggg_Sprite* sprite = (cls_cbggg_Sprite*) sprite_obj;
    if (!batch->fld_drawing) {
        gdx_teavm_throw_spritebatch_not_drawing();
        return;
    }

    TeaVM_Array* batch_vertices_array = batch->fld_vertices;
    int32_t current_idx = batch->fld_idx;
    gdx_teavm_spritebatch_append_sprite(batch_obj, batch, sprite, batch_vertices_array, &current_idx);
    batch->fld_idx = current_idx;
}

void gdx_teavm_spritebatch_draw_sprite_array(void* batch_obj, void* sprites_array_obj, int32_t count,
        float rotation_delta, float scale) {
    if (count <= 0) {
        return;
    }

    TeaVM_Array* sprites_array = (TeaVM_Array*) teavm_nullCheck(sprites_array_obj);
    int32_t rotate = rotation_delta != 0.0f;
    int32_t scale_changed = scale != 1.0f;
    cls_cbggg_SpriteBatch* batch = NULL;
    TeaVM_Array* batch_vertices_array = NULL;
    int32_t current_idx = 0;

    for (int32_t i = 0; i < count; i++) {
        int32_t sprite_index = teavm_checkBounds(i, sprites_array);
        void* sprite_obj = TEAVM_ARRAY_AT(sprites_array, void*, sprite_index);
        cls_cbggg_Sprite* sprite = (cls_cbggg_Sprite*) teavm_nullCheck(sprite_obj);

        if (rotate) {
            sprite->fld_rotation += rotation_delta;
            sprite->fld_dirty = 1;
        }
        if (scale_changed) {
            sprite->fld_scaleX = scale;
            sprite->fld_scaleY = scale;
            sprite->fld_dirty = 1;
        }

        if (batch == NULL) {
            batch_obj = teavm_nullCheck(batch_obj);
            batch = (cls_cbggg_SpriteBatch*) batch_obj;
            if (!batch->fld_drawing) {
                gdx_teavm_throw_spritebatch_not_drawing();
                return;
            }
            batch_vertices_array = batch->fld_vertices;
            current_idx = batch->fld_idx;
        }

        gdx_teavm_spritebatch_append_sprite(batch_obj, batch, sprite, batch_vertices_array, &current_idx);
    }

    batch->fld_idx = current_idx;
}

void gdx_teavm_spritebatch_draw_texture_transform(void* batch_obj, void* texture, float x, float y,
        float origin_x, float origin_y, float width, float height, float scale_x, float scale_y, float rotation,
        int32_t src_x, int32_t src_y, int32_t src_width, int32_t src_height, int32_t flip_x, int32_t flip_y) {
    cls_cbggg_SpriteBatch* batch = (cls_cbggg_SpriteBatch*) batch_obj;
    if (!batch->fld_drawing) {
        gdx_teavm_throw_spritebatch_not_drawing();
        return;
    }

    TeaVM_Array* batch_vertices_array = batch->fld_vertices;
    int32_t current_idx = batch->fld_idx;

    if (texture != batch->fld_lastTexture) {
        meth_cbggg_SpriteBatch_flush(batch_obj);
        teavm_gc_writeBarrier(batch_obj);
        batch->fld_lastTexture = texture;

        void* texture_data = TEAVM_FIELD(texture, cls_cbgg_Texture, fld_data);
        batch->fld_invTexWidth = 1.0f / (float) TEAVM_METHOD(texture_data, cbgg_TextureData_VT, virt_getWidth)(texture_data);
        batch->fld_invTexHeight = 1.0f / (float) TEAVM_METHOD(texture_data, cbgg_TextureData_VT, virt_getHeight)(texture_data);
        current_idx = batch->fld_idx;
    }

    if (current_idx > TEAVM_ARRAY_LENGTH(batch_vertices_array) - GDX_SPRITE_SIZE) {
        meth_cbggg_SpriteBatch_flush(batch_obj);
        current_idx = batch->fld_idx;
    }

    float world_origin_x = x + origin_x;
    float world_origin_y = y + origin_y;
    float fx = -origin_x;
    float fy = -origin_y;
    float fx2 = width - origin_x;
    float fy2 = height - origin_y;

    if (scale_x != 1.0f || scale_y != 1.0f) {
        fx *= scale_x;
        fy *= scale_y;
        fx2 *= scale_x;
        fy2 *= scale_y;
    }

    float x1;
    float y1;
    float x2;
    float y2;
    float x3;
    float y3;
    float x4;
    float y4;

    if (rotation != 0.0f) {
        float sin;
        float cos;
        if (gdx_teavm_sin_cos_cache_ready && rotation == gdx_teavm_sin_cos_cache_rotation) {
            sin = gdx_teavm_sin_cos_cache_sin;
            cos = gdx_teavm_sin_cos_cache_cos;
        }
        else {
            float* sin_table = gdx_teavm_get_sin_table();
            cos = sin_table[((int32_t) ((rotation + 90.0f) * GDX_DEG_TO_INDEX)) & GDX_SIN_MASK];
            sin = sin_table[((int32_t) (rotation * GDX_DEG_TO_INDEX)) & GDX_SIN_MASK];
            gdx_teavm_sin_cos_cache_rotation = rotation;
            gdx_teavm_sin_cos_cache_sin = sin;
            gdx_teavm_sin_cos_cache_cos = cos;
            gdx_teavm_sin_cos_cache_ready = 1;
        }
        float fx_cos = fx * cos;
        float fx_sin = fx * sin;
        float fy_cos = fy * cos;
        float fy_sin = fy * sin;
        float fx2_cos = fx2 * cos;
        float fx2_sin = fx2 * sin;
        float fy2_cos = fy2 * cos;
        float fy2_sin = fy2 * sin;

        x1 = fx_cos - fy_sin;
        y1 = fy_cos + fx_sin;
        x2 = fx_cos - fy2_sin;
        y2 = fy2_cos + fx_sin;
        x3 = fx2_cos - fy2_sin;
        y3 = fy2_cos + fx2_sin;
        x4 = x1 + (x3 - x2);
        y4 = y3 - (y2 - y1);
    }
    else {
        x1 = fx;
        y1 = fy;
        x2 = fx;
        y2 = fy2;
        x3 = fx2;
        y3 = fy2;
        x4 = fx2;
        y4 = fy;
    }

    x1 += world_origin_x;
    y1 += world_origin_y;
    x2 += world_origin_x;
    y2 += world_origin_y;
    x3 += world_origin_x;
    y3 += world_origin_y;
    x4 += world_origin_x;
    y4 += world_origin_y;

    float inv_tex_width = batch->fld_invTexWidth;
    float inv_tex_height = batch->fld_invTexHeight;
    float u = (float) src_x * inv_tex_width;
    float v = (float) (src_y + src_height) * inv_tex_height;
    float u2 = (float) (src_x + src_width) * inv_tex_width;
    float v2 = (float) src_y * inv_tex_height;

    if (flip_x) {
        float tmp = u;
        u = u2;
        u2 = tmp;
    }
    if (flip_y) {
        float tmp = v;
        v = v2;
        v2 = tmp;
    }

    float color = batch->fld_colorPacked;
    float* out = TEAVM_ARRAY_DATA(batch_vertices_array, float) + current_idx;
    out[0] = x1;
    out[1] = y1;
    out[2] = color;
    out[3] = u;
    out[4] = v;
    out[5] = x2;
    out[6] = y2;
    out[7] = color;
    out[8] = u;
    out[9] = v2;
    out[10] = x3;
    out[11] = y3;
    out[12] = color;
    out[13] = u2;
    out[14] = v2;
    out[15] = x4;
    out[16] = y4;
    out[17] = color;
    out[18] = u2;
    out[19] = v;
    batch->fld_idx = current_idx + GDX_SPRITE_SIZE;
}

void gdx_teavm_spritebatch_draw_texture_rect(void* batch_obj, void* texture, float x, float y,
        float width, float height) {
    cls_cbggg_SpriteBatch* batch = (cls_cbggg_SpriteBatch*) batch_obj;
    if (!batch->fld_drawing) {
        gdx_teavm_throw_spritebatch_not_drawing();
        return;
    }

    TeaVM_Array* batch_vertices_array = batch->fld_vertices;
    int32_t current_idx = batch->fld_idx;
    int32_t vertices_length = TEAVM_ARRAY_LENGTH(batch_vertices_array);

    if (texture != batch->fld_lastTexture) {
        meth_cbggg_SpriteBatch_flush(batch_obj);
        teavm_gc_writeBarrier(batch_obj);
        batch->fld_lastTexture = texture;

        void* texture_data = TEAVM_FIELD(texture, cls_cbgg_Texture, fld_data);
        batch->fld_invTexWidth = 1.0f / (float) TEAVM_METHOD(texture_data, cbgg_TextureData_VT, virt_getWidth)(texture_data);
        batch->fld_invTexHeight = 1.0f / (float) TEAVM_METHOD(texture_data, cbgg_TextureData_VT, virt_getHeight)(texture_data);
        current_idx = batch->fld_idx;
    }
    else if (current_idx == vertices_length) {
        meth_cbggg_SpriteBatch_flush(batch_obj);
        current_idx = batch->fld_idx;
    }

    float x2 = x + width;
    float y2 = y + height;
    float color = batch->fld_colorPacked;
    float* out = TEAVM_ARRAY_DATA(batch_vertices_array, float) + current_idx;
    out[0] = x;
    out[1] = y;
    out[2] = color;
    out[3] = 0.0f;
    out[4] = 1.0f;
    out[5] = x;
    out[6] = y2;
    out[7] = color;
    out[8] = 0.0f;
    out[9] = 0.0f;
    out[10] = x2;
    out[11] = y2;
    out[12] = color;
    out[13] = 1.0f;
    out[14] = 0.0f;
    out[15] = x2;
    out[16] = y;
    out[17] = color;
    out[18] = 1.0f;
    out[19] = 1.0f;
    batch->fld_idx = current_idx + GDX_SPRITE_SIZE;
}
