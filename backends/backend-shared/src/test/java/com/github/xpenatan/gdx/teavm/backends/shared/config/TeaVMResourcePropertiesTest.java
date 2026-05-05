package com.github.xpenatan.gdx.teavm.backends.shared.config;

import org.junit.Assert;
import org.junit.Test;

public class TeaVMResourcePropertiesTest {

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
}

