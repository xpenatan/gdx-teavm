package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.plugins.TeaReflectionSupplier;
import com.github.xpenatan.gdx.backends.web.gen.SkipClass;
import com.github.xpenatan.gdx.examples.tests.GearsDemo;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.teavm.tooling.TeaVMTool;

@SkipClass
public class Build {
    /**
     * There are 2 ways to build using teaVM module.
     * 1) Add jar or build folder to additionalClasspath
     * 2) Core.jar from core module is automatically added by getting it from gradle implementation classpath.
     */
    private final static boolean BUILD_FROM_COMPILED_FOLDER = false;

    public static void main(String[] args) {
        String reflectionPackage = "com.github.xpenatan.gdx.examples.tests.reflection.models";
        TeaReflectionSupplier.addReflectionClass(reflectionPackage);

        TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration() {
            @Override
            public boolean acceptClasspath(URL url) {
                if(BUILD_FROM_COMPILED_FOLDER && url.getPath().contains("/core.jar")) {
                    // Ignore core.jar file if it's building from a folder
                    return false;
                }
                return super.acceptClasspath(url);
            }
        };
        teaBuildConfiguration.assetsPath.add(new File("../desktop/assets"));
        teaBuildConfiguration.webappPath = new File(".").getAbsolutePath();
        teaBuildConfiguration.obfuscate = false;
//        teaBuildConfiguration.mainApplicationClass = ReflectionTest.class.getName();
//		teaBuildConfiguration.mainApplicationClass = FreeTypeTest.class.getName();
//		teaBuildConfiguration.mainApplicationClass = FreeTypeMetricsTest.class.getName();
        teaBuildConfiguration.setApplicationListener(GearsDemo.class);
//		teaBuildConfiguration.mainApplicationClass = Box2DTest.class.getName();

        if(BUILD_FROM_COMPILED_FOLDER) {
            File rootFile = new File("");
            String path = rootFile.getAbsolutePath();
            String compiledRootClasses = path + File.separator + ".." + File.separator + "core" + File.separator + "build" + File.separator + "classes" + File.separator + "java" + File.separator + "main" + File.separator;
            URL appJarAppUrl = null;
            try {
                appJarAppUrl = new File(compiledRootClasses).toURI().toURL();
            }
            catch(MalformedURLException e) {
                e.printStackTrace();
            }
            teaBuildConfiguration.additionalClasspath.add(appJarAppUrl);
        }

        TeaVMTool tool = TeaBuilder.config(teaBuildConfiguration);
        TeaBuilder.build(tool);
    }
}
