#include <GL/glew.h>
#include <stdint.h>
#include "teavm_optimizations.h"

int64_t gdx_teavm_glfw_get_win32_window(int64_t glfw_window);

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

#if defined(_WIN32)
#define GLFW_INCLUDE_NONE
#define GLFW_EXPOSE_NATIVE_WIN32
#define GLFW_NATIVE_INCLUDE_NONE
#if !defined(_WINDEF_)
typedef struct HWND__* HWND;
#endif
#include <GLFW/glfw3native.h>
#endif

int64_t gdx_teavm_glfw_get_win32_window(int64_t glfw_window) {
#if defined(_WIN32)
    if(glfw_window == 0) {
        return 0;
    }
    GLFWwindow* window = (GLFWwindow*) (intptr_t) glfw_window;
    HWND native_window = glfwGetWin32Window(window);
    return (int64_t) (intptr_t) native_window;
#else
    (void) glfw_window;
    return 0;
#endif
}
