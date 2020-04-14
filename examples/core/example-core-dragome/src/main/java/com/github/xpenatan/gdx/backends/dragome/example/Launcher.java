package com.github.xpenatan.gdx.backends.dragome.example;

import com.badlogic.gdx.tests.dragome.examples.GearsDemo;
import com.github.xpenatan.gdx.backend.web.WebApplication;
import com.github.xpenatan.gdx.backend.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backends.dragome.DragomeApplicationConfiguration;

public class Launcher
{
	public static void main(String[] args) {
		WebApplicationConfiguration config = new DragomeApplicationConfiguration("canvas");
		GearsDemo gearsDemo = new GearsDemo();
		new WebApplication(gearsDemo, config);
	}
}
