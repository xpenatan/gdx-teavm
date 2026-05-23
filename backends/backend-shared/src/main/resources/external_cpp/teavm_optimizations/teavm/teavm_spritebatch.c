#include <stdint.h>
#include <string.h>
#include "../../../src/core.h"
#include "../../../src/exceptions.h"
#include "../../../src/classes/com/badlogic/gdx/graphics/Texture.h"
#include "../../../src/classes/com/badlogic/gdx/graphics/TextureData.h"
#include "../../../src/classes/com/badlogic/gdx/graphics/g2d/Sprite.h"
#include "../../../src/classes/com/badlogic/gdx/graphics/g2d/SpriteBatch.h"
#include "../../../src/classes/java/lang/IllegalStateException.h"
#include "../../../src/classes/java/lang/RuntimeException.h"
#include "../../../src/classes/org/teavm/runtime/Allocator.h"
#include "../../../src/classes/org/teavm/runtime/ExceptionHandling.h"
#include "../pure/spritebatch.h"

static void teavm_throw_spritebatch_not_drawing(void) {
    void* exception = meth_otr_Allocator_allocate(&jl_IllegalStateException_Cls);
    meth_jl_RuntimeException__init_(exception);
    meth_otr_ExceptionHandling_throwException(exception);
}

static void teavm_spritebatch_switch_texture(void* batch_obj, cls_cbggg_SpriteBatch* batch, void* texture) {
    meth_cbggg_SpriteBatch_flush(batch_obj);
    teavm_gc_writeBarrier(batch_obj);
    batch->fld_lastTexture = texture;

    void* texture_data = TEAVM_FIELD(texture, cls_cbgg_Texture, fld_data);
    batch->fld_invTexWidth = 1.0f / (float)TEAVM_METHOD(texture_data, cbgg_TextureData_VT, virt_getWidth)(texture_data);
    batch->fld_invTexHeight = 1.0f / (float)TEAVM_METHOD(texture_data, cbgg_TextureData_VT, virt_getHeight)(texture_data);
}

static void teavm_spritebatch_append_sprite(void* batch_obj, cls_cbggg_SpriteBatch* batch,
        cls_cbggg_Sprite* sprite, TeaVM_Array* batch_vertices_array, int32_t* current_idx_ptr) {
    void* texture = sprite->parent.fld_texture;
    int32_t current_idx = *current_idx_ptr;

    if (texture != batch->fld_lastTexture) {
        batch->fld_idx = current_idx;
        teavm_spritebatch_switch_texture(batch_obj, batch, texture);
        current_idx = batch->fld_idx;
    }

    if (current_idx > TEAVM_ARRAY_LENGTH(batch_vertices_array) - SPRITEBATCH_SPRITE_SIZE) {
        batch->fld_idx = current_idx;
        meth_cbggg_SpriteBatch_flush(batch_obj);
        current_idx = batch->fld_idx;
    }

    TeaVM_Array* sprite_vertices_array = sprite->fld_vertices;
    float* sprite_vertices = TEAVM_ARRAY_DATA(sprite_vertices_array, float);
    if (sprite->fld_dirty) {
        sprite->fld_dirty = 0;
        spritebatch_update_sprite_vertices(sprite_vertices,
                sprite->fld_x, sprite->fld_y,
                sprite->fld_originX, sprite->fld_originY,
                sprite->fld_width, sprite->fld_height,
                sprite->fld_scaleX, sprite->fld_scaleY,
                sprite->fld_rotation);
    }

    float* batch_vertices = TEAVM_ARRAY_DATA(batch_vertices_array, float) + current_idx;
    memcpy(batch_vertices, sprite_vertices, SPRITEBATCH_SPRITE_SIZE * sizeof(float));
    *current_idx_ptr = current_idx + SPRITEBATCH_SPRITE_SIZE;
}

void teavm_spritebatch_draw_sprite(void* batch_obj, void* sprite_obj) {
    batch_obj = teavm_nullCheck(batch_obj);
    sprite_obj = teavm_nullCheck(sprite_obj);
    cls_cbggg_SpriteBatch* batch = (cls_cbggg_SpriteBatch*)batch_obj;
    cls_cbggg_Sprite* sprite = (cls_cbggg_Sprite*)sprite_obj;
    if (!batch->fld_drawing) {
        teavm_throw_spritebatch_not_drawing();
        return;
    }

    TeaVM_Array* batch_vertices_array = batch->fld_vertices;
    int32_t current_idx = batch->fld_idx;
    teavm_spritebatch_append_sprite(batch_obj, batch, sprite, batch_vertices_array, &current_idx);
    batch->fld_idx = current_idx;
}

