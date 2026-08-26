package com.github.xpenatan.gdx.teavm.benchmarks.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkApplication;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkConfig;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.SpriteBatchTest;

public class Lwjgl3BenchmarkLauncher {
    public static void main(String[] args) {
        BenchmarkConfig benchmarkConfig = BenchmarkConfig.fromArgs(args);

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("gdx-teavm benchmark LWJGL3");
        config.setWindowedMode(benchmarkConfig.width, benchmarkConfig.height);
        config.setForegroundFPS(0);
        config.useVsync(false);

        new Lwjgl3Application(
                new BenchmarkApplication("lwjgl3", benchmarkConfig, new SpriteBatchTest()),
                config);
    }
}
