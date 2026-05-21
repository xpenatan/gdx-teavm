package com.github.xpenatan.gdx.teavm.backends.shared.config.plugin;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetOutput;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetsCopy;
import com.github.xpenatan.gdx.teavm.backends.shared.config.TeaAssets;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class GdxTeaVMPluginAssetSupport {

    public static ArrayList<AssetFileHandle> toAssetHandles(GdxTeaVMPluginConfig config) {
        ArrayList<AssetFileHandle> result = new ArrayList<>();
        for(String assetPath : config.assets) {
            result.add(new AssetFileHandle(assetPath));
        }
        for(String resourcePath : config.classpathAssets) {
            result.add(new AssetFileHandle(resourcePath, FileType.Classpath));
        }
        return result;
    }

    public static AssetsCopy.AssetPlan copyNativeAssets(
            GdxTeaVMPluginConfig config,
            ClassLoader classLoader,
            ArrayList<URL> classPathURLs,
            File releasePath,
            File cOutputPath
    ) throws IOException {
        AssetsCopy.AssetPlan plan = AssetsCopy.createAssetPlan(classLoader, classPathURLs, toAssetHandles(config), null);
        AssetOutput assetOutput = AssetOutput.fileHandle(new FileHandle(releasePath));
        AssetsCopy.copyPlanAssets(classLoader, plan, assetOutput, "assets");
        AssetsCopy.writeManifest(plan, assetOutput, "assets/" + TeaAssets.ASSETS_FILE_NAME);
        AssetsCopy.copyClasspathResources(classLoader, plan.cppFiles, null,
                AssetOutput.fileHandle(new FileHandle(cOutputPath)), "");
        return plan;
    }
}
