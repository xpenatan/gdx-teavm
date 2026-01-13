package com.github.xpenatan.gdx.teavm.example.basic.teavm;

import com.github.xpenatan.gdx.example.basic.tests.Basic3DTest;
import com.github.xpenatan.gdx.teavm.backend.teavm.glfw.GLFWApplication;

public class TestCLauncher {

    public static void main(String[] args) {
        System.setProperty("os.name", "Windows"); // TODO figure out how to obtain OS name
//        new GLFWApplication(new TestCApplication());
//        new GLFWApplication(new GearsDemo());
//        new GLFWApplication(new ReadPixelsTest());
        new GLFWApplication(new Basic3DTest());
//        new GLFWApplication(new HelloTriangle());
    }

}