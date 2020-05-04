package com.github.xpenatan.gdx.backends.teavm.example;

import java.io.File;
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;
import com.badlogic.gdx.utils.Array;
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
