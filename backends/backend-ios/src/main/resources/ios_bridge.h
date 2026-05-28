#pragma once

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

int32_t gdx_teavm_ios_start(const char* workingDirectory);
void gdx_teavm_ios_set_viewport(int32_t width, int32_t height);
int32_t gdx_teavm_ios_gl_error(void);
void gdx_teavm_ios_get_viewport(int32_t* viewport);
int32_t gdx_teavm_ios_is_scissor_enabled(void);
void gdx_teavm_ios_get_scissor_box(int32_t* box);
void gdx_teavm_ios_debug_triangle(void);
void gdx_teavm_ios_resize(int32_t width, int32_t height, float scale);
void gdx_teavm_ios_render(void);
void gdx_teavm_ios_pause(void);
void gdx_teavm_ios_resume(void);
void gdx_teavm_ios_dispose(void);
void gdx_teavm_ios_touch(int32_t type, int32_t pointer, int32_t x, int32_t y, float pressure);
void gdx_teavm_ios_key(int32_t type, int32_t keycode);
int32_t gdx_teavm_ios_status_code(void);

#ifdef __cplusplus
}
#endif
