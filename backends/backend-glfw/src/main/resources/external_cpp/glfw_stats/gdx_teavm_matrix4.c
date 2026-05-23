#include <stdint.h>
#include <string.h>
#include "../../src/core.h"
#include "../../src/exceptions.h"

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

static float* gdx_teavm_matrix4_array(void* array_obj) {
    TeaVM_Array* array = (TeaVM_Array*) teavm_nullCheck(array_obj);
    teavm_checkBounds(15, array);
    return TEAVM_ARRAY_DATA(array, float);
}

static void gdx_teavm_check_index_i64(TeaVM_Array* array, int64_t index) {
    if (index < INT32_MIN || index > INT32_MAX) {
        teavm_checkBounds(-1, array);
        return;
    }
    teavm_checkBounds((int32_t) index, array);
}

static float* gdx_teavm_vector_array(void* array_obj, int32_t offset, int32_t num_vecs, int32_t stride) {
    TeaVM_Array* array = (TeaVM_Array*) teavm_nullCheck(array_obj);
    int64_t first = offset;
    int64_t last_base = (int64_t) offset + ((int64_t) num_vecs - 1) * (int64_t) stride;
    int64_t min_index = first < last_base ? first : last_base;
    int64_t max_index = first > last_base ? first + 2 : last_base + 2;
    gdx_teavm_check_index_i64(array, min_index);
    gdx_teavm_check_index_i64(array, max_index);
    return TEAVM_ARRAY_DATA(array, float);
}

static void gdx_teavm_matrix4_mul_into(const float* a, const float* b, float* out) {
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

static float gdx_teavm_matrix4_det_ptr(const float* v) {
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

float gdx_teavm_matrix4_det(void* values_obj) {
    return gdx_teavm_matrix4_det_ptr(gdx_teavm_matrix4_array(values_obj));
}

void gdx_teavm_matrix4_mul(void* mata_obj, void* matb_obj) {
    float* a = gdx_teavm_matrix4_array(mata_obj);
    float* b = gdx_teavm_matrix4_array(matb_obj);
    float tmp[16];
    gdx_teavm_matrix4_mul_into(a, b, tmp);
    memcpy(a, tmp, sizeof(tmp));
}

void gdx_teavm_matrix4_mul_left(void* target_obj, void* left_obj) {
    float* target = gdx_teavm_matrix4_array(target_obj);
    float* left = gdx_teavm_matrix4_array(left_obj);
    float tmp[16];
    gdx_teavm_matrix4_mul_into(left, target, tmp);
    memcpy(target, tmp, sizeof(tmp));
}

int32_t gdx_teavm_matrix4_inv(void* values_obj) {
    float* v = gdx_teavm_matrix4_array(values_obj);
    float det = gdx_teavm_matrix4_det_ptr(v);
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

void gdx_teavm_matrix4_mul_vec(void* mat_obj, void* vecs_obj, int32_t offset, int32_t num_vecs, int32_t stride) {
    if (num_vecs <= 0) {
        return;
    }
    float* mat = gdx_teavm_matrix4_array(mat_obj);
    float* vecs = gdx_teavm_vector_array(vecs_obj, offset, num_vecs, stride);
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

void gdx_teavm_matrix4_prj(void* mat_obj, void* vecs_obj, int32_t offset, int32_t num_vecs, int32_t stride) {
    if (num_vecs <= 0) {
        return;
    }
    float* mat = gdx_teavm_matrix4_array(mat_obj);
    float* vecs = gdx_teavm_vector_array(vecs_obj, offset, num_vecs, stride);
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

void gdx_teavm_matrix4_rot(void* mat_obj, void* vecs_obj, int32_t offset, int32_t num_vecs, int32_t stride) {
    if (num_vecs <= 0) {
        return;
    }
    float* mat = gdx_teavm_matrix4_array(mat_obj);
    float* vecs = gdx_teavm_vector_array(vecs_obj, offset, num_vecs, stride);
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
