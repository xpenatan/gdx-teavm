package com.github.xpenatan.gdx.backends.teavm.example;

import java.io.File;
import java.util.ArrayList;

import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.TeaConfiguration;

public class Build {

	public static void main(String[] args) {


		TeaBuilder.build(new TeaConfiguration() {

			@Override
			public boolean minifying() {
				return false;
			}

			@Override
			public String getTargetDirectory() {
				return new File("webapp").getAbsolutePath();
			}

			@Override
			public String getMain() {
				return Launcher.class.getName();
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
