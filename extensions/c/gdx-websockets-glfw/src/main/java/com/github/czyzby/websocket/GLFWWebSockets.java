package com.github.czyzby.websocket;

import com.github.czyzby.websocket.impl.GLFWWebSocket;

/** Initializes the TeaVM C GLFW websocket implementation.
 * Native backend support currently includes Windows and Linux. */
public class GLFWWebSockets {

    private GLFWWebSockets() {
    }

    public static void initiate() {
        WebSockets.FACTORY = new GLFWWebSocketFactory();
    }

    protected static class GLFWWebSocketFactory implements WebSockets.WebSocketFactory {
        @Override
        public WebSocket newWebSocket(String url) {
            return new GLFWWebSocket(url);
        }
    }
}
