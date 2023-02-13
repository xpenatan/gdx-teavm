package com.github.xpenatan.gdx.backends.teavm.preloader;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.teavm.AssetLoaderListener;
import com.github.xpenatan.gdx.backends.teavm.dom.DocumentWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.EventHandlerWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.EventListenerWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.EventWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.HTMLElementWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.HTMLImageElementWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.LocationWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.NodeWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.ProgressEventWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.WindowWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.XMLHttpRequestWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;
import com.github.xpenatan.gdx.backends.teavm.preloader.AssetDownloader.AssetDownload;
import com.github.xpenatan.gdx.backends.teavm.util.TeaJSHelper;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.Window;

public class AssetDownloadImpl implements AssetDownload {

    private TeaJSHelper jsHelper;

    private int queue;
    private boolean useBrowserCache = true;
    private boolean useInlineBase64;

    private boolean showLog = true;

    public AssetDownloadImpl(TeaJSHelper jsHelper) {
        this.jsHelper = jsHelper;
    }

    @Override
    public boolean isUseBrowserCache() {
        return false;
    }

    @Override
    public String getHostPageBaseURL() {
        WindowWrapper currentWindow = jsHelper.getCurrentWindow();
        LocationWrapper location = currentWindow.getLocation();
        String href = location.getHref();
        return href;
    }

    @Override
    public int getQueue() {
        return queue;
    }

    @Override
    public void subtractQueue() {
        queue--;
    }

    @Override
    public void addQueue() {
        queue++;
    }

    @Override
    public void load(boolean async, String url, AssetType type, String mimeType, AssetLoaderListener<?> listener) {
        switch(type) {
            case Text:
                loadText(async, url, (AssetLoaderListener<String>)listener);
                break;
            case Image:
                loadImage(async, url, mimeType, (AssetLoaderListener<HTMLImageElementWrapper>)listener);
                break;
            case Binary:
                loadBinary(async, url, (AssetLoaderListener<Blob>)listener);
                break;
            case Audio:
                loadAudio(async, url, (AssetLoaderListener<Void>)listener);
                break;
            case Directory:
                listener.onSuccess(url, null);
                break;
            default:
                throw new GdxRuntimeException("Unsupported asset type " + type);
        }
    }

    @Override
    public void loadText(boolean async, String url, AssetLoaderListener<String> listener) {
        if(showLog)
            System.out.println("Loading asset : " + url);
        final XMLHttpRequestWrapper request = (XMLHttpRequestWrapper)XMLHttpRequest.create();
        request.setOnreadystatechange(new EventHandlerWrapper() {
            @Override
            public void handleEvent(EventWrapper evt) {
                if(request.getReadyState() == XMLHttpRequestWrapper.DONE) {
                    if(request.getStatus() != 200) {
                        listener.onFailure(url);
                    }
                    else {
                        if(showLog)
                            System.out.println("Asset loaded: " + url);
                        listener.onSuccess(url, request.getResponseText());
                    }
                    subtractQueue();
                }
            }
        });
        addQueue();
        setOnProgress(request, listener);
        request.open("GET", url, async);
        request.setRequestHeader("Content-Type", "text/plain; charset=utf-8");
        request.send();
    }

    @Override
    public void loadScript(boolean async, String url, AssetLoaderListener<Object> listener) {
        if(showLog)
            System.out.println("Loading script : " + url);
        final XMLHttpRequestWrapper request = (XMLHttpRequestWrapper)XMLHttpRequest.create();
        request.setOnreadystatechange(new EventHandlerWrapper() {
            @Override
            public void handleEvent(EventWrapper evt) {
                if(request.getReadyState() == XMLHttpRequestWrapper.DONE) {
                    boolean subtractQueue = true;
                    if(request.getStatus() != 200) {
                        listener.onFailure(url);
                    }
                    else {
                        if(showLog)
                            System.out.println("Script loaded: " + url);
                        NodeWrapper response = request.getResponse();
                        WindowWrapper currentWindow = jsHelper.getCurrentWindow();
                        DocumentWrapper document = currentWindow.getDocument();
                        HTMLElementWrapper scriptElement = document.createElement("script");
                        scriptElement.appendChild(document.createTextNode(response));
                        document.getBody().appendChild(scriptElement);
                        listener.onSuccess(url, request.getResponseText());
                    }
                    subtractQueue();
                }
            }
        });
        addQueue();
        setOnProgress(request, listener);
        request.open("GET", url, async);
        request.setRequestHeader("Content-Type", "text/plain; charset=utf-8");
        request.send();
    }

