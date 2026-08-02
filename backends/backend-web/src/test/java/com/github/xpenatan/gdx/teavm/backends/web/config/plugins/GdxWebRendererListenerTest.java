package com.github.xpenatan.gdx.teavm.backends.web.config.plugins;

import static com.google.common.truth.Truth.assertThat;

import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetsCopy;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.GdxTeaVMPluginConfig;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Test;
import org.teavm.vm.BuildTarget;

public class GdxWebRendererListenerTest {
    @Test
    public void disabledIndexGenerationPreservesExistingIndexAndStillCopiesAssets() throws IOException {
        MemoryBuildTarget target = new MemoryBuildTarget();
        target.put("index.html", "custom index");
        AssetsCopy.AssetPlan plan = assetPlan();
        GdxWebRendererListener renderer = renderer(false, true, false, plan);

        renderer.write(target, GdxWebRendererListener.TargetType.JAVASCRIPT, "app.js");

        assertThat(target.read("index.html")).isEqualTo("custom index");
        assertThat(target.resources).containsKey("WEB-INF/web.xml");
        assertThat(target.resources).containsKey("assets/howler.js");
        assertThat(target.resources).containsKey("scripts/gdx.wasm.js");
        assertThat(target.resources).doesNotContainKey("assets/startup-logo.png");
    }

    @Test
    public void disabledAssetCopyPreservesAssetsWhileLoadingLogoCopiesSeparately() throws IOException {
        MemoryBuildTarget target = new MemoryBuildTarget();
        target.put("assets/howler.js", "custom asset");
        target.put("scripts/gdx.wasm.js", "custom script");
        target.put("assets/startup-logo.png", "custom logo");
        GdxWebRendererListener renderer = renderer(true, false, true, assetPlan());

        renderer.write(target, GdxWebRendererListener.TargetType.JAVASCRIPT, "app.js");

        assertThat(target.read("index.html")).contains("app.js");
        assertThat(target.read("assets/howler.js")).isEqualTo("custom asset");
        assertThat(target.read("scripts/gdx.wasm.js")).isEqualTo("custom script");
        assertThat(target.read("assets/startup-logo.png")).isNotEqualTo("custom logo");
    }

    private GdxWebRendererListener renderer(
            boolean generateIndexHtml,
            boolean copyAssets,
            boolean copyLoadingAsset,
            AssetsCopy.AssetPlan plan
    ) {
        Properties properties = new Properties();
        properties.setProperty(GdxTeaVMPluginConfig.GENERATE_INDEX_HTML, Boolean.toString(generateIndexHtml));
        properties.setProperty(GdxTeaVMPluginConfig.COPY_ASSETS, Boolean.toString(copyAssets));
        properties.setProperty(GdxTeaVMPluginConfig.COPY_LOADING_ASSET, Boolean.toString(copyLoadingAsset));
        GdxTeaVMPluginConfig config = GdxTeaVMPluginConfig.from(properties);
        return new GdxWebRendererListener(
                config,
                GdxWebRendererListenerTest.class.getClassLoader(),
                new ArrayList<>(),
                plan
        );
    }

    private AssetsCopy.AssetPlan assetPlan() {
        AssetsCopy.AssetPlan plan = new AssetsCopy.AssetPlan();
        plan.assetOnlyClasspathResources.add("howler.js");
        plan.scripts.add("gdx.wasm.js");
        return plan;
    }

    private static class MemoryBuildTarget implements BuildTarget {
        private final Map<String, ByteArrayOutputStream> resources = new LinkedHashMap<>();

        @Override
        public OutputStream createResource(String fileName) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            resources.put(fileName, output);
            return output;
        }

        void put(String fileName, String value) throws IOException {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(value.getBytes(StandardCharsets.UTF_8));
            resources.put(fileName, output);
        }

        String read(String fileName) {
            return resources.get(fileName).toString(StandardCharsets.UTF_8);
        }
    }
}
