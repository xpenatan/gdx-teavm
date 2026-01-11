package com.github.xpenatan.gdx.teavm.example.freetype.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.github.xpenatan.gdx.teavm.example.freetype.FreetypeDemo;

public class Main {

    public static void main(String[] args) {
        new LwjglApplication(new FreetypeDemo());
    }
}
