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

package com.github.xpenatan.gdx.backend.web.preloader;


import com.github.xpenatan.gdx.backend.web.AssetLoaderListener;
import com.github.xpenatan.gdx.backend.web.preloader.AssetType;

/** Adapted from gwt backend
 * @author xpenatan */
public class AssetDownloader
{
	private static AssetDownload instance;

	private AssetDownloader(){}

	public static AssetDownload getInstance() {
		return AssetDownloader.instance;
	}

	public static void setInstance(AssetDownload instance) {
		AssetDownloader.instance = instance;
	}


	static public interface AssetDownload {
		public void load(final String url, AssetType type, String mimeType, AssetLoaderListener<?> listener);
		public void loadText(final String url, final AssetLoaderListener<String> listener);
		public void loadScript(final String url, final AssetLoaderListener<Object> listener);
		public boolean isUseBrowserCache();
		public String getHostPageBaseURL();
		public int getQueue();
	}

}
