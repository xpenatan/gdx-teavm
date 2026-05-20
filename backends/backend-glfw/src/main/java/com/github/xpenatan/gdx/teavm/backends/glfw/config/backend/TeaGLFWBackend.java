package com.github.xpenatan.gdx.teavm.backends.glfw.config.backend;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Files.FileType;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetsCopy;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaBackend;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaCompilerData;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.teavm.tooling.TeaVMTargetType;

public class TeaGLFWBackend extends TeaBackend {

    private static final String BUILD_SCRIPT_TEMPLATE_ROOT = "templates/glfw/";

    public enum NativeBuildType {
        DEBUG("Debug", "debug"),
        RELEASE("Release", "release");

        private final String cmakeConfig;
        private final String outputSuffix;

        NativeBuildType(String cmakeConfig, String outputSuffix) {
            this.cmakeConfig = cmakeConfig;
            this.outputSuffix = outputSuffix;
        }

        public String getCmakeConfig() {
            return cmakeConfig;
        }

        public String getOutputSuffix() {
            return outputSuffix;
        }

        public static NativeBuildType fromString(String value) {
            if(value == null || value.isBlank()) {
                return DEBUG;
            }
            String normalized = value.trim().toLowerCase(Locale.ROOT);
            if(normalized.equals("debug")) {
                return DEBUG;
            }
            if(normalized.equals("release")) {
                return RELEASE;
            }
            throw new IllegalArgumentException("Unsupported GLFW native build type: " + value);
        }
    }

    public boolean shouldGenerateSource = true;
    public boolean buildExecutableAfterBuild;
    public boolean runExecutableAfterBuild;
    public boolean runExecutableWithConsoleLog;
    public List<String> additionalSourcePaths = new ArrayList<>();

    protected String buildRootPath;
    protected String generatedSources;
    protected String externalSources;
    protected NativeBuildType nativeBuildType = NativeBuildType.DEBUG;

    public TeaGLFWBackend setBuildType(NativeBuildType buildType) {
        if(buildType == null) {
            throw new IllegalArgumentException("buildType cannot be null");
        }
        nativeBuildType = buildType;
        return this;
    }

    public TeaGLFWBackend setBuildType(String buildType) {
        nativeBuildType = NativeBuildType.fromString(buildType);
        return this;
    }

    public TeaGLFWBackend setBuildExecutableAfterBuild(boolean buildExecutableAfterBuild) {
        this.buildExecutableAfterBuild = buildExecutableAfterBuild;
        return this;
    }

    public TeaGLFWBackend setRunExecutableAfterBuild(boolean runExecutableAfterBuild) {
        this.runExecutableAfterBuild = runExecutableAfterBuild;
        if(runExecutableAfterBuild) {
            this.buildExecutableAfterBuild = true;
        }
        return this;
    }

    public TeaGLFWBackend setRunExecutableWithConsoleLog(boolean runExecutableWithConsoleLog) {
        this.runExecutableWithConsoleLog = runExecutableWithConsoleLog;
        return this;
    }

    @Override
    protected void setup(TeaCompilerData data) {
        targetType = TeaVMTargetType.C;
        if(data.releasePath != null) {
            releasePath = new FileHandle(data.releasePath.getAbsolutePath().replace("\\", "/"));
        }
        else {
            releasePath = new FileHandle(new File(data.output, "c/release").getAbsolutePath().replace("\\", "/"));
        }
        buildRootPath = data.output.getAbsolutePath().replace("\\", "/");
        generatedSources = buildRootPath +  "/c/src";
        externalSources = buildRootPath +  "/c/external_cpp";
        additionalSourcePaths.add(externalSources + "/gdx");
        tool.setTargetDirectory(new File(generatedSources));
    }

