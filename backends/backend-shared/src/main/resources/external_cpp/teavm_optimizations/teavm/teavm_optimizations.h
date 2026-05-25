#ifndef TEAVM_OPTIMIZATIONS_H
#define TEAVM_OPTIMIZATIONS_H

#include <stdint.h>

float teavm_fastmath_sin_deg(float degrees);
float teavm_fastmath_cos_deg(float degrees);
void teavm_spritebatch_draw_sprite(void* batch_obj, void* sprite_obj, int32_t texture_width, int32_t texture_height);
void teavm_spritebatch_draw_sprite_array(void* batch_obj, void* sprites_array_obj, int32_t count, float rotation_delta, float scale);
void teavm_spritebatch_draw_texture_transform(void* batch_obj, void* texture, int32_t texture_width, int32_t texture_height, float x, float y, float origin_x, float origin_y, float width, float height, float scale_x, float scale_y, float rotation, int32_t src_x, int32_t src_y, int32_t src_width, int32_t src_height, int32_t flip_x, int32_t flip_y);
void teavm_spritebatch_draw_texture_rect(void* batch_obj, void* texture, int32_t texture_width, int32_t texture_height, float x, float y, float width, float height);
void teavm_sprite_update_vertices(void* sprite_obj);
void teavm_matrix4_mul(void* mata_obj, void* matb_obj);
void teavm_matrix4_mul_left(void* target_obj, void* left_obj);
float teavm_matrix4_det(void* values_obj);
int32_t teavm_matrix4_inv(void* values_obj);
void teavm_matrix4_mul_vec(void* mat_obj, void* vecs_obj, int32_t offset, int32_t num_vecs, int32_t stride);
void teavm_matrix4_prj(void* mat_obj, void* vecs_obj, int32_t offset, int32_t num_vecs, int32_t stride);
void teavm_matrix4_rot(void* mat_obj, void* vecs_obj, int32_t offset, int32_t num_vecs, int32_t stride);
int64_t teavm_memory_heap_used_bytes(void);
int64_t teavm_memory_heap_free_bytes(void);
int64_t teavm_memory_heap_committed_bytes(void);
int64_t teavm_memory_heap_max_bytes(void);
int64_t teavm_memory_direct_buffer_live_bytes(void);
int32_t teavm_memory_direct_buffer_count(void);

#endif
