#include "teavm_websocket_glfw.h"

#if defined(_WIN32)
#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#include <winhttp.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define WS_EVENT_NONE 0
#define WS_EVENT_OPEN 1
#define WS_EVENT_MESSAGE_TEXT 2
#define WS_EVENT_ERROR 3
#define WS_EVENT_CLOSE 4

#define WS_STATE_CONNECTING 0
#define WS_STATE_OPEN 1
#define WS_STATE_CLOSING 2
#define WS_STATE_CLOSED 3

typedef struct TeavmWsEvent {
    int type;
    int data0;
    int data1;
    char* message;
} TeavmWsEvent;

typedef struct TeavmWsHandle {
    HINTERNET session;
    HINTERNET connection;
    HINTERNET request;
    HINTERNET websocket;
    WCHAR* host;
    WCHAR* path;
    INTERNET_PORT port;
    int secure;
    volatile LONG state;
    volatile LONG open_emitted;
    volatile LONG closed_emitted;
    volatile LONG error_flag;
    volatile LONG close_code;
    HANDLE thread;
    CRITICAL_SECTION event_lock;
    TeavmWsEvent event;
} TeavmWsHandle;

static char g_last_error[2048];

static void teavm_ws_set_error(const char* message) {
    if(message == NULL) {
        g_last_error[0] = '\0';
        return;
    }
    strncpy(g_last_error, message, sizeof(g_last_error) - 1);
    g_last_error[sizeof(g_last_error) - 1] = '\0';
}

static int teavm_ws_copy_string(char* target, int capacity, const char* value) {
    if(target == NULL || capacity <= 0) {
        return 0;
    }
    if(value == NULL) {
        target[0] = '\0';
        return 0;
    }
    int length = (int)strlen(value);
    int copy_length = length < capacity ? length : capacity - 1;
    memcpy(target, value, copy_length);
    target[copy_length] = '\0';
    return copy_length;
}

static WCHAR* teavm_ws_utf8_to_wide(const char* value) {
    if(value == NULL) {
        return NULL;
    }
    int needed = MultiByteToWideChar(CP_UTF8, 0, value, -1, NULL, 0);
    if(needed <= 0) {
        return NULL;
    }
    WCHAR* result = (WCHAR*)calloc((size_t)needed, sizeof(WCHAR));
    if(result == NULL) {
        return NULL;
    }
    if(MultiByteToWideChar(CP_UTF8, 0, value, -1, result, needed) <= 0) {
        free(result);
        return NULL;
    }
    return result;
}

static int teavm_ws_starts_with_ignore_case(const char* value, const char* prefix) {
    if(value == NULL || prefix == NULL) {
        return 0;
    }
    while(*prefix != '\0') {
        char a = *value++;
        char b = *prefix++;
        if(a >= 'A' && a <= 'Z') {
            a = (char)(a - 'A' + 'a');
        }
        if(b >= 'A' && b <= 'Z') {
            b = (char)(b - 'A' + 'a');
        }
        if(a != b) {
            return 0;
        }
    }
    return 1;
}

static char* teavm_ws_normalize_url_for_winhttp(const char* url, int* secure) {
    if(url == NULL) {
        return NULL;
    }
    if(teavm_ws_starts_with_ignore_case(url, "ws://")) {
        const char* suffix = url + 5;
        size_t suffix_length = strlen(suffix);
        char* normalized = (char*)calloc(suffix_length + 8, sizeof(char));
        if(normalized != NULL) {
            strcpy(normalized, "http://");
            strcat(normalized, suffix);
        }
        if(secure != NULL) {
            *secure = 0;
        }
        return normalized;
    }
    if(teavm_ws_starts_with_ignore_case(url, "wss://")) {
        const char* suffix = url + 6;
        size_t suffix_length = strlen(suffix);
        char* normalized = (char*)calloc(suffix_length + 9, sizeof(char));
        if(normalized != NULL) {
            strcpy(normalized, "https://");
            strcat(normalized, suffix);
        }
        if(secure != NULL) {
            *secure = 1;
        }
        return normalized;
    }
    if(teavm_ws_starts_with_ignore_case(url, "http://")) {
        if(secure != NULL) {
            *secure = 0;
        }
        return _strdup(url);
    }
    if(teavm_ws_starts_with_ignore_case(url, "https://")) {
        if(secure != NULL) {
            *secure = 1;
        }
        return _strdup(url);
    }
    teavm_ws_set_error("Only ws:// and wss:// websocket URLs are supported.");
    return NULL;
}

