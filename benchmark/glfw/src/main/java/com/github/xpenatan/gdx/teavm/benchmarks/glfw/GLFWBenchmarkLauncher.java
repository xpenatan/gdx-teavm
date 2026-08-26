package com.github.xpenatan.gdx.teavm.benchmarks.glfw;

import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplication;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkApplication;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkConfig;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.SpriteBatchTest;

public class GLFWBenchmarkLauncher {
    public static void main(String[] args) {
        BenchmarkConfig benchmarkConfig = BenchmarkConfig.fromArgs(args);

        GLFWApplicationConfiguration config = new GLFWApplicationConfiguration();
        config.setTitle("gdx-teavm benchmark GLFW");
        config.setWindowedMode(benchmarkConfig.width, benchmarkConfig.height);
        config.useVsync(false);
        config.setForegroundFPS(0);

        System.setProperty("os.name", "Windows");
        new GLFWApplication(
                new BenchmarkApplication("teavm-glfw", benchmarkConfig, new SpriteBatchTest()),
                config);
    }
}
