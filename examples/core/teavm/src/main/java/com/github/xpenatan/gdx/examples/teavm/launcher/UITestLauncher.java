package com.github.xpenatan.gdx.examples.teavm.launcher;

import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.examples.tests.UITest;

public class UITestLauncher {

    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        new TeaApplication(new UITest(), config);
    }
}
