#include <stdint.h>
#include "../../src/core.h"

extern int32_t sfld_otr_GC_freeMemory;
extern void* sfld_otr_GC_firstDirectBuffer;

static int64_t teavm_memory_clamped_free_heap(void) {
    int64_t free_bytes = (int64_t) sfld_otr_GC_freeMemory;
    if (free_bytes < 0) {
        return 0;
    }
    if (free_bytes > teavm_gc_availableBytes) {
        return teavm_gc_availableBytes;
    }
    return free_bytes;
}

int64_t teavm_memory_heap_used_bytes(void) {
    return teavm_gc_availableBytes - teavm_memory_clamped_free_heap();
}

int64_t teavm_memory_heap_free_bytes(void) {
    return teavm_memory_clamped_free_heap();
}

int64_t teavm_memory_heap_committed_bytes(void) {
    return teavm_gc_availableBytes;
}

int64_t teavm_memory_heap_max_bytes(void) {
    return teavm_gc_maxAvailableBytes;
}

static void* teavm_memory_next_direct_buffer(void* buffer) {
    return *(void**) ((char*) buffer + sizeof(TeaVM_Object));
}

static int32_t teavm_memory_direct_buffer_capacity(void* buffer) {
    TeaVM_Class* cls = TEAVM_CLASS_OF(buffer);
    while (cls != NULL) {
        if (cls->enumValues != NULL) {
            intptr_t base_offset = (intptr_t) cls->enumValues;
            int32_t capacity = *(int32_t*) ((char*) buffer + base_offset + (sizeof(void*) * 2));
            return capacity > 0 ? capacity : 0;
        }
        cls = cls->superclass;
    }
    return 0;
}

int64_t teavm_memory_direct_buffer_live_bytes(void) {
    int64_t bytes = 0;
    int32_t guard = 0;
    void* buffer = sfld_otr_GC_firstDirectBuffer;
    while (buffer != NULL && guard < INT32_C(1000000)) {
        bytes += teavm_memory_direct_buffer_capacity(buffer);
        buffer = teavm_memory_next_direct_buffer(buffer);
        guard++;
    }
    return bytes;
}

int32_t teavm_memory_direct_buffer_count(void) {
    int32_t count = 0;
    void* buffer = sfld_otr_GC_firstDirectBuffer;
    while (buffer != NULL && count < INT32_C(1000000)) {
        buffer = teavm_memory_next_direct_buffer(buffer);
        count++;
    }
    return count;
}
