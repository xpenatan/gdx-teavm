package com.github.xpenatan.gdx.teavm.backends.ios.config.plugins;

import com.github.xpenatan.gdx.teavm.backends.ios.config.backend.TeaIOSNativeProject;
import com.github.xpenatan.gdx.teavm.backends.shared.config.TeaLogHelper;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.DelegatingTeaVMTarget;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.GdxTeaVMPluginAssetSupport;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.GdxTeaVMPluginConfig;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.TeaVMTargetInstaller;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import org.teavm.model.ListableClassHolderSource;
import org.teavm.vm.BuildTarget;
import org.teavm.vm.TeaVMTarget;
import org.teavm.vm.spi.TeaVMHost;

public class GdxIOSTargetWrapper extends DelegatingTeaVMTarget {
    private final GdxTeaVMPluginConfig config;
    private final ClassLoader classLoader;
    private final ArrayList<URL> classPathURLs;

    private GdxIOSTargetWrapper(
            TeaVMTarget delegate,
            GdxTeaVMPluginConfig config,
            ClassLoader classLoader,
            ArrayList<URL> classPathURLs) {
        super(delegate);
        this.config = config;
        this.classLoader = classLoader;
        this.classPathURLs = classPathURLs;
    }

    public static void install(TeaVMHost host, GdxTeaVMPluginConfig config, ArrayList<URL> classPathURLs) {
        TeaVMTargetInstaller.install(host, GdxIOSTargetWrapper.class, target ->
                new GdxIOSTargetWrapper(target, config, host.getClassLoader(), classPathURLs));
    }

    @Override
    public void emit(ListableClassHolderSource classes, BuildTarget buildTarget, String outputName) throws IOException {
        delegate.emit(classes, buildTarget, outputName);
        String projectName = outputName == null || outputName.isBlank() ? "app" : outputName;
        File buildRoot = requiredDirectory(config.nativeOutputRoot, "iOS output root");
        File generatedSources = requiredDirectory(config.nativeGeneratedSources, "iOS generated sources");
        File releasePath = requiredDirectory(config.nativeReleasePath, "iOS release path");
        File xcodeProjectDir = requiredPath(config.iosXcodeProjectDir, "iOS Xcode project directory");
        File cOutputPath = new File(buildRoot, "c");

        TeaLogHelper.logHeader("COPYING IOS NATIVE RESOURCES");
        GdxTeaVMPluginAssetSupport.copyNativeAssets(config, classLoader, classPathURLs, releasePath, cOutputPath);

        TeaIOSNativeProject nativeProject = new TeaIOSNativeProject(classLoader, buildRoot, generatedSources);
        nativeProject.write(projectName, xcodeProjectDir);
    }

    private File requiredDirectory(String path, String name) {
        File file = requiredPath(path, name);
        if(!file.exists() && !file.mkdirs()) {
            throw new IllegalStateException("Unable to create " + name + ": " + file.getAbsolutePath());
        }
        return file;
    }

    private File requiredPath(String path, String name) {
        if(path == null || path.isBlank()) {
            throw new IllegalStateException(name + " was not configured");
        }
        File file = new File(path);
        return file;
    }
}
