package com.github.xpenatan.gdx.teavm.backends.shared.config.plugin;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.teavm.extension.ExtensionEnvironmentImpl;
import org.teavm.extension.introspect.IntrospectClass;
import org.teavm.extension.introspect.IntrospectMember;
import org.teavm.extension.spi.reflection.ReflectionPolicy;
import org.teavm.model.ClassHierarchy;
import org.teavm.model.ReferenceCache;
import org.teavm.parsing.ClasspathClassHolderSource;
import org.teavm.parsing.ClasspathResourceProvider;

public class TeaReflectionPolicyTest {
    private List<String> previousClasses;
    private ExtensionEnvironmentImpl environment;
    private ReflectionPolicy policy;

    @Before
    public void setUp() {
        previousClasses = new ArrayList<>(TeaReflectionSupplier.getReflectionClasses());
        TeaReflectionSupplier.getReflectionClasses().clear();
        ClassLoader loader = getClass().getClassLoader();
        ClasspathResourceProvider resources = new ClasspathResourceProvider(loader);
        ClasspathClassHolderSource source = new ClasspathClassHolderSource(resources, new ReferenceCache(), loader);
        environment = new ExtensionEnvironmentImpl(resources, new ClassHierarchy(source), loader, null, Properties::new);
        policy = ServiceLoader.load(ReflectionPolicy.class, loader).stream()
                .filter(provider -> provider.type() == TeaReflectionPolicy.class)
                .findFirst().orElseThrow().get();
        policy.initialize(environment);
    }

    @After
    public void tearDown() {
        TeaReflectionSupplier.getReflectionClasses().clear();
        TeaReflectionSupplier.getReflectionClasses().addAll(previousClasses);
    }

    @Test
    public void exposesAllMembersAndNameLookupAfterRegistration() {
        IntrospectClass<?> type = environment.findClass(Style.class);
        assertThat(policy.isClassFoundByName(type)).isFalse();
        assertThat(policy.classAccessibleMembers(type)).isEmpty();

        // Registration can happen after TeaVM discovers and initializes the policy service.
        TeaReflectionSupplier.addReflectionClass(Style.class);

        assertThat(policy.isClassFoundByName(type)).isTrue();
        assertThat(policy.classAccessibleMembers(type).stream()
                .map(IntrospectMember::name).collect(Collectors.toList()))
                .containsExactly("value", "shared", "<init>", "<init>", "getStyle", "setStyle", "reset");
    }

    @Test
    public void preservesNestedClassLookupWithoutExposingUnregisteredClasses() {
        TeaReflectionSupplier.addReflectionClass(Style.class);

        IntrospectClass<?> nested = environment.findClass(Style.Nested.class);
        assertThat(policy.isClassFoundByName(nested)).isTrue();
        assertThat(policy.classAccessibleMembers(nested)).isNotEmpty();

        IntrospectClass<?> unrelated = environment.findClass(Unregistered.class);
        assertThat(policy.isClassFoundByName(unrelated)).isFalse();
        assertThat(policy.classAccessibleMembers(unrelated)).isEmpty();
    }

    public static class Style {
        private int value;
        public static int shared;

        public Style() {
        }

        private Style(int value, int extra) {
            this.value = value + extra;
        }

        public int getStyle() {
            return value;
        }

        public void setStyle(int value) {
            this.value = value;
        }

        private static void reset() {
            shared = 0;
        }

        public static class Nested {
        }
    }

    public static class Unregistered {
    }
}
