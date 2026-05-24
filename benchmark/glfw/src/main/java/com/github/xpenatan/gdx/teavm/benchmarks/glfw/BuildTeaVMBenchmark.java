package com.github.xpenatan.gdx.teavm.benchmarks.glfw;

import com.github.xpenatan.gdx.teavm.backends.glfw.config.backend.TeaGLFWBackend;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilder;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkConfig;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildTeaVMBenchmark {
    private static final String OUTPUT_NAME = "benchmark";
    private static final int NATIVE_MIN_HEAP_SIZE = 64 * 1024 * 1024;
    private static final int NATIVE_MAX_HEAP_SIZE = 512 * 1024 * 1024;
    private static final int NATIVE_MIN_DIRECT_BUFFER_SIZE = 64 * 1024 * 1024;

    public static void main(String[] args) throws IOException, InterruptedException {
        BuildOptions buildOptions = BuildOptions.fromArgs(args);
        File outputDir = new File("build/dist");
        if(!buildOptions.action.runExisting) {
            build(outputDir, buildOptions);
        }
        if(buildOptions.action.runExecutable) {
            runExecutable(outputDir, buildOptions);
        }
    }

    private static void build(File outputDir, BuildOptions buildOptions) {
        AssetFileHandle assetsPath = new AssetFileHandle("../../examples/basic/assets/data/badlogicsmall.jpg");
        assetsPath.assetsChildDir = "data";
        TeaGLFWBackend cBackend = new TeaGLFWBackend()
                .setBuildType(buildOptions.buildType)
                .setBuildExecutableAfterBuild(buildOptions.action.buildExecutable)
                .setRunExecutableAfterBuild(false);

        new TeaBuilder(cBackend)
                .addAssets(assetsPath)
                .setObfuscated(false)
                .setOptimizationLevel(TeaVMOptimizationLevel.FULL)
                .setMinHeapSize(NATIVE_MIN_HEAP_SIZE)
                .setMaxHeapSize(NATIVE_MAX_HEAP_SIZE)
                .setMinDirectBuffersSize(NATIVE_MIN_DIRECT_BUFFER_SIZE)
                .setMainClass(GLFWBenchmarkLauncher.class.getName())
                .setOutputName(OUTPUT_NAME)
                .build(outputDir);
    }

    private static void runExecutable(File outputDir, BuildOptions buildOptions)
            throws IOException, InterruptedException {
        File releasePath = new File(outputDir, "c/release");
        File executable = new File(releasePath, executableName(buildOptions.buildType));
        if(!executable.isFile()) {
            throw new IllegalStateException("Expected GLFW benchmark executable was not built: " + executable.getAbsolutePath());
        }

        List<String> command = new ArrayList<>();
        command.add(executable.getAbsolutePath());
        List<String> benchmarkArgs = new ArrayList<>(buildOptions.benchmarkArgs);
        File liveLogFile = new File(outputDir, "benchmark_glfw_live.log");
        if(buildOptions.consoleLog) {
            Files.deleteIfExists(liveLogFile.toPath());
            benchmarkArgs.add("--logFile=" + liveLogFile.getAbsolutePath());
        }
        command.addAll(benchmarkArgs);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(releasePath);
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        Process process = processBuilder.start();
        Thread outputThread = buildOptions.consoleLog ? tailBenchmarkLog(liveLogFile, process) : null;

        BenchmarkConfig benchmarkConfig = BenchmarkConfig.fromArgs(benchmarkArgs.toArray(new String[0]));
        int timeoutSeconds = benchmarkConfig.runtimeTimeoutSeconds();
        boolean completed = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        if(!completed) {
            killProcess(process, outputThread);
            System.out.println("BENCH_TIMEOUT backend=teavm-glfw test=" + benchmarkConfig.testName
                    + " timeoutSeconds=" + timeoutSeconds);
            writeTimeoutResult(benchmarkConfig);
            if(buildOptions.continueOnTimeout) {
                return;
            }
            throw new RuntimeException("GLFW benchmark timed out after " + timeoutSeconds + " seconds");
        }
        if(outputThread != null) {
            outputThread.join(1000);
        }
        int exitCode = process.exitValue();
        if(exitCode != 0) {
            throw new RuntimeException("GLFW benchmark failed with exit code " + exitCode);
        }
    }

    private static String executableName(TeaGLFWBackend.NativeBuildType buildType) {
        String suffix = buildType.getOutputSuffix();
        return OUTPUT_NAME + "_" + suffix + (isWindows() ? ".exe" : "");
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows");
    }

    private static Thread tailBenchmarkLog(File logFile, Process process) {
        Thread outputThread = new Thread(() -> {
            long position = 0;
            while(process.isAlive() || hasUnreadData(logFile, position)) {
                if(logFile.isFile()) {
                    try(RandomAccessFile reader = new RandomAccessFile(logFile, "r")) {
                        reader.seek(position);
                        String line;
                        while((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                        position = reader.getFilePointer();
                    } catch(IOException ignored) {
                    }
                }
                try {
                    Thread.sleep(50);
                } catch(InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "glfw-benchmark-log-tail");
        outputThread.setDaemon(true);
        outputThread.start();
        return outputThread;
    }

    private static boolean hasUnreadData(File logFile, long position) {
        return logFile.isFile() && logFile.length() > position;
    }

    private static void killProcess(Process process, Thread outputThread) throws InterruptedException {
        process.destroy();
        if(!process.waitFor(2, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            process.waitFor(5, TimeUnit.SECONDS);
        }
        if(outputThread != null) {
            outputThread.join(1000);
        }
    }

    private static void writeTimeoutResult(BenchmarkConfig config) throws IOException {
        if(config.resultFile == null || config.resultFile.length() == 0) {
            return;
        }

        File file = new File(config.resultFile);
        File parent = file.getParentFile();
        if(parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Unable to create benchmark result directory: " + parent.getAbsolutePath());
        }

        boolean writeHeader = !file.isFile() || file.length() == 0;
        StringBuilder builder = new StringBuilder();
        if(writeHeader) {
            builder.append("backend\ttest\tsprites\twidth\theight\trotate\tscale\tclear\tvsync\tavgFps\tminFps\tmaxFps\tsamples\n");
        }
        builder.append("teavm-glfw")
                .append('\t').append(config.testName)
                .append('\t').append(config.sprites)
                .append('\t').append(config.width)
                .append('\t').append(config.height)
                .append('\t').append(config.rotate)
                .append('\t').append(config.scale)
                .append('\t').append(config.clear)
                .append('\t').append(BenchmarkConfig.VSYNC_ENABLED)
                .append('\t').append("TIMEOUT")
                .append('\t').append(0)
                .append('\t').append(0)
                .append('\t').append(0)
                .append('\n');
        Files.writeString(file.toPath(), builder.toString(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private static class BuildOptions {
        private final TeaGLFWBackend.NativeBuildType buildType;
        private final BuildAction action;
        private final boolean consoleLog;
        private final boolean continueOnTimeout;
        private final List<String> benchmarkArgs;

        private BuildOptions(
                TeaGLFWBackend.NativeBuildType buildType,
                BuildAction action,
                boolean consoleLog,
                boolean continueOnTimeout,
                List<String> benchmarkArgs) {
            this.buildType = buildType;
            this.action = action;
            this.consoleLog = consoleLog;
            this.continueOnTimeout = continueOnTimeout;
            this.benchmarkArgs = benchmarkArgs;
        }

        private static BuildOptions fromArgs(String[] args) {
            TeaGLFWBackend.NativeBuildType buildType = TeaGLFWBackend.NativeBuildType.RELEASE;
            BuildAction action = BuildAction.GENERATE;
            List<String> benchmarkArgs = new ArrayList<>();

            int index = 0;
            if(index < args.length && !args[index].startsWith("--")) {
                buildType = TeaGLFWBackend.NativeBuildType.fromString(args[index]);
                index++;
            }
            if(index < args.length && !args[index].startsWith("--")) {
                action = BuildAction.fromArg(args[index]);
                index++;
            }
            boolean consoleLog = false;
            if(index < args.length && isConsoleArg(args[index])) {
                consoleLog = true;
                index++;
            }
            boolean continueOnTimeout = false;
            while(index < args.length) {
                String arg = args[index];
                Boolean parsedContinueOnTimeout = parseContinueOnTimeoutArg(arg);
                if(parsedContinueOnTimeout != null) {
                    continueOnTimeout = parsedContinueOnTimeout;
                }
                else {
                    benchmarkArgs.add(arg);
                }
                index++;
            }
            if(consoleLog && !action.runExecutable) {
                throw new IllegalArgumentException("Console logging requires the run or runExisting action");
            }
            return new BuildOptions(buildType, action, consoleLog, continueOnTimeout, benchmarkArgs);
        }

        private static boolean isConsoleArg(String arg) {
            String normalized = arg.trim().toLowerCase(Locale.ROOT);
            return normalized.equals("console") || normalized.equals("--console");
        }

        private static Boolean parseContinueOnTimeoutArg(String arg) {
            String normalized = arg.trim().toLowerCase(Locale.ROOT);
            if(normalized.equals("continueontimeout") || normalized.equals("--continueontimeout")) {
                return true;
            }
            String prefix = "--continueontimeout=";
            if(normalized.startsWith(prefix)) {
                return Boolean.parseBoolean(normalized.substring(prefix.length()));
            }
            return null;
        }
    }

    private enum BuildAction {
        GENERATE(false, false, false),
        BUILD(true, false, false),
        RUN(true, true, false),
        RUN_EXISTING(false, true, true);

        private final boolean buildExecutable;
        private final boolean runExecutable;
        private final boolean runExisting;

        BuildAction(boolean buildExecutable, boolean runExecutable, boolean runExisting) {
            this.buildExecutable = buildExecutable;
            this.runExecutable = runExecutable;
            this.runExisting = runExisting;
        }

        private static BuildAction fromArg(String arg) {
            String normalized = arg.trim().toLowerCase(Locale.ROOT);
            if(normalized.equals("generate") || normalized.equals("--generate")) {
                return GENERATE;
            }
            if(normalized.equals("build") || normalized.equals("--build")) {
                return BUILD;
            }
            if(normalized.equals("run") || normalized.equals("--run")) {
                return RUN;
            }
            if(normalized.equals("runexisting") || normalized.equals("run-existing") || normalized.equals("--runexisting")
                    || normalized.equals("--run-existing")) {
                return RUN_EXISTING;
            }
            throw new IllegalArgumentException("Unsupported GLFW benchmark action argument: " + arg);
        }
    }
}
