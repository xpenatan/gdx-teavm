package com.github.xpenatan.gdx.examples.dragome;

import com.github.xpenatan.gdx.backends.web.WebApplication;
import com.github.xpenatan.gdx.backends.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backends.dragome.DragomeApplicationConfiguration;
import com.github.xpenatan.gdx.examples.tests.UITest;

public class Launcher {
    public static void main(String[] args) {
        WebApplicationConfiguration config = new DragomeApplicationConfiguration("canvas");
//		new WebApplication(new GearsDemo(), config);
        new WebApplication(new UITest(), config);
//		new WebApplication(new AnimationTest(), config);
    }
}
