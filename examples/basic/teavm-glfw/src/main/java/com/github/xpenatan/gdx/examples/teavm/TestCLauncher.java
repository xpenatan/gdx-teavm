package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.example.basic.tests.HelloTriangle;
import com.github.xpenatan.gdx.teavm.backend.teavm.glfw.GLFWApplication;
import com.github.xpenatan.gdx.example.basic.tests.GearsDemo;

public class TestCLauncher {

    public static void main(String[] args) {
        System.setProperty("os.name", "Windows"); // TODO figure out how to obtain OS name
//        new GLFWApplication(new TestCApplication());
//        new GLFWApplication(new GearsDemo());
        new GLFWApplication(new HelloTriangle());
    }

}