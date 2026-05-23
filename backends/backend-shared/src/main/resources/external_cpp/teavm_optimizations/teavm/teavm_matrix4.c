#include <stdint.h>
#include "../../../src/core.h"
#include "../../../src/exceptions.h"
#include "../pure/matrix4.h"

static float* teavm_matrix4_array(void* array_obj) {
    TeaVM_Array* array = (TeaVM_Array*) teavm_nullCheck(array_obj);
    teavm_checkBounds(15, array);
    return TEAVM_ARRAY_DATA(array, float);
}

static void teavm_check_index_i64(TeaVM_Array* array, int64_t index) {
    if (index < INT32_MIN || index > INT32_MAX) {
        teavm_checkBounds(-1, array);
        return;
    }
    teavm_checkBounds((int32_t) index, array);
}

static float* teavm_vector_array(void* array_obj, int32_t offset, int32_t num_vecs, int32_t stride) {
    TeaVM_Array* array = (TeaVM_Array*) teavm_nullCheck(array_obj);
    int64_t first = offset;
    int64_t last_base = (int64_t) offset + ((int64_t) num_vecs - 1) * (int64_t) stride;
    int64_t min_index = first < last_base ? first : last_base;
    int64_t max_index = first > last_base ? first + 2 : last_base + 2;
    teavm_check_index_i64(array, min_index);
    teavm_check_index_i64(array, max_index);
    return TEAVM_ARRAY_DATA(array, float);
}

void teavm_matrix4_mul(void* mata_obj, void* matb_obj) {
    matrix4_mul(teavm_matrix4_array(mata_obj), teavm_matrix4_array(matb_obj));
}

void teavm_matrix4_mul_left(void* target_obj, void* left_obj) {
    matrix4_mul_left(teavm_matrix4_array(target_obj), teavm_matrix4_array(left_obj));
}

float teavm_matrix4_det(void* values_obj) {
    return matrix4_det(teavm_matrix4_array(values_obj));
}

int32_t teavm_matrix4_inv(void* values_obj) {
    return matrix4_inv(teavm_matrix4_array(values_obj));
}

void teavm_matrix4_mul_vec(void* mat_obj, void* vecs_obj, int32_t offset, int32_t num_vecs, int32_t stride) {
    if (num_vecs <= 0) {
        return;
    }
    matrix4_mul_vec(teavm_matrix4_array(mat_obj),
            teavm_vector_array(vecs_obj, offset, num_vecs, stride), offset, num_vecs, stride);
}

void teavm_matrix4_prj(void* mat_obj, void* vecs_obj, int32_t offset, int32_t num_vecs, int32_t stride) {
    if (num_vecs <= 0) {
        return;
    }
    matrix4_prj(teavm_matrix4_array(mat_obj),
            teavm_vector_array(vecs_obj, offset, num_vecs, stride), offset, num_vecs, stride);
}

void teavm_matrix4_rot(void* mat_obj, void* vecs_obj, int32_t offset, int32_t num_vecs, int32_t stride) {
    if (num_vecs <= 0) {
        return;
    }
    matrix4_rot(teavm_matrix4_array(mat_obj),
            teavm_vector_array(vecs_obj, offset, num_vecs, stride), offset, num_vecs, stride);
}
