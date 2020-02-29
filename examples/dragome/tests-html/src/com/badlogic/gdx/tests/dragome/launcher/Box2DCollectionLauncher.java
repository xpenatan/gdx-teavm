package com.badlogic.gdx.tests.dragome.launcher;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.dragome.DragomeApplication;
import com.badlogic.gdx.backends.dragome.DragomeApplicationConfiguration;
import com.badlogic.gdx.backends.dragome.DragomeWindow;
import com.badlogic.gdx.backends.dragome.preloader.AssetDownloader;
import com.badlogic.gdx.backends.dragome.preloader.AssetDownloader.AssetLoaderListener;
import com.badlogic.gdx.backends.dragome.preloader.AssetType;
import com.badlogic.gdx.tests.dragome.examples.Box2DTestCollection;
import com.dragome.web.annotations.PageAlias;

@PageAlias(alias= "Box2DCollection")
public class Box2DCollectionLauncher extends DragomeApplication
{
	@Override
	public ApplicationListener createApplicationListener()
	{
		getPreloader().loadAsset("data/badlogic.jpg", AssetType.Image, null, new AssetLoaderListener<Object>());
		getPreloader().loadAsset("data/arial-15_00.png", AssetType.Image, null, new AssetLoaderListener<Object>());
		getPreloader().loadAsset("data/arial-15.fnt", AssetType.Text, null, new AssetLoaderListener<Object>());
		return new Box2DTestCollection();
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
