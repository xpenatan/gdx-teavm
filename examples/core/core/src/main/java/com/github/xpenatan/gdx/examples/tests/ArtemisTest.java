package com.github.xpenatan.gdx.examples.tests;

import com.artemis.World;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ArtemisTest extends ApplicationAdapter {

    private AssetManager mAssetManager;
    private World mEngine;

    @Override
    public void create() {
        mAssetManager = new AssetManager();
        mAssetManager.load("skin/skin.json", Skin.class);
        mAssetManager.finishLoading();
    }
}