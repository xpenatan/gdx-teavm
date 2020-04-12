package com.github.xpenatan.gdx.backends.teavm.example;

import com.badlogic.gdx.tests.dragome.examples.GearsDemo;
import com.github.xpenatan.gdx.backend.web.WebApplication;
import com.github.xpenatan.gdx.backend.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;

public class Launcher {

	public static void main(String[] args) {
		new Launcher().build();
	}

	public void build() {
		WebApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
		GearsDemo gearsDemo = new GearsDemo();
		new WebApplication(gearsDemo, config);
	}
}