    public void loadAudio(boolean async, final String url, final AssetLoaderListener<Void> listener) {
        loadBinary(async, url, new AssetLoaderListener<Blob>() {
            @Override
            public void onProgress(double amount) {
                listener.onProgress(amount);
            }

            @Override
            public void onFailure(String url) {
                listener.onFailure(url);
            }

            @Override
            public boolean onSuccess(String url, Blob result) {
                return listener.onSuccess(url, null);
            }
        });
    }

    public void loadBinary(boolean async, final String url, final AssetLoaderListener<Blob> listener) {
        if(showLog)
            System.out.println("Loading asset : " + url);
        XMLHttpRequestWrapper request = (XMLHttpRequestWrapper)XMLHttpRequest.create();
        request.setOnreadystatechange(new EventHandlerWrapper() {
            @Override
            public void handleEvent(EventWrapper evt) {
                if(request.getReadyState() == XMLHttpRequestWrapper.DONE) {
                    if(request.getStatus() != 200) {
                        listener.onFailure(url);
                    }
                    else {
                        if(showLog)
                            System.out.println("Asset loaded: " + url);

                        ArrayBufferWrapper response = (ArrayBufferWrapper)request.getResponse();
                        Int8ArrayWrapper data = TypedArrays.getInstance().createInt8Array(response);
                        listener.onSuccess(url, new Blob(response, data));
                    }
                    subtractQueue();
                }
            }
        });

        addQueue();
        setOnProgress(request, listener);
        request.open("GET", url, async);
        if(async) {
            request.setResponseType("arraybuffer");
        }
        request.send();
    }

    public void loadImage(boolean async, final String url, final String mimeType,
                          final AssetLoaderListener<HTMLImageElementWrapper> listener) {
        loadImage(async, url, mimeType, null, listener);
    }

    public void loadImage(boolean async, final String url, final String mimeType, final String crossOrigin,
                          final AssetLoaderListener<HTMLImageElementWrapper> listener) {
        boolean isUseInlineBase64 = false;
        loadBinary(async, url, new AssetLoaderListener<Blob>() {
            @Override
            public void onProgress(double amount) {
                listener.onProgress(amount);
            }

            @Override
            public void onFailure(String url) {
                listener.onFailure(url);
            }

            @Override
            public boolean onSuccess(String url, Blob result) {
                DocumentWrapper document = (DocumentWrapper)Window.current().getDocument();
                final HTMLImageElementWrapper image = (HTMLImageElementWrapper)document.createElement("img");

                if(crossOrigin != null) {
                    image.setAttribute("crossOrigin", crossOrigin);
                }
                addQueue();
                hookImgListener(image, new EventListenerWrapper() {
                    @Override
                    public void handleEvent(EventWrapper evt) {
                        if(evt.getType().equals("error"))
                            listener.onFailure(url);
                        else
                            listener.onSuccess(url, image);
                        subtractQueue();
                    }
                });
                if(isUseInlineBase64) {
                    image.setSrc("data:" + mimeType + ";base64," + result.toBase64());
                }
                else {
                    image.setSrc(url);
                }
                return false;
            }
        });
    }

    static void hookImgListener(HTMLImageElementWrapper img, final EventListenerWrapper listener) {
        img.addEventListener("load", listener, false);
        img.addEventListener("error", listener, false);
    }

    private void setOnProgress(XMLHttpRequestWrapper req, final AssetLoaderListener<?> listener) {
        req.setOnprogress(new EventHandlerWrapper() {
            @Override
            public void handleEvent(EventWrapper evt) {
                ProgressEventWrapper progressEvent = (ProgressEventWrapper)evt;
                listener.onProgress(progressEvent.getLoaded());
            }
        });
    }
}
