package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.teavm.jso.browser.Window;

/**
 * Implementation of the libGDX networking interface over TeaVM.
 *
 * @author noblemaster
 */
public class TeaNet implements Net {

  @Override
  public void sendHttpRequest(HttpRequest httpRequest, HttpResponseListener httpResponseListener) {

  }

  @Override
  public void cancelHttpRequest(HttpRequest httpRequest) {

  }

  @Override
  public ServerSocket newServerSocket(Protocol protocol, String hostname, int port, ServerSocketHints hints) {
    throw new GdxRuntimeException("Streaming sockets not available via WebGL.");
  }

  @Override
  public ServerSocket newServerSocket(Protocol protocol, int port, ServerSocketHints hints) {
    throw new GdxRuntimeException("Streaming sockets not available via WebGL.");
  }

  @Override
  public Socket newClientSocket(Protocol protocol, String host, int port, SocketHints hints) {
    throw new GdxRuntimeException("Streaming sockets not available via WebGL.");
  }

  @Override
  public boolean openURI(String URI) {
    // open via TeaVM...
    Window.current().open(URI, URI);
    return true;
  }
}
