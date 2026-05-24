package com.github.xpenatan.gdx.teavm.benchmarks.graalvm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkApplication;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkBackend;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkConfig;
import java.io.File;

public class GraalVMBenchmarkLauncher {
    private static final String LWJGL_JNI_FUNCTION_COUNT = "org.lwjgl.system.JNINativeInterfaceSize";
    private static final String JAVA_24_PLUS_JNI_FUNCTION_COUNT = "233";

    public static void main(String[] args) {
        configureExternalNativeLibraries();
        if(System.getProperty(LWJGL_JNI_FUNCTION_COUNT) == null) {
            System.setProperty(LWJGL_JNI_FUNCTION_COUNT, JAVA_24_PLUS_JNI_FUNCTION_COUNT);
        }

        BenchmarkConfig benchmarkConfig = BenchmarkConfig.fromArgs(args);

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("gdx-teavm benchmark GraalVM");
        config.setWindowedMode(benchmarkConfig.width, benchmarkConfig.height);
        config.setForegroundFPS(0);
        config.useVsync(false);

        new Lwjgl3Application(new BenchmarkApplication(new GraalVMBenchmarkBackend(), benchmarkConfig), config);
    }

    private static void configureExternalNativeLibraries() {
        String workingDirectory = System.getProperty("user.dir");
        if(workingDirectory == null || workingDirectory.length() == 0) {
            return;
        }
        System.setProperty("org.lwjgl.librarypath", workingDirectory);
        System.setProperty("org.lwjgl.system.SharedLibraryExtractPath", workingDirectory);
        System.setProperty("java.library.path", workingDirectory);
        loadGdxNativeLibrary(workingDirectory);
    }

    private static void loadGdxNativeLibrary(String workingDirectory) {
        String mappedName = new SharedLibraryLoader().mapLibraryName("gdx");
        File libraryFile = new File(workingDirectory, mappedName);
        if(!libraryFile.isFile()) {
            return;
        }
        System.load(libraryFile.getAbsolutePath());
        SharedLibraryLoader.setLoaded("gdx");
    }

    private static class GraalVMBenchmarkBackend implements BenchmarkBackend {
        @Override
        public String getName() {
            return "graalvm";
        }

        @Override
        public Batch createSpriteBatch(int maxSprites) {
            return new SpriteBatch(maxSprites);
        }

        @Override
        public Texture createTexture(String internalPath) {
            return new Texture(Gdx.files.internal(internalPath));
        }
    }
}
