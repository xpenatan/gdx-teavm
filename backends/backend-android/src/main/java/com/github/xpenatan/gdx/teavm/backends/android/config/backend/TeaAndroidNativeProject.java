package com.github.xpenatan.gdx.teavm.backends.android.config.backend;

import com.github.xpenatan.gdx.teavm.backends.shared.config.backend.TeaNativeProject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class TeaAndroidNativeProject extends TeaNativeProject {

    public TeaAndroidNativeProject(ClassLoader classLoader, File buildRoot, File generatedSources) {
        super(classLoader, buildRoot, generatedSources);
    }

    public void write(String projectName) throws IOException {
        ensureDirectory(buildRoot, "Android output root");
        ensureDirectory(generatedSources, "Android generated sources");

        copyResource("app_include.c", new File(generatedSources, "app_include.c"));
        copyResource("android_jni.c", new File(generatedSources, "android_jni.c"));
        writeCMakeLists(projectName);
        writeMetadata(projectName);
    }

    private void writeCMakeLists(String projectName) throws IOException {
        writeTemplate("CMakeLists.txt", new File(buildRoot, "CMakeLists.txt"),
                Map.of("${PROJECT_NAME}", projectName), false);
    }

    private void writeMetadata(String projectName) throws IOException {
        String content = "library.name=" + projectName + System.lineSeparator()
                + "cmake.path=" + new File(buildRoot, "CMakeLists.txt").getAbsolutePath().replace('\\', '/') + System.lineSeparator()
                + "generated.sources=" + generatedSources.getAbsolutePath().replace('\\', '/') + System.lineSeparator();
        Files.writeString(new File(buildRoot, "metadata.properties").toPath(), content);
    }
}
