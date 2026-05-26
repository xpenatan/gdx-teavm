package com.github.xpenatan.gdx.teavm.backends.shared.mock;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class TeaUnsupportedNet implements Net {
    @Override
    public void sendHttpRequest(HttpRequest httpRequest, HttpResponseListener httpResponseListener) {
        if(httpResponseListener != null) {
            httpResponseListener.failed(new GdxRuntimeException("HTTP requests are not implemented for this TeaVM backend"));
        }
    }

    @Override
    public void cancelHttpRequest(HttpRequest httpRequest) {
    }

    @Override
    public boolean isHttpRequestPending(HttpRequest httpRequest) {
        return false;
    }

    @Override
    public ServerSocket newServerSocket(Protocol protocol, String ipAddress, int port, ServerSocketHints hints) {
        throw new GdxRuntimeException("Server sockets are not implemented for this TeaVM backend");
    }

    @Override
    public ServerSocket newServerSocket(Protocol protocol, int port, ServerSocketHints hints) {
        throw new GdxRuntimeException("Server sockets are not implemented for this TeaVM backend");
    }

    @Override
    public Socket newClientSocket(Protocol protocol, String host, int port, SocketHints hints) {
        throw new GdxRuntimeException("Client sockets are not implemented for this TeaVM backend");
    }

    @Override
    public boolean openURI(String uri) {
        return false;
    }
}
