package com.github.xpenatan.examples.dragome.launcher;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.tests.html.examples.AnimationTest;
import com.dragome.web.annotations.PageAlias;
import com.github.xpenatan.gdx.backends.web.AssetLoaderListener;
import com.github.xpenatan.gdx.backends.web.preloader.AssetType;
import com.github.xpenatan.gdx.backends.dragome.DragomeApplication;
import com.github.xpenatan.gdx.backends.dragome.DragomeApplicationConfiguration;
import com.github.xpenatan.gdx.backends.dragome.DragomeWindow;

@PageAlias(alias= "AnimationTest")
public class AnimationLauncher extends DragomeApplication
{
	@Override
	public ApplicationListener createApplicationListener()
	{
		getPreloader().loadAsset("data/walkanim.png", AssetType.Image, null, new AssetLoaderListener<Object>());
		return new AnimationTest();
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
