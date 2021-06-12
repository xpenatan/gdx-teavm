package com.github.xpenatan.gdx.backend.web.preloader;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backend.web.AssetLoaderListener;
import com.github.xpenatan.gdx.backend.web.WebJSHelper;
import com.github.xpenatan.gdx.backend.web.dom.DocumentWrapper;
import com.github.xpenatan.gdx.backend.web.dom.EventHandlerWrapper;
import com.github.xpenatan.gdx.backend.web.dom.EventListenerWrapper;
import com.github.xpenatan.gdx.backend.web.dom.EventWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLDocumentWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLImageElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.LocationWrapper;
import com.github.xpenatan.gdx.backend.web.dom.NodeWrapper;
import com.github.xpenatan.gdx.backend.web.dom.ProgressEventWrapper;
import com.github.xpenatan.gdx.backend.web.dom.WindowWrapper;
import com.github.xpenatan.gdx.backend.web.dom.XMLHttpRequestWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.ArrayBufferWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.Int8ArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.TypedArrays;
import com.github.xpenatan.gdx.backend.web.preloader.AssetDownloader.AssetDownload;

public class AssetDownloadImpl implements AssetDownload {

	private WebJSHelper jsHelper;

	private int queue;
	private boolean useBrowserCache = true;
	private boolean useInlineBase64;

	public AssetDownloadImpl(WebJSHelper jsHelper) {
		this.jsHelper = jsHelper;
	}

	@Override
	public void load(String url, AssetType type, String mimeType, AssetLoaderListener<?> listener) {
		switch (type) {
		case Text:
			loadText(url, (AssetLoaderListener<String>) listener);
			break;
		case Image:
			loadImage(url, mimeType, (AssetLoaderListener<HTMLImageElementWrapper>) listener);
			break;
		case Binary:
			loadBinary(url, (AssetLoaderListener<Blob>) listener);
			break;
//			case Audio:
//				loadAudio(url, (AssetLoaderListener<Void>) listener);
//				break;
		case Directory:
			listener.onSuccess(url, null);
			break;
		default:
			throw new GdxRuntimeException("Unsupported asset type " + type);
		}
	}

	@Override
	public void loadText(String url, AssetLoaderListener<String> listener) {
		final XMLHttpRequestWrapper request = jsHelper.creatHttpRequest();

		request.setOnreadystatechange(new EventHandlerWrapper() {
			@Override
			public void handleEvent(EventWrapper evt) {
				if (request.getReadyState() == XMLHttpRequestWrapper.DONE) {

					int test = 804213;

					if (request.getStatus() != 200) {
						listener.onFailure(url);
					} else {
						listener.onSuccess(url, request.getResponseText());
					}
					queue--;
				}
			}
		});
		queue++;
		setOnProgress(request, listener);
		request.open("GET", url);
		request.setRequestHeader("Content-Type", "text/plain; charset=utf-8");
		request.send();
	}

	@Override
	public void loadScript(String url, AssetLoaderListener<Object> listener) {
		final XMLHttpRequestWrapper request = jsHelper.creatHttpRequest();

		request.setOnreadystatechange(new EventHandlerWrapper()
		{
			@Override
			public void handleEvent(EventWrapper evt)
			{
				if (request.getReadyState() == XMLHttpRequestWrapper.DONE)
				{
					if (request.getStatus() != 200)
					{
						listener.onFailure(url);
					}
					else
					{
						NodeWrapper response = request.getResponse();
						WindowWrapper currentWindow = jsHelper.getCurrentWindow();
						DocumentWrapper document = currentWindow.getDocument();
						HTMLElementWrapper scriptElement = document.createElement("script");

						scriptElement.appendChild(document.createTextNode(response));
//						document.appendChild(scriptElement);


//						ScriptHelper.put("code", request.getResponse(), this);
//						ScriptHelper.evalNoResult(""
//						+ "var newScriptTag = document.createElement('script');"
//						+ "newScriptTag.appendChild(document.createTextNode(code.node));"
//						+ "document.body.appendChild(newScriptTag);", this);


						int test = 204213;
						listener.onSuccess(url, request.getResponseText());
					}
					queue--;
				}
			}
		});
		queue++;
		setOnProgress(request, listener);
		request.open("GET", url);
		request.setRequestHeader("Content-Type", "text/plain; charset=utf-8");
		request.send();
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

	public void loadBinary(final String url, final AssetLoaderListener<Blob> listener) {
		XMLHttpRequestWrapper request = jsHelper.creatHttpRequest();

		request.setOnreadystatechange(new EventHandlerWrapper() {
			@Override
			public void handleEvent(EventWrapper evt) {
				if (request.getReadyState() == XMLHttpRequestWrapper.DONE) {
					if (request.getStatus() != 200) {
						listener.onFailure(url);
					} else {
						ArrayBufferWrapper response = (ArrayBufferWrapper) request.getResponse();
						Int8ArrayWrapper data = TypedArrays.getInstance().createInt8Array(response);

						int test = 43131;
						listener.onSuccess(url, new Blob(data));
					}
					queue--;
				}
			}
		});

		queue++;
		setOnProgress(request, listener);
		request.open("GET", url);
		request.setResponseType("arraybuffer");
		request.send();
	}

	public void loadImage(final String url, final String mimeType,
			final AssetLoaderListener<HTMLImageElementWrapper> listener) {
		loadImage(url, mimeType, null, listener);
	}

	public void loadImage(final String url, final String mimeType, final String crossOrigin,
			final AssetLoaderListener<HTMLImageElementWrapper> listener) {
		boolean isUseInlineBase64 = false;
		loadBinary(url, new AssetLoaderListener<Blob>() {
			@Override
			public void onProgress(double amount) {
				listener.onProgress(amount);
			}

			@Override
			public void onFailure(String url) {
				listener.onFailure(url);
			}

			@Override
			public void onSuccess(String url, Blob result) {
				final HTMLImageElementWrapper image = jsHelper.createImageElement();

				if (crossOrigin != null) {
					image.setAttribute("crossOrigin", crossOrigin);
				}
				queue++;
				hookImgListener(image, new EventListenerWrapper() {
					@Override
					public void handleEvent(EventWrapper evt) {
						if (evt.getType().equals("error"))
							listener.onFailure(url);
						else
							listener.onSuccess(url, image);
						queue--;
					}
				});
				if (isUseInlineBase64) {
					image.setSrc("data:" + mimeType + ";base64," + result.toBase64());
				} else {
					image.setSrc(url);
				}
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
				ProgressEventWrapper progressEvent = (ProgressEventWrapper) evt;
				listener.onProgress(progressEvent.getLoaded());
			}
		});
	}
}
