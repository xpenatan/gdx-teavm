package com.github.xpenatan.gdx.backends.teavm.config.compiler;

import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildReflectionListener;
import java.io.File;
import org.teavm.tooling.TeaVMSourceFilePolicy;
import org.teavm.tooling.sources.SourceFileProvider;
import org.teavm.vm.TeaVMOptimizationLevel;

public class TeaCompiler {

    private TeaCompilerData data = new TeaCompilerData();

    public TeaCompiler setBackend(TeaBackend backend) {
        data.backend = backend;
        return this;
    }

    public TeaCompiler setObfuscated(boolean flag) {
        data.isObfuscated = flag;
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

    public TeaCompiler addReflectionClass(String fullClassName) {
        data.reflectionClasses.add(fullClassName);
        return this;
    }

    public TeaCompiler setReflectionListener(TeaBuildReflectionListener reflectionListener) {
        data.reflectionListener = reflectionListener;
        return this;
    }

    public TeaCompiler setOutputName(String outputName) {
        data.outputName = outputName;
        return this;
    }

    public void build(File output) {
        if(data != null) {
            if(data.backend != null) {
                data.output = output;
                data.backend.compile(data);
            }
        }
        data = null;
    }
}
