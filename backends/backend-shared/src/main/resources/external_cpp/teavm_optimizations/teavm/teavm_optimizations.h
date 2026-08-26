#ifndef TEAVM_OPTIMIZATIONS_H
#define TEAVM_OPTIMIZATIONS_H

#include <stdint.h>

float teavm_fastmath_sin_deg(float degrees);
float teavm_fastmath_cos_deg(float degrees);
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
