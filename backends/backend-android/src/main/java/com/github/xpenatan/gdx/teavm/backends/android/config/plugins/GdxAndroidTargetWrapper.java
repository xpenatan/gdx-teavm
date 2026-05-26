package com.github.xpenatan.gdx.teavm.backends.android.config.plugins;

import com.github.xpenatan.gdx.teavm.backends.android.config.backend.TeaAndroidNativeProject;
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

public class GdxAndroidTargetWrapper extends DelegatingTeaVMTarget {
    private final GdxTeaVMPluginConfig config;
    private final ClassLoader classLoader;
    private final ArrayList<URL> classPathURLs;

    private GdxAndroidTargetWrapper(
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
        TeaVMTargetInstaller.install(host, GdxAndroidTargetWrapper.class, target ->
                new GdxAndroidTargetWrapper(target, config, host.getClassLoader(), classPathURLs));
    }

    @Override
    public void emit(ListableClassHolderSource classes, BuildTarget buildTarget, String outputName) throws IOException {
        delegate.emit(classes, buildTarget, outputName);
        String projectName = outputName == null || outputName.isBlank() ? "app" : outputName;
        File buildRoot = requiredDirectory(config.nativeOutputRoot, "Android output root");
        File generatedSources = requiredDirectory(config.nativeGeneratedSources, "Android generated sources");
        File cOutputPath = new File(buildRoot, "c");

        TeaLogHelper.logHeader("COPYING ANDROID NATIVE RESOURCES");
        GdxTeaVMPluginAssetSupport.copyNativeCppResources(classLoader, classPathURLs, cOutputPath);

        TeaAndroidNativeProject nativeProject = new TeaAndroidNativeProject(classLoader, buildRoot, generatedSources);
        nativeProject.write(projectName);
    }

    private File requiredDirectory(String path, String name) {
        if(path == null || path.isBlank()) {
            throw new IllegalStateException(name + " was not configured");
        }
        File file = new File(path);
        if(!file.exists() && !file.mkdirs()) {
            throw new IllegalStateException("Unable to create " + name + ": " + file.getAbsolutePath());
        }
        return file;
    }
}
