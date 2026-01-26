#define GUGL_IMPLEMENTATION

#include <vramalloc.h>
#include <pspuser.h>
#include <pspdebug.h>
#include "gu2gl.h"

#ifdef __PSP__
#include <pspkernel.h>
PSP_MODULE_INFO("${PROJECT_NAME}", 0, 1, 1);
#endif

static unsigned int __attribute__((aligned(16))) list[262144];

#include <stdarg.h>
#include <string.h>
#include <stdio.h>
#include <stdint.h>

int fprintf(FILE* stream, const char* format, ...) {
    va_list args;
    va_start(args, format);
    // Check if format is "%ls"
    if (strcmp(format, "%ls") == 0) {
        int_least32_t* wstr = va_arg(args, int_least32_t*);
        // Convert UTF-32 to UTF-8
        static char buf[4096]; // Larger buffer
        char* dst = buf;
        while (*wstr) {
            uint_least32_t c = *wstr++;
            if (c < 0x80) {
                *dst++ = (char)c;
            } else if (c < 0x800) {
                *dst++ = (char)(0xC0 | (c >> 6));
                *dst++ = (char)(0x80 | (c & 0x3F));
            } else if (c < 0x10000) {
                *dst++ = (char)(0xE0 | (c >> 12));
                *dst++ = (char)(0x80 | ((c >> 6) & 0x3F));
                *dst++ = (char)(0x80 | (c & 0x3F));
            } else {
                *dst++ = (char)(0xF0 | (c >> 18));
                *dst++ = (char)(0x80 | ((c >> 12) & 0x3F));
                *dst++ = (char)(0x80 | ((c >> 6) & 0x3F));
                *dst++ = (char)(0x80 | (c & 0x3F));
            }
        }
        *dst = '\0';
        fputs(buf, stream);
        va_end(args);
        return (int)(dst - buf);
    } else {
        int result = vfprintf(stream, format, args);
        va_end(args);
        return result;
    }
}

void glInit() {
    guglInit(list);
}

void glStartFrame(int dialog) {
    guglStartFrame(list, dialog);
}

#include "all.c"
