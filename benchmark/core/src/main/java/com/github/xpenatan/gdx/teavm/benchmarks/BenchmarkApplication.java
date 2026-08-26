package com.github.xpenatan.gdx.teavm.benchmarks;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.TimeUtils;

public class BenchmarkApplication extends ApplicationAdapter {
    private static final long SECOND = 1_000_000_000L;

    private final String backendName;
    private final BenchmarkConfig config;
    private final ApplicationListener test;
    private final String testName;
    private long secondStartNanos;
    private int secondFrames;
    private int loggedSeconds;
    private int sampleCount;
    private int minFps = Integer.MAX_VALUE;
    private int maxFps;
    private long totalFps;
    private boolean finished;

    public BenchmarkApplication(String backendName, BenchmarkConfig config, ApplicationListener test) {
        if(test == null) {
            throw new IllegalArgumentException("test cannot be null");
        }
        this.backendName = backendName;
        this.config = config;
        this.test = test;
        this.testName = test.getClass().getSimpleName();
    }

    @Override
    public void create() {
        test.create();
        secondStartNanos = TimeUtils.nanoTime();
        log("BENCH_START backend=" + backendName
                + " test=" + testName
                + " size=" + config.width + "x" + config.height
                + " warmup=" + config.warmupSeconds
                + " seconds=" + config.seconds
                + " vsync=" + BenchmarkConfig.VSYNC_ENABLED);
    }

    @Override
    public void resize(int width, int height) {
        test.resize(width, height);
    }

    @Override
    public void render() {
        if(finished) {
            return;
        }

        test.render();
        secondFrames++;

        long now = TimeUtils.nanoTime();
        long elapsed = now - secondStartNanos;
        if(elapsed >= SECOND) {
            boolean warmup = loggedSeconds < config.warmupSeconds;
            int fps = (int)(((long)secondFrames * SECOND + elapsed / 2L) / elapsed);
            log("BENCH_SECOND backend=" + backendName
                    + " test=" + testName
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
    public void pause() {
        test.pause();
    }

    @Override
    public void resume() {
        test.resume();
    }

    @Override
    public void dispose() {
        test.dispose();
    }

    private void printResult() {
        int min = sampleCount == 0 ? 0 : minFps;
        int avg = sampleCount == 0 ? 0 : (int)Math.round((double)totalFps / sampleCount);
        log("BENCH_RESULT backend=" + backendName
                + " test=" + testName
                + " width=" + config.width
                + " height=" + config.height
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
            builder.append(BenchmarkConfig.RESULT_HEADER);
        }
        builder.append(backendName)
                .append('\t').append(testName)
                .append('\t').append(config.width)
                .append('\t').append(config.height)
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
