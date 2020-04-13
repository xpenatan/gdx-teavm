package com.github.xpenatan.gdx.backends.dragome.example;

import java.io.File;
import java.util.ArrayList;
import com.github.xpenatan.gdx.backends.dragome.DragomeBuilder;
import com.github.xpenatan.gdx.backends.dragome.DragomeBuildConfigurator;

public class Build {

	public static void main(String[] args) {

		DragomeBuilder.build(new DragomeBuildConfigurator() {

			@Override
			public Class getMainClass() {
				return Launcher.class;
			}

			@Override
			public String getWebAppPath() {
				return new File("webapp").getAbsolutePath();
			}

			@Override
			public void assetsClasspath(ArrayList<String> classPaths) {
			}

			@Override
			public void assetsPath(ArrayList<File> paths) {
			}

			@Override
			public boolean minifying() {
				return false;
			}

		});
	}
}
