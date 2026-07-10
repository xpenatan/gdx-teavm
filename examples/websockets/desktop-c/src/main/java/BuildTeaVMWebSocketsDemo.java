import com.github.xpenatan.gdx.teavm.backends.glfw.config.backend.TeaGLFWBackend;
import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

    public static void main(String[] args) throws IOException {
        BuildOptions buildOptions = BuildOptions.fromArgs(args);
        packageLinuxCurlRuntime();
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

    private static void packageLinuxCurlRuntime() throws IOException {
        if(!isLinuxHost()) {
            return;
        }

        String configuredPath = resolveLinuxCurlPath();
        if(configuredPath == null) {
            return;
        }

        Path sourcePath = Path.of(configuredPath);
        if(!Files.isRegularFile(sourcePath)) {
            throw new IllegalArgumentException("Configured Linux libcurl runtime was not found: " + configuredPath);
        }

        Path releaseDirectory = RELEASE_PATH.toPath();
        Files.createDirectories(releaseDirectory);
        Path targetPath = releaseDirectory.resolve(LINUX_CURL_OUTPUT_NAME);
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Packaged Linux libcurl runtime: " + sourcePath + " -> " + targetPath);
    }

    private static String resolveLinuxCurlPath() {
        String configuredProperty = System.getProperty(LINUX_CURL_PROPERTY);
        if(configuredProperty != null && !configuredProperty.isBlank()) {
            return configuredProperty.trim();
        }
        String configuredEnv = System.getenv(LINUX_CURL_ENV);
        if(configuredEnv != null && !configuredEnv.isBlank()) {
            return configuredEnv.trim();
        }
        return null;
    }

    private static boolean isLinuxHost() {
        String osName = System.getProperty("os.name");
        return osName != null && osName.toLowerCase(Locale.ROOT).contains("linux");
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
