#pragma once

#if defined(__has_include)
#if __has_include(<uchar.h>)
#define GDX_TEAVM_IOS_HAS_UCHAR 1
#endif
#endif

#if GDX_TEAVM_IOS_HAS_UCHAR

#include <uchar.h>

#else

#include <stddef.h>
#include <stdint.h>
#include <wchar.h>

typedef uint16_t char16_t;
typedef uint32_t char32_t;

static inline size_t c16rtomb(char* s, char16_t c16, mbstate_t* ps) {
    (void)ps;
    if(s) {
        *s = (char)c16;
    }
    return 1;
}

static inline size_t mbrtoc16(char16_t* pc16, const char* s, size_t n, mbstate_t* ps) {
    (void)ps;
    if(pc16 && s && n > 0) {
        *pc16 = (char16_t)*s;
    }
    return 1;
}

#endif
