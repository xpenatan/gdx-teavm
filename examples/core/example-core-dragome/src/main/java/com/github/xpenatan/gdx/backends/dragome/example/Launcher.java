package com.github.xpenatan.gdx.backends.dragome.example;

import com.badlogic.gdx.tests.dragome.examples.AnimationTest;
import com.badlogic.gdx.tests.dragome.examples.GearsDemo;
import com.badlogic.gdx.tests.dragome.examples.UITest;
import com.github.xpenatan.gdx.backend.web.WebApplication;
import com.github.xpenatan.gdx.backend.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backends.dragome.DragomeApplicationConfiguration;

public class Launcher
{
	public static void main(String[] args) {
		WebApplicationConfiguration config = new DragomeApplicationConfiguration("canvas");
//		new WebApplication(new GearsDemo(), config);
		new WebApplication(new UITest(), config);
//		new WebApplication(new AnimationTest(), config);
	}
}
