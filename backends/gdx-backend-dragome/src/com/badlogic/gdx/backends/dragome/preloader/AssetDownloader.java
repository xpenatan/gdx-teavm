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

package com.badlogic.gdx.backends.dragome.preloader;

import org.w3c.dom.EventHandler;
import org.w3c.dom.XMLHttpRequest;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.ProgressEvent;
import org.w3c.dom.html.HTMLImageElement;
import org.w3c.dom.typedarray.ArrayBuffer;
import org.w3c.dom.typedarray.Int8Array;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.w3c.HTMLImageElementExtension;
import com.dragome.web.html.dom.w3c.TypedArraysFactory;

/** Adapted from gwt backend
 * @author xpenatan */
public class AssetDownloader
{
	static int queue;
	static boolean useBrowserCache = true;
	static boolean useInlineBase64;
	
	
	private AssetDownloader(){}

	public static void setUseBrowserCache(boolean useBrowserCache)
	{
		AssetDownloader.useBrowserCache= useBrowserCache;
	}

	public static boolean isUseBrowserCache()
	{
		return AssetDownloader.useBrowserCache;
	}

	public static void setUseInlineBase64(boolean useInlineBase64)
	{
		AssetDownloader.useInlineBase64= useInlineBase64;
	}

	public static boolean isUseInlineBase64()
	{
		return AssetDownloader.useInlineBase64;
	}

	public static class AssetLoaderListener<T>
	{
		@MethodAlias(local_alias= "onProgress")
		public void onProgress(double amount){}

		public void onFailure(){}

		public void onSuccess(T result){}
	}

	public static void load(String url, AssetType type, String mimeType, AssetLoaderListener<?> listener)
	{
		switch (type)
		{
			case Text:
				loadText(url, (AssetLoaderListener<String>) listener);
				break;
			case Image:
				loadImage(url, mimeType, (AssetLoaderListener<HTMLImageElement>) listener);
				break;
			case Binary:
				loadBinary(url, (AssetLoaderListener<Blob>) listener);
				break;
			case Audio:
				loadAudio(url, (AssetLoaderListener<Void>) listener);
				break;
			case Directory:
				listener.onSuccess(null);
				break;
			default:
				throw new GdxRuntimeException("Unsupported asset type " + type);
		}
	}

	public static void loadText(String url, final AssetLoaderListener<String> listener)
	{
		final XMLHttpRequest request= ScriptHelper.evalCasting("new XMLHttpRequest()", XMLHttpRequest.class, null);

		request.setOnreadystatechange(new EventHandler()
		{
			@Override
			public void handleEvent(Event evt)
			{
				if (request.getReadyState() == XMLHttpRequest.DONE)
				{
					if (request.getStatus() != 200)
					{
						listener.onFailure();
					}
					else
					{
						listener.onSuccess(request.getResponseText());
					}
					queue--;
				}
			}
		});
		queue++;
		ScriptHelper.put("request", request, null);
		setOnProgress(request, listener);
		request.open("GET", url);
		request.setRequestHeader("Content-Type", "text/plain; charset=utf-8");
		request.send();
	}
	
	public static void loadScript(String url, final AssetLoaderListener<Object> listener)
	{
		final XMLHttpRequest request= ScriptHelper.evalCasting("new XMLHttpRequest()", XMLHttpRequest.class, null);
		
		request.setOnreadystatechange(new EventHandler()
		{
			@Override
			public void handleEvent(Event evt)
			{
				if (request.getReadyState() == XMLHttpRequest.DONE)
				{
					if (request.getStatus() != 200)
					{
						listener.onFailure();
					}
					else
					{
						ScriptHelper.put("code", request.getResponse(), this);
						ScriptHelper.evalNoResult(""
						+ "var newScriptTag = document.createElement('script');"
						+ "newScriptTag.appendChild(document.createTextNode(code.node));"
						+ "document.body.appendChild(newScriptTag);", this);
						listener.onSuccess(request.getResponseText());
					}
					queue--;
				}
			}
		});
		queue++;
		ScriptHelper.put("request", request, null);
		setOnProgress(request, listener);
		request.open("GET", url);
		request.setRequestHeader("Content-Type", "text/plain; charset=utf-8");
		request.send();
	}

	public static int getQueue()
	{
		return queue;
	}
	public static ArrayBuffer getResponseArrayBuffer(XMLHttpRequest anInstance)
	{
		ScriptHelper.put("instance", anInstance, null);
		Object instance= ScriptHelper.eval("instance.node.response", null);
		return JsCast.castTo(instance, ArrayBuffer.class);
	}

