package com.github.xpenatan.gdx.teavm.backends.glfw.config.backend;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TeaGLFWNativeProjectTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void writesOrderedQuotedDefinitionsToWindowsAndShellConfigureLines() throws Exception {
        File buildRoot = temporaryFolder.newFolder("native-project");
        LinkedHashMap<String, String> definitions = new LinkedHashMap<>();
        definitions.put("EXAMPLE_FEATURE", "ENABLED");
        definitions.put("RESOURCE_ROOT", "native resources!");

        TeaGLFWNativeProject project = new TeaGLFWNativeProject(
                TeaGLFWNativeProject.class.getClassLoader(),
                buildRoot,
                new File(buildRoot, "c/src"),
                new File(buildRoot, "release"),
                definitions);
        project.write("test_app");

        String batchScript = read(buildRoot, "app_debug.bat");
        assertThat(batchScript).contains(
                "\"%CMAKE_PATH%\" -S . -B build\\cmake "
                        + "\"-DEXAMPLE_FEATURE=ENABLED\" "
                        + "\"-DRESOURCE_ROOT=native resources!\"");
        assertThat(batchScript).contains("setlocal disabledelayedexpansion");
        assertWindowsCMakeDiscovery(batchScript);

        String releaseBatchScript = read(buildRoot, "app_release.bat");
        assertWindowsCMakeDiscovery(releaseBatchScript);

        String shellScript = read(buildRoot, "app_debug.sh");
        assertThat(shellScript).contains(
                "cmake -S . -B build/cmake -DCMAKE_BUILD_TYPE=\"$BUILD_CONFIG\" "
                        + "'-DEXAMPLE_FEATURE=ENABLED' "
                        + "'-DRESOURCE_ROOT=native resources!'");
    }

    @Test
    public void manualBackendKeepsDefinitionsInDeclarationOrder() {
        TeaGLFWBackend backend = new TeaGLFWBackend()
                .cmakeDefinition("FIRST", "one")
                .cmakeDefinition("SECOND", "two");

        assertThat(new ArrayList<>(backend.getCMakeDefinitions().keySet()))
                .containsExactly("FIRST", "SECOND")
                .inOrder();
    }

    @Test
    public void rejectsBlankNamesAndNullValues() {
        TeaGLFWBackend backend = new TeaGLFWBackend();
        assertThrows(IllegalArgumentException.class, () -> backend.cmakeDefinition(" ", "value"));
        assertThrows(IllegalArgumentException.class, () -> backend.cmakeDefinition("NAME", null));

        Map<String, String> nullValue = new LinkedHashMap<>();
        nullValue.put("NAME", null);
        assertThrows(IllegalArgumentException.class, () -> new TeaGLFWNativeProject(
                TeaGLFWNativeProject.class.getClassLoader(),
                temporaryFolder.getRoot(),
                temporaryFolder.getRoot(),
                temporaryFolder.getRoot(),
                nullValue));
    }

    private String read(File root, String name) throws Exception {
        return Files.readString(new File(root, name).toPath(), StandardCharsets.UTF_8);
    }

    private void assertWindowsCMakeDiscovery(String script) {
        assertThat(script).contains("Microsoft Visual Studio\\Installer\\vswhere.exe");
        assertThat(script).contains(
                "-latest -products * -requires Microsoft.VisualStudio.Component.VC.CMake.Project");
        assertThat(script).contains(
                "-find Common7\\IDE\\CommonExtensions\\Microsoft\\CMake\\CMake\\bin\\cmake.exe");
        assertThat(script).contains("for %%V in (18 2022 2019 2017)");
        assertThat(script).contains("for %%S in (Community Professional Enterprise BuildTools)");
        assertThat(script).contains("\"%CMAKE_PATH%\" -S . -B build\\cmake");
        assertThat(script).doesNotContain("!CMAKE_PATH!");

        int disabledExpansion = script.indexOf("setlocal disabledelayedexpansion");
        int pathDiscovery = script.indexOf("where.exe cmake.exe");
        int visualStudioDiscovery = script.indexOf("Microsoft Visual Studio\\Installer\\vswhere.exe");
        int configureCommand = script.indexOf("\"%CMAKE_PATH%\" -S . -B build\\cmake");
        assertThat(disabledExpansion).isAtLeast(0);
        assertThat(disabledExpansion).isLessThan(pathDiscovery);
        assertThat(pathDiscovery).isLessThan(visualStudioDiscovery);
        assertThat(visualStudioDiscovery).isLessThan(configureCommand);
    }
}
