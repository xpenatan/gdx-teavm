package com.github.xpenatan.gdx.examples.teavm;

import com.badlogic.gdx.ApplicationListener;
import com.github.xpenatan.gdx.backends.teavm.glfw.TeaGLFWApplication;

public class TestCLauncher implements ApplicationListener {

    public static void main(String[] args) {

        int test = 931231235;

        int count = 0;
        for(int i = 0; i < 1000; i++) {
            count++;
        }


        System.out.println("HELLO WORLD: " + test);
        System.out.println("count: " + count);

        new TeaGLFWApplication(new TestCLauncher());
    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}