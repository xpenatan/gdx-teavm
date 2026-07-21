package com.github.xpenatan.gdx.teavm.benchmarks;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.xpenatan.gdx.teavm.benchmarks.cases.SpriteBatchBeginEndBenchmark;
import com.github.xpenatan.gdx.teavm.benchmarks.cases.SpriteBatchDefaultBenchmark;
import com.github.xpenatan.gdx.teavm.benchmarks.cases.SpriteBatchDirectArrayStateBenchmark;
import com.github.xpenatan.gdx.teavm.benchmarks.cases.SpriteBatchDirectSpriteGettersBenchmark;
import com.github.xpenatan.gdx.teavm.benchmarks.cases.SpriteBatchFastBenchmark;
import com.github.xpenatan.gdx.teavm.benchmarks.cases.SpriteBatchPrecomputedArrayCopyBenchmark;
import com.github.xpenatan.gdx.teavm.benchmarks.cases.SpriteBatchSimpleDirectBenchmark;

public class BenchmarkApplication extends ApplicationAdapter {
    private static final long SECOND = 1000000000L;

    private final BenchmarkBackend backend;
    private final BenchmarkConfig config;
    private BenchmarkCase benchmarkCase;
    private long startNanos;
    private long secondStartNanos;
    private int secondFrames;
    private int loggedSeconds;
    private int sampleCount;
    private int minFps = Integer.MAX_VALUE;
    private int maxFps;
    private long totalFps;
    private boolean finished;

    public BenchmarkApplication(BenchmarkBackend backend, BenchmarkConfig config) {
        this.backend = backend;
        this.config = config;
    }

    @Override
    public void create() {
        benchmarkCase = createCase(config.testName);
        benchmarkCase.create(backend, config);
        startNanos = TimeUtils.nanoTime();
        secondStartNanos = startNanos;
        log("BENCH_START backend=" + backend.getName()
                + " test=" + benchmarkCase.getName()
                + " sprites=" + config.sprites
                + " size=" + config.width + "x" + config.height
                + " warmup=" + config.warmupSeconds
                + " seconds=" + config.seconds
                + " rotate=" + config.rotate
                + " scale=" + config.scale
                + " clear=" + config.clear
                + " vsync=" + BenchmarkConfig.VSYNC_ENABLED);
    }

    @Override
    public void resize(int width, int height) {
        if(benchmarkCase != null) {
            benchmarkCase.resize(width, height);
        }
    }

    @Override
    public void render() {
        if(finished) {
            return;
        }

        benchmarkCase.render();
        secondFrames++;

        long now = TimeUtils.nanoTime();
        if(now - secondStartNanos >= SECOND) {
            boolean warmup = loggedSeconds < config.warmupSeconds;
            int fps = secondFrames;
            log("BENCH_SECOND backend=" + backend.getName()
                    + " test=" + benchmarkCase.getName()
                    + " sprites=" + config.sprites
                    + " warmup=" + warmup
                    + " fps=" + fps);
            if(!warmup) {
                sampleCount++;
                totalFps += fps;
                minFps = Math.min(minFps, fps);
                maxFps = Math.max(maxFps, fps);
                if(sampleCount >= config.seconds) {
                    finish();
                    return;
                }
            }
            loggedSeconds++;
            secondFrames = 0;
            secondStartNanos = now;
        }
    }

    @Override
    public void dispose() {
        if(benchmarkCase != null) {
            benchmarkCase.dispose();
        }
    }

    private BenchmarkCase createCase(String testName) {
        if("default".equalsIgnoreCase(testName)
                || "spritebatch_default".equalsIgnoreCase(testName)) {
            return new SpriteBatchDefaultBenchmark();
        }
        if("fast_sprite_batch".equalsIgnoreCase(testName) || "spritebatch_fast".equalsIgnoreCase(testName)) {
            return new SpriteBatchFastBenchmark();
        }
        if("direct_sprite_getters".equalsIgnoreCase(testName)
                || "spritebatch_direct_getters".equalsIgnoreCase(testName)) {
            return new SpriteBatchDirectSpriteGettersBenchmark();
        }
        if("direct_array_state".equalsIgnoreCase(testName)
                || "spritebatch_direct_array_state".equalsIgnoreCase(testName)) {
            return new SpriteBatchDirectArrayStateBenchmark();
        }
        if("simple_direct".equalsIgnoreCase(testName) || "spritebatch_simple_direct".equalsIgnoreCase(testName)) {
            return new SpriteBatchSimpleDirectBenchmark();
        }
        if("precomputed_arraycopy".equalsIgnoreCase(testName)
                || "spritebatch_precomputed_arraycopy".equalsIgnoreCase(testName)) {
            return new SpriteBatchPrecomputedArrayCopyBenchmark();
        }
        if("begin_end".equalsIgnoreCase(testName)
                || "begin_end_only".equalsIgnoreCase(testName)
                || "spritebatch_begin_end".equalsIgnoreCase(testName)) {
            return new SpriteBatchBeginEndBenchmark();
        }
        throw new IllegalArgumentException("Unknown benchmark test: " + testName);
    }

    private void printResult() {
        int min = sampleCount == 0 ? 0 : minFps;
        int avg = sampleCount == 0 ? 0 : (int)Math.round((double)totalFps / sampleCount);
        log("BENCH_RESULT backend=" + backend.getName()
                + " test=" + benchmarkCase.getName()
                + " sprites=" + config.sprites
                + " width=" + config.width
                + " height=" + config.height
                + " rotate=" + config.rotate
                + " scale=" + config.scale
                + " clear=" + config.clear
                + " vsync=" + BenchmarkConfig.VSYNC_ENABLED
                + " avgFps=" + avg
                + " minFps=" + min
                + " maxFps=" + maxFps
                + " samples=" + sampleCount);
        writeResult(avg, min);
    }

    private void writeResult(int avg, int min) {
        if(config.resultFile == null || config.resultFile.length() == 0) {
            return;
        }

        FileHandle file = Gdx.files.absolute(config.resultFile);
        FileHandle parent = file.parent();
        if(parent != null) {
            parent.mkdirs();
        }

        boolean writeHeader = !file.exists() || file.length() == 0;
        StringBuilder builder = new StringBuilder();
        if(writeHeader) {
            builder.append("backend\ttest\tsprites\twidth\theight\trotate\tscale\tclear\tvsync\tavgFps\tminFps\tmaxFps\tsamples\n");
        }
        builder.append(backend.getName())
                .append('\t').append(benchmarkCase.getName())
                .append('\t').append(config.sprites)
                .append('\t').append(config.width)
                .append('\t').append(config.height)
                .append('\t').append(config.rotate)
                .append('\t').append(config.scale)
                .append('\t').append(config.clear)
                .append('\t').append(BenchmarkConfig.VSYNC_ENABLED)
                .append('\t').append(avg)
                .append('\t').append(min)
                .append('\t').append(maxFps)
                .append('\t').append(sampleCount)
                .append('\n');
        file.writeString(builder.toString(), true);
    }

    private void log(String line) {
        System.out.println(line);
        if(config.logFile == null || config.logFile.length() == 0) {
            return;
        }

        FileHandle file = Gdx.files.absolute(config.logFile);
        FileHandle parent = file.parent();
        if(parent != null) {
            parent.mkdirs();
        }
        file.writeString(line + "\n", true);
    }

    private void finish() {
        if(finished) {
            return;
        }
        finished = true;
        printResult();
        Gdx.app.exit();
    }
}
