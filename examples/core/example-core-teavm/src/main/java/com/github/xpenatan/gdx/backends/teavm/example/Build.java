package com.github.xpenatan.gdx.backends.teavm.example;

import com.badlogic.gdx.utils.Array;
import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;

import java.io.File;

public class Build {

	public static void main(String[] args) {
		TeaBuilder.build(new TeaBuildConfiguration() {

			@Override
			public Class getMainClass() {
				return Launcher.class;
			}

			@Override
			public String getWebAppPath() {
				return new File("webapp").getAbsolutePath();
			}

			@Override
			public boolean minifying() {
				return true;
			}


			@Override
			public boolean assetsPath(Array<File> paths) {
				File assetPath = new File("../example-core-desktop/assets");
				paths.add(assetPath);
				return true;
			}

			@Override
			public void assetsClasspath(Array<String> classPaths) {

			}
		});
	}
}
