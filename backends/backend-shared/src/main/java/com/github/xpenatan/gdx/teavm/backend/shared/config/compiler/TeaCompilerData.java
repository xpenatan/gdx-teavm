package com.github.xpenatan.gdx.teavm.backend.shared.config.compiler;

import com.github.xpenatan.gdx.teavm.backend.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backend.shared.config.TeaBuildReflectionListener;
import java.io.File;
import java.util.ArrayList;
import org.teavm.tooling.TeaVMSourceFilePolicy;
import org.teavm.tooling.sources.SourceFileProvider;
import org.teavm.vm.TeaVMOptimizationLevel;

public class TeaCompilerData {
    public TeaBackend backend;
    public String outputName = "app";
    public boolean obfuscated;
    public TeaVMOptimizationLevel optimizationLevel;
    public String mainClass;
    public File releasePath;
    public File output;
    public final ArrayList<AssetFileHandle> assets = new ArrayList<>();
    public final ArrayList<SourceFileProvider> sourceFileProviders = new ArrayList<>();
    public boolean debugInformationGenerated;
    public boolean sourceMapsFileGenerated;
    public TeaVMSourceFilePolicy sourceFilePolicy = TeaVMSourceFilePolicy.COPY;
    public int minHeapSize = 4 * (1 << 20);
    public int maxHeapSize = 128 * (1 << 20);
    public int minDirectBuffersSize = 2 * (1 << 20);
    public int maxDirectBuffersSize = 32 * (1 << 20);
    public final ArrayList<String> reflectionClasses = new ArrayList<>();
    public TeaBuildReflectionListener reflectionListener;
}