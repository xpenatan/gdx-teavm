#ifndef TEAVM_WEBSOCKET_GLFW_H
#define TEAVM_WEBSOCKET_GLFW_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

int gdx_teavm_ws_glfw_supported(void);
int64_t gdx_teavm_ws_glfw_create(const char* url);
int gdx_teavm_ws_glfw_state(int64_t handle);
int gdx_teavm_ws_glfw_send_text(int64_t handle, const char* text);
int gdx_teavm_ws_glfw_close(int64_t handle, int code, const char* reason);
int gdx_teavm_ws_glfw_poll_event(int64_t handle, int32_t* event_data, void* message_buffer, int message_buffer_capacity);
void gdx_teavm_ws_glfw_destroy(int64_t handle);
int gdx_teavm_ws_glfw_last_error(void* target_buffer, int target_buffer_capacity);

#ifdef __cplusplus
}
#endif

#endif
