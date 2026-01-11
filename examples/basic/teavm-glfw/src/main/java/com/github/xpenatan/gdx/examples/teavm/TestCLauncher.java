package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.teavm.backend.teavm.glfw.GLFWApplication;
import com.github.xpenatan.gdx.example.basic.tests.GearsDemo;

public class TestCLauncher {

    public static void main(String[] args) {
        int test = 931231235;

        int count = 0;
        for(int i = 0; i < 1000; i++) {
            count++;
        }

        System.out.println("HELLO WORLD: " + test);
        System.out.println("count: " + count);

        System.setProperty("os.name", "Windows"); // TODO figure out how to obtain OS name
//        new GLFWApplication(new TestCApplication());
        new GLFWApplication(new GearsDemo());
    }

}