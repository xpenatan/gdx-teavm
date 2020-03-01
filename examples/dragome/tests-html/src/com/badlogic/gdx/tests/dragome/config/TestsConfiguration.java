package com.badlogic.gdx.tests.dragome.config;

import java.io.File;

import com.badlogic.gdx.utils.Array;
import com.dragome.commons.DragomeConfiguratorImplementor;
import com.github.xpenatan.gdx.backends.dragome.DragomeGdxConfiguration;

@DragomeConfiguratorImplementor(priority = 11)
public class TestsConfiguration extends DragomeGdxConfiguration{

	@Override
	public int filterClassLog () {
		return 1;
	}

	@Override
	public boolean isRemoveUnusedCode () {
		return false;
	}

	@Override
	public boolean isObfuscateCode () {
		return false;
	}

	@Override
	public void assetsPath(Array<File> paths) {
		paths.add(new File("../tests-desktop/assets"));
	}

	@Override
	public void assetsClasspath(Array<String> classPaths) {
	}

	@Override
	public boolean filterClassPathLog () {
		return true;
	}
}
