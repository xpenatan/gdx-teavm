package com.github.xpenatan.gdx.teavm.backends.shared.config.compiler;

import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.reflection.DefaultReflectionListener;
import java.io.File;
import org.teavm.tooling.TeaVMSourceFilePolicy;
import org.teavm.tooling.sources.SourceFileProvider;
import org.teavm.vm.TeaVMOptimizationLevel;

public class TeaCompiler {

    private TeaBackend backend;
    private TeaCompilerData data = new TeaCompilerData();

    public TeaCompiler(TeaBackend backend) {
        backend.preSetup(data);
        this.backend = backend;
    }

    public TeaCompiler setObfuscated(boolean flag) {
        data.obfuscated = flag;
        return this;
    }

    public TeaCompiler setOptimizationLevel(TeaVMOptimizationLevel optimizationLevel) {
        data.optimizationLevel = optimizationLevel;
        return this;
    }

    public TeaCompiler setMainClass(String mainClass) {
        data.mainClass = mainClass;
        return this;
    }

    public TeaCompiler addAssets(AssetFileHandle assetsPath) {
        data.assets.add(assetsPath);
        return this;
    }

    public TeaCompiler addSourceFileProvider(SourceFileProvider sourceProvider) {
        data.sourceFileProviders.add(sourceProvider);
        return this;
    }

    public TeaCompiler setDebugInformationGenerated(boolean debugInformationGenerated) {
        data.debugInformationGenerated = debugInformationGenerated;
        return this;
    }

    public TeaCompiler setSourceMapsFileGenerated(boolean sourceMapsFileGenerated) {
        data.sourceMapsFileGenerated = sourceMapsFileGenerated;
        return this;
    }

    public TeaCompiler setSourceFilePolicy(TeaVMSourceFilePolicy sourceFilePolicy) {
        data.sourceFilePolicy = sourceFilePolicy;
        return this;
    }

    public TeaCompiler setMinHeapSize(int minHeapSize) {
        data.minHeapSize = minHeapSize;
        return this;
    }

    public TeaCompiler setMaxHeapSize(int maxHeapSize) {
        data.maxHeapSize = maxHeapSize;
        return this;
    }

    public TeaCompiler setMinDirectBuffersSize(int minDirectBuffersSize) {
        data.minDirectBuffersSize = minDirectBuffersSize;
        return this;
    }

    public TeaCompiler setMaxDirectBuffersSize(int maxDirectBuffersSize) {
        data.maxDirectBuffersSize = maxDirectBuffersSize;
        return this;
    }

    public TeaCompiler setReflectionListener(DefaultReflectionListener reflectionListener) {
        data.reflectionListener = reflectionListener;
        return this;
    }

    public TeaCompiler addReflectionClass(Class<?> type) {
        data.reflectionListener.addClassOrPackage(type.getName());
        return this;
    }

    public TeaCompiler addReflectionClass(String classOrPackage) {
        data.reflectionListener.addClassOrPackage(classOrPackage);
        return this;
    }

    public TeaCompiler setOutputName(String outputName) {
        data.outputName = outputName;
        return this;
    }

    public TeaCompiler setReleasePath(File releasePath) {
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
