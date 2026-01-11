package com.github.xpenatan.gdx.teavm.backend.web;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.*;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import org.teavm.jso.ajax.ReadyStateChangeHandler;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.Window;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Implementation of the libGDX networking interface over TeaVM. Please note, streaming sockets are generally
 * not supported via JavaScript. Please use WebSockets instead.
 * <p>
 * IMPORTANT: HTTP requests are currently only implemented to support transfer of text. Sending binary data
 * is currently not supported.
 *
 * @author noblemaster
 */
public class TeaNet implements Net {

  /** The list of active requests. */
  private final Map<HttpRequest, HttpResponseListener> httpResponseListeners;

  public TeaNet() {
    httpResponseListeners = new HashMap<HttpRequest, HttpResponseListener>(16);
  }

  @Override
  public void sendHttpRequest(final HttpRequest httpRequest, HttpResponseListener httpResponseListener) {
    httpResponseListeners.put(httpRequest, httpResponseListener);

    // AJAX request
    XMLHttpRequest request = XMLHttpRequest.create();

    // override content type
    Map<String, String> headers = httpRequest.getHeaders();
    final String contentType = headers.get("Content-Type");
    if (contentType != null) {
      request.overrideMimeType(contentType);
    }

    // waits until data is ready
    request.setOnReadyStateChange(new ReadyStateChangeHandler() {
      @Override
      public void stateChanged() {
        try {
          // the result is available as soon as the status changes to 'DONE'
          if (request.getReadyState() == XMLHttpRequest.DONE) {
            // report only as long as we didn't cancel yet
            HttpResponseListener httpResponseListener = httpResponseListeners.get(httpRequest);
            if (httpResponseListener != null) {
              // parse the status & content
              final HttpStatus status = new HttpStatus(request.getStatus());
              final String content = request.getResponseText();

              // parse the header
              final Map<String, List<String>> headers = new HashMap<String, List<String>>(8);
              String allHeaders = request.getAllResponseHeaders();
              String[] allHeaderLines = allHeaders.split("\r?\n|\r");
              for (String line : allHeaderLines) {
                int index = line.indexOf(':');
                if (index >= 0) {
                  String name = line.substring(0, index);
                  String value = line.substring(index + 1).trim();
                  List<String> list = headers.get(name);
                  if (list == null) {
                    list = new ArrayList<String>(1);
                    headers.put(name, list);
                  }
                  list.add(value);
                }
              }

              // return via listener...
              httpResponseListeners.remove(httpRequest);
              httpResponseListener.handleHttpResponse(new HttpResponse() {
                @Override
                public byte[] getResult() {
                  try {
                    return content.getBytes("UTF-8");
                  } catch (UnsupportedEncodingException e) {
                    throw new GdxRuntimeException("Cannot create input stream.", e);
                  }
                }

                @Override
                public String getResultAsString() {
                  return content;
                }

                @Override
                public InputStream getResultAsStream() {
                  return new ByteArrayInputStream(getResult());
                }

                @Override
                public HttpStatus getStatus() {
                  return status;
                }

                @Override
                public String getHeader(String name) {
                  List<String> list = headers.get(name);
                  return (list != null) && (list.size() >= 1) ? list.get(0) : null;
                }

                @Override
                public Map<String, List<String>> getHeaders() {
                  return headers;
                }
              });
            }
          }
        } catch (Exception e) {
          // return error...
          httpResponseListeners.remove(httpRequest);
          httpResponseListener.failed(e);
        }
      }
    });

    // obtain the content as text (streams not supported on WebGL)
    String content = httpRequest.getContent();
    if (content == null) {
      InputStream input = httpRequest.getContentStream();
      if (input != null) {
        // read input
        try {
          content = StreamUtils.copyStreamToString(input);
        } catch (IOException e) {
          throw new GdxRuntimeException("Error reading string from stream.", e);
        }
      }
    }

    // determine method
    String method = httpRequest.getMethod();
    boolean doingOutput = (content != null) && (method.equalsIgnoreCase(HttpMethods.POST) ||
            method.equalsIgnoreCase(HttpMethods.PUT));

    // create URL
    String url = httpRequest.getUrl();
    if ((content != null) && (!"".equals(content)) && (!doingOutput)) {
      url += "?" + content;
    }

    // open the connection
    boolean async = true;  // <-- report via listener (will wait otherwise for result)
    request.open(method, url, async);

    // write headers
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      request.setRequestHeader(entry.getKey(), entry.getValue());
    }

    // log information (for debugging)
//  Gdx.app.log("TeaNet", "HTTP " + method + ": " + url + " [Data: " + (content != null ? "YES" : "n/a")
//          + ", Content-Type: " + contentType + "]");

    // send with or without data...
    if (doingOutput) {
      // send the data as next...
      request.send(content);
    } else {
      // send without data...
      request.send();
    }
  }

  @Override
  public void cancelHttpRequest(HttpRequest httpRequest) {
    // cancel and remove from listeners list
    HttpResponseListener httpResponseListener = httpResponseListeners.get(httpRequest);
    if (httpResponseListener != null) {
      httpResponseListener.cancelled();
      httpResponseListeners.remove(httpRequest);
    }
  }

  @Override
  public boolean isHttpRequestPending(HttpRequest httpRequest) {
    return httpResponseListeners.get(httpRequest) != null && httpResponseListeners.get(httpRequest) != null;
  }

  @Override
  public ServerSocket newServerSocket(Protocol protocol, String hostname, int port, ServerSocketHints hints) {
    throw new GdxRuntimeException("Streaming sockets not available via JavaScript.");
  }

  @Override
  public ServerSocket newServerSocket(Protocol protocol, int port, ServerSocketHints hints) {
    throw new GdxRuntimeException("Streaming sockets not available via JavaScript.");
  }

  @Override
  public Socket newClientSocket(Protocol protocol, String host, int port, SocketHints hints) {
    throw new GdxRuntimeException("Streaming sockets not available via JavaScript.");
  }

  @Override
  public boolean openURI(String URI) {
    // open via TeaVM...
    Window.current().open(URI, URI);
    return true;
  }
}
