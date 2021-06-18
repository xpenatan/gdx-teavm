package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.ApplicationListener;
import com.github.xpenatan.gdx.backend.web.WebApplication;
import com.github.xpenatan.gdx.backend.web.WebApplicationConfiguration;

public class TeaLauncher {

	public static void main(String[] args) {
		WebApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
		Object appListener = getApplicationListener();
		if(appListener != null) {
			ApplicationListener listener = (ApplicationListener) appListener;
			new WebApplication(listener, config);
		}
	}

	private static native Object getApplicationListener();
}
