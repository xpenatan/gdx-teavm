package com.github.xpenatan.gdx.backends.teavm.example;

import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;

import java.io.File;

public class Build {

	public static void main(String[] args) {
		TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
		teaBuildConfiguration.assetsPath.add(new File("../example-core-desktop/assets"));;
		teaBuildConfiguration.webappPath = new File("webapp").getAbsolutePath();
		teaBuildConfiguration.mainClass = Launcher.class;
		teaBuildConfiguration.obfuscate = false;
		TeaBuilder.build(teaBuildConfiguration);
	}
}
