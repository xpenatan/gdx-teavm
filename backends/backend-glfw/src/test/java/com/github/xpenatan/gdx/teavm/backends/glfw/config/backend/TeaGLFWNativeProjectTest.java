package com.github.xpenatan.gdx.teavm.backends.glfw.config.backend;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.xpenatan.gdx.teavm.backends.glfw.utils.GLFWNative;
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
        definitions.put("CMAKE_MSVC_RUNTIME_LIBRARY", "MultiThreadedDLL");

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
                        + "\"-DRESOURCE_ROOT=native resources!\" "
                        + "\"-DCMAKE_MSVC_RUNTIME_LIBRARY=MultiThreadedDLL\"");
        assertThat(batchScript).contains("setlocal disabledelayedexpansion");
        assertWindowsCMakeDiscovery(batchScript);

        String releaseBatchScript = read(buildRoot, "app_release.bat");
        assertWindowsCMakeDiscovery(releaseBatchScript);

        String shellScript = read(buildRoot, "app_debug.sh");
        assertThat(shellScript).contains(
                "cmake -S . -B build/cmake -DCMAKE_BUILD_TYPE=\"$BUILD_CONFIG\" "
                        + "'-DEXAMPLE_FEATURE=ENABLED' "
                        + "'-DRESOURCE_ROOT=native resources!' "
                        + "'-DCMAKE_MSVC_RUNTIME_LIBRARY=MultiThreadedDLL'");
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
    public void windowsConsoleRunWaitsBeforeClosingAndPreservesExitCode() throws Exception {
        String script = readResource("templates/glfw/app_console.bat");

        assertThat(script).contains("set \"APP_EXIT_CODE=%ERRORLEVEL%\"");
        assertThat(script).contains("Application terminated with exit code %APP_EXIT_CODE%.");
        assertThat(script).contains("pause >nul");
        assertThat(script).contains("exit /b %APP_EXIT_CODE%");

        int captureExitCode = script.indexOf("set \"APP_EXIT_CODE=%ERRORLEVEL%\"");
        int pause = script.indexOf("pause >nul");
        int restoreExitCode = script.indexOf("exit /b %APP_EXIT_CODE%");
        assertThat(captureExitCode).isAtLeast(0);
        assertThat(captureExitCode).isLessThan(pause);
        assertThat(pause).isLessThan(restoreExitCode);
    }

    @Test
    public void windowsNativeProjectSelectsMatchingPrebuiltRuntimeLibraries() throws Exception {
        File buildRoot = temporaryFolder.newFolder("runtime-flexible-project");
        TeaGLFWNativeProject project = new TeaGLFWNativeProject(
                TeaGLFWNativeProject.class.getClassLoader(),
                buildRoot,
                new File(buildRoot, "c/src"),
                new File(buildRoot, "release"));

        project.write("test_app");

        String cmake = read(buildRoot, "CMakeLists.txt");
        assertThat(cmake).contains("NOT DEFINED CMAKE_MSVC_RUNTIME_LIBRARY");
        assertThat(cmake).contains("set(CMAKE_MSVC_RUNTIME_LIBRARY \"MultiThreaded\")");
        assertThat(cmake).contains("get_target_property(TEAVM_MSVC_RUNTIME_LIBRARY");
        assertThat(cmake).contains("set(TEAVM_GLFW_LIBRARY glfw3_mt)");
        assertThat(cmake).contains("set(TEAVM_GLFW_LIBRARY glfw3)");
        assertThat(cmake).contains("set(TEAVM_GLEW_LIBRARY glew32s)");
        assertThat(cmake).contains("set(TEAVM_GLEW_LIBRARY glew32s_md)");
        assertThat(TeaGLFWNativeProject.class.getClassLoader().getResource(
                "external_cpp/glfw/lib-vc2022/glfw3_mt.lib")).isNotNull();
        assertThat(TeaGLFWNativeProject.class.getClassLoader().getResource(
                "external_cpp/glfw/lib-vc2022/glfw3.lib")).isNotNull();
        assertThat(TeaGLFWNativeProject.class.getClassLoader().getResource(
                "external_cpp/glew-2.3.0/lib/Release/x64/glew32s.lib")).isNotNull();
        assertThat(TeaGLFWNativeProject.class.getClassLoader().getResource(
                "external_cpp/glew-2.3.0/lib/Release/x64/glew32s_md.lib")).isNotNull();
    }

    @Test
    public void unixNativeProjectBuildsPinnedStaticDependenciesByDefault() throws Exception {
        File buildRoot = temporaryFolder.newFolder("unix-static-dependencies-project");
        TeaGLFWNativeProject project = new TeaGLFWNativeProject(
                TeaGLFWNativeProject.class.getClassLoader(),
                buildRoot,
                new File(buildRoot, "c/src"),
                new File(buildRoot, "release"));

        project.write("test_app");

        String cmake = read(buildRoot, "CMakeLists.txt");
        assertThat(cmake).contains("option(GDX_TEAVM_GLFW_USE_SYSTEM_LIBS");
        assertThat(cmake).contains("set(GLFW_LIBRARY_TYPE STATIC");
        assertThat(cmake).contains("FetchContent_Declare(gdx_teavm_glfw");
        assertThat(cmake).contains("glfw/glfw/releases/download/3.4/glfw-3.4.zip");
        assertThat(cmake).contains(
                "SHA256=b5ec004b2712fd08e8861dc271428f048775200a2df719ccf575143ba749a3e9");
        assertThat(cmake).contains("FetchContent_Declare(gdx_teavm_glew_source");
        assertThat(cmake).contains("glew/releases/download/glew-2.3.0/glew-2.3.0.zip");
        assertThat(cmake).contains(
                "SHA256=fe8fdbaa77cfa354ff400da323ea5e32b3641ad58a218607de74d2998b872e66");
        assertThat(cmake).contains("add_library(gdx_teavm_glew STATIC");
        assertThat(cmake).contains(
                "target_compile_definitions(gdx_teavm_glew PUBLIC GLEW_STATIC GLEW_NO_GLU)");
        assertThat(cmake).contains("target_link_libraries(gdx_teavm_glew PUBLIC ${X11_LIBRARIES})");
        assertThat(cmake).contains("if(GDX_TEAVM_GLFW_USE_SYSTEM_LIBS)");
        assertThat(cmake).contains("find_package(glfw3 3.4 CONFIG REQUIRED)");
        assertThat(cmake).contains("find_package(GLEW REQUIRED)");
        assertThat(cmake).contains("option(GDX_TEAVM_GLFW_NATIVE_X11");
        assertThat(cmake).contains("option(GDX_TEAVM_GLFW_NATIVE_WAYLAND");
        assertThat(cmake).contains("set(GDX_TEAVM_GLFW_NATIVE_X11 ${GLFW_BUILD_X11})");
        assertThat(cmake).contains("set(GDX_TEAVM_GLFW_NATIVE_WAYLAND ${GLFW_BUILD_WAYLAND})");
        assertThat(cmake).contains("GDX_TEAVM_GLFW_NATIVE_X11=1");
        assertThat(cmake).contains("GDX_TEAVM_GLFW_NATIVE_WAYLAND=1");
        assertThat(cmake).doesNotContain("set(GLEW_USE_STATIC_LIBS TRUE)");
    }

    @Test
    public void nativeProjectProvidesCrossPlatformWindowHandleBridge() throws Exception {
        File buildRoot = temporaryFolder.newFolder("native-window-handle-project");
        TeaGLFWNativeProject project = new TeaGLFWNativeProject(
                TeaGLFWNativeProject.class.getClassLoader(),
                buildRoot,
                new File(buildRoot, "c/src"),
                new File(buildRoot, "release"));

        project.write("test_app");

        String appInclude = read(new File(buildRoot, "c/src"), "app_include.c");
        assertThat(appInclude).contains("gdx_teavm_glfw_get_platform");
        assertThat(appInclude).contains("GLFW_EXPOSE_NATIVE_WIN32");
        assertThat(appInclude).contains("GLFW_EXPOSE_NATIVE_X11");
        assertThat(appInclude).contains("GLFW_EXPOSE_NATIVE_WAYLAND");
        assertThat(appInclude).contains("GLFW_EXPOSE_NATIVE_COCOA");
        assertThat(appInclude).contains("gdx_teavm_glfw_get_win32_window");
        assertThat(appInclude).contains("gdx_teavm_glfw_get_x11_display");
        assertThat(appInclude).contains("gdx_teavm_glfw_get_x11_window");
        assertThat(appInclude).contains("gdx_teavm_glfw_get_wayland_display");
        assertThat(appInclude).contains("gdx_teavm_glfw_get_wayland_window");
        assertThat(appInclude).contains("gdx_teavm_glfw_get_cocoa_window");
        assertThat(appInclude).contains("glfwGetPlatform");
        assertThat(appInclude).contains("glfwGetWin32Window");
        assertThat(appInclude).contains("glfwGetX11Display");
        assertThat(appInclude).contains("glfwGetX11Window");
        assertThat(appInclude).contains("glfwGetWaylandDisplay");
        assertThat(appInclude).contains("glfwGetWaylandWindow");
        assertThat(appInclude).contains("glfwGetCocoaWindow");
        assertThat(appInclude).doesNotContain("dlsym");
    }

    @Test
    public void exposesGenericNativeHandleApi() throws Exception {
        assertThat(GLFWNative.class.getDeclaredMethod("getPlatform").getReturnType()).isEqualTo(int.class);
        assertThat(GLFWNative.class.getDeclaredMethod("getWin32Window", long.class).getReturnType())
                .isEqualTo(long.class);
        assertThat(GLFWNative.class.getDeclaredMethod("getX11Display").getReturnType()).isEqualTo(long.class);
        assertThat(GLFWNative.class.getDeclaredMethod("getX11Window", long.class).getReturnType())
                .isEqualTo(long.class);
        assertThat(GLFWNative.class.getDeclaredMethod("getWaylandDisplay").getReturnType()).isEqualTo(long.class);
        assertThat(GLFWNative.class.getDeclaredMethod("getWaylandWindow", long.class).getReturnType())
                .isEqualTo(long.class);
        assertThat(GLFWNative.class.getDeclaredMethod("getCocoaWindow", long.class).getReturnType())
                .isEqualTo(long.class);
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

    private String readResource(String name) throws Exception {
        try(InputStream input = TeaGLFWNativeProject.class.getClassLoader().getResourceAsStream(name)) {
            assertThat(input).isNotNull();
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
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
