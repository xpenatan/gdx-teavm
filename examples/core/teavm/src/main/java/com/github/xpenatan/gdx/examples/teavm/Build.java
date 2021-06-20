package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;
import com.github.xpenatan.gdx.examples.tests.UITest;

import java.io.File;

public class Build {

	public static void main(String[] args) {
		TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
		teaBuildConfiguration.assetsPath.add(new File("../desktop/assets"));;
		teaBuildConfiguration.webappPath = new File(".").getAbsolutePath();
		teaBuildConfiguration.obfuscate = false;
		teaBuildConfiguration.mainApplicationClass = UITest.class.getName();
		TeaBuilder.build(teaBuildConfiguration);
	}
}
