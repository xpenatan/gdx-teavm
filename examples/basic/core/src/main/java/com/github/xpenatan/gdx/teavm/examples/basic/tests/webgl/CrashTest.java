package com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.github.quillraven.fleks.World;
import java.lang.reflect.Field;

public class CrashTest extends ApplicationAdapter {
    @Override
    public void create() {
        Field[] declaredFields = FreeTypeFontGenerator.FreeTypeFontParameter.class.getDeclaredFields();
        System.out.println("size: " + declaredFields.length);
        var manager = new AssetManager();
        manager.update();
    }

    void plusAssign(World world) {
    }
}