static char* teavm_ws_wide_to_utf8(const WCHAR* value) {
    if(value == NULL) {
        return NULL;
    }
    int needed = WideCharToMultiByte(CP_UTF8, 0, value, -1, NULL, 0, NULL, NULL);
    if(needed <= 0) {
        return NULL;
    }
    char* result = (char*)calloc((size_t)needed, sizeof(char));
    if(result == NULL) {
        return NULL;
    }
    if(WideCharToMultiByte(CP_UTF8, 0, value, -1, result, needed, NULL, NULL) <= 0) {
        free(result);
        return NULL;
    }
    return result;
}

static int teavm_ws_parse_url(const char* url, TeavmWsHandle* handle) {
    int secure = 0;
    char* normalized_url = teavm_ws_normalize_url_for_winhttp(url, &secure);
    WCHAR* wide_url = teavm_ws_utf8_to_wide(normalized_url);
    if(normalized_url != NULL) {
        free(normalized_url);
    }
    if(wide_url == NULL) {
        teavm_ws_set_error("Failed to convert websocket URL to UTF-16.");
        return 0;
    }

    URL_COMPONENTSW components;
    memset(&components, 0, sizeof(components));
    components.dwStructSize = sizeof(components);
    components.dwSchemeLength = (DWORD)-1;
    components.dwHostNameLength = (DWORD)-1;
    components.dwUrlPathLength = (DWORD)-1;
    components.dwExtraInfoLength = (DWORD)-1;

    if(!WinHttpCrackUrl(wide_url, 0, 0, &components)) {
        free(wide_url);
        teavm_ws_set_error("Failed to parse websocket URL.");
        return 0;
    }

    if(!(components.nScheme == INTERNET_SCHEME_HTTP || components.nScheme == INTERNET_SCHEME_HTTPS)) {
        free(wide_url);
        teavm_ws_set_error("Failed to normalize websocket URL for WinHTTP.");
        return 0;
    }

    handle->host = (WCHAR*)calloc((size_t)components.dwHostNameLength + 1, sizeof(WCHAR));
    handle->path = (WCHAR*)calloc((size_t)(components.dwUrlPathLength + components.dwExtraInfoLength + 1), sizeof(WCHAR));
    if(handle->host == NULL || handle->path == NULL) {
        free(wide_url);
        teavm_ws_set_error("Failed to allocate websocket URL buffers.");
        return 0;
    }

    wcsncpy(handle->host, components.lpszHostName, components.dwHostNameLength);
    handle->host[components.dwHostNameLength] = 0;
    if(components.dwUrlPathLength > 0) {
        wcsncpy(handle->path, components.lpszUrlPath, components.dwUrlPathLength);
    }
    if(components.dwExtraInfoLength > 0) {
        wcsncpy(handle->path + components.dwUrlPathLength, components.lpszExtraInfo, components.dwExtraInfoLength);
    }
    handle->path[components.dwUrlPathLength + components.dwExtraInfoLength] = 0;
    if(handle->path[0] == 0) {
        handle->path[0] = L'/';
        handle->path[1] = 0;
    }
    handle->port = components.nPort;
    handle->secure = secure;
    free(wide_url);
    return 1;
}

static void teavm_ws_destroy_event(TeavmWsEvent* event) {
    if(event->message != NULL) {
        free(event->message);
        event->message = NULL;
    }
    event->type = WS_EVENT_NONE;
    event->data0 = 0;
    event->data1 = 0;
}

static void teavm_ws_push_event(TeavmWsHandle* handle, int type, int data0, int data1, const char* message) {
    EnterCriticalSection(&handle->event_lock);
    teavm_ws_destroy_event(&handle->event);
    handle->event.type = type;
    handle->event.data0 = data0;
    handle->event.data1 = data1;
    if(message != NULL) {
        size_t length = strlen(message);
        handle->event.message = (char*)calloc(length + 1, sizeof(char));
        if(handle->event.message != NULL) {
            memcpy(handle->event.message, message, length);
            handle->event.message[length] = '\0';
        }
    }
    LeaveCriticalSection(&handle->event_lock);
}

