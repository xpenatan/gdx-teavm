package com.github.xpenatan.gdx.teavm.backends.shared.config.builder;

import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.backend.TeaBackend;
import com.github.xpenatan.gdx.teavm.backends.shared.config.reflection.DefaultReflectionListener;
import java.io.File;
import org.teavm.tooling.TeaVMSourceFilePolicy;
import org.teavm.tooling.sources.SourceFileProvider;
import org.teavm.vm.TeaVMOptimizationLevel;

public class TeaBuilder {

    private TeaBackend backend;
    private TeaBuilderData data = new TeaBuilderData();

    public TeaBuilder(TeaBackend backend) {
        backend.setupDefaults(data);
        this.backend = backend;
    }

    public TeaBuilder setObfuscated(boolean flag) {
        data.obfuscated = flag;
        return this;
    }

    public TeaBuilder setOptimizationLevel(TeaVMOptimizationLevel optimizationLevel) {
        data.optimizationLevel = optimizationLevel;
        return this;
    }

    public TeaBuilder setMainClass(String mainClass) {
        data.mainClass = mainClass;
        return this;
    }

    /**
     * Add an asset entry to be copied into the {@code assets/} folder.
     *
     * <p>Two modes, selected by the handle's {@link com.badlogic.gdx.Files.FileType FileType}:</p>
     * <ul>
     *   <li><b>Disk folder</b> – any non-classpath {@code FileType}: the handle points
     *       to a directory on disk that gets recursively copied. Manifest entries are
     *       emitted with the handle's destination type.</li>
     *   <li><b>Classpath resource</b> – {@code FileType.Classpath}: the string is treated
     *       as a classpath path (e.g. {@code "com/kotcrab/vis/ui/skin/x1"} or its dotted
     *       equivalent {@code "com.kotcrab.vis.ui.skin.x1"}). Files are looked up via
     *       the build classloader, copied to {@code assets/<resource-path>/...} and
     *       registered as classpath assets so {@code Gdx.files.classpath(...)} resolves
     *       them at runtime.</li>
     * </ul>
     *
     * <pre>
     * teaBuilder.addAssets(new AssetFileHandle("assets"));                                     // disk folder
     * teaBuilder.addAssets(new AssetFileHandle("com/kotcrab/vis/ui/skin/x1", FileType.Classpath));  // jar resource
     * </pre>
     */
    public TeaBuilder addAssets(AssetFileHandle assetsPath) {
        data.assets.add(assetsPath);
        return this;
    }

    public TeaBuilder addSourceFileProvider(SourceFileProvider sourceProvider) {
        data.sourceFileProviders.add(sourceProvider);
        return this;
    }

    public TeaBuilder setDebugInformationGenerated(boolean debugInformationGenerated) {
        data.debugInformationGenerated = debugInformationGenerated;
        return this;
    }

    public TeaBuilder setSourceMapsFileGenerated(boolean sourceMapsFileGenerated) {
        data.sourceMapsFileGenerated = sourceMapsFileGenerated;
        return this;
    }

    public TeaBuilder setSourceFilePolicy(TeaVMSourceFilePolicy sourceFilePolicy) {
        data.sourceFilePolicy = sourceFilePolicy;
        return this;
    }

    public TeaBuilder setMinHeapSize(int minHeapSize) {
        data.minHeapSize = minHeapSize;
        return this;
    }

    public TeaBuilder setMaxHeapSize(int maxHeapSize) {
        data.maxHeapSize = maxHeapSize;
        return this;
    }

    public TeaBuilder setMinDirectBuffersSize(int minDirectBuffersSize) {
        data.minDirectBuffersSize = minDirectBuffersSize;
        return this;
    }

    public TeaBuilder setReflectionListener(DefaultReflectionListener reflectionListener) {
        data.reflectionListener = reflectionListener;
        return this;
    }

    public TeaBuilder addReflectionClass(Class<?> type) {
        data.reflectionListener.addClassOrPackage(type.getName());
        return this;
    }

    public TeaBuilder addReflectionClass(String classOrPackage) {
        data.reflectionListener.addClassOrPackage(classOrPackage);
        return this;
    }

    public TeaBuilder setOutputName(String outputName) {
        data.outputName = outputName;
        return this;
    }

    public TeaBuilder setReleasePath(File releasePath) {
        data.releasePath = releasePath;
        return this;
    }

    public void build(File output) {
        if(data != null) {
            data.output = output;
            backend.compile(data);
        }
        backend = null;
        data = null;
    }
}
