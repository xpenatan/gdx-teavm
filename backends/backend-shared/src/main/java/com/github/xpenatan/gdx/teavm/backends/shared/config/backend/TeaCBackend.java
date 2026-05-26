package com.github.xpenatan.gdx.teavm.backends.shared.config.backend;

import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetOutput;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetsCopy;
import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilderData;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.GdxTeaVMPluginConfig;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.teavm.tooling.TeaVMTargetType;

public abstract class TeaCBackend extends TeaBackend {
    public boolean shouldGenerateSource = true;
    public boolean autoExecuteBuild;
    public List<String> additionalSourcePaths = new ArrayList<>();

    protected String buildRootPath;
    protected String generatedSources;
    protected String externalSources;

    protected abstract String getNativeBackendName();

    protected abstract void writeNativeProject(TeaBuilderData data) throws IOException;

    public TeaCBackend setAutoExecuteBuild(boolean autoExecuteBuild) {
        this.autoExecuteBuild = autoExecuteBuild;
        return this;
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
        String gdxSourcePath = externalSources + "/gdx";
        if(!additionalSourcePaths.contains(gdxSourcePath)) {
            additionalSourcePaths.add(gdxSourcePath);
        }
        tool.getProperties().setProperty(GdxTeaVMPluginConfig.NATIVE_BACKEND, getNativeBackendName());
        try {
            Files.createDirectories(new File(generatedSources).toPath());
        } catch(IOException e) {
            throw new RuntimeException("Failed to create " + getNativeBackendName() + " generated source directory", e);
        }
        tool.setTargetDirectory(new File(generatedSources));
    }

    @Override
    protected void build(TeaBuilderData data) {
        if(shouldGenerateSource) {
            super.build(data);
        }
        try {
            writeNativeProject(data);
        } catch(IOException e) {
            throw new RuntimeException("Failed to setup " + getNativeBackendName() + " native project", e);
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
}
