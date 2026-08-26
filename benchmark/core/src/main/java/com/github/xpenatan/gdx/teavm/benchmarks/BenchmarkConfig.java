package com.github.xpenatan.gdx.teavm.benchmarks;

public class BenchmarkConfig {
    public static final String RESULT_HEADER = "backend\ttest\twidth\theight\tvsync\tavgFps\tminFps\tmaxFps\tsamples\n";
    public static final boolean VSYNC_ENABLED = false;

    public int width = 640;
    public int height = 480;
    public int warmupSeconds = 3;
    public int seconds = 15;
    public String resultFile;
    public String logFile;

    public static BenchmarkConfig fromArgs(String[] args) {
        BenchmarkConfig config = new BenchmarkConfig();
        for(String arg : args) {
            if(arg == null || arg.length() == 0) {
                continue;
            }
            if(!arg.startsWith("--")) {
                continue;
            }

            int eq = arg.indexOf('=');
            String key = eq >= 0 ? arg.substring(2, eq) : arg.substring(2);
            String value = eq >= 0 ? arg.substring(eq + 1) : "true";

            if("width".equalsIgnoreCase(key)) {
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
