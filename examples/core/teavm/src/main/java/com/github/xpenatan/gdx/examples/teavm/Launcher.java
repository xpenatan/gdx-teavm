package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backend.web.WebApplication;
import com.github.xpenatan.gdx.backend.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.examples.tests.UITest;


public class Launcher {

	public static void main(String[] args) {
		WebApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
//		new WebApplication(new GearsDemo(), config);
		new WebApplication(new UITest(), config);
//		new WebApplication(new AnimationTest(), config);
//		new WebApplication(new SoundTest(), config);
	}
}
