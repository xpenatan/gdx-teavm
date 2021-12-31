package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;
import com.github.xpenatan.gdx.examples.tests.AnimationTest;
import com.github.xpenatan.gdx.examples.tests.UITest;
import com.github.xpenatan.gdx.examples.tests.freetype.FreeTypeAtlasTest;
import com.github.xpenatan.gdx.examples.tests.freetype.FreeTypeMetricsTest;
import com.github.xpenatan.gdx.examples.tests.freetype.FreeTypePackTest;
import com.github.xpenatan.gdx.examples.tests.freetype.FreeTypeTest;
import com.github.xpenatan.gdx.examples.tests.reflection.ReflectionTest;

import java.io.File;

public class Build {

	public static void main(String[] args) {
		TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
		teaBuildConfiguration.assetsPath.add(new File("../desktop/assets"));;
		teaBuildConfiguration.webappPath = new File(".").getAbsolutePath();
		teaBuildConfiguration.obfuscate = false;
		teaBuildConfiguration.mainApplicationClass = FreeTypeAtlasTest.class.getName();
//		teaBuildConfiguration.mainApplicationClass = FreeTypeTest.class.getName();
//		teaBuildConfiguration.mainApplicationClass = FreeTypeMetricsTest.class.getName();
//		teaBuildConfiguration.mainApplicationClass = FreeTypePackTest.class.getName();
		TeaBuilder.build(teaBuildConfiguration);
	}
}
