#include "teavm_optimizations.h"

#ifdef __ANDROID__
#include <signal.h>
#include <string.h>

static int teavm_android_sigwaitinfo(const sigset_t* signals, siginfo_t* info) {
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

#define sigwaitinfo teavm_android_sigwaitinfo
#endif

#include "all.c"
#include "android_jni.c"
