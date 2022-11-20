package com.github.xpenatan.gdx.backends.teavm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import org.teavm.backend.c.CTarget;
import org.teavm.backend.c.generate.CNameProvider;
import org.teavm.backend.javascript.JavaScriptTarget;
import org.teavm.backend.wasm.WasmTarget;
import org.teavm.backend.wasm.render.WasmBinaryVersion;
import org.teavm.cache.AlwaysStaleCacheStatus;
import org.teavm.cache.CacheStatus;
import org.teavm.cache.DiskCachedClassReaderSource;
import org.teavm.cache.DiskMethodNodeCache;
import org.teavm.cache.DiskProgramCache;
import org.teavm.cache.EmptyProgramCache;
import org.teavm.cache.FileSymbolTable;
import org.teavm.debugging.information.DebugInformation;
import org.teavm.debugging.information.DebugInformationBuilder;
import org.teavm.dependency.DependencyInfo;
import org.teavm.dependency.FastDependencyAnalyzer;
import org.teavm.dependency.PreciseDependencyAnalyzer;
import org.teavm.diagnostics.ProblemProvider;
import org.teavm.model.ClassHolderSource;
import org.teavm.model.ClassHolderTransformer;
import org.teavm.model.ClassReader;
import org.teavm.model.PreOptimizingClassHolderSource;
import org.teavm.model.ProgramCache;
import org.teavm.model.ReferenceCache;
import org.teavm.parsing.ClasspathClassHolderSource;
import org.teavm.tooling.EmptyTeaVMToolLog;
import org.teavm.tooling.InstructionLocationReader;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.tooling.TeaVMTool;
import org.teavm.tooling.TeaVMToolException;
import org.teavm.tooling.TeaVMToolLog;
import org.teavm.tooling.sources.SourceFileProvider;
import org.teavm.tooling.sources.SourceFilesCopier;
import org.teavm.vm.BuildTarget;
import org.teavm.vm.DirectoryBuildTarget;
import org.teavm.vm.TeaVM;
import org.teavm.vm.TeaVMBuilder;
import org.teavm.vm.TeaVMOptimizationLevel;
import org.teavm.vm.TeaVMProgressListener;
import org.teavm.vm.TeaVMTarget;

public class CustomTeaVMTool {
    private File targetDirectory = new File(".");
    private TeaVMTargetType targetType;
    private String targetFileName;
    private boolean obfuscated;
    private boolean strict;
    private int maxTopLevelNames;
    private String mainClass;
    private String entryPointName;
    private Properties properties;
    private boolean debugInformationGenerated;
    private boolean sourceMapsFileGenerated;
    private boolean sourceFilesCopied;
    private boolean incremental;
    private File cacheDirectory;
    private List<String> transformers;
    private List<String> classesToPreserve;
    private TeaVMToolLog log;
    private ClassLoader classLoader;
    private DiskCachedClassReaderSource cachedClassSource;
    private DiskProgramCache programCache;
    private DiskMethodNodeCache astCache;
    private FileSymbolTable symbolTable;
    private FileSymbolTable fileTable;
    private FileSymbolTable variableTable;
    private boolean cancelled;
    private TeaVMProgressListener progressListener;
    private TeaVM vm;
    private boolean fastDependencyAnalysis;
    private TeaVMOptimizationLevel optimizationLevel;
    private List<SourceFileProvider> sourceFileProviders;
    private DebugInformationBuilder debugEmitter;
    private JavaScriptTarget javaScriptTarget;
    private WasmTarget webAssemblyTarget;
    private WasmBinaryVersion wasmVersion;
    private CTarget cTarget;
    private Set<File> generatedFiles;
    private int minHeapSize;
    private int maxHeapSize;
    private ReferenceCache referenceCache;
    private boolean longjmpSupported;
    private boolean heapDump;

    public CustomTeaVMTool() {
        this.targetType = TeaVMTargetType.JAVASCRIPT;
        this.targetFileName = "";
        this.obfuscated = true;
        this.maxTopLevelNames = 10000;
        this.entryPointName = "main";
        this.properties = new Properties();
        this.cacheDirectory = new File("./teavm-cache");
        this.transformers = new ArrayList();
        this.classesToPreserve = new ArrayList();
        this.log = new EmptyTeaVMToolLog();
        this.classLoader = TeaVMTool.class.getClassLoader();
        this.optimizationLevel = TeaVMOptimizationLevel.SIMPLE;
        this.sourceFileProviders = new ArrayList();
        this.wasmVersion = WasmBinaryVersion.V_0x1;
        this.generatedFiles = new HashSet();
        this.minHeapSize = 4194304;
        this.maxHeapSize = 134217728;
        this.longjmpSupported = true;
    }

