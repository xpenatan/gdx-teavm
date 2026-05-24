package com.github.xpenatan.gdx.teavm.benchmarks.glfw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplication;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkApplication;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkBackend;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkConfig;

public class GLFWBenchmarkLauncher {
    public static void main(String[] args) {
        BenchmarkConfig benchmarkConfig = BenchmarkConfig.fromArgs(args);

        GLFWApplicationConfiguration config = new GLFWApplicationConfiguration();
        config.useVsync(false);
        config.setForegroundFPS(0);

        System.setProperty("os.name", "Windows");
        new GLFWApplication(new BenchmarkApplication(new GLFWBenchmarkBackend(), benchmarkConfig), config);
    }

    private static class GLFWBenchmarkBackend implements BenchmarkBackend {
        @Override
        public String getName() {
            return "teavm-glfw";
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