static DWORD WINAPI teavm_ws_reader_thread(LPVOID parameter) {
    TeavmWsHandle* handle = (TeavmWsHandle*)parameter;
    BYTE buffer[16384];

    for(;;) {
        if(InterlockedCompareExchange(&handle->state, handle->state, handle->state) >= WS_STATE_CLOSING) {
            return 0;
        }

        WINHTTP_WEB_SOCKET_BUFFER_TYPE buffer_type = WINHTTP_WEB_SOCKET_UTF8_MESSAGE_BUFFER_TYPE;
        DWORD bytes_read = 0;
        DWORD result = WinHttpWebSocketReceive(handle->websocket, buffer, sizeof(buffer), &bytes_read, &buffer_type);
        if(result != NO_ERROR) {
            InterlockedExchange(&handle->state, WS_STATE_CLOSED);
            teavm_ws_push_event(handle, WS_EVENT_ERROR, 0, 0, "WinHTTP websocket receive failed.");
            return 0;
        }

        if(buffer_type == WINHTTP_WEB_SOCKET_CLOSE_BUFFER_TYPE) {
            USHORT close_code = 1000;
            WCHAR close_reason[256];
            DWORD reason_length = 0;
            WinHttpWebSocketQueryCloseStatus(handle->websocket, &close_code, close_reason, sizeof(close_reason), &reason_length);
            char* reason_utf8 = teavm_ws_wide_to_utf8(close_reason);
            InterlockedExchange(&handle->close_code, close_code);
            InterlockedExchange(&handle->state, WS_STATE_CLOSED);
            teavm_ws_push_event(handle, WS_EVENT_CLOSE, close_code, reason_utf8 == NULL ? 0 : (int)strlen(reason_utf8), reason_utf8);
            if(reason_utf8 != NULL) {
                free(reason_utf8);
            }
            return 0;
        }

        if(buffer_type == WINHTTP_WEB_SOCKET_UTF8_MESSAGE_BUFFER_TYPE) {
            ((char*)buffer)[bytes_read < sizeof(buffer) ? bytes_read : sizeof(buffer) - 1] = '\0';
            teavm_ws_push_event(handle, WS_EVENT_MESSAGE_TEXT, (int)bytes_read, 0, (const char*)buffer);
        }
    }
}

int gdx_teavm_ws_glfw_supported(void) {
    return 1;
}

