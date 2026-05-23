#ifndef MATRIX4_H
#define MATRIX4_H

#include <stdint.h>
#include <string.h>

#if defined(_MSC_VER)
#define MATRIX4_INLINE static __forceinline
#elif defined(__GNUC__) || defined(__clang__)
#define MATRIX4_INLINE static inline __attribute__((always_inline))
#else
#define MATRIX4_INLINE static inline
#endif

enum {
    M00 = 0,
    M01 = 4,
    M02 = 8,
    M03 = 12,
    M10 = 1,
    M11 = 5,
    M12 = 9,
    M13 = 13,
    M20 = 2,
    M21 = 6,
    M22 = 10,
    M23 = 14,
    M30 = 3,
    M31 = 7,
    M32 = 11,
    M33 = 15
};

MATRIX4_INLINE void matrix4_mul_into(const float* a, const float* b, float* out) {
    out[M00] = a[M00] * b[M00] + a[M01] * b[M10] + a[M02] * b[M20] + a[M03] * b[M30];
    out[M01] = a[M00] * b[M01] + a[M01] * b[M11] + a[M02] * b[M21] + a[M03] * b[M31];
    out[M02] = a[M00] * b[M02] + a[M01] * b[M12] + a[M02] * b[M22] + a[M03] * b[M32];
    out[M03] = a[M00] * b[M03] + a[M01] * b[M13] + a[M02] * b[M23] + a[M03] * b[M33];
    out[M10] = a[M10] * b[M00] + a[M11] * b[M10] + a[M12] * b[M20] + a[M13] * b[M30];
    out[M11] = a[M10] * b[M01] + a[M11] * b[M11] + a[M12] * b[M21] + a[M13] * b[M31];
    out[M12] = a[M10] * b[M02] + a[M11] * b[M12] + a[M12] * b[M22] + a[M13] * b[M32];
    out[M13] = a[M10] * b[M03] + a[M11] * b[M13] + a[M12] * b[M23] + a[M13] * b[M33];
    out[M20] = a[M20] * b[M00] + a[M21] * b[M10] + a[M22] * b[M20] + a[M23] * b[M30];
    out[M21] = a[M20] * b[M01] + a[M21] * b[M11] + a[M22] * b[M21] + a[M23] * b[M31];
    out[M22] = a[M20] * b[M02] + a[M21] * b[M12] + a[M22] * b[M22] + a[M23] * b[M32];
    out[M23] = a[M20] * b[M03] + a[M21] * b[M13] + a[M22] * b[M23] + a[M23] * b[M33];
    out[M30] = a[M30] * b[M00] + a[M31] * b[M10] + a[M32] * b[M20] + a[M33] * b[M30];
    out[M31] = a[M30] * b[M01] + a[M31] * b[M11] + a[M32] * b[M21] + a[M33] * b[M31];
    out[M32] = a[M30] * b[M02] + a[M31] * b[M12] + a[M32] * b[M22] + a[M33] * b[M32];
    out[M33] = a[M30] * b[M03] + a[M31] * b[M13] + a[M32] * b[M23] + a[M33] * b[M33];
}

MATRIX4_INLINE void matrix4_mul(float* mata, const float* matb) {
    float tmp[16];
    matrix4_mul_into(mata, matb, tmp);
    memcpy(mata, tmp, sizeof(tmp));
}

MATRIX4_INLINE void matrix4_mul_left(float* target, const float* left) {
    float tmp[16];
    matrix4_mul_into(left, target, tmp);
    memcpy(target, tmp, sizeof(tmp));
}

