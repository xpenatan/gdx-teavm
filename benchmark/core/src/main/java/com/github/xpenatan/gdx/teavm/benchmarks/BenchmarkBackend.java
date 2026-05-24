package com.github.xpenatan.gdx.teavm.benchmarks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public interface BenchmarkBackend {
    String getName();

    Batch createSpriteBatch(int maxSprites);

    Texture createTexture(String internalPath);
}
