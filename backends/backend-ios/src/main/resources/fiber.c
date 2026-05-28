#include "fiber.h"
#include "definitions.h"
#include <locale.h>
#include <stdint.h>
#include <sys/time.h>
#include <pthread.h>

static pthread_mutex_t teavm_ios_fiberMutex = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t teavm_ios_fiberCond = PTHREAD_COND_INITIALIZER;
static int teavm_ios_fiberInterrupted;

void teavm_initFiber() {
    setlocale(LC_ALL, "");
}

void teavm_waitFor(int64_t timeout) {
    struct timeval now;
    gettimeofday(&now, NULL);

    int64_t targetNanos = ((int64_t)now.tv_usec * 1000) + ((timeout % 1000) * 1000000L);
    struct timespec deadline = {
            .tv_sec = now.tv_sec + (time_t)(timeout / 1000) + (time_t)(targetNanos / 1000000000L),
            .tv_nsec = (long)(targetNanos % 1000000000L)
    };

    pthread_mutex_lock(&teavm_ios_fiberMutex);
    if(!teavm_ios_fiberInterrupted) {
        pthread_cond_timedwait(&teavm_ios_fiberCond, &teavm_ios_fiberMutex, &deadline);
    }
    teavm_ios_fiberInterrupted = 0;
    pthread_mutex_unlock(&teavm_ios_fiberMutex);
}

void teavm_interrupt() {
    pthread_mutex_lock(&teavm_ios_fiberMutex);
    teavm_ios_fiberInterrupted = 1;
    pthread_cond_signal(&teavm_ios_fiberCond);
    pthread_mutex_unlock(&teavm_ios_fiberMutex);
}
