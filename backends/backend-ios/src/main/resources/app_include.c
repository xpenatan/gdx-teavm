#include "teavm_optimizations.h"

#if defined(__APPLE__)
#include <signal.h>
#include <string.h>

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
#endif

#define main gdx_teavm_ios_teavm_main
#include "all.c"
#undef main

#include "ios_bridge.c"
