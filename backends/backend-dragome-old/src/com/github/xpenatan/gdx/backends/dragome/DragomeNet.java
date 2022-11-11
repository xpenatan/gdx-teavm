/*******************************************************************************
 * Copyright 2016 Natan Guilherme.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.github.xpenatan.gdx.backends.dragome;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.EventHandler;
import org.w3c.dom.XMLHttpRequest;
import org.w3c.dom.events.Event;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.dragome.commons.javascript.ScriptHelper;

/**
 * Ported from GWT backend.
 *
 * @author xpenatan
 */
public class DragomeNet implements Net {

    ObjectMap<HttpRequest, XMLHttpRequest> requests;
    ObjectMap<HttpRequest, HttpResponseListener> listeners;

    private final class HttpClientResponse implements HttpResponse {

        private XMLHttpRequest response;
        private HttpStatus status;

        public HttpClientResponse(XMLHttpRequest response) {
            this.response = response;
            this.status = new HttpStatus(response.getStatus());
        }

        @Override
        public byte[] getResult() {
            return null;
        }

        @Override
        public String getResultAsString() {
            return response.getResponseText();
        }

        @Override
        public InputStream getResultAsStream() {
            return null;
        }

        @Override
        public HttpStatus getStatus() {
            return status;
        }

        @Override
        public Map<String, List<String>> getHeaders() {
            Map<String, List<String>> headers = new HashMap<String, List<String>>();
            Header[] responseHeaders = getHeaderss();
            for(int i = 0; i < responseHeaders.length; i++) {
                Header header = responseHeaders[i];
                if(header != null) {
                    String headerName = responseHeaders[i].getName();
                    List<String> headerValues = headers.get(headerName);
                    if(headerValues == null) {
                        headerValues = new ArrayList<String>();
                        headers.put(headerName, headerValues);
                    }
                    headerValues.add(responseHeaders[i].getValue());
                }
            }
            return headers;
        }

        @Override
        public String getHeader(String name) {
            return response.getResponseHeader(name);
        }

        public Header[] getHeaderss() {
            String allHeaders = response.getAllResponseHeaders();
            String[] unparsedHeaders = allHeaders.split("\n");
            List<Header> parsedHeaders = new ArrayList<Header>();

            for(String unparsedHeader : unparsedHeaders) {

                if(unparsedHeader == null || unparsedHeader.trim().isEmpty()) {
                    continue;
                }

                int endOfNameIdx = unparsedHeader.indexOf(':');
                if(endOfNameIdx < 0) {
                    continue;
                }

                final String name = unparsedHeader.substring(0, endOfNameIdx).trim();
                final String value = unparsedHeader.substring(endOfNameIdx + 1).trim();
                Header header = new Header() {
                    @Override
                    public String getName() {
                        return name;
                    }

                    @Override
                    public String getValue() {
                        return value;
                    }

                    @Override
                    public String toString() {
                        return name + " : " + value;
                    }
                };

                parsedHeaders.add(header);
            }

            return parsedHeaders.toArray(new Header[parsedHeaders.size()]);
        }
    }

    public DragomeNet() {
        requests = new ObjectMap<HttpRequest, XMLHttpRequest>();
        listeners = new ObjectMap<HttpRequest, HttpResponseListener>();
    }

    @Override
    public void sendHttpRequest(final HttpRequest httpRequest, final HttpResponseListener httpResultListener) {
        if(httpRequest.getUrl() == null) {
            httpResultListener.failed(new GdxRuntimeException("can't process a HTTP request without URL set"));
            return;
        }

        final String method = httpRequest.getMethod();
        final String value = httpRequest.getContent();
        final boolean valueInBody = method.equalsIgnoreCase(HttpMethods.POST) || method.equals(HttpMethods.PUT);

        String url = httpRequest.getUrl();
        String type = "";
        if(method.equalsIgnoreCase(HttpMethods.GET)) {
            if(value != null) {
                url += "?" + value;
            }
            type = "GET";
        }
        else if(method.equalsIgnoreCase(HttpMethods.POST)) {
            type = "POST";
        }
        else if(method.equalsIgnoreCase(HttpMethods.DELETE)) {
            if(value != null) {
                url += "?" + value;
            }
            type = "DELETE";
        }
        else if(method.equalsIgnoreCase(HttpMethods.PUT)) {
            type = "PUT";
        }
        else {
            throw new GdxRuntimeException("Unsupported HTTP Method");
        }

        final XMLHttpRequest request = ScriptHelper.evalCasting("new XMLHttpRequest()", XMLHttpRequest.class, this);

        request.setOnreadystatechange(new EventHandler() {
            @Override
            public void handleEvent(Event evt) {
                if(request.getReadyState() == XMLHttpRequest.DONE) {
                    if(request.getStatus() != 200) {
                        httpResultListener.handleHttpResponse(new HttpClientResponse(request));
                        requests.remove(httpRequest);
                        listeners.remove(httpRequest);
                    }
                    else {
                        httpResultListener.failed(new GdxRuntimeException("HttpRequest send error"));
                        requests.remove(httpRequest);
                        listeners.remove(httpRequest);
                    }
                }
            }
        });

        Map<String, String> content = httpRequest.getHeaders();
        Set<String> keySet = content.keySet();

        for(String name : keySet) {
            request.setRequestHeader(name, content.get(name));
        }

        request.setTimeout(httpRequest.getTimeOut());
        request.setWithCredentials(httpRequest.getIncludeCredentials());
        requests.put(httpRequest, request);
        listeners.put(httpRequest, httpResultListener);
        request.open(type, url);
        if(valueInBody)
            request.send(value);
        else
            request.send();
    }

    @Override
    public void cancelHttpRequest(HttpRequest httpRequest) {
        HttpResponseListener httpResponseListener = listeners.get(httpRequest);
        XMLHttpRequest request = requests.get(httpRequest);

        if(httpResponseListener != null && request != null) {
            ScriptHelper.put("request", request, this);
            ScriptHelper.evalNoResult("request.node.onreadystatechange=function(){};", this);
            request.abort();
            httpResponseListener.cancelled();
            requests.remove(httpRequest);
            listeners.remove(httpRequest);
        }
    }

    @Override
    public ServerSocket newServerSocket(Protocol protocol, String hostname, int port, ServerSocketHints hints) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public ServerSocket newServerSocket(Protocol protocol, int port, ServerSocketHints hints) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Socket newClientSocket(Protocol protocol, String host, int port, SocketHints hints) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean openURI(String URI) {
        String features = null;
        ScriptHelper.put("URI", URI, this);
        ScriptHelper.put("features", features, this);
        ScriptHelper.evalNoResult("window.open(URI, _blank, features);", this);
        return true;
    }

    abstract class Header {
        /**
         * Returns the name of the HTTP header.
         *
         * @return name of the HTTP header
         */
        public abstract String getName();

        /**
         * Returns the value of the HTTP header.
         *
         * @return value of the HTTP header
         */
        public abstract String getValue();
    }
}
