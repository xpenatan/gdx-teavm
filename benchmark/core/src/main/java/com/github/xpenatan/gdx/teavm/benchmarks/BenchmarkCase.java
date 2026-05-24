package com.github.xpenatan.gdx.teavm.benchmarks;

public interface BenchmarkCase {
    String getName();

    void create(BenchmarkBackend backend, BenchmarkConfig config);

    void resize(int width, int height);

    void render();

    void dispose();
}
