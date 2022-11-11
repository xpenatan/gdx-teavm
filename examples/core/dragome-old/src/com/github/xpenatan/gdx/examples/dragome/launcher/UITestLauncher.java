package com.github.xpenatan.examples.dragome.launcher;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.tests.html.examples.UITest;
import com.dragome.web.annotations.PageAlias;
import com.github.xpenatan.gdx.backends.web.AssetLoaderListener;
import com.github.xpenatan.gdx.backends.web.preloader.AssetType;
import com.github.xpenatan.gdx.backends.dragome.DragomeApplication;
import com.github.xpenatan.gdx.backends.dragome.DragomeApplicationConfiguration;
import com.github.xpenatan.gdx.backends.dragome.DragomeWindow;

@PageAlias(alias = "UITest")
public class UITestLauncher extends DragomeApplication {
    @Override
    public ApplicationListener createApplicationListener() {
        getPreloader().loadAsset("data/uiskin.atlas", AssetType.Text, null, new AssetLoaderListener<>());
        getPreloader().loadAsset("data/uiskin.atlas", AssetType.Text, null, new AssetLoaderListener<>());
        getPreloader().loadAsset("data/uiskin.json", AssetType.Text, null, new AssetLoaderListener<>());
        getPreloader().loadAsset("data/uiskin.png", AssetType.Image, null, new AssetLoaderListener<>());
        getPreloader().loadAsset("data/default.fnt", AssetType.Text, null, new AssetLoaderListener<>());
        getPreloader().loadAsset("data/default.png", AssetType.Image, null, new AssetLoaderListener<>());
        getPreloader().loadAsset("data/badlogicsmall.jpg", AssetType.Image, null, new AssetLoaderListener<>());
        getPreloader().loadAsset("data/badlogic.jpg", AssetType.Image, null, new AssetLoaderListener<>());
        getPreloader().loadAsset("data/jsonTest.json", AssetType.Text, null, new AssetLoaderListener<>());
        return new UITest();
    }

    @Override
    public DragomeApplicationConfiguration getConfig() {
        return null;
    }

    @Override
    protected void onResize() {
        int clientWidth = DragomeWindow.getInnerWidth();
        int clientHeight = DragomeWindow.getInnerHeight();
        getCanvas().setWidth(clientWidth);
        getCanvas().setHeight(clientHeight);
        getCanvas().setCoordinateSpaceWidth(clientWidth);
        getCanvas().setCoordinateSpaceHeight(clientHeight);
        super.onResize();
    }
}
