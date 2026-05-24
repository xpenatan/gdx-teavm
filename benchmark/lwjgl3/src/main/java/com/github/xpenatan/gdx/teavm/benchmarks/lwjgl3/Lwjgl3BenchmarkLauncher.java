package com.github.xpenatan.gdx.teavm.benchmarks.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkApplication;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkBackend;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkConfig;

public class Lwjgl3BenchmarkLauncher {
    public static void main(String[] args) {
        BenchmarkConfig benchmarkConfig = BenchmarkConfig.fromArgs(args);

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("gdx-teavm benchmark LWJGL3");
        config.setWindowedMode(benchmarkConfig.width, benchmarkConfig.height);
        config.setForegroundFPS(0);
        config.useVsync(false);

        new Lwjgl3Application(new BenchmarkApplication(new Lwjgl3BenchmarkBackend(), benchmarkConfig), config);
    }

    private static class Lwjgl3BenchmarkBackend implements BenchmarkBackend {
        @Override
        public String getName() {
            return "lwjgl3";
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
