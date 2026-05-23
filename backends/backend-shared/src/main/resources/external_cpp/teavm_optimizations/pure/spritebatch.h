#ifndef SPRITEBATCH_H
#define SPRITEBATCH_H

#include <stdint.h>

enum {
    SPRITEBATCH_SPRITE_SIZE = 20
};

void spritebatch_update_sprite_vertices(float* vertices, float x, float y, float origin_x, float origin_y,
        float width, float height, float scale_x, float scale_y, float rotation);
void spritebatch_fill_texture_transform(float* out, float color, float inv_tex_width, float inv_tex_height,
        float x, float y, float origin_x, float origin_y, float width, float height,
        float scale_x, float scale_y, float rotation,
        int32_t src_x, int32_t src_y, int32_t src_width, int32_t src_height,
        int32_t flip_x, int32_t flip_y);
void spritebatch_fill_texture_rect(float* out, float color, float x, float y, float width, float height);

#endif
