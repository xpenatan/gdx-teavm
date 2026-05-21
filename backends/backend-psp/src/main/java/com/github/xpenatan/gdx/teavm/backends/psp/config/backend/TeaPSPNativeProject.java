package com.github.xpenatan.gdx.teavm.backends.psp.config.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;

public class TeaPSPNativeProject {
    private final ClassLoader classLoader;
    private final File buildRoot;
    private final File generatedSources;

    public TeaPSPNativeProject(ClassLoader classLoader, File buildRoot, File generatedSources) {
        this.classLoader = classLoader;
        this.buildRoot = buildRoot;
        this.generatedSources = generatedSources;
    }

    public void write(String projectName, boolean debugMemory) throws IOException {
        ensureDirectory(buildRoot, "PSP output root");
        ensureDirectory(generatedSources, "PSP generated sources");

        copyAppInclude(projectName);
        writeCMakeLists(projectName, debugMemory);
        writeBuildScript("build.bat", projectName, false);
        writeBuildScript("build.sh", projectName, true);
    }

    public void executeBuildScript() {
        ProcessBuilder processBuilder;
        if(isWindows()) {
            processBuilder = new ProcessBuilder("cmd", "/c", new File(buildRoot, "build.bat").getAbsolutePath());
        }
        else {
            processBuilder = new ProcessBuilder("bash", new File(buildRoot, "build.sh").getAbsolutePath());
        }
        processBuilder.directory(buildRoot);
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            try(BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            int exitCode = process.waitFor();
            if(exitCode != 0) {
                throw new RuntimeException("PSP native build failed with exit code " + exitCode);
            }
        } catch(Exception e) {
            throw new RuntimeException("PSP native build failed", e);
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

    private void copyAppInclude(String projectName) throws IOException {
        try(var input = classLoader.getResourceAsStream("app_include.c")) {
            if(input == null) {
                throw new IOException("app_include.c not found in resources");
            }
            String content = new String(input.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("${PROJECT_NAME}", projectName);
            Files.writeString(new File(generatedSources, "app_include.c").toPath(), content, StandardCharsets.UTF_8);
        }
    }

    private void writeCMakeLists(String projectName, boolean debugMemory) throws IOException {
        try(var input = classLoader.getResourceAsStream("CMakeLists.txt")) {
            if(input == null) {
                throw new IOException("CMakeLists.txt template not found in resources");
            }
            String content = new String(input.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("${PROJECT_NAME}", projectName);
            if(debugMemory) {
                content = content.replace(
                        "include_directories(\"${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/psp\")",
                        "include_directories(\"${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/psp\")\n"
                                + "add_definitions(-DPSP_DEBUG_MEMORY)");
            }
            Files.writeString(new File(buildRoot, "CMakeLists.txt").toPath(), content, StandardCharsets.UTF_8);
        }
    }

    private void writeBuildScript(String scriptName, String projectName, boolean executable) throws IOException {
        try(var input = classLoader.getResourceAsStream(scriptName)) {
            if(input == null) {
                throw new IOException(scriptName + " template not found in resources");
            }
            String content = new String(input.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("${PROJECT_NAME}", projectName);
            File script = new File(buildRoot, scriptName);
            Files.writeString(script.toPath(), content, StandardCharsets.UTF_8);
            if(executable) {
                script.setExecutable(true, false);
            }
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows");
    }
}
