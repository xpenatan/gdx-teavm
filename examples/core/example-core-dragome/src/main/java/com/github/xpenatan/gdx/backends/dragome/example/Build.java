package com.github.xpenatan.gdx.backends.dragome.example;

import java.io.File;
import java.util.ArrayList;
import com.github.xpenatan.gdx.backends.dragome.DragomeBuilder;
import com.github.xpenatan.gdx.backends.dragome.DragomeConfigurator;


public class Build {

	public static void main(String[] args) {

		DragomeBuilder.build(new DragomeConfigurator());
//
//		DragomeBuilder.build(new DragomeTestConfiguration() {

//			@Override
//			public boolean minifying() {
//				return false;
//			}
//
//			@Override
//			public String getTargetDirectory() {
//				return new File("webapp").getAbsolutePath();
//			}
//
//			@Override
//			public String getMain() {
//				return Launcher.class.getName();
//			}
//
//			@Override
//			public void assetsPath(ArrayList<File> paths) {
//			}
//
//			@Override
//			public void assetsClasspath(ArrayList<String> classPaths) {
//
//			}
//		});
	}
}
