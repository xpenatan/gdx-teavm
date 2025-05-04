package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface XMLHttpRequestWrapper extends XMLHttpRequestEventTargetWrapper {

    public static final short UNSENT = 0;
    public static final short OPENED = 1;
    public static final short HEADERS_RECEIVED = 2;
    public static final short LOADING = 3;
    public static final short DONE = 4;

    @JSProperty
    void setOnreadystatechange(EventHandlerWrapper onreadystatechange);

    void open(String method, String url);

    void open(String method, String url, boolean async);

    void setRequestHeader(String header, String value);

    @JSProperty
    void setResponseType(String type);

    void send();

    @JSProperty
    short getReadyState();

    @JSProperty
    short getStatus();

    @JSProperty
    NodeWrapper getResponse();

    @JSProperty
    String getResponseText();
}