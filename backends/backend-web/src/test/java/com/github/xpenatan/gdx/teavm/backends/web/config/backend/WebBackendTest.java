package com.github.xpenatan.gdx.teavm.backends.web.config.backend;

import static com.google.common.truth.Truth.assertThat;

import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.TeaClassLoader;
import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilderData;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.GdxTeaVMPluginConfig;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.tooling.TeaVMTool;

public class WebBackendTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void webOutputControlsAreEnabledByDefaultAndFluent() {
        WebBackend backend = new WebBackend();

        assertThat(backend.generateIndexHtml).isTrue();
        assertThat(backend.copyAssets).isTrue();
        assertThat(backend.copyLoadingAsset).isTrue();
        assertThat(backend.setGenerateIndexHtml(false)).isSameInstanceAs(backend);
        assertThat(backend.setCopyAssets(false)).isSameInstanceAs(backend);
        assertThat(backend.setCopyLoadingAsset(false)).isSameInstanceAs(backend);
        assertThat(backend.generateIndexHtml).isFalse();
        assertThat(backend.copyAssets).isFalse();
        assertThat(backend.copyLoadingAsset).isFalse();
    }

    @Test
    public void disabledIndexAndAssetCopyPreserveFilesWhileLogoAndManifestRemainIndependent() throws IOException {
        File releaseDir = temporaryFolder.newFolder("release");
        File sourceAssets = temporaryFolder.newFolder("source-assets");
        write(releaseDir, "index.html", "custom index");
        write(releaseDir, "assets/game.txt", "custom asset");
        write(releaseDir, "assets/startup-logo.png", "custom logo");
        write(sourceAssets, "game.txt", "new asset");

        TeaBuilderData data = new TeaBuilderData();
        data.outputName = "app";
        data.assets.add(new AssetFileHandle(sourceAssets.getAbsolutePath()));

        try(TestWebBackend backend = new TestWebBackend(releaseDir)) {
            backend.setGenerateIndexHtml(false)
                    .setCopyAssets(false)
                    .setCopyLoadingAsset(true);

            backend.setupWebappForTest(data);
            backend.copyAssetsForTest(data);

            assertThat(read(releaseDir, "index.html")).isEqualTo("custom index");
            assertThat(read(releaseDir, "assets/game.txt")).isEqualTo("custom asset");
            assertThat(Files.size(releaseDir.toPath().resolve("assets/startup-logo.png")))
                    .isGreaterThan((long)"custom logo".length());
            assertThat(backend.assetManifest()).contains("/game.txt:9:");
            assertThat(releaseDir.toPath().resolve("WEB-INF/web.xml").toFile().isFile()).isTrue();
        }
    }

    private void write(File root, String relativePath, String value) throws IOException {
        var path = root.toPath().resolve(relativePath);
        Files.createDirectories(path.getParent());
        Files.writeString(path, value);
    }

    private String read(File root, String relativePath) throws IOException {
        return Files.readString(root.toPath().resolve(relativePath));
    }

    private static class TestWebBackend extends WebBackend implements AutoCloseable {
        TestWebBackend(File releaseDir) {
            acceptedURL = new ArrayList<>();
            classLoader = new TeaClassLoader(new URL[0], WebBackendTest.class.getClassLoader());
            tool = new TeaVMTool();
            targetType = TeaVMTargetType.JAVASCRIPT;
            releasePath = new FileHandle(releaseDir);
        }

        void setupWebappForTest(TeaBuilderData data) {
            setupWebapp(data);
        }

        void copyAssetsForTest(TeaBuilderData data) {
            copyAssets(data);
        }

        String assetManifest() {
            return tool.getProperties().getProperty(GdxTeaVMPluginConfig.ASSET_MANIFEST);
        }

        @Override
        public void close() throws IOException {
            classLoader.close();
        }
    }
}
