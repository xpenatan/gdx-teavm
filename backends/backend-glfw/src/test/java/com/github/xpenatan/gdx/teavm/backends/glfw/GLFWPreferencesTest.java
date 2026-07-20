package com.github.xpenatan.gdx.teavm.backends.glfw;

import static com.google.common.truth.Truth.assertThat;

import com.badlogic.gdx.files.FileHandle;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GLFWPreferencesTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void flushAndReloadUsesPortablePropertiesEncoding() throws Exception {
        File preferencesFile = new File(temporaryFolder.getRoot(), "preferences.properties");
        FileHandle fileHandle = new FileHandle(preferencesFile);
        String unicodeValue = "Olá 日本";

        GLFWPreferences preferences = new GLFWPreferences(fileHandle);
        preferences.putBoolean("boolean", true);
        preferences.putInteger("integer", 42);
        preferences.putLong("long", 4_294_967_296L);
        preferences.putFloat("float", 1.5f);
        preferences.putString("unicode", unicodeValue);
        preferences.flush();

        assertThat(Files.readString(preferencesFile.toPath(), StandardCharsets.UTF_8))
                .contains("unicode=Ol\\u00E1 \\u65E5\\u672C");

        GLFWPreferences reloaded = new GLFWPreferences(fileHandle);
        assertThat(reloaded.getBoolean("boolean")).isTrue();
        assertThat(reloaded.getInteger("integer")).isEqualTo(42);
        assertThat(reloaded.getLong("long")).isEqualTo(4_294_967_296L);
        assertThat(reloaded.getFloat("float")).isEqualTo(1.5f);
        assertThat(reloaded.getString("unicode")).isEqualTo(unicodeValue);
    }
}