void teavm_spritebatch_draw_sprite_array(void* batch_obj, void* sprites_array_obj, int32_t count,
        float rotation_delta, float scale) {
    if (count <= 0) {
        return;
    }

    TeaVM_Array* sprites_array = (TeaVM_Array*)teavm_nullCheck(sprites_array_obj);
    int32_t rotate = rotation_delta != 0.0f;
    int32_t scale_changed = scale != 1.0f;
    cls_cbggg_SpriteBatch* batch = NULL;
    TeaVM_Array* batch_vertices_array = NULL;
    int32_t current_idx = 0;

    for (int32_t i = 0; i < count; i++) {
        int32_t sprite_index = teavm_checkBounds(i, sprites_array);
        void* sprite_obj = TEAVM_ARRAY_AT(sprites_array, void*, sprite_index);
        cls_cbggg_Sprite* sprite = (cls_cbggg_Sprite*)teavm_nullCheck(sprite_obj);

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
            batch = (cls_cbggg_SpriteBatch*)batch_obj;
            if (!batch->fld_drawing) {
                teavm_throw_spritebatch_not_drawing();
                return;
            }
            batch_vertices_array = batch->fld_vertices;
            current_idx = batch->fld_idx;
        }

        teavm_spritebatch_append_sprite(batch_obj, batch, sprite, batch_vertices_array, &current_idx);
    }

    batch->fld_idx = current_idx;
}

void teavm_spritebatch_draw_texture_transform(void* batch_obj, void* texture, float x, float y,
        float origin_x, float origin_y, float width, float height, float scale_x, float scale_y, float rotation,
        int32_t src_x, int32_t src_y, int32_t src_width, int32_t src_height, int32_t flip_x, int32_t flip_y) {
    cls_cbggg_SpriteBatch* batch = (cls_cbggg_SpriteBatch*)batch_obj;
    if (!batch->fld_drawing) {
        teavm_throw_spritebatch_not_drawing();
        return;
    }

    TeaVM_Array* batch_vertices_array = batch->fld_vertices;
    int32_t current_idx = batch->fld_idx;

    if (texture != batch->fld_lastTexture) {
        teavm_spritebatch_switch_texture(batch_obj, batch, texture);
        current_idx = batch->fld_idx;
    }

    if (current_idx > TEAVM_ARRAY_LENGTH(batch_vertices_array) - SPRITEBATCH_SPRITE_SIZE) {
        meth_cbggg_SpriteBatch_flush(batch_obj);
        current_idx = batch->fld_idx;
    }

    float* out = TEAVM_ARRAY_DATA(batch_vertices_array, float) + current_idx;
    spritebatch_fill_texture_transform(out, batch->fld_colorPacked, batch->fld_invTexWidth, batch->fld_invTexHeight,
            x, y, origin_x, origin_y, width, height, scale_x, scale_y, rotation,
            src_x, src_y, src_width, src_height, flip_x, flip_y);
    batch->fld_idx = current_idx + SPRITEBATCH_SPRITE_SIZE;
}

void teavm_spritebatch_draw_texture_rect(void* batch_obj, void* texture, float x, float y,
        float width, float height) {
    cls_cbggg_SpriteBatch* batch = (cls_cbggg_SpriteBatch*)batch_obj;
    if (!batch->fld_drawing) {
        teavm_throw_spritebatch_not_drawing();
        return;
    }

    TeaVM_Array* batch_vertices_array = batch->fld_vertices;
    int32_t current_idx = batch->fld_idx;
    int32_t vertices_length = TEAVM_ARRAY_LENGTH(batch_vertices_array);

    if (texture != batch->fld_lastTexture) {
        teavm_spritebatch_switch_texture(batch_obj, batch, texture);
        current_idx = batch->fld_idx;
    }
    else if (current_idx == vertices_length) {
        meth_cbggg_SpriteBatch_flush(batch_obj);
        current_idx = batch->fld_idx;
    }

    float* out = TEAVM_ARRAY_DATA(batch_vertices_array, float) + current_idx;
    spritebatch_fill_texture_rect(out, batch->fld_colorPacked, x, y, width, height);
    batch->fld_idx = current_idx + SPRITEBATCH_SPRITE_SIZE;
}
