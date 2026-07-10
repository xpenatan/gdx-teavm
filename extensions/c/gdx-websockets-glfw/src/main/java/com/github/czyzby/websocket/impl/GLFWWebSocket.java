package com.github.czyzby.websocket.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.websocket.WebSockets;
import com.github.czyzby.websocket.data.WebSocketCloseCode;
import com.github.czyzby.websocket.data.WebSocketException;
import com.github.czyzby.websocket.data.WebSocketState;
import java.nio.charset.StandardCharsets;
import org.teavm.interop.Address;
import org.teavm.interop.Import;
import org.teavm.interop.c.Include;

@Include("teavm_websocket_glfw.h")
public class GLFWWebSocket extends AbstractWebSocket {

    private static final int EVENT_NONE = 0;
    private static final int EVENT_OPEN = 1;
    private static final int EVENT_MESSAGE_TEXT = 2;
    private static final int EVENT_ERROR = 3;
    private static final int EVENT_CLOSE = 4;

    private static final int STATE_CONNECTING = 0;
    private static final int STATE_OPEN = 1;
    private static final int STATE_CLOSING = 2;
    private static final int STATE_CLOSED = 3;

    private static final int MESSAGE_BUFFER_SIZE = 16 * 1024;
    private static final int REASON_BUFFER_SIZE = 1024;

    private static final byte[] MESSAGE_BUFFER = new byte[MESSAGE_BUFFER_SIZE];
    private static final byte[] REASON_BUFFER = new byte[REASON_BUFFER_SIZE];
    private static final int[] EVENT_DATA = new int[4];

    private static final Array<GLFWWebSocket> SOCKETS = new Array<>();
    private static boolean pumpRegistered;

    private long nativeHandle;

    public GLFWWebSocket(String url) {
        super(url);
        ensurePumpRegistered();
    }

    @Override
    public void connect() throws WebSocketException {
        if(nativeHandle != 0 && getState() != WebSocketState.CLOSED) {
            close(WebSockets.ABNORMAL_AUTOMATIC_CLOSE_CODE, "reconnect");
        }
        long handle = createSocket(getUrl());
        if(handle == 0) {
            throw new WebSocketException(readReasonBuffer());
        }
        nativeHandle = handle;
        registerSocket(this);
    }

    @Override
    public WebSocketState getState() {
        if(nativeHandle == 0) {
            return WebSocketState.CLOSED;
        }
        int stateId = socketState(nativeHandle);
        if(stateId < STATE_CONNECTING || stateId > STATE_CLOSED) {
            return WebSocketState.CLOSED;
        }
        return WebSocketState.getById(stateId);
    }

    @Override
    protected void sendBinary(byte[] packet) throws Exception {
        throw new WebSocketException("Binary websocket frames are not implemented for TeaVM GLFW yet.");
    }

    @Override
    protected void sendString(String packet) throws Exception {
        if(nativeHandle == 0) {
            throw new WebSocketException("WebSocket is not connected.");
        }
        if(packet == null) {
            packet = "";
        }
        if(!sendText(nativeHandle, packet)) {
            throw new WebSocketException(readReasonBuffer());
        }
    }

    @Override
    public void close(int closeCode, String reason) throws WebSocketException {
        WebSocketCloseCode.checkIfAllowedInClient(closeCode);
        if(nativeHandle == 0) {
            return;
        }
        if(reason == null) {
            reason = "";
        }
        if(!closeSocket(nativeHandle, closeCode, reason)) {
            throw new WebSocketException(readReasonBuffer());
        }
    }

    @Override
    public boolean isSupported() {
        return isNativeSupported();
    }

    private void handleNativeEvents() {
        if(nativeHandle == 0) {
            return;
        }
        while(pollEvent(nativeHandle, EVENT_DATA, Address.ofData(MESSAGE_BUFFER), MESSAGE_BUFFER_SIZE)) {
            int eventType = EVENT_DATA[0];
            if(eventType == EVENT_NONE) {
                return;
            }
            if(eventType == EVENT_OPEN) {
                postOpenEvent();
                continue;
            }
            if(eventType == EVENT_MESSAGE_TEXT) {
                postMessageEvent(readMessageBuffer(EVENT_DATA[1]));
                continue;
            }
            if(eventType == EVENT_ERROR) {
                postErrorEvent(new WebSocketException(readMessageBuffer(EVENT_DATA[1])));
                continue;
            }
            if(eventType == EVENT_CLOSE) {
                int closeCode = EVENT_DATA[1];
                String closeReason = readMessageBuffer(EVENT_DATA[2]);
                postCloseEvent(closeCode, closeReason);
                disposeNativeHandle();
                return;
            }
        }
    }

    private void disposeNativeHandle() {
        if(nativeHandle != 0) {
            destroySocket(nativeHandle);
            nativeHandle = 0;
        }
        unregisterSocket(this);
    }

    private static void ensurePumpRegistered() {
        if(!pumpRegistered) {
            pumpRegistered = true;
            Gdx.app.postRunnable(GLFWWebSocket::pumpSockets);
        }
    }

    private static void pumpSockets() {
        for(int i = SOCKETS.size - 1; i >= 0; i--) {
            SOCKETS.get(i).handleNativeEvents();
        }
        Gdx.app.postRunnable(GLFWWebSocket::pumpSockets);
    }

    private static void registerSocket(GLFWWebSocket socket) {
        if(!SOCKETS.contains(socket, true)) {
            SOCKETS.add(socket);
        }
    }

    private static void unregisterSocket(GLFWWebSocket socket) {
        SOCKETS.removeValue(socket, true);
    }

    private static String readMessageBuffer(int length) {
        if(length <= 0) {
            return "";
        }
        int size = Math.min(length, MESSAGE_BUFFER_SIZE);
        return new String(MESSAGE_BUFFER, 0, size, StandardCharsets.UTF_8);
    }

    private static String readReasonBuffer() {
        int length = readLastError(Address.ofData(REASON_BUFFER), REASON_BUFFER_SIZE);
        if(length <= 0) {
            return "Native websocket error";
        }
        int size = Math.min(length, REASON_BUFFER_SIZE);
        return new String(REASON_BUFFER, 0, size, StandardCharsets.UTF_8);
    }

    @Import(name = "gdx_teavm_ws_glfw_supported")
    private static native boolean isNativeSupported();

    @Import(name = "gdx_teavm_ws_glfw_create")
    private static native long createSocket(String url);

    @Import(name = "gdx_teavm_ws_glfw_state")
    private static native int socketState(long handle);

    @Import(name = "gdx_teavm_ws_glfw_send_text")
    private static native boolean sendText(long handle, String text);

    @Import(name = "gdx_teavm_ws_glfw_close")
    private static native boolean closeSocket(long handle, int code, String reason);

    @Import(name = "gdx_teavm_ws_glfw_poll_event")
    private static native boolean pollEvent(long handle, int[] eventData, Address messageBuffer, int messageBufferCapacity);

    @Import(name = "gdx_teavm_ws_glfw_destroy")
    private static native void destroySocket(long handle);

    @Import(name = "gdx_teavm_ws_glfw_last_error")
    private static native int readLastError(Address targetBuffer, int targetBufferCapacity);
}