MATRIX4_INLINE float matrix4_det(const float* v) {
    return v[M30] * v[M21] * v[M12] * v[M03] - v[M20] * v[M31] * v[M12] * v[M03]
            - v[M30] * v[M11] * v[M22] * v[M03] + v[M10] * v[M31] * v[M22] * v[M03]
            + v[M20] * v[M11] * v[M32] * v[M03] - v[M10] * v[M21] * v[M32] * v[M03]
            - v[M30] * v[M21] * v[M02] * v[M13] + v[M20] * v[M31] * v[M02] * v[M13]
            + v[M30] * v[M01] * v[M22] * v[M13] - v[M00] * v[M31] * v[M22] * v[M13]
            - v[M20] * v[M01] * v[M32] * v[M13] + v[M00] * v[M21] * v[M32] * v[M13]
            + v[M30] * v[M11] * v[M02] * v[M23] - v[M10] * v[M31] * v[M02] * v[M23]
            - v[M30] * v[M01] * v[M12] * v[M23] + v[M00] * v[M31] * v[M12] * v[M23]
            + v[M10] * v[M01] * v[M32] * v[M23] - v[M00] * v[M11] * v[M32] * v[M23]
            - v[M20] * v[M11] * v[M02] * v[M33] + v[M10] * v[M21] * v[M02] * v[M33]
            + v[M20] * v[M01] * v[M12] * v[M33] - v[M00] * v[M21] * v[M12] * v[M33]
            - v[M10] * v[M01] * v[M22] * v[M33] + v[M00] * v[M11] * v[M22] * v[M33];
}

MATRIX4_INLINE int32_t matrix4_inv(float* v) {
    float det = matrix4_det(v);
    if (det == 0.0f) {
        return 0;
    }

    float tmp[16];
    tmp[M00] = v[M12] * v[M23] * v[M31] - v[M13] * v[M22] * v[M31] + v[M13] * v[M21] * v[M32] - v[M11] * v[M23] * v[M32] - v[M12] * v[M21] * v[M33] + v[M11] * v[M22] * v[M33];
    tmp[M01] = v[M03] * v[M22] * v[M31] - v[M02] * v[M23] * v[M31] - v[M03] * v[M21] * v[M32] + v[M01] * v[M23] * v[M32] + v[M02] * v[M21] * v[M33] - v[M01] * v[M22] * v[M33];
    tmp[M02] = v[M02] * v[M13] * v[M31] - v[M03] * v[M12] * v[M31] + v[M03] * v[M11] * v[M32] - v[M01] * v[M13] * v[M32] - v[M02] * v[M11] * v[M33] + v[M01] * v[M12] * v[M33];
    tmp[M03] = v[M03] * v[M12] * v[M21] - v[M02] * v[M13] * v[M21] - v[M03] * v[M11] * v[M22] + v[M01] * v[M13] * v[M22] + v[M02] * v[M11] * v[M23] - v[M01] * v[M12] * v[M23];
    tmp[M10] = v[M13] * v[M22] * v[M30] - v[M12] * v[M23] * v[M30] - v[M13] * v[M20] * v[M32] + v[M10] * v[M23] * v[M32] + v[M12] * v[M20] * v[M33] - v[M10] * v[M22] * v[M33];
    tmp[M11] = v[M02] * v[M23] * v[M30] - v[M03] * v[M22] * v[M30] + v[M03] * v[M20] * v[M32] - v[M00] * v[M23] * v[M32] - v[M02] * v[M20] * v[M33] + v[M00] * v[M22] * v[M33];
    tmp[M12] = v[M03] * v[M12] * v[M30] - v[M02] * v[M13] * v[M30] - v[M03] * v[M10] * v[M32] + v[M00] * v[M13] * v[M32] + v[M02] * v[M10] * v[M33] - v[M00] * v[M12] * v[M33];
    tmp[M13] = v[M02] * v[M13] * v[M20] - v[M03] * v[M12] * v[M20] + v[M03] * v[M10] * v[M22] - v[M00] * v[M13] * v[M22] - v[M02] * v[M10] * v[M23] + v[M00] * v[M12] * v[M23];
    tmp[M20] = v[M11] * v[M23] * v[M30] - v[M13] * v[M21] * v[M30] + v[M13] * v[M20] * v[M31] - v[M10] * v[M23] * v[M31] - v[M11] * v[M20] * v[M33] + v[M10] * v[M21] * v[M33];
    tmp[M21] = v[M03] * v[M21] * v[M30] - v[M01] * v[M23] * v[M30] - v[M03] * v[M20] * v[M31] + v[M00] * v[M23] * v[M31] + v[M01] * v[M20] * v[M33] - v[M00] * v[M21] * v[M33];
    tmp[M22] = v[M01] * v[M13] * v[M30] - v[M03] * v[M11] * v[M30] + v[M03] * v[M10] * v[M31] - v[M00] * v[M13] * v[M31] - v[M01] * v[M10] * v[M33] + v[M00] * v[M11] * v[M33];
    tmp[M23] = v[M03] * v[M11] * v[M20] - v[M01] * v[M13] * v[M20] - v[M03] * v[M10] * v[M21] + v[M00] * v[M13] * v[M21] + v[M01] * v[M10] * v[M23] - v[M00] * v[M11] * v[M23];
    tmp[M30] = v[M12] * v[M21] * v[M30] - v[M11] * v[M22] * v[M30] - v[M12] * v[M20] * v[M31] + v[M10] * v[M22] * v[M31] + v[M11] * v[M20] * v[M32] - v[M10] * v[M21] * v[M32];
    tmp[M31] = v[M01] * v[M22] * v[M30] - v[M02] * v[M21] * v[M30] + v[M02] * v[M20] * v[M31] - v[M00] * v[M22] * v[M31] - v[M01] * v[M20] * v[M32] + v[M00] * v[M21] * v[M32];
    tmp[M32] = v[M02] * v[M11] * v[M30] - v[M01] * v[M12] * v[M30] - v[M02] * v[M10] * v[M31] + v[M00] * v[M12] * v[M31] + v[M01] * v[M10] * v[M32] - v[M00] * v[M11] * v[M32];
    tmp[M33] = v[M01] * v[M12] * v[M20] - v[M02] * v[M11] * v[M20] + v[M02] * v[M10] * v[M21] - v[M00] * v[M12] * v[M21] - v[M01] * v[M10] * v[M22] + v[M00] * v[M11] * v[M22];

    float inv_det = 1.0f / det;
    for (int32_t i = 0; i < 16; i++) {
        v[i] = tmp[i] * inv_det;
    }
    return 1;
}

