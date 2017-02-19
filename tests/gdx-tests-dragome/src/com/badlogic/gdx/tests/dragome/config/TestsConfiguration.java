package com.badlogic.gdx.tests.dragome.config;

import java.io.File;

import com.badlogic.gdx.backends.dragome.DragomeConfiguration;
import com.badlogic.gdx.utils.Array;
import com.dragome.commons.DragomeConfiguratorImplementor;

@DragomeConfiguratorImplementor(priority = 11)
public class TestsConfiguration extends DragomeConfiguration{

	@Override
	public boolean isRemoveUnusedCode() {
		return false;
	}

	@Override
	public void assetsPath(Array<File> paths) {
		paths.add(new File("../gdx-tests-dragome-desktop/assets"));
	}

	@Override
	public void assetsClasspath(Array<String> classPaths) {
	}
}