    @Override
    protected void build(TeaCompilerData data) {
        if(shouldGenerateSource) {
            super.build(data);
        }

        // Copy app_include.c from resources
        String appIncludePath = generatedSources + "/app_include.c";
        try (var input = getClass().getClassLoader().getResourceAsStream("app_include.c")) {
            if (input == null) {
                throw new RuntimeException("app_include.c not found in resources");
            }
            String template = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            Files.write(Paths.get(appIncludePath), template.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy app_include.c", e);
        }

        generateCMakeLists(data);
    }

    private void generateCMakeLists(TeaCompilerData data) {
        String cmakePath = buildRootPath + "/CMakeLists.txt";
        String projectName = data.outputName;
        String releasePathStr = releasePath.path();

        // Copy the CMakeLists.txt template from resources
        try (var input = getClass().getClassLoader().getResourceAsStream("CMakeLists.txt")) {
            if (input == null) {
                throw new RuntimeException("CMakeLists.txt template in resources");
            }
            String template = new String(input.readAllBytes(), StandardCharsets.UTF_8);

            // Replace placeholders in template
            template = template.replace("${PROJECT_NAME}", projectName);
            template = template.replace("${RELEASE_PATH}", releasePathStr.replace("\\", "/"));

            Files.write(Paths.get(cmakePath), template.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup CMakeLists.txt", e);
        }

        // Generate scripts for building the project
        generateBuildScripts(projectName);

        if(buildExecutableAfterBuild || runExecutableAfterBuild) {
            executeBuildScript(nativeBuildType);
        }
        if(runExecutableAfterBuild) {
            runExecutable(data, nativeBuildType);
        }
    }

    private void generateBuildScripts(String projectName) {
        // Ensure the dist directory exists
        File distDir = new File(buildRootPath);
        if (!distDir.exists() && !distDir.mkdirs()) {
            throw new RuntimeException("Dist directory could not be created");
        }

        // Generate app_release.bat
        writeBuildScript("app_release.bat", projectName, "Release", false);

        // Generate app_debug.bat
        writeBuildScript("app_debug.bat", projectName, "Debug", false);

        // Generate app_release.sh
        writeBuildScript("app_release.sh", projectName, "Release", true);

        // Generate app_debug.sh
        writeBuildScript("app_debug.sh", projectName, "Debug", true);
    }

    private void writeBuildScript(String scriptName, String projectName, String buildConfig, boolean executable) {
        String path = buildRootPath + "/" + scriptName;
        String templatePath = BUILD_SCRIPT_TEMPLATE_ROOT + scriptName;
        try (var input = getClass().getClassLoader().getResourceAsStream(templatePath)) {
            if (input == null) {
                throw new RuntimeException(templatePath + " template not found in resources");
            }
            String template = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            String content = template
                    .replace("${PROJECT_NAME}", scriptName.endsWith(".sh") ? escapeShellValue(projectName) : projectName)
                    .replace("${BUILD_CONFIG}", buildConfig);

            Files.write(Paths.get(path), content.getBytes(StandardCharsets.UTF_8));
            if(executable) {
                new File(path).setExecutable(true, false);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup " + path, e);
        }
    }

    private String escapeShellValue(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("$", "\\$")
                .replace("`", "\\`");
    }

    private void executeBuildScript(NativeBuildType buildType) {
        File scriptFile = new File(buildRootPath, getBuildScriptName(buildType));
        if(!scriptFile.isFile()) {
            throw new RuntimeException("Expected GLFW build script was not generated: " + scriptFile.getAbsolutePath());
        }

        ProcessBuilder processBuilder;
        if(isWindows()) {
            processBuilder = new ProcessBuilder("cmd", "/c", scriptFile.getAbsolutePath());
        }
        else {
            processBuilder = new ProcessBuilder("bash", scriptFile.getAbsolutePath());
        }
        processBuilder.directory(new File(buildRootPath));
        executeProcess(processBuilder, "GLFW native build failed");
    }

    private void runExecutable(TeaCompilerData data, NativeBuildType buildType) {
        File executableFile = releasePath.child(getExecutableName(data.outputName, buildType)).file();
        if(!executableFile.isFile()) {
            throw new RuntimeException("Expected GLFW executable was not built: " + executableFile.getAbsolutePath());
        }

        if(runExecutableWithConsoleLog && isWindows()) {
            ProcessBuilder processBuilder = createWindowsConsoleProcess(executableFile);
            executeProcess(processBuilder, "GLFW executable failed");
        }
        else {
            ProcessBuilder processBuilder = new ProcessBuilder(executableFile.getAbsolutePath());
            processBuilder.directory(releasePath.file());
            executeProcess(processBuilder, "GLFW executable failed", runExecutableWithConsoleLog);
        }
    }

    private ProcessBuilder createWindowsConsoleProcess(File executableFile) {
        File consoleScript = writeWindowsConsoleRunScript(executableFile);
        String title = "GLFW " + executableFile.getName();
        return new ProcessBuilder("cmd", "/c", "start", title, "/wait", consoleScript.getAbsolutePath());
    }

    private File writeWindowsConsoleRunScript(File executableFile) {
        File scriptFile = new File(buildRootPath, removeExtension(executableFile.getName()) + "_console.bat");
        String templatePath = BUILD_SCRIPT_TEMPLATE_ROOT + "app_console.bat";
        try (var input = getClass().getClassLoader().getResourceAsStream(templatePath)) {
            if (input == null) {
                throw new RuntimeException(templatePath + " template not found in resources");
            }
            String template = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            String content = template
                    .replace("${WINDOW_TITLE}", escapeBatchValue("GLFW " + executableFile.getName()))
                    .replace("${WORKING_DIRECTORY}", escapeBatchValue(releasePath.file().getAbsolutePath()))
                    .replace("${EXECUTABLE_PATH}", escapeBatchValue(executableFile.getAbsolutePath()));
            Files.write(Paths.get(scriptFile.getAbsolutePath()), content.getBytes(StandardCharsets.UTF_8));
            return scriptFile;
        } catch(Exception e) {
            throw new RuntimeException("Failed to setup " + scriptFile.getAbsolutePath(), e);
        }
    }

    private String removeExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if(dotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, dotIndex);
    }

    private String escapeBatchValue(String value) {
        return value.replace("%", "%%");
    }

    private void executeProcess(ProcessBuilder processBuilder, String failureMessage) {
        executeProcess(processBuilder, failureMessage, false);
    }

    private void executeProcess(ProcessBuilder processBuilder, String failureMessage, boolean inheritIO) {
        processBuilder.redirectErrorStream(true);
        try {
            if(inheritIO) {
                processBuilder.inheritIO();
            }
            Process process = processBuilder.start();
            if(!inheritIO) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }
            }

            int exitCode = process.waitFor();
            if(exitCode != 0) {
                throw new RuntimeException(failureMessage + " with exit code " + exitCode);
            }
        } catch(Exception e) {
            throw new RuntimeException(failureMessage, e);
        }
    }

    private String getBuildScriptName(NativeBuildType buildType) {
        return "app_" + buildType.getOutputSuffix() + (isWindows() ? ".bat" : ".sh");
    }

    private String getExecutableName(String outputName, NativeBuildType buildType) {
        return outputName + "_" + buildType.getOutputSuffix() + (isWindows() ? ".exe" : "");
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows");
    }

    @Override
    protected void copyAssets(TeaCompilerData data) {
        super.copyAssets(data);
        FileHandle outputFolder = new FileHandle(buildRootPath + "/c");
        AssetsCopy.copyResources(classLoader, cppFiles, null, outputFolder, FileType.Classpath);
    }
}
