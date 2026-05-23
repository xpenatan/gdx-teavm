#include "spritebatch.h"
#include <math.h>

#define SPRITEBATCH_SIN_MASK 16383
#define SPRITEBATCH_SIN_COUNT 16384
#define SPRITEBATCH_DEG_TO_INDEX 0x1.6c16c2p5f
#define SPRITEBATCH_RAD_FULL 6.28318530717958647692f

static float spritebatch_sin_table[SPRITEBATCH_SIN_COUNT];
static int32_t spritebatch_sin_table_ready;
static int32_t spritebatch_sin_cos_cache_ready;
static float spritebatch_sin_cos_cache_rotation;
static float spritebatch_sin_cos_cache_sin;
static float spritebatch_sin_cos_cache_cos;

static float* spritebatch_get_sin_table(void) {
    if (!spritebatch_sin_table_ready) {
        for (int32_t i = 0; i < SPRITEBATCH_SIN_COUNT; i++) {
            spritebatch_sin_table[i] = sinf(((float)i + 0.5f) / (float)SPRITEBATCH_SIN_COUNT * SPRITEBATCH_RAD_FULL);
        }
        for (int32_t i = 0; i < 360; i += 90) {
            spritebatch_sin_table[(int32_t)((float)i * SPRITEBATCH_DEG_TO_INDEX) & SPRITEBATCH_SIN_MASK] =
                    sinf((float)i * SPRITEBATCH_RAD_FULL / 360.0f);
        }
        spritebatch_sin_table_ready = 1;
    }
    return spritebatch_sin_table;
}

static void spritebatch_sin_cos(float rotation, float* sin_out, float* cos_out) {
    if (spritebatch_sin_cos_cache_ready && rotation == spritebatch_sin_cos_cache_rotation) {
        *sin_out = spritebatch_sin_cos_cache_sin;
        *cos_out = spritebatch_sin_cos_cache_cos;
        return;
    }

    float* sin_table = spritebatch_get_sin_table();
    float cos_value = sin_table[((int32_t)((rotation + 90.0f) * SPRITEBATCH_DEG_TO_INDEX)) & SPRITEBATCH_SIN_MASK];
    float sin_value = sin_table[((int32_t)(rotation * SPRITEBATCH_DEG_TO_INDEX)) & SPRITEBATCH_SIN_MASK];
    spritebatch_sin_cos_cache_rotation = rotation;
    spritebatch_sin_cos_cache_sin = sin_value;
    spritebatch_sin_cos_cache_cos = cos_value;
    spritebatch_sin_cos_cache_ready = 1;
    *sin_out = sin_value;
    *cos_out = cos_value;
}

static void spritebatch_quad(float origin_x, float origin_y, float width, float height,
        float scale_x, float scale_y, float rotation,
        float* x1, float* y1, float* x2, float* y2, float* x3, float* y3, float* x4, float* y4) {
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

    if (rotation != 0.0f) {
        float sin_value;
        float cos_value;
        spritebatch_sin_cos(rotation, &sin_value, &cos_value);
        float fx_cos = fx * cos_value;
        float fx_sin = fx * sin_value;
        float fy_cos = fy * cos_value;
        float fy_sin = fy * sin_value;
        float fx2_cos = fx2 * cos_value;
        float fx2_sin = fx2 * sin_value;
        float fy2_cos = fy2 * cos_value;
        float fy2_sin = fy2 * sin_value;

        *x1 = fx_cos - fy_sin;
        *y1 = fy_cos + fx_sin;
        *x2 = fx_cos - fy2_sin;
        *y2 = fy2_cos + fx_sin;
        *x3 = fx2_cos - fy2_sin;
        *y3 = fy2_cos + fx2_sin;
        *x4 = *x1 + (*x3 - *x2);
        *y4 = *y3 - (*y2 - *y1);
    }
    else {
        *x1 = fx;
        *y1 = fy;
        *x2 = fx;
        *y2 = fy2;
        *x3 = fx2;
        *y3 = fy2;
        *x4 = fx2;
        *y4 = fy;
    }
}

void spritebatch_update_sprite_vertices(float* vertices, float x, float y, float origin_x, float origin_y,
        float width, float height, float scale_x, float scale_y, float rotation) {
    float x1;
    float y1;
    float x2;
    float y2;
    float x3;
    float y3;
    float x4;
    float y4;
    spritebatch_quad(origin_x, origin_y, width, height, scale_x, scale_y, rotation,
            &x1, &y1, &x2, &y2, &x3, &y3, &x4, &y4);

    float world_origin_x = x + origin_x;
    float world_origin_y = y + origin_y;
    vertices[0] = x1 + world_origin_x;
    vertices[1] = y1 + world_origin_y;
    vertices[5] = x2 + world_origin_x;
    vertices[6] = y2 + world_origin_y;
    vertices[10] = x3 + world_origin_x;
    vertices[11] = y3 + world_origin_y;
    vertices[15] = x4 + world_origin_x;
    vertices[16] = y4 + world_origin_y;
}

void spritebatch_fill_texture_transform(float* out, float color, float inv_tex_width, float inv_tex_height,
        float x, float y, float origin_x, float origin_y, float width, float height,
        float scale_x, float scale_y, float rotation,
        int32_t src_x, int32_t src_y, int32_t src_width, int32_t src_height,
        int32_t flip_x, int32_t flip_y) {
    float x1;
    float y1;
    float x2;
    float y2;
    float x3;
    float y3;
    float x4;
    float y4;
    spritebatch_quad(origin_x, origin_y, width, height, scale_x, scale_y, rotation,
            &x1, &y1, &x2, &y2, &x3, &y3, &x4, &y4);

    float world_origin_x = x + origin_x;
    float world_origin_y = y + origin_y;
    x1 += world_origin_x;
    y1 += world_origin_y;
    x2 += world_origin_x;
    y2 += world_origin_y;
    x3 += world_origin_x;
    y3 += world_origin_y;
    x4 += world_origin_x;
    y4 += world_origin_y;

    float u = (float)src_x * inv_tex_width;
    float v = (float)(src_y + src_height) * inv_tex_height;
    float u2 = (float)(src_x + src_width) * inv_tex_width;
    float v2 = (float)src_y * inv_tex_height;

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
}

void spritebatch_fill_texture_rect(float* out, float color, float x, float y, float width, float height) {
    float x2 = x + width;
    float y2 = y + height;
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
}
