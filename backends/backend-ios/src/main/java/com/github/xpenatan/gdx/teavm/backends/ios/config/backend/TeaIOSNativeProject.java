package com.github.xpenatan.gdx.teavm.backends.ios.config.backend;

import com.github.xpenatan.gdx.teavm.backends.shared.config.backend.TeaNativeProject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class TeaIOSNativeProject extends TeaNativeProject {

    public TeaIOSNativeProject(ClassLoader classLoader, File buildRoot, File generatedSources) {
        super(classLoader, buildRoot, generatedSources);
    }

    public void write(String projectName, File xcodeProjectDir) throws IOException {
        ensureDirectory(buildRoot, "iOS output root");
        ensureDirectory(generatedSources, "iOS generated sources");

        copyResource("app_include.c", new File(generatedSources, "app_include.c"));
        copyResource("fiber.c", new File(generatedSources, "fiber.c"));
        copyResource("file.c", new File(generatedSources, "file.c"));
        copyResource("uchar.h", new File(generatedSources, "uchar.h"));
        copyResource("ios_bridge.c", new File(generatedSources, "ios_bridge.c"));
        copyResource("ios_bridge.h", new File(generatedSources, "ios_bridge.h"));
        writeCMakeLists(projectName);
        writeMetadata(projectName, xcodeProjectDir);
    }

    private void writeCMakeLists(String projectName) throws IOException {
        writeTemplate("CMakeLists.txt", new File(buildRoot, "CMakeLists.txt"),
                Map.of("${PROJECT_NAME}", projectName), false);
    }

    private void writeMetadata(String projectName, File xcodeProjectDir) throws IOException {
        String content = "library.name=" + projectName + System.lineSeparator()
                + "cmake.path=" + new File(buildRoot, "CMakeLists.txt").getAbsolutePath().replace('\\', '/') + System.lineSeparator()
                + "generated.sources=" + generatedSources.getAbsolutePath().replace('\\', '/') + System.lineSeparator()
                + "bridge.header=" + new File(generatedSources, "ios_bridge.h").getAbsolutePath().replace('\\', '/') + System.lineSeparator()
                + "xcode.project.dir=" + xcodeProjectDir.getAbsolutePath().replace('\\', '/') + System.lineSeparator();
        Files.writeString(new File(buildRoot, "metadata.properties").toPath(), content);
    }
}
