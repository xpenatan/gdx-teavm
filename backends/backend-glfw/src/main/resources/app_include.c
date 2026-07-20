#include <GL/glew.h>
#include <stdint.h>
#include "teavm_optimizations.h"

int gdx_teavm_glfw_get_platform(void);
int64_t gdx_teavm_glfw_get_win32_window(int64_t glfw_window);
int64_t gdx_teavm_glfw_get_x11_display(void);
int64_t gdx_teavm_glfw_get_x11_window(int64_t glfw_window);
int64_t gdx_teavm_glfw_get_wayland_display(void);
int64_t gdx_teavm_glfw_get_wayland_window(int64_t glfw_window);
int64_t gdx_teavm_glfw_get_cocoa_window(int64_t glfw_window);

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
#elif defined(__linux__)
#if defined(GDX_TEAVM_GLFW_NATIVE_X11)
#define GLFW_EXPOSE_NATIVE_X11
#endif
#if defined(GDX_TEAVM_GLFW_NATIVE_WAYLAND)
#define GLFW_EXPOSE_NATIVE_WAYLAND
#endif
#if defined(GLFW_EXPOSE_NATIVE_X11) || defined(GLFW_EXPOSE_NATIVE_WAYLAND)
#include <GLFW/glfw3native.h>
#endif
#elif defined(__APPLE__)
#define GLFW_EXPOSE_NATIVE_COCOA
#include <GLFW/glfw3native.h>
#endif

int gdx_teavm_glfw_get_platform(void) {
    return glfwGetPlatform();
}

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

int64_t gdx_teavm_glfw_get_x11_display(void) {
#if defined(__linux__) && defined(GDX_TEAVM_GLFW_NATIVE_X11)
    return (int64_t) (intptr_t) glfwGetX11Display();
#else
    return 0;
#endif
}

int64_t gdx_teavm_glfw_get_x11_window(int64_t glfw_window) {
#if defined(__linux__) && defined(GDX_TEAVM_GLFW_NATIVE_X11)
    if(glfw_window == 0) {
        return 0;
    }
    return (int64_t) glfwGetX11Window((GLFWwindow*) (intptr_t) glfw_window);
#else
    (void)glfw_window;
    return 0;
#endif
}

int64_t gdx_teavm_glfw_get_wayland_display(void) {
#if defined(__linux__) && defined(GDX_TEAVM_GLFW_NATIVE_WAYLAND)
    return (int64_t) (intptr_t) glfwGetWaylandDisplay();
#else
    return 0;
#endif
}

int64_t gdx_teavm_glfw_get_wayland_window(int64_t glfw_window) {
#if defined(__linux__) && defined(GDX_TEAVM_GLFW_NATIVE_WAYLAND)
    if(glfw_window == 0) {
        return 0;
    }
    return (int64_t) (intptr_t) glfwGetWaylandWindow((GLFWwindow*) (intptr_t) glfw_window);
#else
    (void)glfw_window;
    return 0;
#endif
}

int64_t gdx_teavm_glfw_get_cocoa_window(int64_t glfw_window) {
#if defined(__APPLE__)
    if(glfw_window == 0) {
        return 0;
    }
    return (int64_t) (intptr_t) glfwGetCocoaWindow((GLFWwindow*) (intptr_t) glfw_window);
#else
    (void)glfw_window;
    return 0;
#endif
}
