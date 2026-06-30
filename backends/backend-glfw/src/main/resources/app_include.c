#include <GL/glew.h>
#include "teavm_optimizations.h"

int gdx_teavm_glfw_os(void) {
#if defined(_WIN32)
    return 1;
#elif defined(__linux__)
    return 2;
#elif defined(__APPLE__)
    return 3;
#else
    return 0;
#endif
}

#if defined(__APPLE__)
#include <signal.h>
#include <string.h>

static int teavm_glfw_sigwaitinfo(const sigset_t* signals, siginfo_t* info) {
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

#define sigwaitinfo teavm_glfw_sigwaitinfo
#endif

#include "all.c"
