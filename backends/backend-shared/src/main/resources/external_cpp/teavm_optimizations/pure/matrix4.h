#ifndef MATRIX4_H
#define MATRIX4_H

#include <stdint.h>

void matrix4_mul(float* mata, const float* matb);
void matrix4_mul_left(float* target, const float* left);
float matrix4_det(const float* values);
int32_t matrix4_inv(float* values);
void matrix4_mul_vec(const float* mat, float* vecs, int32_t offset, int32_t num_vecs, int32_t stride);
void matrix4_prj(const float* mat, float* vecs, int32_t offset, int32_t num_vecs, int32_t stride);
void matrix4_rot(const float* mat, float* vecs, int32_t offset, int32_t num_vecs, int32_t stride);

#endif
