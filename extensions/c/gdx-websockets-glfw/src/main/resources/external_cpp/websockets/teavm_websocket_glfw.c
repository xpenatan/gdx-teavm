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

#elif defined(__linux__) || defined(__APPLE__)

#include <dlfcn.h>
#include <limits.h>
#if defined(__APPLE__)
#include <mach-o/dyld.h>
#endif
#include <pthread.h>
#include <stdatomic.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>

#define WS_EVENT_NONE 0
#define WS_EVENT_OPEN 1
#define WS_EVENT_MESSAGE_TEXT 2
#define WS_EVENT_ERROR 3
#define WS_EVENT_CLOSE 4

#define WS_STATE_CONNECTING 0
#define WS_STATE_OPEN 1
#define WS_STATE_CLOSING 2
#define WS_STATE_CLOSED 3

#define TEAVM_WS_LINUX_RECV_BUFFER_SIZE 16384
#define TEAVM_WS_LINUX_ERROR_BUFFER_SIZE 512
#define TEAVM_WS_LINUX_CLOSE_REASON_LIMIT 123
#define TEAVM_WS_LINUX_CLOSE_CODE_NORMAL 1000
#define TEAVM_WS_LINUX_POLL_DELAY_MILLIS 5
#define TEAVM_WS_LINUX_RUNTIME_CURL_ENV "GDX_TEAVM_LIBCURL_PATH"

#ifndef PATH_MAX
#define PATH_MAX 4096
#endif

typedef struct CURL CURL;
typedef int CURLcode;
typedef int CURLoption;
typedef long long curl_off_t;

struct curl_ws_frame {
    int age;
    int flags;
    curl_off_t offset;
    curl_off_t bytesleft;
    size_t len;
};

#define CURLWS_TEXT (1 << 0)
#define CURLWS_CLOSE (1 << 3)

#define CURLOPTTYPE_LONG 0
#define CURLOPTTYPE_OBJECTPOINT 10000
#define CURLOPTTYPE_STRINGPOINT CURLOPTTYPE_OBJECTPOINT

#define CURLOPT_URL (CURLOPTTYPE_STRINGPOINT + 2)
#define CURLOPT_ERRORBUFFER (CURLOPTTYPE_OBJECTPOINT + 10)
#define CURLOPT_USERAGENT (CURLOPTTYPE_STRINGPOINT + 18)
#define CURLOPT_CONNECT_ONLY (CURLOPTTYPE_LONG + 141)
#define CURLOPT_TIMEOUT_MS (CURLOPTTYPE_LONG + 155)

#define CURL_GLOBAL_DEFAULT 3L

#define CURLE_OK 0
#define CURLE_GOT_NOTHING 52
#define CURLE_AGAIN 81

typedef struct TeavmWsEvent {
    int type;
    int data0;
    int data1;
    char* message;
} TeavmWsEvent;

typedef struct TeavmWsHandle {
    CURL* curl;
    pthread_t thread;
    int thread_started;
    pthread_mutex_t event_lock;
    atomic_int state;
    atomic_int stop_requested;
    int close_code;
    char* close_reason;
    char error_buffer[TEAVM_WS_LINUX_ERROR_BUFFER_SIZE];
    TeavmWsEvent event;
} TeavmWsHandle;

typedef CURLcode (*TeavmCurlGlobalInitFn)(long flags);
typedef void (*TeavmCurlEasyCleanupFn)(CURL* curl);
typedef CURL* (*TeavmCurlEasyInitFn)(void);
typedef CURLcode (*TeavmCurlEasySetoptFn)(CURL* curl, CURLoption option, ...);
typedef CURLcode (*TeavmCurlEasyPerformFn)(CURL* curl);
typedef const char* (*TeavmCurlEasyStrerrorFn)(CURLcode code);
typedef CURLcode (*TeavmCurlWsRecvFn)(CURL* curl, void* buffer, size_t buflen, size_t* recv,
                                      const struct curl_ws_frame** meta);
typedef CURLcode (*TeavmCurlWsSendFn)(CURL* curl, const void* buffer, size_t buflen, size_t* sent,
                                      curl_off_t fragsize, unsigned int flags);

