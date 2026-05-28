#include "teavm_optimizations.h"

#ifndef TEAVM_GENERATED_SHORT_FILE_NAMES
#define TEAVM_GENERATED_SHORT_FILE_NAMES 1
#endif

#if defined(__APPLE__)
#include <signal.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <OpenGLES/ES3/gl.h>

static int teavm_ios_sigwaitinfo(const sigset_t* signals, siginfo_t* info) {
    int signal = 0;
    int result = sigwait(signals, &signal);
    if(result != 0) {
        return -1;
    }
    if(info != NULL) {
        memset(info, 0, sizeof(siginfo_t));
        info->si_signo = signal;
    }
    return signal;
}

#define sigwaitinfo teavm_ios_sigwaitinfo

static void gdx_teavm_ios_glShaderSource(GLuint shader, GLsizei length, const int32_t* sourceData) {
    if(sourceData == NULL || length < 0) {
        return;
    }

    char* source = (char*) malloc((size_t) length + 1);
    if(source == NULL) {
        return;
    }

    for(GLsizei i = 0; i < length; i++) {
        source[i] = (char) (sourceData[i] & 0xff);
    }
    source[length] = '\0';

    const GLchar* strings[1] = { source };
    glShaderSource(shader, 1, strings, NULL);
    free(source);
}

static void gdx_teavm_ios_log(const char* message) {
    if(message != NULL) {
        fprintf(stderr, "%s\n", message);
        fflush(stderr);
    }
}
#endif

#define main gdx_teavm_ios_teavm_main
#include "all.c"
#undef main

#include "../external_cpp/gdx/gdx2d.c"
#include "../external_cpp/teavm_optimizations/teavm/teavm_fastmath.c"
#include "../external_cpp/teavm_optimizations/teavm/teavm_matrix4.c"
#include "../external_cpp/teavm_optimizations/teavm/teavm_spritebatch.c"

#include "ios_bridge.c"
