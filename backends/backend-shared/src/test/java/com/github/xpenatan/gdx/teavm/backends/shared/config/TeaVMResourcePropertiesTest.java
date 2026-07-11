package com.github.xpenatan.gdx.teavm.backends.shared.config;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.Assert;
import org.junit.Test;

public class TeaVMResourcePropertiesTest {

    @Test
    public void createAssetPlan_discoversScriptFromDependencyJar() throws Exception {
        Path jar = Files.createTempFile("gdx-freetype-web", ".jar");
        try {
            try(ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(jar))) {
                zip.putNextEntry(new ZipEntry("META-INF/gdx-teavm.properties"));
                zip.write("# marker\n".getBytes(StandardCharsets.UTF_8));
                zip.closeEntry();
                zip.putNextEntry(new ZipEntry("freetype.js"));
                zip.write("window.freetype = true;\n".getBytes(StandardCharsets.UTF_8));
                zip.closeEntry();
            }

            URL jarUrl = jar.toUri().toURL();
            ArrayList<URL> classpath = new ArrayList<>();
            classpath.add(jarUrl);

            try(URLClassLoader classLoader = new URLClassLoader(new URL[] { jarUrl })) {
                AssetsCopy.AssetPlan plan = AssetsCopy.createAssetPlan(classLoader, classpath, Collections.emptyList(), null);

                Assert.assertTrue(plan.scripts.contains("/freetype.js"));
            }
        }
        finally {
            Files.deleteIfExists(jar);
        }
    }

    @Test
    public void createAssetPlan_preservesPortableSharedLibrariesAsCppResources() throws Exception {
        Path jar = Files.createTempFile("native-shared-libraries", ".jar");
        try {
            try(ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(jar))) {
                writeEntry(zip, "META-INF/gdx-teavm.properties", "# marker\n");
                writeEntry(zip, "external_cpp/native/windows/jWebGPU64.DLL", "windows");
                writeEntry(zip, "external_cpp/native/linux/libjWebGPU.so", "linux");
                writeEntry(zip, "external_cpp/native/macos/libjWebGPU.DyLiB", "macos");
                writeEntry(zip, "external_cpp/cmake/linkage.CMAKE", "cmake");
                writeEntry(zip, "external_cpp/native/README.txt", "not a native build resource");
                writeEntry(zip, "application/config.json", "{}");
            }

            URL jarUrl = jar.toUri().toURL();
            ArrayList<URL> classpath = new ArrayList<>();
            classpath.add(jarUrl);

            try(URLClassLoader classLoader = new URLClassLoader(new URL[] { jarUrl })) {
                AssetsCopy.AssetPlan plan = AssetsCopy.createAssetPlan(
                        classLoader, classpath, Collections.emptyList(), null);

                Assert.assertTrue(plan.cppFiles.contains("/external_cpp/native/windows/jWebGPU64.DLL"));
                Assert.assertTrue(plan.cppFiles.contains("/external_cpp/native/linux/libjWebGPU.so"));
                Assert.assertTrue(plan.cppFiles.contains("/external_cpp/native/macos/libjWebGPU.DyLiB"));
                Assert.assertTrue(plan.cppFiles.contains("/external_cpp/cmake/linkage.CMAKE"));
                Assert.assertFalse(plan.cppFiles.contains("/external_cpp/native/README.txt"));
                Assert.assertEquals(1, plan.assets.size());
                Assert.assertEquals("application/config.json", plan.assets.get(0).classpathPath);
            }
        }
        finally {
            Files.deleteIfExists(jar);
        }
    }

    private static void writeEntry(ZipOutputStream zip, String path, String content) throws Exception {
        zip.putNextEntry(new ZipEntry(path));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    @Test
    public void matchesMainJarArtifact_matchesRealVersionSiblingArtifact() {
        boolean matches = TeaVMResourceProperties.matchesMainJarArtifact(
                "E:/cache/runtime-web-wasm-1.2.3.jar",
                "E:/cache/runtime-web-1.2.3.jar");

        Assert.assertTrue(matches);
    }

    @Test
    public void matchesMainJarArtifact_matchesRealVersionSiblingArtifactWithPreReleaseTag() {
        boolean matches = TeaVMResourceProperties.matchesMainJarArtifact(
                "E:/cache/runtime-web-wasm-1.2.3-RC1.jar",
                "E:/cache/runtime-web-1.2.3-RC1.jar");

        Assert.assertTrue(matches);
    }

    @Test
    public void matchesMainJarArtifact_doesNotMatchDifferentVersion() {
        boolean matches = TeaVMResourceProperties.matchesMainJarArtifact(
                "E:/cache/runtime-web-wasm-1.2.4.jar",
                "E:/cache/runtime-web-1.2.3.jar");

        Assert.assertFalse(matches);
    }

    @Test
    public void matchesMainJarArtifact_doesNotMatchDifferentBaseArtifact() {
        boolean matches = TeaVMResourceProperties.matchesMainJarArtifact(
                "E:/cache/runtime-desktop-wasm-1.2.3.jar",
                "E:/cache/runtime-web-1.2.3.jar");

        Assert.assertFalse(matches);
    }

    @Test
    public void matchesMainJarArtifact_matchesSnapshotClassifierSibling() {
        boolean matches = TeaVMResourceProperties.matchesMainJarArtifact(
                "E:/cache/runtime-web-wasm--SNAPSHOT.jar",
                "E:/cache/runtime-web--SNAPSHOT.jar");

        Assert.assertTrue(matches);
    }

    @Test
    public void matchesMainJarArtifact_doesNotMatchDifferentSnapshotSuffix() {
        boolean matches = TeaVMResourceProperties.matchesMainJarArtifact(
                "E:/cache/runtime-web-wasm--RC1.jar",
                "E:/cache/runtime-web--SNAPSHOT.jar");

        Assert.assertFalse(matches);
    }

    @Test
    public void matchesMainJarArtifact_doesNotMatchSameJar() {
        boolean matches = TeaVMResourceProperties.matchesMainJarArtifact(
                "E:/cache/runtime-web--SNAPSHOT.jar",
                "E:/cache/runtime-web--SNAPSHOT.jar");

        Assert.assertFalse(matches);
    }

    @Test
    public void matchesMainJarArtifact_matchesRealVersionSiblingArtifactWithUnderscoreClassifier() {
        boolean matches = TeaVMResourceProperties.matchesMainJarArtifact(
                "E:/cache/runtime-web_wasm-1.2.3.jar",
                "E:/cache/runtime-web-1.2.3.jar");

        Assert.assertTrue(matches);
    }

    @Test
    public void matchesMainJarArtifact_matchesRealVersionSiblingArtifactWithPreReleaseTagAndUnderscoreClassifier() {
        boolean matches = TeaVMResourceProperties.matchesMainJarArtifact(
                "E:/cache/runtime-web_wasm-1.2.3-RC1.jar",
                "E:/cache/runtime-web-1.2.3-RC1.jar");

        Assert.assertTrue(matches);
    }

    @Test
    public void matchesMainJarArtifact_doesNotMatchDifferentVersionWithUnderscoreClassifier() {
        boolean matches = TeaVMResourceProperties.matchesMainJarArtifact(
                "E:/cache/runtime-web_wasm-1.2.4.jar",
                "E:/cache/runtime-web-1.2.3.jar");

        Assert.assertFalse(matches);
    }

    @Test
    public void matchesMainJarArtifact_doesNotMatchDifferentBaseArtifactWithUnderscoreClassifier() {
        boolean matches = TeaVMResourceProperties.matchesMainJarArtifact(
                "E:/cache/runtime-desktop_wasm-1.2.3.jar",
                "E:/cache/runtime-web-1.2.3.jar");

        Assert.assertFalse(matches);
    }

    @Test
    public void matchesMainJarArtifact_matchesSnapshotClassifierSiblingWithUnderscoreClassifier() {
        boolean matches = TeaVMResourceProperties.matchesMainJarArtifact(
                "E:/cache/runtime-web_wasm--SNAPSHOT.jar",
                "E:/cache/runtime-web--SNAPSHOT.jar");

        Assert.assertTrue(matches);
    }

    @Test
    public void matchesMainJarArtifact_doesNotMatchDifferentSnapshotSuffixWithUnderscoreClassifier() {
        boolean matches = TeaVMResourceProperties.matchesMainJarArtifact(
                "E:/cache/runtime-web_wasm--RC1.jar",
                "E:/cache/runtime-web--SNAPSHOT.jar");

        Assert.assertFalse(matches);
    }

    @Test
    public void matchesMainJarArtifact_doesNotMatchSameJarWithUnderscoreClassifier() {
        boolean matches = TeaVMResourceProperties.matchesMainJarArtifact(
                "E:/cache/runtime-web--SNAPSHOT.jar",
                "E:/cache/runtime-web--SNAPSHOT.jar");

        Assert.assertFalse(matches);
    }
}
