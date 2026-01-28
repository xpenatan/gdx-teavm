package com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.utils.ScreenUtils;

public class EmptyApplicationTest implements ApplicationListener {

    FPSLogger fpsLogger;

    @Override
    public void create() {
        System.out.println("CREATE");
        fpsLogger = new FPSLogger();
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("RESIZE: " + width + ", " + height);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 1, 0, 1.0f, true);
        fpsLogger.log();
    }

    @Override
    public void pause() {
        System.out.println("PAUSE");
    }

    @Override
    public void resume() {
        System.out.println("RESUME");
    }

    @Override
    public void dispose() {
        System.out.println("DISPOSE");
    }
}