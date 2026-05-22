package com.github.xpenatan.gdx.teavm.backends.shared.config.reflection;

import org.junit.Assert;
import org.junit.Test;

public class DefaultReflectionListenerTest {

    @Test
    public void shouldEnableReflection_matchesNestedClassesWhenOuterClassConfigured() {
        DefaultReflectionListener listener = new DefaultReflectionListener();

        listener.addClassOrPackage("com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator");

        Assert.assertTrue(listener.shouldEnableReflection(
                "com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator$FreeTypeFontParameter"));
    }

    @Test
    public void shouldEnableReflection_matchesDeepNestedClassesWhenParentNestedClassConfigured() {
        DefaultReflectionListener listener = new DefaultReflectionListener();

        listener.addClassOrPackage("com.example.Outer$Inner");

        Assert.assertTrue(listener.shouldEnableReflection("com.example.Outer$Inner$Parameter"));
        Assert.assertFalse(listener.shouldEnableReflection("com.example.Outer$Other"));
    }
}
