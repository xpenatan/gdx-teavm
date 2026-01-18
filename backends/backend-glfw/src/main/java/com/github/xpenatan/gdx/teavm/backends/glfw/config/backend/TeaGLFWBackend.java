package com.github.xpenatan.gdx.teavm.backends.glfw.config.backend;

import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetsCopy;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaBackend;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaCompilerData;
import java.io.File;
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
//        cTarget.setLineNumbersGenerated(true);
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

        generateCMakeLists(data);
    }

    private void generateCMakeLists(TeaCompilerData data) {
        String cmakePath = buildRootPath + "/CMakeLists.txt";
        String projectName = data.outputName;
        String generatedSourcesPath = generatedSources;
        String externalCppPath = externalSources;
        String glfwPath = externalCppPath + "/glfw";
        String stbPath = externalCppPath + "/stb";
        String glewPath = externalCppPath + "/glew-2.3.0";
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
        cmakeContent.append("set(CMAKE_MSVC_RUNTIME_LIBRARY \"MultiThreaded$<IF:$<CONFIG:Debug>,Debug,>\")\n");
        cmakeContent.append("add_definitions(-DGLEW_STATIC)\n");
        cmakeContent.append("include_directories(\"").append(generatedSourcesPath).append("\")\n");
        cmakeContent.append("include_directories(\"").append(glfwPath).append("/include\")\n");
        cmakeContent.append("include_directories(\"").append(stbPath).append("/include\")\n");
        cmakeContent.append("include_directories(\"").append(glewPath).append("/include\")\n");
        cmakeContent.append("link_directories(\"").append(glfwPath).append("/lib-vc2022\")\n");
        cmakeContent.append("link_directories(\"").append(glewPath).append("/lib/Release/x64\")\n");
        cmakeContent.append("set(SOURCES \"").append(externalCppPath).append("/app_include.c\")\n");
        cmakeContent.append("add_executable(").append(projectName).append(" ${SOURCES})\n");
        cmakeContent.append("set_target_properties(").append(projectName).append(" PROPERTIES OUTPUT_NAME \"").append(projectName).append("_$<IF:$<CONFIG:Debug>,debug,release>\")\n");
        cmakeContent.append("set_target_properties(").append(projectName).append(" PROPERTIES VS_DEBUGGER_WORKING_DIRECTORY \"").append(releasePathStr).append("\")\n");
        cmakeContent.append("target_link_libraries(").append(projectName).append(" glfw3 glew32s opengl32.lib kernel32.lib user32.lib gdi32.lib winspool.lib shell32.lib ole32.lib oleaut32.lib uuid.lib comdlg32.lib advapi32.lib)\n");

        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(cmakePath), cmakeContent.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void copyAssets(TeaCompilerData data) {
        super.copyAssets(data);
        FileHandle outputFolder = new FileHandle(buildRootPath + "/c");
        AssetsCopy.copyResources(classLoader, cppFiles, null, outputFolder);
    }
}
