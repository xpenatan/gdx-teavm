#include <GL/glew.h>
#include <stdint.h>

void gdx_teavm_spritebatch_draw_sprite(void* batch_obj, void* sprite_obj);
void gdx_teavm_spritebatch_draw_sprite_array(void* batch_obj, void* sprites_array_obj, int32_t count, float rotation_delta, float scale);
void gdx_teavm_spritebatch_draw_texture_transform(void* batch_obj, void* texture, float x, float y, float origin_x, float origin_y, float width, float height, float scale_x, float scale_y, float rotation, int32_t src_x, int32_t src_y, int32_t src_width, int32_t src_height, int32_t flip_x, int32_t flip_y);
void gdx_teavm_spritebatch_draw_texture_rect(void* batch_obj, void* texture, float x, float y, float width, float height);

#include "all.c"