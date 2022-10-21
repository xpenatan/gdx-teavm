package com.github.xpenatan.gdx.backends.web;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.web.preloader.Preloader;

/**
 * @author xpenatan
 */
public class WebFiles implements Files {

	final Preloader preloader;

	public WebFiles (Preloader preloader) {
		this.preloader = preloader;
	}

	@Override
	public FileHandle getFileHandle (String path, FileType type) {
		if (type != FileType.Internal) throw new GdxRuntimeException("FileType '" + type + "' not supported in Web backend");
		return new WebFileHandle(preloader, path, type);
	}

	@Override
	public FileHandle classpath (String path) {
		return new WebFileHandle(preloader, path, FileType.Classpath);
	}

	@Override
	public FileHandle internal (String path) {
		return new WebFileHandle(preloader, path, FileType.Internal);
	}

	@Override
	public FileHandle external (String path) {
		throw new GdxRuntimeException("Not supported in Web backend");
	}

	@Override
	public FileHandle absolute (String path) {
		throw new GdxRuntimeException("Not supported in Web backend");
	}

	@Override
	public FileHandle local (String path) {
		throw new GdxRuntimeException("Not supported in Web backend");
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
