package com.github.xpenatan.gdx.backends.teavm.example;

import java.io.File;
import java.util.ArrayList;

import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;

public class Build {

	public static void main(String[] args) {
		TeaBuilder.build(new TeaBuildConfiguration() {

			@Override
			public String getWebAppPath() {
				return new File("webapp").getAbsolutePath();
			}

			@Override
			public Class getMainClass() {
				return Launcher.class;
			}

			@Override
			public boolean minifying() {
				return false;
			}


			@Override
			public void assetsPath(ArrayList<File> paths) {

			}

			@Override
			public void assetsClasspath(ArrayList<String> classPaths) {

			}
		});
	}
}
