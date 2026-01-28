package com.github.xpenatan.gdx.teavm.backends.psp.config.backend;

import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetsCopy;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaBackend;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaCompilerData;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.teavm.tooling.TeaVMTargetType;

public class TeaPSPBackend extends TeaBackend {

    public boolean shouldGenerateSource = true;
    public boolean autoExecuteBuild = true;
    public boolean debugMemory;
    public List<String> additionalSourcePaths = new ArrayList<>();

    protected String buildRootPath;
    protected String generatedSources;
    protected String externalSources;

    @Override
    protected void initializeTeavmTool(TeaCompilerData data) {
        data.minHeapSize = 2 * (1 << 20);
        data.maxHeapSize = 8 * (1 << 20);
        data.minDirectBuffersSize = 2 * (1 << 20);
        data.maxDirectBuffersSize = 12 * (1 << 20);
        super.initializeTeavmTool(data);
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
        try {
            Files.createDirectories(Paths.get(generatedSources));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directories", e);
        }
        tool.setTargetDirectory(new File(generatedSources));
    }

    @Override
    protected void build(TeaCompilerData data) {
        if(shouldGenerateSource) {
            super.build(data);
        }

        // Copy app_include.c from resources
        String appIncludePath = generatedSources + "/app_include.c";
        String projectName = data.outputName;
        try (var input = getClass().getClassLoader().getResourceAsStream("app_include.c")) {
            if (input == null) {
                throw new RuntimeException("app_include.c not found in resources");
            }
            String template = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            template = template.replace("${PROJECT_NAME}", projectName);
            Files.write(Paths.get(appIncludePath), template.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy app_include.c", e);
        }

        generateCMakeLists(data);
        generateBat(data);

        if (autoExecuteBuild) {
            executeBuildScript();
        }
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
            if (debugMemory) {
                template = template.replace("include_directories(\"${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/psp\")", "include_directories(\"${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/psp\")\nadd_definitions(-DPSP_DEBUG_MEMORY)");
            }

            Files.write(Paths.get(cmakePath), template.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup CMakeLists.txt", e);
        }
    }

    private void generateBat(TeaCompilerData data) {
        String projectName = data.outputName;

        // Generate build.bat
        String batPath = buildRootPath + "/build.bat";
        try (var input = getClass().getClassLoader().getResourceAsStream("build.bat")) {
            if (input == null) {
                throw new RuntimeException("build.bat template not found in resources");
            }
            String template = new String(input.readAllBytes(), StandardCharsets.UTF_8);

            // Replace placeholders in template
            template = template.replace("${PROJECT_NAME}", projectName);

            Files.write(Paths.get(batPath), template.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup build.bat", e);
        }

        // Generate build.sh
        String shPath = buildRootPath + "/build.sh";
        try (var input = getClass().getClassLoader().getResourceAsStream("build.sh")) {
            if (input == null) {
                throw new RuntimeException("build.sh template not found in resources");
            }
            String template = new String(input.readAllBytes(), StandardCharsets.UTF_8);

            // Replace placeholders in template
            template = template.replace("${PROJECT_NAME}", projectName);

            Files.write(Paths.get(shPath), template.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup build.sh", e);
        }
    }

    private void executeBuildScript() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            ProcessBuilder pb;
            if (os.contains("windows")) {
                String batPath = buildRootPath + "/build.bat";
                pb = new ProcessBuilder("cmd", "/c", batPath);
            } else {
                String shPath = buildRootPath + "/build.sh";
                pb = new ProcessBuilder("bash", shPath);
            }
            pb.directory(new File(buildRootPath));
            pb.redirectErrorStream(true); // Merge stderr into stdout for ordered output
            Process p = pb.start();

            // Read merged output
            try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = p.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Build script execution failed with exit code " + exitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute build script", e);
        }
    }

    @Override
    protected void copyAssets(TeaCompilerData data) {
        super.copyAssets(data);
        FileHandle outputFolder = new FileHandle(buildRootPath + "/c");
        AssetsCopy.copyResources(classLoader, cppFiles, null, outputFolder);
    }
}
