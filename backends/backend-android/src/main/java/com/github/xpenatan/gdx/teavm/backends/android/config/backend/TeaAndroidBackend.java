package com.github.xpenatan.gdx.teavm.backends.android.config.backend;

import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetOutput;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetsCopy;
import com.github.xpenatan.gdx.teavm.backends.shared.config.backend.TeaCBackend;
import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilderData;
import java.io.File;
import java.io.IOException;

public class TeaAndroidBackend extends TeaCBackend {

    @Override
    protected String getNativeBackendName() {
        return "android";
    }

    @Override
    protected void writeNativeProject(TeaBuilderData data) throws IOException {
        TeaAndroidNativeProject nativeProject = new TeaAndroidNativeProject(
                getClass().getClassLoader(),
                new File(buildRootPath),
                new File(generatedSources));
        nativeProject.write(data.outputName);
    }

    @Override
    protected void copyAssets(TeaBuilderData data) {
        try {
            scripts.clear();
            cppFiles.clear();
            AssetsCopy.AssetPlan plan = AssetsCopy.createAssetPlan(classLoader, acceptedURL, null, assetFilter);
            scripts.addAll(plan.scripts);
            cppFiles.addAll(plan.cppFiles);

            FileHandle outputFolder = new FileHandle(buildRootPath + "/c");
            AssetsCopy.copyClasspathResources(classLoader, cppFiles, null, AssetOutput.fileHandle(outputFolder), "");
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
