#include "../../src/file.h"
#include <stdlib.h>

#if TEAVM_PSP
static int32_t teavm_psp_copyPath(const char* path, char16_t** result) {
    int32_t length = 0;
    while(path[length] != 0) {
        length++;
    }

    char16_t* copy = malloc(sizeof(char16_t) * (length + 1));
    if(copy == NULL) {
        *result = NULL;
        return -1;
    }

    for(int32_t i = 0; i < length; i++) {
        copy[i] = (unsigned char)path[i];
    }
    copy[length] = 0;
    *result = copy;
    return length;
}

static int32_t teavm_psp_copyChars(char16_t* value, int32_t length, char16_t** result) {
    char16_t* copy = malloc(sizeof(char16_t) * (length + 1));
    if(copy == NULL) {
        *result = NULL;
        return -1;
    }

    for(int32_t i = 0; i < length; i++) {
        copy[i] = value[i];
    }
    copy[length] = 0;
    *result = copy;
    return length;
}

int32_t teavm_file_homeDirectory(char16_t** result) {
    return teavm_psp_copyPath("ms0:/", result);
}

int32_t teavm_file_workDirectory(char16_t** result) {
    return teavm_psp_copyPath("ms0:/", result);
}

int32_t teavm_file_tempDirectory(char16_t** result) {
    return teavm_psp_copyPath("ms0:/tmp", result);
}

int32_t teavm_file_isWindows() {
    return 0;
}

int32_t teavm_file_canonicalize(char16_t* path, int32_t pathSize, char16_t** result) {
    return teavm_psp_copyChars(path, pathSize, result);
}
#endif
