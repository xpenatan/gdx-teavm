package com.github.xpenatan.gdx.backends.teavm.example;

import com.badlogic.gdx.tests.dragome.examples.GearsDemo;
import com.badlogic.gdx.tests.dragome.examples.SoundTest;
import com.badlogic.gdx.tests.dragome.examples.UITest;
import com.github.xpenatan.gdx.backend.web.WebApplication;
import com.github.xpenatan.gdx.backend.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;

public class Launcher {

	public static void main(String[] args) {
		WebApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
		GearsDemo gearsDemo = new GearsDemo();
//		new WebApplication(gearsDemo, config);
		new WebApplication(new UITest(), config);
	}
}
