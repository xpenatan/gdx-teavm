package com.github.xpenatan.gdx.teavm.backends.glfw.config.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;

public class TeaGLFWNativeProject {
    private static final String BUILD_SCRIPT_TEMPLATE_ROOT = "templates/glfw/";

    private final ClassLoader classLoader;
    private final File buildRoot;
    private final File generatedSources;
    private final File releasePath;

    public TeaGLFWNativeProject(ClassLoader classLoader, File buildRoot, File generatedSources, File releasePath) {
        this.classLoader = classLoader;
        this.buildRoot = buildRoot;
        this.generatedSources = generatedSources;
        this.releasePath = releasePath;
    }

    public void write(String projectName) throws IOException {
        ensureDirectory(buildRoot, "GLFW output root");
        ensureDirectory(generatedSources, "GLFW generated sources");
        ensureDirectory(releasePath, "GLFW release path");

        copyAppInclude();
        writeCMakeLists(projectName);
        writeBuildScripts(projectName);
    }

    public void executeBuildScript(TeaGLFWBackend.NativeBuildType buildType) {
        File scriptFile = new File(buildRoot, getBuildScriptName(buildType));
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
        processBuilder.directory(buildRoot);
        executeProcess(processBuilder, "GLFW native build failed", false);
    }

    public void runExecutable(String projectName, TeaGLFWBackend.NativeBuildType buildType, boolean consoleLog) {
        File executableFile = new File(releasePath, getExecutableName(projectName, buildType));
        if(!executableFile.isFile()) {
            throw new RuntimeException("Expected GLFW executable was not built: " + executableFile.getAbsolutePath());
        }

        if(consoleLog && isWindows()) {
            ProcessBuilder processBuilder = createWindowsConsoleProcess(executableFile);
            executeProcess(processBuilder, "GLFW executable failed", false);
        }
        else {
            ProcessBuilder processBuilder = new ProcessBuilder(executableFile.getAbsolutePath());
            processBuilder.directory(releasePath);
            executeProcess(processBuilder, "GLFW executable failed", consoleLog);
        }
    }

    private void ensureDirectory(File file, String name) {
        if(file == null) {
            throw new IllegalStateException(name + " was not configured");
        }
        if(!file.exists() && !file.mkdirs()) {
            throw new IllegalStateException("Unable to create " + name + ": " + file.getAbsolutePath());
        }
    }

    private void copyAppInclude() throws IOException {
        try(var input = classLoader.getResourceAsStream("app_include.c")) {
            if(input == null) {
                throw new IOException("app_include.c not found in resources");
            }
            Files.writeString(new File(generatedSources, "app_include.c").toPath(),
                    new String(input.readAllBytes(), StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        }
    }

    private void writeCMakeLists(String projectName) throws IOException {
        try(var input = classLoader.getResourceAsStream("CMakeLists.txt")) {
            if(input == null) {
                throw new IOException("CMakeLists.txt template not found in resources");
            }
            String template = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            template = template.replace("${PROJECT_NAME}", projectName);
            template = template.replace("${RELEASE_PATH}", releasePath.getAbsolutePath().replace("\\", "/"));
            Files.writeString(new File(buildRoot, "CMakeLists.txt").toPath(), template, StandardCharsets.UTF_8);
        }
    }

    private void writeBuildScripts(String projectName) throws IOException {
        writeBuildScript("app_release.bat", projectName, "Release", false);
        writeBuildScript("app_debug.bat", projectName, "Debug", false);
        writeBuildScript("app_release.sh", projectName, "Release", true);
        writeBuildScript("app_debug.sh", projectName, "Debug", true);
    }

    private void writeBuildScript(String scriptName, String projectName, String buildConfig, boolean executable)
            throws IOException {
        String templatePath = BUILD_SCRIPT_TEMPLATE_ROOT + scriptName;
        try(var input = classLoader.getResourceAsStream(templatePath)) {
            if(input == null) {
                throw new IOException(templatePath + " template not found in resources");
            }
            String content = new String(input.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("${PROJECT_NAME}", scriptName.endsWith(".sh") ? escapeShellValue(projectName) : projectName)
                    .replace("${BUILD_CONFIG}", buildConfig);
            File script = new File(buildRoot, scriptName);
            Files.writeString(script.toPath(), content, StandardCharsets.UTF_8);
            if(executable) {
                script.setExecutable(true, false);
            }
        }
    }

    private ProcessBuilder createWindowsConsoleProcess(File executableFile) {
        File consoleScript = writeWindowsConsoleRunScript(executableFile);
        String title = "GLFW " + executableFile.getName();
        return new ProcessBuilder("cmd", "/c", "start", title, "/wait", consoleScript.getAbsolutePath());
    }

    private File writeWindowsConsoleRunScript(File executableFile) {
        File scriptFile = new File(buildRoot, removeExtension(executableFile.getName()) + "_console.bat");
        String templatePath = BUILD_SCRIPT_TEMPLATE_ROOT + "app_console.bat";
        try(var input = classLoader.getResourceAsStream(templatePath)) {
            if(input == null) {
                throw new RuntimeException(templatePath + " template not found in resources");
            }
            String content = new String(input.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("${WINDOW_TITLE}", escapeBatchValue("GLFW " + executableFile.getName()))
                    .replace("${WORKING_DIRECTORY}", escapeBatchValue(releasePath.getAbsolutePath()))
                    .replace("${EXECUTABLE_PATH}", escapeBatchValue(executableFile.getAbsolutePath()));
            Files.writeString(scriptFile.toPath(), content, StandardCharsets.UTF_8);
            return scriptFile;
        } catch(IOException e) {
            throw new RuntimeException("Failed to setup " + scriptFile.getAbsolutePath(), e);
        }
    }

    private void executeProcess(ProcessBuilder processBuilder, String failureMessage, boolean inheritIO) {
        processBuilder.redirectErrorStream(true);
        try {
            if(inheritIO) {
                processBuilder.inheritIO();
            }
            Process process = processBuilder.start();
            if(!inheritIO) {
                try(BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
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

    private String getBuildScriptName(TeaGLFWBackend.NativeBuildType buildType) {
        return "app_" + buildType.getOutputSuffix() + (isWindows() ? ".bat" : ".sh");
    }

    private String getExecutableName(String projectName, TeaGLFWBackend.NativeBuildType buildType) {
        return projectName + "_" + buildType.getOutputSuffix() + (isWindows() ? ".exe" : "");
    }

    private String removeExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if(dotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, dotIndex);
    }

    private String escapeShellValue(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("$", "\\$")
                .replace("`", "\\`");
    }

    private String escapeBatchValue(String value) {
        return value.replace("%", "%%");
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows");
    }
}