	public static void loadBinary(final String url, final AssetLoaderListener<Blob> listener)
	{
		final XMLHttpRequest request= ScriptHelper.evalCasting("new XMLHttpRequest()", XMLHttpRequest.class, null);
		request.setOnreadystatechange(new EventHandler()
		{
			@Override
			public void handleEvent(Event evt)
			{
				if (request.getReadyState() == XMLHttpRequest.DONE)
				{
					if (request.getStatus() != 200)
					{
						listener.onFailure();
					}
					else
					{
						ArrayBuffer responseArrayBuffer= getResponseArrayBuffer(request);
						Int8Array data= TypedArraysFactory.createInstanceOf(Int8Array.class, responseArrayBuffer);
						listener.onSuccess(new Blob(data));
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

	public static void loadAudio(String url, final AssetLoaderListener<Void> listener)
	{
		if (useBrowserCache)
		{
			loadBinary(url, new AssetLoaderListener<Blob>()
			{
				@Override
				public void onProgress(double amount)
				{
					listener.onProgress(amount);
				}

				@Override
				public void onFailure()
				{
					listener.onFailure();
				}

				@Override
				public void onSuccess(Blob result)
				{
					listener.onSuccess(null);
				}

			});
		}
		else
		{
			listener.onSuccess(null);
		}
	}

	public static void loadImage(final String url, final String mimeType, final AssetLoaderListener<HTMLImageElement> listener)
	{
		loadImage(url, mimeType, null, listener);
	}

	public static void loadImage(final String url, final String mimeType, final String crossOrigin, final AssetLoaderListener<HTMLImageElement> listener)
	{
		if (useBrowserCache || useInlineBase64)
		{
			loadBinary(url, new AssetLoaderListener<Blob>()
			{
				@Override
				public void onProgress(double amount)
				{
					listener.onProgress(amount);
				}

				@Override
				public void onFailure()
				{
					listener.onFailure();
				}

				@Override
				public void onSuccess(Blob result)
				{
					final HTMLImageElementExtension image= createImage();

					if (crossOrigin != null)
					{
						image.setAttribute("crossOrigin", crossOrigin);
					}
					queue++;
					hookImgListener(image, new ImgEventListener()
					{
						@Override
						public void onEvent(Event event)
						{
							if (event.getType().equals("error"))
								listener.onFailure();
							else
								listener.onSuccess(image);
							queue--;
						}
					});
					if (isUseInlineBase64())
					{
						image.setSrc("data:" + mimeType + ";base64," + result.toBase64());
					}
					else
					{
						image.setSrc(url);
					}
				}

			});
		}
		else
		{
			final HTMLImageElementExtension image= createImage();
			if (crossOrigin != null)
			{
				image.setAttribute("crossOrigin", crossOrigin);
			}
			queue++;
			hookImgListener(image, new ImgEventListener()
			{
				@Override
				public void onEvent(Event event)
				{
					if (event.getType().equals("error"))
						listener.onFailure();
					else
						listener.onSuccess(image);
					queue--;
				}
			});
			image.setSrc(url);
		}
	}

	private static interface ImgEventListener
	{
		@MethodAlias(local_alias= "onEvent")
		public void onEvent(Event event);
	}

	static void hookImgListener(HTMLImageElementExtension img, final ImgEventListener h)
	{
		EventTarget eventTarget= JsCast.castTo(img, EventTarget.class);
		EventListener listener= new EventListener()
		{
			public void handleEvent(Event evt)
			{
				h.onEvent(evt);
			}
		};
		eventTarget.addEventListener("load", listener, false);
		eventTarget.addEventListener("error", listener, false);
	}

	static HTMLImageElementExtension createImage()
	{
		Object instance= ScriptHelper.eval("new Image()", null);
		HTMLImageElementExtension img= JsCast.castTo(instance, HTMLImageElementExtension.class);
		return img;
	}

	private static void setOnProgress(XMLHttpRequest req, final AssetLoaderListener<?> listener)
	{
		req.setOnprogress(new EventHandler()
		{
			public void handleEvent(Event evt)
			{
				ProgressEvent progressEvent= JsCast.castTo(evt, ProgressEvent.class);
				listener.onProgress(progressEvent.getLoaded());
			}
		});
	}

	public static String getHostPageBaseURL()
	{
		String code= "var s = location.href; " + "var i = s.indexOf('#');" + "if (i != -1)" + "   s = s.substring(0, i);" + "i = s.indexOf('?');" + "if (i != -1)" + "  s = s.substring(0, i);" + "i = s.lastIndexOf('/');" + "if (i != -1)" + "  s = s.substring(0, i);" + "s.length > 0 ? s + '/' : '';";
		String base= (String) ScriptHelper.eval(code, null);
		return base;
	}
}
