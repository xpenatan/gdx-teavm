package com.github.xpenatan.gdx.teavm.backends.glfw.config.backend;

import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetsCopy;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaBackend;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaCompilerData;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.teavm.backend.c.CustomCTarget;
import org.teavm.backend.c.generate.CNameProvider;
import org.teavm.backend.c.generate.ShorteningFileNameProvider;
import org.teavm.backend.c.generate.SimpleFileNameProvider;
import org.teavm.cache.AlwaysStaleCacheStatus;
import org.teavm.cache.EmptyProgramCache;
import org.teavm.model.PreOptimizingClassHolderSource;
import org.teavm.model.ReferenceCache;
import org.teavm.parsing.ClasspathClassHolderSource;
import org.teavm.parsing.ClasspathResourceProvider;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.vm.BuildTarget;
import org.teavm.vm.DirectoryBuildTarget;
import org.teavm.vm.TeaVM;
import org.teavm.vm.TeaVMBuilder;

public class TeaGLFWBackend extends TeaBackend {

    public boolean shouldGenerateSource = true;
    public boolean shouldUseCustomGeneration = true;

    protected String buildRootPath;
    protected String generatedSources;
    protected String externalSources;

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
        tool.setTargetDirectory(new File(generatedSources));
    }

    private void generate(TeaCompilerData data) {
        ClasspathResourceProvider classpathResourceProvider = new ClasspathResourceProvider(classLoader);

        CustomCTarget cTarget = new CustomCTarget(new CNameProvider());
        cTarget.setMinHeapSize(data.minHeapSize);
        cTarget.setMaxHeapSize(data.maxHeapSize);
        cTarget.setLineNumbersGenerated(true);
        cTarget.setHeapDump(true);
        cTarget.setObfuscated(data.obfuscated);
        cTarget.setFileNames(new ShorteningFileNameProvider(new SimpleFileNameProvider()));
//        cTarget.setFileNames(new SimpleFileNameProvider());
        ReferenceCache referenceCache = new ReferenceCache();
        TeaVMBuilder vmBuilder = new TeaVMBuilder(cTarget);
        vmBuilder.setReferenceCache(referenceCache);

        PreOptimizingClassHolderSource preOptimizingClassHolderSource = new PreOptimizingClassHolderSource(new ClasspathClassHolderSource(classpathResourceProvider, referenceCache));
        vmBuilder.setClassLoader(classLoader);
        vmBuilder.setClassSource(preOptimizingClassHolderSource);

        Properties properties = new Properties();

        TeaVM vm = vmBuilder.build();
        vm.setProgressListener(obtainProgressListener());
        vm.setProperties(properties);
        vm.setProgramCache(EmptyProgramCache.INSTANCE);
        vm.setCacheStatus(AlwaysStaleCacheStatus.INSTANCE);
        vm.setOptimizationLevel(data.optimizationLevel);
        vm.installPlugins();
        vm.setEntryPoint(data.mainClass);
        vm.setEntryPointName("main");
        for(String className : data.reflectionClasses) {
            vm.preserveType(className);
        }
        File targetDirectory = tool.getTargetDirectory();
        if(!targetDirectory.exists() && !targetDirectory.mkdirs()) {
            System.err.println("Target directory could not be created");
            System.exit(-1);
        }

        BuildTarget buildTarget = new DirectoryBuildTarget(targetDirectory);
        vm.build(buildTarget, data.outputName);

        logBuild(vm.getProblemProvider(), vm.getClasses(), vm.getDependencyInfo().getCallGraph());
    }

    @Override
    protected void build(TeaCompilerData data) {
        if(shouldGenerateSource) {
            if(shouldUseCustomGeneration) {
                generate(data);
            }
            else {
                super.build(data);
            }
        }

        try {
            Files.writeString(Path.of(generatedSources, "app_include.c"),
                    "#include <GL/glew.h>\n" +
                    "#define STB_IMAGE_IMPLEMENTATION\n" +
                    "#include \"stb_image.h\"\n" +
                    "#include \"all.c\"");
        } catch (IOException e) {
            throw new RuntimeException("Build Failed", e);
        }

        generateCMakeLists(data);
    }

    private void generateCMakeLists(TeaCompilerData data) {
        String cmakePath = buildRootPath + "/CMakeLists.txt";
        String projectName = data.outputName;
        String glfwPath = externalSources + "/glfw";
        String stbPath = externalSources + "/stb";
        String glewPath = externalSources + "/glew-2.3.0";
        String releasePathStr = releasePath.path();

        StringBuilder cmakeContent = new StringBuilder();
        cmakeContent.append("cmake_minimum_required(VERSION 3.10)\n");
        cmakeContent.append("project(").append(projectName).append(" C)\n");
        cmakeContent.append("set(CMAKE_C_STANDARD 11)\n");
        cmakeContent.append("if(CMAKE_CONFIGURATION_TYPES)\n");
        cmakeContent.append("    foreach(config ${CMAKE_CONFIGURATION_TYPES})\n");
        cmakeContent.append("        string(TOUPPER ${config} config_upper)\n");
        cmakeContent.append("        set(CMAKE_RUNTIME_OUTPUT_DIRECTORY_${config_upper} \"").append(releasePathStr).append("\")\n");
        cmakeContent.append("    endforeach()\n");
        cmakeContent.append("else()\n");
        cmakeContent.append("    set(CMAKE_RUNTIME_OUTPUT_DIRECTORY \"").append(releasePathStr).append("\")\n");
        cmakeContent.append("endif()\n");
        cmakeContent.append("if(NOT CMAKE_BUILD_TYPE)\n");
        cmakeContent.append("    set(CMAKE_BUILD_TYPE Release)\n");
        cmakeContent.append("endif()\n");
        cmakeContent.append("set(CMAKE_C_FLAGS_DEBUG \"${CMAKE_C_FLAGS_DEBUG} -g -std=c11\")\n");
        cmakeContent.append("set(CMAKE_C_FLAGS_RELEASE \"${CMAKE_C_FLAGS_RELEASE} -O3 -std=c11\")\n");
        cmakeContent.append("add_definitions(-DGLEW_STATIC)\n");
        cmakeContent.append("include_directories(\"").append(glfwPath).append("/include\")\n");
        cmakeContent.append("include_directories(\"").append(stbPath).append("/include\")\n");
        cmakeContent.append("include_directories(\"").append(glewPath).append("/include\")\n");
        cmakeContent.append("link_directories(\"").append(glfwPath).append("/lib-vc2022\")\n");
        cmakeContent.append("link_directories(\"").append(glewPath).append("/lib/Release/x64\")\n");
        cmakeContent.append("set(SOURCES \"").append(generatedSources).append("/app_include.c\")\n");
        cmakeContent.append("add_executable(").append(projectName).append(" ${SOURCES})\n");
        cmakeContent.append("set_target_properties(").append(projectName).append(" PROPERTIES OUTPUT_NAME \"").append(projectName).append("_$<IF:$<CONFIG:Debug>,debug,release>\")\n");
        cmakeContent.append("set_target_properties(").append(projectName).append(" PROPERTIES VS_DEBUGGER_WORKING_DIRECTORY \"").append(releasePathStr).append("\")\n");
        cmakeContent.append("target_link_libraries(").append(projectName).append(" glfw3 opengl32 glew32s)\n");

        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(cmakePath), cmakeContent.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Generate bat files for building the project
        generateBuildBatFiles(projectName, releasePathStr);
    }

    private void generateBuildBatFiles(String projectName, String releasePathStr) {
        String releaseBatPath = buildRootPath + "/app_release.bat";
        String debugBatPath = buildRootPath + "/app_debug.bat";

        // Ensure the dist directory exists
        File distDir = new File(buildRootPath);
        if (!distDir.exists() && !distDir.mkdirs()) {
            System.err.println("Dist directory could not be created");
            return;
        }

        // Generate app_release.bat
        String releaseBatContent = generateBatContent(projectName, "Release");
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(releaseBatPath), releaseBatContent.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Generate app_debug.bat
        String debugBatContent = generateBatContent(projectName, "Debug");
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(debugBatPath), debugBatContent.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateBatContent(String projectName, String buildConfig) {
        StringBuilder batContent = new StringBuilder();
        batContent.append("@echo off\n");
        batContent.append("setlocal enabledelayedexpansion\n");
        batContent.append("\n");
        batContent.append(":: Build ").append(buildConfig).append(" Configuration\n");
        batContent.append("set NAME=").append(projectName).append("\n");
        batContent.append("set BUILD_CONFIG=").append(buildConfig).append("\n");
        batContent.append("\n");
        batContent.append(":: Change to the c directory where CMakeLists.txt is located\n");
        batContent.append("cd /d \"%~dp0\"\n");
        batContent.append("if errorlevel 1 (\n");
        batContent.append("    echo Failed to change directory\n");
        batContent.append("    exit /b 1\n");
        batContent.append(")\n");
        batContent.append("\n");
        batContent.append(":: Use CMake to create build files\n");
        batContent.append("echo Generating CMake build files for !BUILD_CONFIG!...\n");
        batContent.append("\n");
        batContent.append(":: Try to find cmake\n");
        batContent.append("set CMAKE_PATH=cmake\n");
        batContent.append("where cmake >nul 2>&1\n");
        batContent.append("if errorlevel 1 (\n");
        batContent.append("    echo cmake not found in PATH, trying Visual Studio paths...\n");
        batContent.append("    if exist \"C:\\Program Files\\Microsoft Visual Studio\\18\\Community\\Common7\\IDE\\CommonExtensions\\Microsoft\\CMake\\CMake\\bin\\cmake.exe\" (\n");
        batContent.append("        set \"CMAKE_PATH=C:\\Program Files\\Microsoft Visual Studio\\18\\Community\\Common7\\IDE\\CommonExtensions\\Microsoft\\CMake\\CMake\\bin\\cmake.exe\"\n");
        batContent.append("    ) else (\n");
        batContent.append("        if exist \"C:\\Program Files (x86)\\Microsoft Visual Studio\\18\\Community\\Common7\\IDE\\CommonExtensions\\Microsoft\\CMake\\CMake\\bin\\cmake.exe\" (\n");
        batContent.append("            set \"CMAKE_PATH=C:\\Program Files (x86)\\Microsoft Visual Studio\\18\\Community\\Common7\\IDE\\CommonExtensions\\Microsoft\\CMake\\CMake\\bin\\cmake.exe\"\n");
        batContent.append("        ) else (\n");
        batContent.append("            echo cmake could not be found. Please install CMake or add it to PATH.\n");
        batContent.append("            exit /b 1\n");
        batContent.append("        )\n");
        batContent.append("    )\n");
        batContent.append(")\n");
        batContent.append("\n");
        batContent.append("!CMAKE_PATH! -S . -B build\\cmake\n");
        batContent.append("if errorlevel 1 (\n");
        batContent.append("    echo CMake build generation failed\n");
        batContent.append("    exit /b 1\n");
        batContent.append(")\n");
        batContent.append("\n");
        batContent.append("echo Changing directory to build\\cmake...\n");
        batContent.append("cd build\\cmake\n");
        batContent.append("if errorlevel 1 (\n");
        batContent.append("    echo Failed to change directory\n");
        batContent.append("    exit /b 1\n");
        batContent.append(")\n");
        batContent.append("\n");
        batContent.append(":: Initialize MSBuild path variable\n");
        batContent.append("set MSBUILD_PATH=\n");
        batContent.append("set VS_EDITION=\n");
        batContent.append("\n");
        batContent.append(":: Search Visual Studio installations (two levels deep)\n");
        batContent.append("for %%R in (\"%ProgramFiles%\" \"%ProgramFiles(x86)%\") do (\n");
        batContent.append("    for /d %%Y in (\"%%~R\\Microsoft Visual Studio\\*\") do (\n");
        batContent.append("        for /d %%E in (\"%%~Y\\*\") do (\n");
        batContent.append("            set \"EDITION_DIR=%%~nxE\"\n");
        batContent.append("            set \"CHECK_PATH=%%~fE\\MSBuild\\Current\\Bin\"\n");
        batContent.append("\n");
        batContent.append("            if exist \"!CHECK_PATH!\\MSBuild.exe\" (\n");
        batContent.append("                set \"MSBUILD_PATH=!CHECK_PATH!\\MSBuild.exe\"\n");
        batContent.append("                set \"VS_EDITION=!EDITION_DIR!\"\n");
        batContent.append("                goto :found\n");
        batContent.append("            )\n");
        batContent.append("            if exist \"!CHECK_PATH!\\amd64\\MSBuild.exe\" (\n");
        batContent.append("                set \"MSBUILD_PATH=!CHECK_PATH!\\amd64\\MSBuild.exe\"\n");
        batContent.append("                set \"VS_EDITION=!EDITION_DIR!\"\n");
        batContent.append("                goto :found\n");
        batContent.append("            )\n");
        batContent.append("            if exist \"!CHECK_PATH!\\x86\\MSBuild.exe\" (\n");
        batContent.append("                set \"MSBUILD_PATH=!CHECK_PATH!\\x86\\MSBuild.exe\"\n");
        batContent.append("                set \"VS_EDITION=!EDITION_DIR!\"\n");
        batContent.append("                goto :found\n");
        batContent.append("            )\n");
        batContent.append("        )\n");
        batContent.append("    )\n");
        batContent.append(")\n");
        batContent.append("\n");
        batContent.append(":: If MSBuild path is not found, check in the registry for installations\n");
        batContent.append("echo MSBuild not found in default paths. Checking registry for installation...\n");
        batContent.append("\n");
        batContent.append("for /f \"tokens=2* delims=    \" %%A in ('reg query \"HKCU\\Software\\Microsoft\\VisualStudio\" /s /f \"MSBuild.exe\" 2^>nul') do (\n");
        batContent.append("    if \"%%B\" neq \"\" (\n");
        batContent.append("        set MSBUILD_PATH=%%B\n");
        batContent.append("        set VS_EDITION=Unknown\n");
        batContent.append("        goto :found\n");
        batContent.append("    )\n");
        batContent.append(")\n");
        batContent.append("\n");
        batContent.append(":: If MSBuild is still not found, display an error\n");
        batContent.append("echo Error: MSBuild not found on your system.\n");
        batContent.append("exit /b 1\n");
        batContent.append("\n");
        batContent.append(":found\n");
        batContent.append(":: Display the found MSBuild path and edition\n");
        batContent.append("echo MSBuild found at: %MSBUILD_PATH%\n");
        batContent.append("echo Visual Studio Edition Detected: %VS_EDITION%\n");
        batContent.append("\n");
        batContent.append("if /i \"%VS_EDITION%\"==\"BuildTools\" (\n");
        batContent.append("    echo Error: Visual Studio Build Tools edition is not supported.\n");
        batContent.append("    exit /b 1\n");
        batContent.append(")\n");
        batContent.append("\n");
        batContent.append(":: Set the solution/project file and build configuration\n");
        batContent.append("set SOLUTION_FILE=\".\\!NAME!.slnx\"\n");
        batContent.append("\n");
        batContent.append(":: Run MSBuild with the specified configuration\n");
        batContent.append("echo Running MSBuild for !BUILD_CONFIG! configuration...\n");
        batContent.append("\"!MSBUILD_PATH!\" !SOLUTION_FILE! /p:Configuration=!BUILD_CONFIG! /p:Platform=x64\n");
        batContent.append("\n");
        batContent.append("if errorlevel 1 (\n");
        batContent.append("    echo Failed to build solution\n");
        batContent.append("    exit /b 1\n");
        batContent.append(")\n");
        batContent.append("\n");
        batContent.append("echo Build completed successfully for !BUILD_CONFIG! configuration.\n");
        batContent.append("cd /d \"%~dp0\"\n");
        batContent.append("endlocal\n");

        return batContent.toString();
    }

    @Override
    protected void copyAssets(TeaCompilerData data) {
        super.copyAssets(data);
        FileHandle outputFolder = new FileHandle(buildRootPath + "/c");
        AssetsCopy.copyResources(classLoader, cppFiles, null, outputFolder);
    }
}