typedef struct TeavmCurlApi {
    void* library;
    int load_state;
    pthread_mutex_t lock;
    TeavmCurlGlobalInitFn global_init;
    TeavmCurlEasyInitFn easy_init;
    TeavmCurlEasyCleanupFn easy_cleanup;
    TeavmCurlEasySetoptFn easy_setopt;
    TeavmCurlEasyPerformFn easy_perform;
    TeavmCurlEasyStrerrorFn easy_strerror;
    TeavmCurlWsRecvFn ws_recv;
    TeavmCurlWsSendFn ws_send;
} TeavmCurlApi;

static char g_last_error[2048];
static TeavmCurlApi g_curl = {
        .library = NULL,
        .load_state = 0,
        .lock = PTHREAD_MUTEX_INITIALIZER,
        .global_init = NULL,
        .easy_init = NULL,
        .easy_cleanup = NULL,
        .easy_setopt = NULL,
        .easy_perform = NULL,
        .easy_strerror = NULL,
        .ws_recv = NULL,
        .ws_send = NULL
};

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

static void teavm_ws_sleep_millis(long millis) {
    struct timespec delay;
    delay.tv_sec = millis / 1000;
    delay.tv_nsec = (millis % 1000) * 1000000L;
    nanosleep(&delay, NULL);
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

static void teavm_ws_push_event_bytes(TeavmWsHandle* handle, int type, int data0, int data1,
                                      const char* message, size_t message_length) {
    pthread_mutex_lock(&handle->event_lock);
    teavm_ws_destroy_event(&handle->event);
    handle->event.type = type;
    handle->event.data0 = data0;
    handle->event.data1 = data1;
    if(message != NULL && message_length > 0) {
        handle->event.message = (char*)calloc(message_length + 1, sizeof(char));
        if(handle->event.message != NULL) {
            memcpy(handle->event.message, message, message_length);
            handle->event.message[message_length] = '\0';
        }
    }
    pthread_mutex_unlock(&handle->event_lock);
}

static void teavm_ws_push_event(TeavmWsHandle* handle, int type, int data0, int data1, const char* message) {
    size_t message_length = message == NULL ? 0 : strlen(message);
    teavm_ws_push_event_bytes(handle, type, data0, data1, message, message_length);
}

static void teavm_ws_push_error_event(TeavmWsHandle* handle, const char* message) {
    size_t message_length = message == NULL ? 0 : strlen(message);
    teavm_ws_push_event_bytes(handle, WS_EVENT_ERROR, (int)message_length, 0, message, message_length);
}

static void teavm_ws_clear_close_reason(TeavmWsHandle* handle) {
    if(handle->close_reason != NULL) {
        free(handle->close_reason);
        handle->close_reason = NULL;
    }
}

static void teavm_ws_store_close_reason(TeavmWsHandle* handle, const char* reason, size_t reason_length) {
    teavm_ws_clear_close_reason(handle);
    if(reason == NULL || reason_length == 0) {
        return;
    }
    handle->close_reason = (char*)calloc(reason_length + 1, sizeof(char));
    if(handle->close_reason != NULL) {
        memcpy(handle->close_reason, reason, reason_length);
        handle->close_reason[reason_length] = '\0';
    }
}

static void teavm_ws_set_error_from_curl(TeavmWsHandle* handle, CURLcode code, const char* fallback) {
    if(handle != NULL && handle->error_buffer[0] != '\0') {
        teavm_ws_set_error(handle->error_buffer);
        return;
    }
    if(g_curl.easy_strerror != NULL) {
        const char* curl_message = g_curl.easy_strerror(code);
        if(curl_message != NULL && curl_message[0] != '\0') {
            teavm_ws_set_error(curl_message);
            return;
        }
    }
    teavm_ws_set_error(fallback);
}

static int teavm_ws_load_symbol(void* library, void** target, const char* symbol) {
    *target = dlsym(library, symbol);
    if(*target != NULL) {
        return 1;
    }
    const char* error = dlerror();
    teavm_ws_set_error(error == NULL ? "Failed to load a libcurl websocket symbol." : error);
    return 0;
}

static void* teavm_ws_linux_try_dlopen(const char* libraryPath) {
    if(libraryPath == NULL || libraryPath[0] == '\0') {
        return NULL;
    }
    dlerror();
    return dlopen(libraryPath, RTLD_LAZY | RTLD_LOCAL);
}

static int teavm_ws_linux_read_executable_path(char* executablePath, size_t executablePathCapacity) {
    if(executablePath == NULL || executablePathCapacity < 2) {
        return 0;
    }

#if defined(__APPLE__)
    uint32_t pathCapacity = (uint32_t)executablePathCapacity;
    if(_NSGetExecutablePath(executablePath, &pathCapacity) != 0) {
        return 0;
    }
#else
    ssize_t executablePathLength = readlink("/proc/self/exe", executablePath, executablePathCapacity - 1);
    if(executablePathLength <= 0) {
        return 0;
    }
    executablePath[executablePathLength] = '\0';
#endif

    char resolvedExecutablePath[PATH_MAX];
    if(realpath(executablePath, resolvedExecutablePath) != NULL) {
        strncpy(executablePath, resolvedExecutablePath, executablePathCapacity - 1);
        executablePath[executablePathCapacity - 1] = '\0';
    }
    return 1;
}

static void* teavm_ws_linux_try_dlopen_from_executable_dir(const char* libraryName) {
    char executablePath[PATH_MAX];
    if(!teavm_ws_linux_read_executable_path(executablePath, sizeof(executablePath))) {
        return NULL;
    }

    char* slash = strrchr(executablePath, '/');
    if(slash == NULL) {
        return NULL;
    }
    *slash = '\0';

    char localLibraryPath[PATH_MAX];
    int written = snprintf(localLibraryPath, sizeof(localLibraryPath), "%s/%s", executablePath, libraryName);
    if(written <= 0 || written >= (int)sizeof(localLibraryPath)) {
        return NULL;
    }
    return teavm_ws_linux_try_dlopen(localLibraryPath);
}

static void* teavm_ws_linux_open_curl_library(void) {
    const char* configuredPath = getenv(TEAVM_WS_LINUX_RUNTIME_CURL_ENV);
    if(configuredPath != NULL && configuredPath[0] != '\0') {
        void* library = teavm_ws_linux_try_dlopen(configuredPath);
        if(library != NULL) {
            return library;
        }
    }

#if defined(__APPLE__)
    void* library = teavm_ws_linux_try_dlopen_from_executable_dir("libcurl.4.dylib");
    if(library != NULL) {
        return library;
    }

    library = teavm_ws_linux_try_dlopen_from_executable_dir("libcurl.dylib");
    if(library != NULL) {
        return library;
    }

    library = teavm_ws_linux_try_dlopen("libcurl.4.dylib");
    if(library != NULL) {
        return library;
    }

    return teavm_ws_linux_try_dlopen("libcurl.dylib");
#else
    void* library = teavm_ws_linux_try_dlopen_from_executable_dir("libcurl.so.4");
    if(library != NULL) {
        return library;
    }

    library = teavm_ws_linux_try_dlopen_from_executable_dir("libcurl.so");
    if(library != NULL) {
        return library;
    }

    library = teavm_ws_linux_try_dlopen("libcurl.so.4");
    if(library != NULL) {
        return library;
    }

    return teavm_ws_linux_try_dlopen("libcurl.so");
#endif
}

static int teavm_ws_linux_load_curl(void) {
    pthread_mutex_lock(&g_curl.lock);
    if(g_curl.load_state == 1) {
        pthread_mutex_unlock(&g_curl.lock);
        return 1;
    }
    if(g_curl.load_state == -1) {
        pthread_mutex_unlock(&g_curl.lock);
        return 0;
    }

    void* library = teavm_ws_linux_open_curl_library();
    if(library == NULL) {
        const char* error = dlerror();
        teavm_ws_set_error(error == NULL ? "Unable to load a libcurl runtime." : error);
        g_curl.load_state = -1;
        pthread_mutex_unlock(&g_curl.lock);
        return 0;
    }

    if(!teavm_ws_load_symbol(library, (void**)&g_curl.global_init, "curl_global_init")
            || !teavm_ws_load_symbol(library, (void**)&g_curl.easy_init, "curl_easy_init")
            || !teavm_ws_load_symbol(library, (void**)&g_curl.easy_cleanup, "curl_easy_cleanup")
            || !teavm_ws_load_symbol(library, (void**)&g_curl.easy_setopt, "curl_easy_setopt")
            || !teavm_ws_load_symbol(library, (void**)&g_curl.easy_perform, "curl_easy_perform")
            || !teavm_ws_load_symbol(library, (void**)&g_curl.easy_strerror, "curl_easy_strerror")
            || !teavm_ws_load_symbol(library, (void**)&g_curl.ws_recv, "curl_ws_recv")
            || !teavm_ws_load_symbol(library, (void**)&g_curl.ws_send, "curl_ws_send")) {
        dlclose(library);
        g_curl.library = NULL;
        g_curl.load_state = -1;
        pthread_mutex_unlock(&g_curl.lock);
        return 0;
    }

    CURLcode init_result = g_curl.global_init(CURL_GLOBAL_DEFAULT);
    if(init_result != CURLE_OK) {
        teavm_ws_set_error("libcurl global initialization failed.");
        dlclose(library);
        g_curl.library = NULL;
        g_curl.load_state = -1;
        pthread_mutex_unlock(&g_curl.lock);
        return 0;
    }

    g_curl.library = library;
    g_curl.load_state = 1;
    pthread_mutex_unlock(&g_curl.lock);
    return 1;
}

static int teavm_ws_easy_setopt_string(TeavmWsHandle* handle, CURLoption option, const char* value,
                                       const char* fallback_message) {
    handle->error_buffer[0] = '\0';
    CURLcode result = g_curl.easy_setopt(handle->curl, option, value);
    if(result == CURLE_OK) {
        return 1;
    }
    teavm_ws_set_error_from_curl(handle, result, fallback_message);
    return 0;
}

static int teavm_ws_easy_setopt_long(TeavmWsHandle* handle, CURLoption option, long value,
                                     const char* fallback_message) {
    handle->error_buffer[0] = '\0';
    CURLcode result = g_curl.easy_setopt(handle->curl, option, value);
    if(result == CURLE_OK) {
        return 1;
    }
    teavm_ws_set_error_from_curl(handle, result, fallback_message);
    return 0;
}

static void teavm_ws_finalize_close(TeavmWsHandle* handle, int close_code, const char* reason, size_t reason_length) {
    atomic_store(&handle->state, WS_STATE_CLOSED);
    teavm_ws_push_event_bytes(handle, WS_EVENT_CLOSE, close_code, (int)reason_length, reason, reason_length);
}

static void teavm_ws_finalize_error(TeavmWsHandle* handle, const char* fallback_message, CURLcode code) {
    atomic_store(&handle->state, WS_STATE_CLOSED);
    teavm_ws_set_error_from_curl(handle, code, fallback_message);
    teavm_ws_push_error_event(handle, g_last_error);
}

static void* teavm_ws_reader_thread(void* parameter) {
    TeavmWsHandle* handle = (TeavmWsHandle*)parameter;
    unsigned char buffer[TEAVM_WS_LINUX_RECV_BUFFER_SIZE];

    for(;;) {
        if(atomic_load(&handle->stop_requested) != 0) {
            return NULL;
        }

        size_t bytes_received = 0;
        const struct curl_ws_frame* meta = NULL;
        handle->error_buffer[0] = '\0';
        CURLcode result = g_curl.ws_recv(handle->curl, buffer, sizeof(buffer) - 1, &bytes_received, &meta);

        if(result == CURLE_AGAIN) {
            teavm_ws_sleep_millis(TEAVM_WS_LINUX_POLL_DELAY_MILLIS);
            continue;
        }
        if(result == CURLE_GOT_NOTHING) {
            if(atomic_load(&handle->state) == WS_STATE_CLOSING) {
                const char* close_reason = handle->close_reason == NULL ? "" : handle->close_reason;
                teavm_ws_finalize_close(handle, handle->close_code, close_reason, strlen(close_reason));
            }
            else {
                teavm_ws_finalize_close(handle, TEAVM_WS_LINUX_CLOSE_CODE_NORMAL, "", 0);
            }
            return NULL;
        }
        if(result != CURLE_OK) {
            teavm_ws_finalize_error(handle, "libcurl websocket receive failed.", result);
            return NULL;
        }
        if(meta == NULL) {
            continue;
        }

        buffer[bytes_received] = '\0';

        if((meta->flags & CURLWS_CLOSE) != 0) {
            int close_code = TEAVM_WS_LINUX_CLOSE_CODE_NORMAL;
            const char* close_reason = "";
            size_t close_reason_length = 0;
            if(bytes_received >= 2) {
                close_code = ((int)buffer[0] << 8) | (int)buffer[1];
                close_reason = (const char*)(buffer + 2);
                close_reason_length = bytes_received - 2;
            }
            else if(atomic_load(&handle->state) == WS_STATE_CLOSING) {
                close_code = handle->close_code;
                if(handle->close_reason != NULL) {
                    close_reason = handle->close_reason;
                    close_reason_length = strlen(handle->close_reason);
                }
            }
            teavm_ws_finalize_close(handle, close_code, close_reason, close_reason_length);
            return NULL;
        }

        if((meta->flags & CURLWS_TEXT) != 0) {
            teavm_ws_push_event_bytes(handle, WS_EVENT_MESSAGE_TEXT, (int)bytes_received, 0,
                    (const char*)buffer, bytes_received);
        }
    }
}

int gdx_teavm_ws_glfw_supported(void) {
    return teavm_ws_linux_load_curl();
}

int64_t gdx_teavm_ws_glfw_create(const char* url) {
    teavm_ws_set_error(NULL);
    if(url == NULL || url[0] == '\0') {
        teavm_ws_set_error("A websocket URL is required.");
        return 0;
    }
    if(!teavm_ws_linux_load_curl()) {
        return 0;
    }

    TeavmWsHandle* handle = (TeavmWsHandle*)calloc(1, sizeof(TeavmWsHandle));
    if(handle == NULL) {
        teavm_ws_set_error("Failed to allocate websocket handle.");
        return 0;
    }

    pthread_mutex_init(&handle->event_lock, NULL);
    atomic_init(&handle->state, WS_STATE_CONNECTING);
    atomic_init(&handle->stop_requested, 0);
    handle->close_code = TEAVM_WS_LINUX_CLOSE_CODE_NORMAL;

    handle->curl = g_curl.easy_init();
    if(handle->curl == NULL) {
        teavm_ws_set_error("libcurl easy handle creation failed.");
        gdx_teavm_ws_glfw_destroy((int64_t)(intptr_t)handle);
        return 0;
    }

    if(!teavm_ws_easy_setopt_string(handle, CURLOPT_URL, url, "Failed to set websocket URL.")
            || !teavm_ws_easy_setopt_string(handle, CURLOPT_USERAGENT, "gdx-teavm-websocket/1.0",
                    "Failed to set libcurl user agent.")
            || !teavm_ws_easy_setopt_string(handle, CURLOPT_ERRORBUFFER, handle->error_buffer,
                    "Failed to configure libcurl error buffer.")
            || !teavm_ws_easy_setopt_long(handle, CURLOPT_CONNECT_ONLY, 2L,
                    "Failed to enable libcurl websocket mode.")
            || !teavm_ws_easy_setopt_long(handle, CURLOPT_TIMEOUT_MS, 10000L,
                    "Failed to configure websocket connection timeout.")) {
        gdx_teavm_ws_glfw_destroy((int64_t)(intptr_t)handle);
        return 0;
    }

    handle->error_buffer[0] = '\0';
    CURLcode perform_result = g_curl.easy_perform(handle->curl);
    if(perform_result != CURLE_OK) {
        teavm_ws_set_error_from_curl(handle, perform_result, "libcurl websocket handshake failed.");
        gdx_teavm_ws_glfw_destroy((int64_t)(intptr_t)handle);
        return 0;
    }

    if(pthread_create(&handle->thread, NULL, teavm_ws_reader_thread, handle) != 0) {
        teavm_ws_set_error("Failed to start native websocket receive thread.");
        gdx_teavm_ws_glfw_destroy((int64_t)(intptr_t)handle);
        return 0;
    }
    handle->thread_started = 1;

    atomic_store(&handle->state, WS_STATE_OPEN);
    teavm_ws_push_event(handle, WS_EVENT_OPEN, 0, 0, NULL);
    return (int64_t)(intptr_t)handle;
}

int gdx_teavm_ws_glfw_state(int64_t handle_value) {
    TeavmWsHandle* handle = (TeavmWsHandle*)(intptr_t)handle_value;
    if(handle == NULL) {
        return WS_STATE_CLOSED;
    }
    return atomic_load(&handle->state);
}

int gdx_teavm_ws_glfw_send_text(int64_t handle_value, const char* text) {
    TeavmWsHandle* handle = (TeavmWsHandle*)(intptr_t)handle_value;
    if(handle == NULL || handle->curl == NULL) {
        teavm_ws_set_error("WebSocket handle is not open.");
        return 0;
    }
    if(text == NULL) {
        text = "";
    }

    size_t total_length = strlen(text);
    size_t offset = 0;
    while(offset < total_length || (total_length == 0 && offset == 0)) {
        size_t sent = 0;
        handle->error_buffer[0] = '\0';
        CURLcode result = g_curl.ws_send(handle->curl, text + offset, total_length - offset, &sent, 0, CURLWS_TEXT);
        if(result == CURLE_AGAIN) {
            teavm_ws_sleep_millis(TEAVM_WS_LINUX_POLL_DELAY_MILLIS);
            continue;
        }
        if(result != CURLE_OK) {
            teavm_ws_set_error_from_curl(handle, result, "libcurl websocket send failed.");
            return 0;
        }
        if(sent == 0 && total_length > offset) {
            teavm_ws_set_error("libcurl websocket send made no progress.");
            return 0;
        }
        offset += sent;
        if(total_length == 0) {
            break;
        }
    }
    return 1;
}

int gdx_teavm_ws_glfw_close(int64_t handle_value, int code, const char* reason) {
    TeavmWsHandle* handle = (TeavmWsHandle*)(intptr_t)handle_value;
    if(handle == NULL || handle->curl == NULL) {
        return 1;
    }
    if(reason == NULL) {
        reason = "";
    }

    size_t reason_length = strlen(reason);
    if(reason_length > TEAVM_WS_LINUX_CLOSE_REASON_LIMIT) {
        reason_length = TEAVM_WS_LINUX_CLOSE_REASON_LIMIT;
    }
    teavm_ws_store_close_reason(handle, reason, reason_length);
    handle->close_code = code;
    atomic_store(&handle->state, WS_STATE_CLOSING);

    unsigned char payload[2 + TEAVM_WS_LINUX_CLOSE_REASON_LIMIT];
    payload[0] = (unsigned char)((code >> 8) & 0xFF);
    payload[1] = (unsigned char)(code & 0xFF);
    if(reason_length > 0) {
        memcpy(payload + 2, reason, reason_length);
    }

    size_t total_length = 2 + reason_length;
    size_t offset = 0;
    while(offset < total_length) {
        size_t sent = 0;
        handle->error_buffer[0] = '\0';
        CURLcode result = g_curl.ws_send(handle->curl, payload + offset, total_length - offset, &sent, 0, CURLWS_CLOSE);
        if(result == CURLE_AGAIN) {
            teavm_ws_sleep_millis(TEAVM_WS_LINUX_POLL_DELAY_MILLIS);
            continue;
        }
        if(result != CURLE_OK) {
            teavm_ws_set_error_from_curl(handle, result, "libcurl websocket close failed.");
            return 0;
        }
        if(sent == 0 && total_length > offset) {
            teavm_ws_set_error("libcurl websocket close made no progress.");
            return 0;
        }
        offset += sent;
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

    pthread_mutex_lock(&handle->event_lock);
    if(handle->event.type != WS_EVENT_NONE) {
        event_data[0] = handle->event.type;
        event_data[1] = handle->event.data0;
        event_data[2] = handle->event.data1;
        if(message_buffer != NULL && message_buffer_capacity > 0 && handle->event.message != NULL) {
            teavm_ws_copy_string((char*)message_buffer, message_buffer_capacity, handle->event.message);
        }
        teavm_ws_destroy_event(&handle->event);
        pthread_mutex_unlock(&handle->event_lock);
        return 1;
    }
    pthread_mutex_unlock(&handle->event_lock);
    return 0;
}

void gdx_teavm_ws_glfw_destroy(int64_t handle_value) {
    TeavmWsHandle* handle = (TeavmWsHandle*)(intptr_t)handle_value;
    if(handle == NULL) {
        return;
    }

    atomic_store(&handle->stop_requested, 1);
    if(handle->thread_started) {
        pthread_join(handle->thread, NULL);
        handle->thread_started = 0;
    }
    if(handle->curl != NULL) {
        g_curl.easy_cleanup(handle->curl);
        handle->curl = NULL;
    }

    pthread_mutex_lock(&handle->event_lock);
    teavm_ws_destroy_event(&handle->event);
    pthread_mutex_unlock(&handle->event_lock);
    pthread_mutex_destroy(&handle->event_lock);
    teavm_ws_clear_close_reason(handle);
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
    const char* message = "TeaVM GLFW websocket backend is not implemented for this platform.";
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
