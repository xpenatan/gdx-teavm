package com.github.xpenatan.gdx.teavm.backends.psp.config.backend;

import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetOutput;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetsCopy;
import com.github.xpenatan.gdx.teavm.backends.shared.config.backend.TeaBackend;
import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilderData;
import java.io.File;
import java.io.IOException;
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
    protected void preSetup(TeaBuilderData data) {
        data.minHeapSize = 2 * (1 << 20);
        data.maxHeapSize = 8 * (1 << 20);
        data.minDirectBuffersSize = 2 * (1 << 20);
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
        try {
            Files.createDirectories(Paths.get(generatedSources));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directories", e);
        }
        tool.setTargetDirectory(new File(generatedSources));
    }

    @Override
    protected void build(TeaBuilderData data) {
        if(shouldGenerateSource) {
            super.build(data);
        }

        TeaPSPNativeProject nativeProject = new TeaPSPNativeProject(
                getClass().getClassLoader(),
                new File(buildRootPath),
                new File(generatedSources));
        try {
            nativeProject.write(data.outputName, debugMemory);
        } catch(IOException e) {
            throw new RuntimeException("Failed to setup PSP native project", e);
        }
        if(autoExecuteBuild) {
            nativeProject.executeBuildScript();
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
