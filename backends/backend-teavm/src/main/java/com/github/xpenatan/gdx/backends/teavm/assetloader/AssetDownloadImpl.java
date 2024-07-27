package com.github.xpenatan.gdx.backends.teavm.assetloader;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetDownloader.AssetDownload;
import com.github.xpenatan.gdx.backends.teavm.dom.LocationWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.impl.TeaWindow;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLScriptElement;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Int8Array;

public class AssetDownloadImpl implements AssetDownload {

    private static final int MAX_DOWNLOAD_ATTEMPT = 3;

    private int queue;
    private final boolean showLogs;
    private boolean showDownloadProgress;

    public AssetDownloadImpl(boolean showDownloadLogs) {
        showLogs = showDownloadLogs;
    }

    @Override
    public String getHostPageBaseURL() {
        TeaWindow currentWindow = TeaWindow.get();
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
    public void load(boolean async, String url, AssetType type, AssetLoaderListener<Blob> listener) {
        AssetLoaderListener<Blob> internalListener = new AssetLoaderListener<>() {
            @Override
            public void onSuccess(String url, Blob result) {
                if(showLogs) {
                    System.out.println("Asset download success: " + url);
                }
                listener.onSuccess(url, result);
            }

            @Override
            public void onFailure(String url) {
                if(showLogs) {
                    System.err.println("Asset download failed: " + url);
                }
            }

            @Override
            public void onProgress(int total, int loaded) {
                if(listener != null) {
                    listener.onProgress(total, loaded);
                }
            }
        };

        if(showLogs) {
            System.out.println("Loading asset: " + url);
        }

        switch(type) {
            case Binary:
                loadBinary(async, url, internalListener, 0);
                break;
            case Directory:
                internalListener.onSuccess(url, null);
                break;
            default:
                throw new GdxRuntimeException("Unsupported asset type " + type);
        }
    }

    @Override
    public void loadScript(boolean async, String url, AssetLoaderListener<String> listener) {
        if(showLogs) {
            System.out.println("Loading script: " + url);
        }

        loadBinary(async, url, new AssetLoaderListener<>() {
            @Override
            public void onSuccess(String url, Blob result) {
                Int8ArrayWrapper data = (Int8ArrayWrapper)result.getData();
                byte[] byteArray = TypedArrays.toByteArray(data);
                String script = new String(byteArray);
                Window current = Window.current();
                HTMLDocument document = current.getDocument();
                HTMLScriptElement scriptElement = (HTMLScriptElement)document.createElement("script");
                scriptElement.setText(script);
                document.getBody().appendChild(scriptElement);

                if(showLogs) {
                    System.out.println("Script download success: " + url);
                }
                if(listener != null) {
                    listener.onSuccess(url, script);
                }
            }

            @Override
            public void onFailure(String url) {
                if(showLogs) {
                    System.err.println("Script download failed: " + url);
                }
                if(listener != null) {
                    listener.onFailure(url);
                }
            }

            @Override
            public void onProgress(int total, int loaded) {
                if(showDownloadProgress) {
                    System.out.println("Total: " + total + " loaded: " + loaded + " URL: " + url);
                }
                if(listener != null) {
                    listener.onProgress(total, loaded);
                }
            }
        }, 0);
    }

    private void loadBinary(boolean async, final String url, final AssetLoaderListener<Blob> listener, int count) {
        if(count == MAX_DOWNLOAD_ATTEMPT) {
            if(listener != null) {
                listener.onFailure(url);
            }
            return;
        }

        // don't load on main thread
        addQueue();
        if(async) {
            new Thread() {
                public void run() {
                    loadBinaryInternally(true, url, listener, count);
                }
            }.start();
        }
        else {
            loadBinaryInternally(false, url, listener, count);
        }
    }

    private void loadBinaryInternally(boolean async, final String url, final AssetLoaderListener<Blob> listener, int count) {
        XMLHttpRequest request = new XMLHttpRequest();
        request.setOnReadyStateChange(evt -> {
            if(request.getReadyState() == XMLHttpRequest.DONE) {
                int status = request.getStatus();
                if(status == 0) {
                    if(listener != null) {
                        listener.onFailure(url);
                    }
                }
                else if(status != 200) {
                    if ((status != 404) && (status != 403)) {
                        // re-try: e.g. failure due to ERR_HTTP2_SERVER_REFUSED_STREAM (too many requests)
                        try {
                            Thread.sleep(100);
                        }
                        catch (Throwable e) {
                            // ignored
                        }
                        int newCount = count + 1;
                        loadBinary(async, url, listener, newCount);
                    }
                    else {
                        if(listener != null) {
                            listener.onFailure(url);
                        }
                    }
                }
                else {
                    JSObject jsResponse = request.getResponse();

                    Int8Array data = null;
                    ArrayBuffer arrayBuffer = null;
                    if(isString(jsResponse)) {
                        // sync downloading is always string
                        String responseStr = toString(jsResponse);
                        Int8ArrayWrapper typedArray = TypedArrays.getTypedArray(responseStr.getBytes());
                        data = (Int8Array)typedArray;
                        arrayBuffer = data.getBuffer();
                    }
                    else {
                        ArrayBufferWrapper response = (ArrayBufferWrapper)jsResponse;
                        data = (Int8Array)TypedArrays.createInt8Array(response);
                        arrayBuffer = (ArrayBuffer)response;
                    }

                    if(listener != null) {
                        listener.onSuccess(url, new Blob(arrayBuffer, data));
                    }
                }
                subtractQueue();
            }
        });

        setOnProgress(request, url, listener);
        request.open("GET", url, async);
        if(async) {
            request.setResponseType("arraybuffer");
        }
        request.send();
    }

    private void setOnProgress(XMLHttpRequest req, String url, final AssetLoaderListener<?> listener) {
        req.onProgress(evt -> {
            int loaded = evt.getLoaded();
            int total = evt.getTotal();
            double percent = (double)loaded / total;
            if(listener != null) {
                listener.onProgress(total, loaded);
            }
        });
    }

    @JSBody(params = "jsObject", script = "return typeof jsObject == 'string';")
    private static native boolean isString(JSObject jsObject);

    @JSBody(params = "jsObject", script = "return jsObject;")
    private static native String toString(JSObject jsObject);
}