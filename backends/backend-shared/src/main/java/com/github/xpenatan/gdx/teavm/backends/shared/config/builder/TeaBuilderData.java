package com.github.xpenatan.gdx.teavm.backends.shared.config.builder;

import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import org.teavm.tooling.TeaVMSourceFilePolicy;
import org.teavm.tooling.sources.SourceFileProvider;
import org.teavm.vm.TeaVMOptimizationLevel;

public class TeaBuilderData {
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
    public final Set<String> preservedClasses = new LinkedHashSet<>();
    public boolean shortFileNames;
}