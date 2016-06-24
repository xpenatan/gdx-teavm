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

package com.badlogic.gdx.backends.dragome;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.dragome.preloader.Preloader;
import com.badlogic.gdx.backends.dragome.utils.Storage;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** @author xpenatan */
public class DragomeFiles implements Files {

	public static final Storage LocalStorage = Storage.getLocalStorageIfSupported();

	final Preloader preloader;

	public DragomeFiles (Preloader preloader) {
		this.preloader = preloader;
	}

	@Override
	public FileHandle getFileHandle (String path, FileType type) {
		if (type != FileType.Internal) throw new GdxRuntimeException("FileType '" + type + "' not supported in Dragome backend");
		return new DragomeFileHandle(preloader, path, type);
	}

	@Override
	public FileHandle classpath (String path) {
		return new DragomeFileHandle(preloader, path, FileType.Classpath);
	}

	@Override
	public FileHandle internal (String path) {
		return new DragomeFileHandle(preloader, path, FileType.Internal);
	}

	@Override
	public FileHandle external (String path) {
		throw new GdxRuntimeException("Not supported in Dragome backend");
	}

	@Override
	public FileHandle absolute (String path) {
		throw new GdxRuntimeException("Not supported in Dragome backend");
	}

	@Override
	public FileHandle local (String path) {
		throw new GdxRuntimeException("Not supported in Dragome backend");
	}

	@Override
	public String getExternalStoragePath () {
		return null;
	}

	@Override
	public boolean isExternalStorageAvailable () {
		return false;
	}

	@Override
	public String getLocalStoragePath () {
		return null;
	}

	@Override
	public boolean isLocalStorageAvailable () {
		return false;
	}
}
