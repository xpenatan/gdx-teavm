package com.github.xpenatan.gdx.teavm.benchmarks.web;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkApplication;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkBackend;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkConfig;

public class WebBenchmarkLauncher {
    public static void main(String[] args) {
        BenchmarkConfig benchmarkConfig = BenchmarkConfig.fromArgs(args);

        WebApplicationConfiguration config = new WebApplicationConfiguration();
        config.width = 0;
        config.height = 0;

        String backendName = backendName(args);
        new WebApplication(new BenchmarkApplication(new WebBenchmarkBackend(backendName), benchmarkConfig), config);
    }

    private static String backendName(String[] args) {
        for(String arg : args) {
            if(arg != null && arg.startsWith("--backend=")) {
                return arg.substring("--backend=".length());
            }
        }
        return "teavm-web";
    }

    private static class WebBenchmarkBackend implements BenchmarkBackend {
        private final String name;

        private WebBenchmarkBackend(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
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
