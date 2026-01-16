package com.github.xpenatan.gdx.teavm.examples.freetype.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.github.xpenatan.gdx.teavm.examples.freetype.FreetypeDemo;

public class Main {

    public static void main(String[] args) {
        new LwjglApplication(new FreetypeDemo());
    }
}
