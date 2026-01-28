#pragma once

#if PSP_DEBUG_MEMORY

#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>

#include "PSPLog.h"

typedef struct AllocHeader {
    size_t   size;
    uint32_t magic;
} AllocHeader;

#define ALLOC_MAGIC  0xBADC0DE1
#define FREED_MAGIC  0xDEADBEEF

static size_t live_allocated_bytes = -1;

void* tracked_malloc(size_t size) {
    if (size == 0) return NULL;

    AllocHeader* header = (AllocHeader*) malloc(sizeof(AllocHeader) + size);
    if (!header) return NULL;

    header->size  = size;
    header->magic = ALLOC_MAGIC;

    live_allocated_bytes += size;

    return (void*)(header + 1);
}

void tracked_free(void* ptr) {
    if (!ptr) return;

    AllocHeader* header = (AllocHeader*)ptr - 1;

    if (header->magic != ALLOC_MAGIC) {
        printf("tracked_free: invalid header at %p\n", ptr);
        return;
    }

    live_allocated_bytes -= header->size;

    header->magic = FREED_MAGIC;

    free(header);
}

void* tracked_calloc(size_t nmemb, size_t size) {
    size_t total = nmemb * size;
    void* p = tracked_malloc(total);
    if (p) memset(p, 0, total);
    return p;
}

void* tracked_realloc(void* ptr, size_t new_size) {
    if (!ptr) return tracked_malloc(new_size);
    if (new_size == 0) {
        tracked_free(ptr);
        return NULL;
    }

    AllocHeader* old_header = (AllocHeader*)ptr - 1;
    if (old_header->magic != ALLOC_MAGIC) {
        printf("tracked_realloc: invalid header at %p\n", ptr);
        return NULL;
    }

    size_t old_size = old_header->size;

    void* new_ptr = tracked_malloc(new_size);
    if (!new_ptr) return NULL;

    size_t copy_bytes = (old_size < new_size) ? old_size : new_size;
    memcpy(new_ptr, ptr, copy_bytes);

    tracked_free(ptr);

    return new_ptr;
}

#undef malloc
#undef free
#undef calloc
#undef realloc

#define malloc(size)     tracked_malloc(size)
#define free(ptr)        tracked_free(ptr)
#define calloc(n, s)     tracked_calloc(n, s)
#define realloc(p, s)    tracked_realloc(p, s)

#endif // PSP_DEBUG_MEMORY

int getAllocatedMemory() {
    #if PSP_DEBUG_MEMORY
        return (int)live_allocated_bytes;
    #else
        return -1;
    #endif // PSP_DEBUG_MEMORY
}