package com.github.xpenatan.gdx.teavm.backends.glfw;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * LWJGL implementation of the {@link Net} API, it could be reused in other Desktop backends since it doesn't depend on LWJGL.
 *
 * @author acoppes
 */
public class GLFWNet implements Net {

//    NetJavaImpl netJavaImpl;

    public GLFWNet(GLFWApplicationConfiguration configuration) {
//        netJavaImpl = new NetJavaImpl(configuration.maxNetThreads);
    }

    @Override
    public void sendHttpRequest(HttpRequest httpRequest, HttpResponseListener httpResponseListener) {
//        netJavaImpl.sendHttpRequest(httpRequest, httpResponseListener);
    }

    @Override
    public void cancelHttpRequest(HttpRequest httpRequest) {
//        netJavaImpl.cancelHttpRequest(httpRequest);
    }

    @Override
    public boolean isHttpRequestPending(HttpRequest httpRequest) {
//        return netJavaImpl.isHttpRequestPending(httpRequest);
        return false;
    }

    @Override
    public ServerSocket newServerSocket(Protocol protocol, String ipAddress, int port, ServerSocketHints hints) {
//        return new NetJavaServerSocketImpl(protocol, ipAddress, port, hints);
        throw new GdxRuntimeException("Not implemented yet");
    }

    @Override
    public ServerSocket newServerSocket(Protocol protocol, int port, ServerSocketHints hints) {
//        return new NetJavaServerSocketImpl(protocol, port, hints);
        throw new GdxRuntimeException("Not implemented yet");
    }

    @Override
    public Socket newClientSocket(Protocol protocol, String host, int port, SocketHints hints) {
//        return new NetJavaSocketImpl(protocol, host, port, hints);
        throw new GdxRuntimeException("Not implemented yet");
    }

    @Override
    public boolean openURI(String uri) {
//        if (SharedLibraryLoader.os == Os.MacOsX) {
//            try {
//                (new ProcessBuilder("open", (new URI(uri).toString()))).start();
//                return true;
//            } catch (Throwable t) {
//                return false;
//            }
//        } else if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
//            try {
//                Desktop.getDesktop().browse(new URI(uri));
//                return true;
//            } catch (Throwable t) {
//                return false;
//            }
//        } else if (SharedLibraryLoader.os == Os.Linux) {
//            try {
//                (new ProcessBuilder("xdg-open", (new URI(uri).toString()))).start();
//                return true;
//            } catch (Throwable t) {
//                return false;
//            }
//        }
        return false;
    }
}