    public File getTargetDirectory() {
        return this.targetDirectory;
    }

    public void setTargetDirectory(File targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public void setTargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }

    public void setObfuscated(boolean obfuscated) {
        this.obfuscated = obfuscated;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public void setMaxTopLevelNames(int maxTopLevelNames) {
        this.maxTopLevelNames = maxTopLevelNames;
    }

    public boolean isIncremental() {
        return this.incremental;
    }

    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
    }

    public String getMainClass() {
        return this.mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public void setEntryPointName(String entryPointName) {
        this.entryPointName = entryPointName;
    }

    public boolean isDebugInformationGenerated() {
        return this.debugInformationGenerated;
    }

    public void setDebugInformationGenerated(boolean debugInformationGenerated) {
        this.debugInformationGenerated = debugInformationGenerated;
    }

    public File getCacheDirectory() {
        return this.cacheDirectory;
    }

    public void setCacheDirectory(File cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    public boolean isSourceMapsFileGenerated() {
        return this.sourceMapsFileGenerated;
    }

    public void setSourceMapsFileGenerated(boolean sourceMapsFileGenerated) {
        this.sourceMapsFileGenerated = sourceMapsFileGenerated;
    }

    public boolean isSourceFilesCopied() {
        return this.sourceFilesCopied;
    }

    public void setSourceFilesCopied(boolean sourceFilesCopied) {
        this.sourceFilesCopied = sourceFilesCopied;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public List<String> getTransformers() {
        return this.transformers;
    }

    public List<String> getClassesToPreserve() {
        return this.classesToPreserve;
    }

    public TeaVMToolLog getLog() {
        return this.log;
    }

    public void setLog(TeaVMToolLog log) {
        this.log = log;
    }

    public TeaVMTargetType getTargetType() {
        return this.targetType;
    }

    public void setTargetType(TeaVMTargetType targetType) {
        this.targetType = targetType;
    }

    public TeaVMOptimizationLevel getOptimizationLevel() {
        return this.optimizationLevel;
    }

    public void setOptimizationLevel(TeaVMOptimizationLevel optimizationLevel) {
        this.optimizationLevel = optimizationLevel;
    }

    public boolean isFastDependencyAnalysis() {
        return this.fastDependencyAnalysis;
    }

    public void setFastDependencyAnalysis(boolean fastDependencyAnalysis) {
        this.fastDependencyAnalysis = fastDependencyAnalysis;
    }

    public void setMinHeapSize(int minHeapSize) {
        this.minHeapSize = minHeapSize;
    }

    public void setMaxHeapSize(int maxHeapSize) {
        this.maxHeapSize = maxHeapSize;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public WasmBinaryVersion getWasmVersion() {
        return this.wasmVersion;
    }

    public void setWasmVersion(WasmBinaryVersion wasmVersion) {
        this.wasmVersion = wasmVersion;
    }

    public void setLongjmpSupported(boolean longjmpSupported) {
        this.longjmpSupported = longjmpSupported;
    }

    public void setHeapDump(boolean heapDump) {
        this.heapDump = heapDump;
    }

    public void setProgressListener(TeaVMProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public boolean wasCancelled() {
        return this.cancelled;
    }

    public ProblemProvider getProblemProvider() {
        return this.vm != null ? this.vm.getProblemProvider() : null;
    }

    public DependencyInfo getDependencyInfo() {
        return this.vm.getDependencyInfo();
    }

    public Collection<String> getClasses() {
        return (Collection)(this.vm != null ? this.vm.getClasses() : Collections.emptyList());
    }

    public Set<File> getGeneratedFiles() {
        return this.generatedFiles;
    }

    public Collection<String> getUsedResources() {
        return (Collection)(this.vm == null ? Collections.emptyList() : InstructionLocationReader.extractUsedResources(this.vm));
    }

    public void addSourceFileProvider(SourceFileProvider sourceFileProvider) {
        this.sourceFileProviders.add(sourceFileProvider);
    }

    private TeaVMTarget prepareTarget() {
        switch(this.targetType) {
            case JAVASCRIPT:
                return this.prepareJavaScriptTarget();
            case WEBASSEMBLY:
                return this.prepareWebAssemblyTarget();
            case C:
                return this.prepareCTarget();
            default:
                throw new IllegalStateException("Unknown target type: " + this.targetType);
        }
    }

    private TeaVMTarget prepareJavaScriptTarget() {
        this.javaScriptTarget = new JavaScriptTarget();
        this.javaScriptTarget.setObfuscated(this.obfuscated);
        this.javaScriptTarget.setStrict(this.strict);
        this.javaScriptTarget.setTopLevelNameLimit(this.maxTopLevelNames);
        this.debugEmitter = !this.debugInformationGenerated && !this.sourceMapsFileGenerated ? null : new DebugInformationBuilder(this.referenceCache);
        this.javaScriptTarget.setDebugEmitter(this.debugEmitter);
        return this.javaScriptTarget;
    }

    private WasmTarget prepareWebAssemblyTarget() {
        this.webAssemblyTarget = new WasmTarget();
        this.webAssemblyTarget.setDebugging(this.debugInformationGenerated);
        this.webAssemblyTarget.setCEmitted(this.debugInformationGenerated);
        this.webAssemblyTarget.setWastEmitted(this.debugInformationGenerated);
        this.webAssemblyTarget.setVersion(this.wasmVersion);
        this.webAssemblyTarget.setMinHeapSize(this.minHeapSize);
        this.webAssemblyTarget.setMaxHeapSize(this.maxHeapSize);
        this.webAssemblyTarget.setObfuscated(this.obfuscated);
        return this.webAssemblyTarget;
    }

    private CTarget prepareCTarget() {
        this.cTarget = new CTarget(new CNameProvider());
        this.cTarget.setMinHeapSize(this.minHeapSize);
        this.cTarget.setMaxHeapSize(this.maxHeapSize);
        this.cTarget.setLineNumbersGenerated(this.debugInformationGenerated);
        this.cTarget.setLongjmpUsed(this.longjmpSupported);
        this.cTarget.setHeapDump(this.heapDump);
        this.cTarget.setObfuscated(this.obfuscated);
        return this.cTarget;
    }

    public void generate() throws TeaVMToolException {
        try {
            this.cancelled = false;
            this.log.info("Running TeaVM");
            this.referenceCache = new ReferenceCache();
            TeaVMBuilder vmBuilder = new TeaVMBuilder(this.prepareTarget());
            vmBuilder.setReferenceCache(this.referenceCache);
            Object cacheStatus;
            if(this.incremental) {
                this.cacheDirectory.mkdirs();
                this.symbolTable = new FileSymbolTable(new File(this.cacheDirectory, "symbols"));
                this.fileTable = new FileSymbolTable(new File(this.cacheDirectory, "files"));
                this.variableTable = new FileSymbolTable(new File(this.cacheDirectory, "variables"));
                ClasspathClassHolderSource innerClassSource = new ClasspathClassHolderSource(this.classLoader, this.referenceCache);
                ClassHolderSource classSource = new PreOptimizingClassHolderSource(innerClassSource);
                this.cachedClassSource = new DiskCachedClassReaderSource(this.cacheDirectory, this.referenceCache, this.symbolTable, this.fileTable, this.variableTable, classSource, innerClassSource);
                this.programCache = new DiskProgramCache(this.cacheDirectory, this.referenceCache, this.symbolTable, this.fileTable, this.variableTable);
                if(this.targetType == TeaVMTargetType.JAVASCRIPT) {
                    this.astCache = new DiskMethodNodeCache(this.cacheDirectory, this.referenceCache, this.symbolTable, this.fileTable, this.variableTable);
                    this.javaScriptTarget.setAstCache(this.astCache);
                }

                try {
                    this.symbolTable.update();
                    this.fileTable.update();
                    this.variableTable.update();
                }
                catch(IOException var15) {
                    this.log.info("Cache is missing");
                }

                vmBuilder.setClassLoader(this.classLoader).setClassSource(this.cachedClassSource);
                cacheStatus = this.cachedClassSource;
            }
            else {
                vmBuilder.setClassLoader(this.classLoader).setClassSource(new PreOptimizingClassHolderSource(new ClasspathClassHolderSource(this.classLoader, this.referenceCache)));
                cacheStatus = AlwaysStaleCacheStatus.INSTANCE;
            }

            vmBuilder.setDependencyAnalyzerFactory(this.fastDependencyAnalysis ? FastDependencyAnalyzer::new : PreciseDependencyAnalyzer::new);
            vmBuilder.setObfuscated(this.obfuscated);
            vmBuilder.setStrict(this.strict);
            this.vm = vmBuilder.build();
            if(this.progressListener != null) {
                this.vm.setProgressListener(this.progressListener);
            }

            this.vm.setProperties(this.properties);
            this.vm.setProgramCache((ProgramCache)(this.incremental ? this.programCache : EmptyProgramCache.INSTANCE));
            this.vm.setCacheStatus((CacheStatus)cacheStatus);
            this.vm.setOptimizationLevel(!this.fastDependencyAnalysis && !this.incremental ? this.optimizationLevel : TeaVMOptimizationLevel.SIMPLE);
            if(this.incremental) {
                this.vm.addVirtualMethods((m) -> {
                    return true;
                });
            }

            this.vm.installPlugins();
            List<ClassHolderTransformer> classHolderTransformers = this.resolveTransformers(this.classLoader);
            Iterator var17 = classHolderTransformers.iterator();

            while(var17.hasNext()) {
                ClassHolderTransformer transformer = (ClassHolderTransformer)var17.next();
                this.vm.add(transformer);
            }

            if(this.mainClass != null) {
                this.vm.entryPoint(this.mainClass, this.entryPointName != null ? this.entryPointName : "main");
            }

            var17 = this.classesToPreserve.iterator();

            String outputName;
            while(var17.hasNext()) {
                outputName = (String)var17.next();
                this.vm.preserveType(outputName);
            }
            vm.setLastKnownClasses(1);

            this.targetDirectory.mkdirs();
            BuildTarget buildTarget = new DirectoryBuildTarget(this.targetDirectory);
            outputName = this.getResolvedTargetFileName();
            this.vm.build(buildTarget, outputName);
            if(this.vm.wasCancelled()) {
                this.log.info("Build cancelled");
                this.cancelled = true;
            }
            else {
                ProblemProvider problemProvider = this.vm.getProblemProvider();
                if(problemProvider.getProblems().isEmpty()) {
                    this.log.info("Output file successfully built");
                }
                else if(problemProvider.getSevereProblems().isEmpty()) {
                    this.log.info("Output file built with warnings");
                }
                else {
                    this.log.info("Output file built with errors");
                }

                File outputFile = new File(this.targetDirectory, outputName);
                this.generatedFiles.add(outputFile);
                if(this.targetType == TeaVMTargetType.JAVASCRIPT) {
                    FileOutputStream output = new FileOutputStream(new File(this.targetDirectory, outputName), true);

                    try {
                        OutputStreamWriter writer = new OutputStreamWriter(output, StandardCharsets.UTF_8);

                        try {
                            this.additionalJavaScriptOutput(writer);
                        }
                        catch(Throwable var13) {
                            try {
                                writer.close();
                            }
                            catch(Throwable var12) {
                                var13.addSuppressed(var12);
                            }

                            throw var13;
                        }

                        writer.close();
                    }
                    catch(Throwable var14) {
                        try {
                            output.close();
                        }
                        catch(Throwable var11) {
                            var14.addSuppressed(var11);
                        }

                        throw var14;
                    }

                    output.close();
                }

                if(this.incremental) {
                    this.programCache.flush();
                    if(this.astCache != null) {
                        this.astCache.flush();
                    }

                    this.cachedClassSource.flush();
                    this.symbolTable.flush();
                    this.fileTable.flush();
                    this.variableTable.flush();
                    this.log.info("Cache updated");
                }

                this.printStats();
            }
        }
        catch(IOException var16) {
            throw new TeaVMToolException("IO error occurred", var16);
        }
    }

    private String getResolvedTargetFileName() {
        if(this.targetFileName.isEmpty()) {
            switch(this.targetType) {
                case JAVASCRIPT:
                    return "classes.js";
                case WEBASSEMBLY:
                    return "classes.wasm";
                case C:
                    return "classes.c";
                default:
                    return "classes";
            }
        }
        else {
            return this.targetFileName;
        }
    }

    private void additionalJavaScriptOutput(Writer writer) throws IOException {
        DebugInformation debugInfo;
        if(this.debugInformationGenerated) {
            assert this.debugEmitter != null;

            debugInfo = this.debugEmitter.getDebugInformation();
            File debugSymbolFile = new File(this.targetDirectory, this.getResolvedTargetFileName() + ".teavmdbg");
            BufferedOutputStream debugInfoOut = new BufferedOutputStream(new FileOutputStream(debugSymbolFile));

            try {
                debugInfo.write(debugInfoOut);
            }
            catch(Throwable var11) {
                try {
                    debugInfoOut.close();
                }
                catch(Throwable var9) {
                    var11.addSuppressed(var9);
                }

                throw var11;
            }

            debugInfoOut.close();
            this.generatedFiles.add(debugSymbolFile);
            this.log.info("Debug information successfully written");
        }

        if(this.sourceMapsFileGenerated) {
            assert this.debugEmitter != null;

            debugInfo = this.debugEmitter.getDebugInformation();
            String sourceMapsFileName = this.getResolvedTargetFileName() + ".map";
            writer.append("\n//# sourceMappingURL=").append(sourceMapsFileName);
            File sourceMapsFile = new File(this.targetDirectory, sourceMapsFileName);
            OutputStreamWriter sourceMapsOut = new OutputStreamWriter(new FileOutputStream(sourceMapsFile), StandardCharsets.UTF_8);

            try {
                debugInfo.writeAsSourceMaps(sourceMapsOut, "src", this.getResolvedTargetFileName());
            }
            catch(Throwable var10) {
                try {
                    sourceMapsOut.close();
                }
                catch(Throwable var8) {
                    var10.addSuppressed(var8);
                }

                throw var10;
            }

            sourceMapsOut.close();
            this.generatedFiles.add(sourceMapsFile);
            this.log.info("Source maps successfully written");
        }

        if(this.sourceFilesCopied) {
            this.copySourceFiles();
            this.log.info("Source files successfully written");
        }
    }

    private void printStats() {
        if(this.vm != null && this.vm.getWrittenClasses() != null) {
            int classCount = this.vm.getWrittenClasses().getClassNames().size();
            int methodCount = 0;

            ClassReader cls;
            for(Iterator var3 = this.vm.getWrittenClasses().getClassNames().iterator(); var3.hasNext(); methodCount += cls.getMethods().size()) {
                String className = (String)var3.next();
                cls = this.vm.getWrittenClasses().get(className);
            }

            this.log.info("Classes compiled: " + classCount);
            this.log.info("Methods compiled: " + methodCount);
        }
    }

    private void copySourceFiles() {
        if(this.vm.getWrittenClasses() != null) {
            List var10002 = this.sourceFileProviders;
            Set var10003 = this.generatedFiles;
            Objects.requireNonNull(var10003);
            SourceFilesCopier copier = new SourceFilesCopier(var10002, var10003::add);
            copier.addClasses(this.vm.getWrittenClasses());
            copier.setLog(this.log);
            copier.copy(new File(this.targetDirectory, "src"));
        }
    }

    private List<ClassHolderTransformer> resolveTransformers(ClassLoader classLoader) {
        List<ClassHolderTransformer> transformerInstances = new ArrayList();
        if(this.transformers == null) {
            return transformerInstances;
        }
        else {
            Iterator var3 = this.transformers.iterator();

            while(var3.hasNext()) {
                String transformerName = (String)var3.next();

                Class transformerRawType;
                try {
                    transformerRawType = Class.forName(transformerName, true, classLoader);
                }
                catch(ClassNotFoundException var11) {
                    this.log.error("Transformer not found: " + transformerName, var11);
                    continue;
                }

                if(!ClassHolderTransformer.class.isAssignableFrom(transformerRawType)) {
                    this.log.error("Transformer " + transformerName + " is not subtype of " + ClassHolderTransformer.class.getName());
                }
                else {
                    Class transformerType = transformerRawType.asSubclass(ClassHolderTransformer.class);

                    Constructor ctor;
                    try {
                        ctor = transformerType.getConstructor();
                    }
                    catch(NoSuchMethodException var10) {
                        this.log.error("Transformer " + transformerName + " has no default constructor");
                        continue;
                    }

                    try {
                        ClassHolderTransformer transformer = (ClassHolderTransformer)ctor.newInstance();
                        transformerInstances.add(transformer);
                    }
                    catch(IllegalAccessException | InvocationTargetException | InstantiationException var9) {
                        this.log.error("Error instantiating transformer " + transformerName, var9);
                    }
                }
            }

            return transformerInstances;
        }
    }
}
