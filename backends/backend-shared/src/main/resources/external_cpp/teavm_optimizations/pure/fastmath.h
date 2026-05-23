#ifndef FASTMATH_H
#define FASTMATH_H

#include <stdint.h>
#include <math.h>

#if defined(_MSC_VER)
#define FASTMATH_INLINE static __forceinline
#elif defined(__GNUC__) || defined(__clang__)
#define FASTMATH_INLINE static inline __attribute__((always_inline))
#else
#define FASTMATH_INLINE static inline
#endif

#define FASTMATH_SIN_MASK 16383
#define FASTMATH_SIN_COUNT 16384
#define FASTMATH_DEG_TO_INDEX 0x1.6c16c2p5f
#define FASTMATH_RAD_FULL 6.28318530717958647692f

static float fastmath_sin_table[FASTMATH_SIN_COUNT];
static int32_t fastmath_sin_table_ready;
static int32_t fastmath_sin_cos_cache_ready;
static float fastmath_sin_cos_cache_rotation;
static float fastmath_sin_cos_cache_sin;
static float fastmath_sin_cos_cache_cos;

FASTMATH_INLINE float* fastmath_get_sin_table(void) {
    if (!fastmath_sin_table_ready) {
        for (int32_t i = 0; i < FASTMATH_SIN_COUNT; i++) {
            fastmath_sin_table[i] = sinf(((float)i + 0.5f) / (float)FASTMATH_SIN_COUNT * FASTMATH_RAD_FULL);
        }
        for (int32_t i = 0; i < 360; i += 90) {
            fastmath_sin_table[(int32_t)((float)i * FASTMATH_DEG_TO_INDEX) & FASTMATH_SIN_MASK] =
                    sinf((float)i * FASTMATH_RAD_FULL / 360.0f);
        }
        fastmath_sin_table_ready = 1;
    }
    return fastmath_sin_table;
}

FASTMATH_INLINE float fastmath_sin_deg(float degrees) {
    float* sin_table = fastmath_get_sin_table();
    return sin_table[((int32_t)(degrees * FASTMATH_DEG_TO_INDEX)) & FASTMATH_SIN_MASK];
}

FASTMATH_INLINE float fastmath_cos_deg(float degrees) {
    float* sin_table = fastmath_get_sin_table();
    return sin_table[((int32_t)((degrees + 90.0f) * FASTMATH_DEG_TO_INDEX)) & FASTMATH_SIN_MASK];
}

FASTMATH_INLINE void fastmath_sin_cos_deg(float degrees, float* sin_out, float* cos_out) {
    if (fastmath_sin_cos_cache_ready && degrees == fastmath_sin_cos_cache_rotation) {
        *sin_out = fastmath_sin_cos_cache_sin;
        *cos_out = fastmath_sin_cos_cache_cos;
        return;
    }

    float* sin_table = fastmath_get_sin_table();
    float sin_value = sin_table[((int32_t)(degrees * FASTMATH_DEG_TO_INDEX)) & FASTMATH_SIN_MASK];
    float cos_value = sin_table[((int32_t)((degrees + 90.0f) * FASTMATH_DEG_TO_INDEX)) & FASTMATH_SIN_MASK];
    fastmath_sin_cos_cache_rotation = degrees;
    fastmath_sin_cos_cache_sin = sin_value;
    fastmath_sin_cos_cache_cos = cos_value;
    fastmath_sin_cos_cache_ready = 1;
    *sin_out = sin_value;
    *cos_out = cos_value;
}

#undef FASTMATH_SIN_MASK
#undef FASTMATH_SIN_COUNT
#undef FASTMATH_DEG_TO_INDEX
#undef FASTMATH_RAD_FULL
#undef FASTMATH_INLINE

#endif
