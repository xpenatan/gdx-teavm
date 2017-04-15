package com.badlogic.gdx.tests.dragome.launcher;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.dragome.DragomeApplication;
import com.badlogic.gdx.backends.dragome.DragomeApplicationConfiguration;
import com.badlogic.gdx.backends.dragome.DragomeWindow;
import com.badlogic.gdx.backends.dragome.preloader.AssetDownloader;
import com.badlogic.gdx.backends.dragome.preloader.AssetDownloader.AssetLoaderListener;
import com.badlogic.gdx.backends.dragome.preloader.AssetType;
import com.badlogic.gdx.tests.dragome.examples.FreeTypePackTest;
import com.dragome.web.annotations.PageAlias;

@PageAlias(alias= "FreeType")
public class FreeTypeLauncher extends DragomeApplication
{
	@Override
	public ApplicationListener createApplicationListener()
	{
		AssetLoaderListener<Object> listener = new AssetLoaderListener<Object>();
		AssetDownloader.loadScript("freetype.js", listener);
		getPreloader().loadAsset("data/arial.ttf", AssetType.Binary, null, new AssetLoaderListener<Object>());
		getPreloader().loadAsset("data/arial-italic.ttf", AssetType.Binary, null, new AssetLoaderListener<Object>());
		return new FreeTypePackTest();
	}

	@Override
	public DragomeApplicationConfiguration getConfig()
	{
		return null;
	}

	@Override
	protected void onResize()
	{
		int clientWidth= DragomeWindow.getInnerWidth();
		int clientHeight= DragomeWindow.getInnerHeight();
		getCanvas().setWidth(clientWidth);
		getCanvas().setHeight(clientHeight);
		getCanvas().setCoordinateSpaceWidth(clientWidth);
		getCanvas().setCoordinateSpaceHeight(clientHeight);
		super.onResize();
	}
}