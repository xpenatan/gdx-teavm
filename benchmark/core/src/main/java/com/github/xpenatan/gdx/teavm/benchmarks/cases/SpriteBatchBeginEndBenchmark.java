package com.github.xpenatan.gdx.teavm.benchmarks.cases;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkBackend;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkCase;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkConfig;

public class SpriteBatchBeginEndBenchmark implements BenchmarkCase {
    private BenchmarkConfig config;
    private ScreenViewport viewport;
    private SpriteBatch spriteBatch;

    @Override
    public String getName() {
        return "spritebatch_begin_end";
    }

    @Override
    public void create(BenchmarkBackend backend, BenchmarkConfig config) {
        this.config = config;
        viewport = new ScreenViewport();
        spriteBatch = (SpriteBatch)backend.createSpriteBatch(config.sprites);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        if(config.clear) {
            ScreenUtils.clear(0, 0, 0, 1);
        }
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        if(spriteBatch != null) {
            spriteBatch.dispose();
            spriteBatch = null;
        }
    }
}
