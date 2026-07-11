package com.github.xpenatan.gdx.teavm.backends.glfw.config.backend;

import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetOutput;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetsCopy;
import com.github.xpenatan.gdx.teavm.backends.shared.config.backend.TeaBackend;
import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilderData;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.GdxTeaVMPluginConfig;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.teavm.tooling.TeaVMTargetType;

public class TeaGLFWBackend extends TeaBackend {

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
    private final LinkedHashMap<String, String> cmakeDefinitions = new LinkedHashMap<>();

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

    /**
     * Adds or replaces a CMake cache definition passed to the generated native configure scripts.
     */
    public TeaGLFWBackend cmakeDefinition(String name, String value) {
        String normalizedName = requireCMakeDefinitionName(name);
        if(value == null) {
            throw new IllegalArgumentException("CMake definition value cannot be null");
        }
        cmakeDefinitions.put(normalizedName, value);
        return this;
    }

    /**
     * Returns the configured CMake definitions in configure-command order.
     */
    public Map<String, String> getCMakeDefinitions() {
        return Collections.unmodifiableMap(cmakeDefinitions);
    }

    @Override
    protected void setup(TeaBuilderData data) {
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
        tool.getProperties().setProperty(GdxTeaVMPluginConfig.NATIVE_BACKEND, "glfw");
        tool.setTargetDirectory(new File(generatedSources));
    }

    @Override
    protected void build(TeaBuilderData data) {
        if(shouldGenerateSource) {
            super.build(data);
        }
        TeaGLFWNativeProject nativeProject = new TeaGLFWNativeProject(
                getClass().getClassLoader(),
                new File(buildRootPath),
                new File(generatedSources),
                releasePath.file(),
                cmakeDefinitions);
        try {
            nativeProject.write(data.outputName);
        } catch(IOException e) {
            throw new RuntimeException("Failed to setup GLFW native project", e);
        }
        if(buildExecutableAfterBuild || runExecutableAfterBuild) {
            nativeProject.executeBuildScript(nativeBuildType);
        }
        if(runExecutableAfterBuild) {
            nativeProject.runExecutable(data.outputName, nativeBuildType, runExecutableWithConsoleLog);
        }
    }

    @Override
    protected void copyAssets(TeaBuilderData data) {
        super.copyAssets(data);
        FileHandle outputFolder = new FileHandle(buildRootPath + "/c");
        try {
            AssetsCopy.copyClasspathResources(classLoader, cppFiles, null, AssetOutput.fileHandle(outputFolder), "");
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String requireCMakeDefinitionName(String name) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("CMake definition name cannot be blank");
        }
        return name.trim();
    }
}
