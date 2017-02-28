package com.badlogic.gdx.tests.dragome.config;

import java.io.File;

import com.badlogic.gdx.backends.dragome.DragomeGdxConfiguration;
import com.badlogic.gdx.utils.Array;
import com.dragome.commons.DragomeConfiguratorImplementor;
import com.dragome.compiler.utils.Log;

@DragomeConfiguratorImplementor(priority = 11)
public class TestsConfiguration extends DragomeGdxConfiguration{

	@Override
	public boolean isRemoveUnusedCode() {
		return true;
	}
	
	@Override
	public int filterClassLog () {
		return 2;
	}

	@Override
	public void assetsPath(Array<File> paths) {
		paths.add(new File("../gdx-tests-dragome-desktop/assets"));
	}

	@Override
	public void assetsClasspath(Array<String> classPaths) {
	}
}
