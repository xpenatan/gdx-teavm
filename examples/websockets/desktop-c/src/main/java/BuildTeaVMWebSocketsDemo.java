import com.github.xpenatan.gdx.teavm.backends.glfw.config.backend.TeaGLFWBackend;
import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Locale;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildTeaVMWebSocketsDemo {

    private static final int NATIVE_MIN_HEAP_SIZE = 64 * 1024 * 1024;
    private static final int NATIVE_MAX_HEAP_SIZE = 512 * 1024 * 1024;
    private static final int NATIVE_MIN_DIRECT_BUFFER_SIZE = 64 * 1024 * 1024;
    private static final File OUTPUT_ROOT = new File("build/dist");
    private static final File RELEASE_PATH = new File(OUTPUT_ROOT, "c/release");
    private static final String LINUX_CURL_PROPERTY = "gdxTeaVMLinuxCurlPath";
    private static final String LINUX_CURL_ENV = "GDX_TEAVM_LINUX_CURL_PATH";
    private static final String LINUX_CURL_OUTPUT_NAME = "libcurl.so.4";
    private static final String MAC_CURL_PROPERTY = "gdxTeaVMMacCurlPath";
    private static final String MAC_CURL_ENV = "GDX_TEAVM_MAC_CURL_PATH";
    private static final String MAC_CURL_OUTPUT_NAME = "libcurl.4.dylib";
    private static final Path DEFAULT_MAC_CURL_BUILD_ROOT = Path.of("build", "libcurl-wss-macos");
    private static final Path DEFAULT_MAC_CURL_RUNTIME = DEFAULT_MAC_CURL_BUILD_ROOT.resolve(Path.of("install", "lib", MAC_CURL_OUTPUT_NAME));
    private static final Path MAC_CURL_BUILD_SCRIPT = Path.of("build-mac-libcurl-wss.sh");

    public static void main(String[] args) throws IOException {
        BuildOptions buildOptions = BuildOptions.fromArgs(args);
        packageHostCurlRuntime();
        TeaGLFWBackend cBackend = new TeaGLFWBackend()
                .setBuildType(buildOptions.buildType)
                .setBuildExecutableAfterBuild(buildOptions.shouldBuildExecutable())
                .setRunExecutableAfterBuild(buildOptions.shouldRunExecutable())
                .setRunExecutableWithConsoleLog(buildOptions.consoleLog);

        new TeaBuilder(cBackend)
                .setOutputName("websockets")
                .setObfuscated(false)
                .setOptimizationLevel(TeaVMOptimizationLevel.FULL)
                .setMinHeapSize(NATIVE_MIN_HEAP_SIZE)
                .setMaxHeapSize(NATIVE_MAX_HEAP_SIZE)
                .setMinDirectBuffersSize(NATIVE_MIN_DIRECT_BUFFER_SIZE)
                .setMainClass(WebSocketsCLauncher.class.getName())
                .setReleasePath(RELEASE_PATH)
                .build(OUTPUT_ROOT);
    }

    private static void packageHostCurlRuntime() throws IOException {
        if(isLinuxHost()) {
            packageConfiguredCurlRuntime(resolveConfiguredPath(LINUX_CURL_PROPERTY, LINUX_CURL_ENV),
                    LINUX_CURL_OUTPUT_NAME,
                    "Linux");
            return;
        }

        if(isMacHost()) {
            packageConfiguredCurlRuntime(resolveOrBuildMacCurlPath(),
                    MAC_CURL_OUTPUT_NAME,
                    "macOS");
        }
    }

    private static String resolveOrBuildMacCurlPath() throws IOException {
        String configuredPath = resolveConfiguredPath(MAC_CURL_PROPERTY, MAC_CURL_ENV);
        if(configuredPath != null) {
            return configuredPath;
        }

        if(Files.isRegularFile(DEFAULT_MAC_CURL_RUNTIME)) {
            return DEFAULT_MAC_CURL_RUNTIME.toString();
        }

        buildMacCurlRuntime();
        if(Files.isRegularFile(DEFAULT_MAC_CURL_RUNTIME)) {
            return DEFAULT_MAC_CURL_RUNTIME.toString();
        }

        throw new IOException("macOS libcurl runtime build completed but no runtime was produced at: " + DEFAULT_MAC_CURL_RUNTIME);
    }

    private static void buildMacCurlRuntime() throws IOException {
        if(!Files.isRegularFile(MAC_CURL_BUILD_SCRIPT)) {
            throw new IOException("macOS libcurl build script was not found: " + MAC_CURL_BUILD_SCRIPT);
        }

        Files.createDirectories(DEFAULT_MAC_CURL_BUILD_ROOT.getParent());
        ProcessBuilder processBuilder = new ProcessBuilder(
                Arrays.asList("bash", MAC_CURL_BUILD_SCRIPT.toString(), DEFAULT_MAC_CURL_BUILD_ROOT.toAbsolutePath().toString())
        );
        processBuilder.inheritIO();
        processBuilder.directory(new File("."));

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if(exitCode != 0) {
                throw new IOException("macOS libcurl build script failed with exit code " + exitCode);
            }
        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while building the macOS libcurl runtime.", e);
        }
    }

    private static void packageConfiguredCurlRuntime(String configuredPath, String outputName, String platformName) throws IOException {
        if(configuredPath == null) {
            return;
        }

        Path sourcePath = Path.of(configuredPath);
        if(!Files.isRegularFile(sourcePath)) {
            throw new IllegalArgumentException("Configured libcurl runtime was not found: " + configuredPath);
        }

        Path releaseDirectory = RELEASE_PATH.toPath();
        Files.createDirectories(releaseDirectory);
        Path targetPath = releaseDirectory.resolve(outputName);
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Packaged " + platformName + " libcurl runtime: " + sourcePath + " -> " + targetPath);
    }

    private static String resolveConfiguredPath(String propertyName, String envName) {
        String configuredProperty = System.getProperty(propertyName);
        if(configuredProperty != null && !configuredProperty.isBlank()) {
            return configuredProperty.trim();
        }
        String configuredEnv = System.getenv(envName);
        if(configuredEnv != null && !configuredEnv.isBlank()) {
            return configuredEnv.trim();
        }
        return null;
    }

    private static boolean isLinuxHost() {
        String osName = System.getProperty("os.name");
        return osName != null && osName.toLowerCase(Locale.ROOT).contains("linux");
    }

    private static boolean isMacHost() {
        String osName = System.getProperty("os.name");
        return osName != null && osName.toLowerCase(Locale.ROOT).contains("mac");
    }

    private static class BuildOptions {

        private final TeaGLFWBackend.NativeBuildType buildType;
        private final BuildAction action;
        private final boolean consoleLog;

        private BuildOptions(TeaGLFWBackend.NativeBuildType buildType, BuildAction action, boolean consoleLog) {
            this.buildType = buildType;
            this.action = action;
            this.consoleLog = consoleLog;
        }

        private static BuildOptions fromArgs(String[] args) {
            if(args.length > 3) {
                throw new IllegalArgumentException("Expected arguments: [Debug|Release] [generate|build|run] [console]");
            }

            TeaGLFWBackend.NativeBuildType buildType = args.length > 0
                    ? parseBuildType(args[0])
                    : TeaGLFWBackend.NativeBuildType.DEBUG;
            BuildAction action = args.length > 1
                    ? BuildAction.fromArg(args[1])
                    : BuildAction.GENERATE;
            boolean consoleLog = args.length > 2 && parseConsoleArg(args[2]);

            if(consoleLog && action != BuildAction.RUN) {
                throw new IllegalArgumentException("Console logging requires the run argument");
            }
            if(buildType == TeaGLFWBackend.NativeBuildType.DEBUG && action == BuildAction.RUN) {
                consoleLog = true;
            }
            return new BuildOptions(buildType, action, consoleLog);
        }

        private static TeaGLFWBackend.NativeBuildType parseBuildType(String arg) {
            String normalized = normalize(arg);
            if(normalized.equals("debug") || normalized.equals("--debug")) {
                return TeaGLFWBackend.NativeBuildType.DEBUG;
            }
            if(normalized.equals("release") || normalized.equals("--release")) {
                return TeaGLFWBackend.NativeBuildType.RELEASE;
            }
            throw new IllegalArgumentException("Unsupported GLFW build type argument: " + arg);
        }

        private static boolean parseConsoleArg(String arg) {
            String normalized = normalize(arg);
            if(normalized.equals("console") || normalized.equals("--console")) {
                return true;
            }
            throw new IllegalArgumentException("Unsupported GLFW console argument: " + arg);
        }

        private static String normalize(String arg) {
            return arg.trim().toLowerCase(Locale.ROOT);
        }

        private boolean shouldBuildExecutable() {
            return action.buildExecutable;
        }

        private boolean shouldRunExecutable() {
            return action.runExecutable;
        }
    }

    private enum BuildAction {
        GENERATE(false, false),
        BUILD(true, false),
        RUN(true, true);

        private final boolean buildExecutable;
        private final boolean runExecutable;

        BuildAction(boolean buildExecutable, boolean runExecutable) {
            this.buildExecutable = buildExecutable;
            this.runExecutable = runExecutable;
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
            throw new IllegalArgumentException("Unsupported GLFW action argument: " + arg);
        }
    }
}
