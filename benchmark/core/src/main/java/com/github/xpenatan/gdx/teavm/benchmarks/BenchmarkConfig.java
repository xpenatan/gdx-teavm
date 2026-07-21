package com.github.xpenatan.gdx.teavm.benchmarks;

public class BenchmarkConfig {
    public static final int MAX_SPRITES_PER_BATCH = 8191;
    public static final boolean VSYNC_ENABLED = false;

    public String testName = "spritebatch_default";
    public final int sprites = 60000;
    public int width = 640;
    public int height = 480;
    public int warmupSeconds = 3;
    public int seconds = 15;
    public boolean rotate = true;
    public boolean scale = true;
    public boolean clear = true;
    public String resultFile;
    public String logFile;

    public static BenchmarkConfig fromArgs(String[] args) {
        BenchmarkConfig config = new BenchmarkConfig();
        for(String arg : args) {
            if(arg == null || arg.length() == 0) {
                continue;
            }
            if(!arg.startsWith("--")) {
                config.testName = arg;
                continue;
            }

            int eq = arg.indexOf('=');
            String key = eq >= 0 ? arg.substring(2, eq) : arg.substring(2);
            String value = eq >= 0 ? arg.substring(eq + 1) : "true";

            if("test".equalsIgnoreCase(key)) {
                config.testName = value;
            }
            else if("width".equalsIgnoreCase(key)) {
                config.width = positiveInt(key, value);
            }
            else if("height".equalsIgnoreCase(key)) {
                config.height = positiveInt(key, value);
            }
            else if("warmup".equalsIgnoreCase(key)) {
                config.warmupSeconds = nonNegativeInt(key, value);
            }
            else if("seconds".equalsIgnoreCase(key)) {
                config.seconds = positiveInt(key, value);
            }
            else if("rotate".equalsIgnoreCase(key)) {
                config.rotate = Boolean.parseBoolean(value);
            }
            else if("scale".equalsIgnoreCase(key)) {
                config.scale = Boolean.parseBoolean(value);
            }
            else if("clear".equalsIgnoreCase(key)) {
                config.clear = Boolean.parseBoolean(value);
            }
            else if("resultFile".equalsIgnoreCase(key)) {
                config.resultFile = value;
            }
            else if("logFile".equalsIgnoreCase(key)) {
                config.logFile = value;
            }
        }
        return config;
    }

    public int runtimeTimeoutSeconds() {
        return warmupSeconds + seconds + 20;
    }

    public int spriteBatchSize() {
        return Math.min(sprites, MAX_SPRITES_PER_BATCH);
    }

    private static int positiveInt(String key, String value) {
        int parsed = Integer.parseInt(value);
        if(parsed <= 0) {
            throw new IllegalArgumentException("--" + key + " must be > 0");
        }
        return parsed;
    }

    private static int nonNegativeInt(String key, String value) {
        int parsed = Integer.parseInt(value);
        if(parsed < 0) {
            throw new IllegalArgumentException("--" + key + " must be >= 0");
        }
        return parsed;
    }
}