int64_t gdx_teavm_ws_glfw_create(const char* url) {
    teavm_ws_set_error(NULL);
    TeavmWsHandle* handle = (TeavmWsHandle*)calloc(1, sizeof(TeavmWsHandle));
    if(handle == NULL) {
        teavm_ws_set_error("Failed to allocate websocket handle.");
        return 0;
    }
    InitializeCriticalSection(&handle->event_lock);
    InterlockedExchange(&handle->state, WS_STATE_CONNECTING);

    if(!teavm_ws_parse_url(url, handle)) {
        DeleteCriticalSection(&handle->event_lock);
        free(handle);
        return 0;
    }

    handle->session = WinHttpOpen(L"gdx-teavm-websocket/1.0",
            WINHTTP_ACCESS_TYPE_DEFAULT_PROXY,
            WINHTTP_NO_PROXY_NAME,
            WINHTTP_NO_PROXY_BYPASS,
            0);
    if(handle->session == NULL) {
        teavm_ws_set_error("WinHTTP session creation failed.");
        gdx_teavm_ws_glfw_destroy((int64_t)(intptr_t)handle);
        return 0;
    }

    handle->connection = WinHttpConnect(handle->session, handle->host, handle->port, 0);
    if(handle->connection == NULL) {
        teavm_ws_set_error("WinHTTP connection failed.");
        gdx_teavm_ws_glfw_destroy((int64_t)(intptr_t)handle);
        return 0;
    }

    DWORD flags = handle->secure ? WINHTTP_FLAG_SECURE : 0;
    handle->request = WinHttpOpenRequest(handle->connection, L"GET", handle->path,
            NULL, WINHTTP_NO_REFERER, WINHTTP_DEFAULT_ACCEPT_TYPES, flags);
    if(handle->request == NULL) {
        teavm_ws_set_error("WinHTTP request creation failed.");
        gdx_teavm_ws_glfw_destroy((int64_t)(intptr_t)handle);
        return 0;
    }

    if(!WinHttpSetOption(handle->request, WINHTTP_OPTION_UPGRADE_TO_WEB_SOCKET, NULL, 0)) {
        teavm_ws_set_error("Failed to enable WinHTTP websocket upgrade.");
        gdx_teavm_ws_glfw_destroy((int64_t)(intptr_t)handle);
        return 0;
    }

    if(!WinHttpSendRequest(handle->request, WINHTTP_NO_ADDITIONAL_HEADERS, 0,
            WINHTTP_NO_REQUEST_DATA, 0, 0, 0)) {
        teavm_ws_set_error("Failed to send websocket handshake request.");
        gdx_teavm_ws_glfw_destroy((int64_t)(intptr_t)handle);
        return 0;
    }

    if(!WinHttpReceiveResponse(handle->request, NULL)) {
        teavm_ws_set_error("Failed to receive websocket handshake response.");
        gdx_teavm_ws_glfw_destroy((int64_t)(intptr_t)handle);
        return 0;
    }

    handle->websocket = WinHttpWebSocketCompleteUpgrade(handle->request, 0);
    if(handle->websocket == NULL) {
        teavm_ws_set_error("WinHTTP websocket upgrade failed.");
        gdx_teavm_ws_glfw_destroy((int64_t)(intptr_t)handle);
        return 0;
    }

    WinHttpCloseHandle(handle->request);
    handle->request = NULL;

    handle->thread = CreateThread(NULL, 0, teavm_ws_reader_thread, handle, 0, NULL);
    if(handle->thread == NULL) {
        teavm_ws_set_error("Failed to start websocket receive thread.");
        gdx_teavm_ws_glfw_destroy((int64_t)(intptr_t)handle);
        return 0;
    }

    InterlockedExchange(&handle->state, WS_STATE_OPEN);
    teavm_ws_push_event(handle, WS_EVENT_OPEN, 0, 0, NULL);
    return (int64_t)(intptr_t)handle;
}

int gdx_teavm_ws_glfw_state(int64_t handle_value) {
    TeavmWsHandle* handle = (TeavmWsHandle*)(intptr_t)handle_value;
    if(handle == NULL) {
        return WS_STATE_CLOSED;
    }
    return (int)InterlockedCompareExchange(&handle->state, handle->state, handle->state);
}

int gdx_teavm_ws_glfw_send_text(int64_t handle_value, const char* text) {
    TeavmWsHandle* handle = (TeavmWsHandle*)(intptr_t)handle_value;
    if(handle == NULL || handle->websocket == NULL) {
        teavm_ws_set_error("WebSocket handle is not open.");
        return 0;
    }
    if(text == NULL) {
        text = "";
    }
    DWORD length = (DWORD)strlen(text);
    DWORD result = WinHttpWebSocketSend(handle->websocket,
            WINHTTP_WEB_SOCKET_UTF8_MESSAGE_BUFFER_TYPE,
            (PVOID)text,
            length);
    if(result != NO_ERROR) {
        teavm_ws_set_error("WinHTTP websocket send failed.");
        return 0;
    }
    return 1;
}

int gdx_teavm_ws_glfw_close(int64_t handle_value, int code, const char* reason) {
    TeavmWsHandle* handle = (TeavmWsHandle*)(intptr_t)handle_value;
    if(handle == NULL || handle->websocket == NULL) {
        return 1;
    }
    if(reason == NULL) {
        reason = "";
    }
    InterlockedExchange(&handle->state, WS_STATE_CLOSING);
    DWORD result = WinHttpWebSocketClose(handle->websocket, (USHORT)code, (PVOID)reason, (DWORD)strlen(reason));
    if(result != NO_ERROR) {
        teavm_ws_set_error("WinHTTP websocket close failed.");
        return 0;
    }
    return 1;
}

