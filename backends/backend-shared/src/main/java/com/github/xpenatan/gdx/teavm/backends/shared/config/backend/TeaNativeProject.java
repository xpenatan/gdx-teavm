package com.github.xpenatan.gdx.teavm.backends.shared.config.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Map;

public abstract class TeaNativeProject {
    protected final ClassLoader classLoader;
    protected final File buildRoot;
    protected final File generatedSources;

    protected TeaNativeProject(ClassLoader classLoader, File buildRoot, File generatedSources) {
        this.classLoader = classLoader;
        this.buildRoot = buildRoot;
        this.generatedSources = generatedSources;
    }

    protected void ensureDirectory(File file, String name) {
        if(file == null) {
            throw new IllegalStateException(name + " was not configured");
        }
        if(!file.exists() && !file.mkdirs()) {
            throw new IllegalStateException("Unable to create " + name + ": " + file.getAbsolutePath());
        }
    }

    protected void copyResource(String resourceName, File outputFile) throws IOException {
        writeTemplate(resourceName, outputFile, Map.of(), false);
    }

    protected void writeTemplate(
            String resourceName,
            File outputFile,
            Map<String, String> replacements,
            boolean executable
    ) throws IOException {
        try(var input = classLoader.getResourceAsStream(resourceName)) {
            if(input == null) {
                throw new IOException(resourceName + " template not found in resources");
            }
            String content = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            for(Map.Entry<String, String> entry : replacements.entrySet()) {
                content = content.replace(entry.getKey(), entry.getValue());
            }
            File parentFile = outputFile.getParentFile();
            if(parentFile != null) {
                ensureDirectory(parentFile, "output directory");
            }
            Files.writeString(outputFile.toPath(), content, StandardCharsets.UTF_8);
            if(executable) {
                outputFile.setExecutable(true, false);
            }
        }
    }

    protected void executeBuildScript(String backendName, String windowsScriptName, String unixScriptName) {
        File scriptFile = new File(buildRoot, isWindows() ? windowsScriptName : unixScriptName);
        ProcessBuilder processBuilder;
        if(isWindows()) {
            processBuilder = new ProcessBuilder("cmd", "/c", scriptFile.getAbsolutePath());
        }
        else {
            processBuilder = new ProcessBuilder("sh", scriptFile.getAbsolutePath());
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
                throw new RuntimeException(backendName + " native build failed with exit code " + exitCode);
            }
        } catch(Exception e) {
            throw new RuntimeException(backendName + " native build failed", e);
        }
    }

    protected static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows");
    }
}