MATRIX4_INLINE void matrix4_mul_vec(const float* mat, float* vecs, int32_t offset, int32_t num_vecs, int32_t stride) {
    for (int32_t i = 0; i < num_vecs; i++) {
        float* vec = vecs + offset;
        float x = vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02] + mat[M03];
        float y = vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12] + mat[M13];
        float z = vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22] + mat[M23];
        vec[0] = x;
        vec[1] = y;
        vec[2] = z;
        offset += stride;
    }
}

MATRIX4_INLINE void matrix4_prj(const float* mat, float* vecs, int32_t offset, int32_t num_vecs, int32_t stride) {
    for (int32_t i = 0; i < num_vecs; i++) {
        float* vec = vecs + offset;
        float inv_w = 1.0f / (vec[0] * mat[M30] + vec[1] * mat[M31] + vec[2] * mat[M32] + mat[M33]);
        float x = (vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02] + mat[M03]) * inv_w;
        float y = (vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12] + mat[M13]) * inv_w;
        float z = (vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22] + mat[M23]) * inv_w;
        vec[0] = x;
        vec[1] = y;
        vec[2] = z;
        offset += stride;
    }
}

MATRIX4_INLINE void matrix4_rot(const float* mat, float* vecs, int32_t offset, int32_t num_vecs, int32_t stride) {
    for (int32_t i = 0; i < num_vecs; i++) {
        float* vec = vecs + offset;
        float x = vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02];
        float y = vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12];
        float z = vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22];
        vec[0] = x;
        vec[1] = y;
        vec[2] = z;
        offset += stride;
    }
}

#undef MATRIX4_INLINE

#endif