int gdx_teavm_ws_glfw_poll_event(int64_t handle_value, int32_t* event_data, void* message_buffer, int message_buffer_capacity) {
    TeavmWsHandle* handle = (TeavmWsHandle*)(intptr_t)handle_value;
    if(handle == NULL || event_data == NULL) {
        return 0;
    }
    event_data[0] = WS_EVENT_NONE;
    event_data[1] = 0;
    event_data[2] = 0;
    event_data[3] = 0;

    EnterCriticalSection(&handle->event_lock);
    if(handle->event.type != WS_EVENT_NONE) {
        event_data[0] = handle->event.type;
        event_data[1] = handle->event.data0;
        event_data[2] = handle->event.data1;
        if(message_buffer != NULL && message_buffer_capacity > 0 && handle->event.message != NULL) {
            teavm_ws_copy_string((char*)message_buffer, message_buffer_capacity, handle->event.message);
        }
        teavm_ws_destroy_event(&handle->event);
        LeaveCriticalSection(&handle->event_lock);
        return 1;
    }
    LeaveCriticalSection(&handle->event_lock);
    return 0;
}

void gdx_teavm_ws_glfw_destroy(int64_t handle_value) {
    TeavmWsHandle* handle = (TeavmWsHandle*)(intptr_t)handle_value;
    if(handle == NULL) {
        return;
    }
    if(handle->websocket != NULL) {
        WinHttpCloseHandle(handle->websocket);
        handle->websocket = NULL;
    }
    if(handle->request != NULL) {
        WinHttpCloseHandle(handle->request);
        handle->request = NULL;
    }
    if(handle->connection != NULL) {
        WinHttpCloseHandle(handle->connection);
        handle->connection = NULL;
    }
    if(handle->session != NULL) {
        WinHttpCloseHandle(handle->session);
        handle->session = NULL;
    }
    if(handle->thread != NULL) {
        WaitForSingleObject(handle->thread, 1000);
        CloseHandle(handle->thread);
        handle->thread = NULL;
    }
    if(handle->host != NULL) {
        free(handle->host);
    }
    if(handle->path != NULL) {
        free(handle->path);
    }
    EnterCriticalSection(&handle->event_lock);
    teavm_ws_destroy_event(&handle->event);
    LeaveCriticalSection(&handle->event_lock);
    DeleteCriticalSection(&handle->event_lock);
    free(handle);
}

int gdx_teavm_ws_glfw_last_error(void* target_buffer, int target_buffer_capacity) {
    return teavm_ws_copy_string((char*)target_buffer, target_buffer_capacity, g_last_error);
}

#else

int gdx_teavm_ws_glfw_supported(void) { return 0; }
int64_t gdx_teavm_ws_glfw_create(const char* url) { (void)url; return 0; }
int gdx_teavm_ws_glfw_state(int64_t handle) { (void)handle; return 3; }
int gdx_teavm_ws_glfw_send_text(int64_t handle, const char* text) { (void)handle; (void)text; return 0; }
int gdx_teavm_ws_glfw_close(int64_t handle, int code, const char* reason) { (void)handle; (void)code; (void)reason; return 0; }
int gdx_teavm_ws_glfw_poll_event(int64_t handle, int32_t* event_data, void* message_buffer, int message_buffer_capacity) {
    (void)handle;
    (void)message_buffer;
    (void)message_buffer_capacity;
    if(event_data != NULL) {
        event_data[0] = 0;
        event_data[1] = 0;
        event_data[2] = 0;
        event_data[3] = 0;
    }
    return 0;
}
void gdx_teavm_ws_glfw_destroy(int64_t handle) { (void)handle; }
int gdx_teavm_ws_glfw_last_error(void* target_buffer, int target_buffer_capacity) {
    const char* message = "TeaVM GLFW websocket backend is currently implemented only for Windows.";
    if(target_buffer != NULL && target_buffer_capacity > 0) {
        size_t length = strlen(message);
        size_t copy_length = length < (size_t)target_buffer_capacity ? length : (size_t)target_buffer_capacity - 1;
        memcpy(target_buffer, message, copy_length);
        ((char*)target_buffer)[copy_length] = '\0';
        return (int)copy_length;
    }
    return 0;
}

#endif
