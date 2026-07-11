package com.github.xpenatan.gdx.teavm.backends.shared.config.plugin;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.Properties;
import org.junit.Test;

public class GdxTeaVMPluginConfigTest {
    @Test
    public void readsIndexedCMakeDefinitionsInDeclaredOrder() {
        Properties properties = new Properties();
        putDefinition(properties, "00000001", "SECOND", "native resources");
        putDefinition(properties, "00000000", "FIRST", "enabled");

        GdxTeaVMPluginConfig config = GdxTeaVMPluginConfig.from(properties);

        assertThat(new ArrayList<>(config.nativeCMakeDefinitions.keySet()))
                .containsExactly("FIRST", "SECOND")
                .inOrder();
        assertThat(config.nativeCMakeDefinitions)
                .containsExactly("FIRST", "enabled", "SECOND", "native resources");
    }

    private void putDefinition(Properties properties, String index, String name, String value) {
        String prefix = GdxTeaVMPluginConfig.NATIVE_CMAKE_DEFINITIONS + "." + index;
        properties.setProperty(prefix + ".name", name);
        properties.setProperty(prefix + ".value", value);
    }
}
