package com.github.xpenatan.gdx.backends.dragome.example;

import java.io.File;
import com.github.xpenatan.gdx.backends.dragome.DragomeBuilder;
import com.badlogic.gdx.utils.Array;
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
			public void assetsClasspath(Array<String> classPaths) {
			}

			@Override
			public boolean assetsPath(Array<File> paths) {
				return true;
			}

			@Override
			public boolean minifying() {
				return false;
			}

		});
	}
}
