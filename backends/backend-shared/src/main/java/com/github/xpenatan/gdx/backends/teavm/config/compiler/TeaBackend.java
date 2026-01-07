package com.github.xpenatan.gdx.backends.teavm.config.compiler;

import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle;
import com.github.xpenatan.gdx.backends.teavm.config.AssetsCopy;
import com.github.xpenatan.gdx.backends.teavm.config.TeaClassLoader;
import com.github.xpenatan.gdx.backends.teavm.config.TeaLogHelper;
import com.github.xpenatan.gdx.backends.teavm.config.TeaVMResourceProperties;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.teavm.diagnostics.DefaultProblemTextConsumer;
import org.teavm.diagnostics.Problem;
import org.teavm.diagnostics.ProblemProvider;
import org.teavm.model.CallLocation;
import org.teavm.model.MethodReference;
import org.teavm.tooling.TeaVMProblemRenderer;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.tooling.TeaVMTool;
import org.teavm.tooling.sources.DirectorySourceFileProvider;
import org.teavm.tooling.sources.JarSourceFileProvider;
import org.teavm.vm.TeaVMPhase;
import org.teavm.vm.TeaVMProgressFeedback;
import org.teavm.vm.TeaVMProgressListener;

public abstract class TeaBackend {
    final public static String ASSETS_FOLDER_NAME = "assets";
    private ArrayList<URL> acceptedURL;
    protected TeaClassLoader classLoader;
    protected TeaVMTool tool;
    protected TeaVMTargetType targetType;
    public boolean logClassNames;
    protected File releasePath;

    protected abstract void setup(TeaCompilerData data);

    final void compile(TeaCompilerData data) {
        acceptedURL = new ArrayList<>();
        configClasspath(data, acceptedURL);
        URL[] classPaths = acceptedURL.toArray(new URL[acceptedURL.size()]);
        classLoader = new TeaClassLoader(classPaths, TeaBackend.class.getClassLoader());

        setupSources(data);
        initializeTeavmTool(data);
        setup(data);
        configAssets(data);
        build(data);
    }

    private void initializeTeavmTool(TeaCompilerData data) {
        tool = new TeaVMTool();
        tool.setObfuscated(data.isObfuscated);
        tool.setOptimizationLevel(data.optimizationLevel);
        tool.setMainClass(data.mainClass);
        tool.setClassLoader(classLoader);
        tool.setSourceFilePolicy(data.sourceFilePolicy);
        for(int i = 0; i < data.sourceFileProviders.size(); i++) {
            tool.addSourceFileProvider(data.sourceFileProviders.get(i));
        }
        tool.setDebugInformationGenerated(data.debugInformationGenerated);
        tool.setSourceMapsFileGenerated(data.sourceMapsFileGenerated);
        tool.setMinHeapSize(data.minHeapSize);
        tool.setMaxHeapSize(data.maxHeapSize);
        tool.setMinDirectBuffersSize(data.minDirectBuffersSize);
        tool.setMaxDirectBuffersSize(data.maxDirectBuffersSize);
        tool.setTargetFileName(data.outputName);
        setupProgressListener();
    }

    private void configClasspath(TeaCompilerData data, ArrayList<URL> acceptedURL) {
        String pathSeparator = System.getProperty("path.separator");
        String[] classPathEntries = System.getProperty("java.class.path").split(pathSeparator);

        for (String path : classPathEntries) {
            File file = new File(path);
            // Ensure the path ends with "/" for directories
            if (file.isDirectory() && !path.endsWith(File.separator)) {
                path += File.separator;
            }
            try {
                acceptedURL.add(new File(path).toURI().toURL());
            } catch(MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        sortAcceptedClassPath(acceptedURL);
        setupReflection(data, acceptedURL);
        TeaLogHelper.logHeader("ACCEPTED CLASSPATH");
        for(int i = 0; i < acceptedURL.size(); i++) {
            TeaLogHelper.log(i + " true: " + acceptedURL.get(i).getPath());
        }
    }


    private void sortAcceptedClassPath(ArrayList<URL> acceptedURL) {
        // The idea here is to replace native java classes with the emulated java class.
        // 0 - TeaVM backend - Contains all teavm api stuff to make it work.
        // 1 - Extensions - Emulate native extension classes
//
//        final String EXTENSION_FREETYPE = "gdx-freetype-teavm";
//        final String EXTENSION_BOX2D_GWT = "gdx-box2d-gwt";
//
//        // TODO make a better sort. Lazy to do it now
//        // Move extensions to be first so native classes are replaced by the emulated classes
//        makeClassPathFirst(acceptedURL, EXTENSION_FREETYPE);
//        makeClassPathFirst(acceptedURL, EXTENSION_BOX2D_GWT);
//        // Move generic backend to be first
//        makeClassPathFirst(acceptedURL, "backend-teavm");
    }

    private void makeClassPathFirst(ArrayList<URL> acceptedURL, String module) {
        for(int i = 0; i < acceptedURL.size(); i++) {
            URL url = acceptedURL.get(i);
            String string = url.toString();
            if(string.contains(module)) {
                acceptedURL.remove(i);
                acceptedURL.add(0, url);
                break;
            }
        }
    }

    private void setupReflection(TeaCompilerData data, ArrayList<URL> acceptedURL) {
        for(URL classPath : acceptedURL) {
            try {
                ZipInputStream zip = new ZipInputStream(classPath.openStream());
                for(ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                    if(!entry.isDirectory() && entry.getName().endsWith(".class")) {
                        // This ZipEntry represents a class. Now, what class does it represent?
                        String className = entry.getName().replace('/', '.'); // including ".class"
                        String name = className.substring(0, className.length() - ".class".length());
                        boolean add = false;
                        if(data.reflectionListener != null) {
                            add = data.reflectionListener.shouldEnableReflection(name);
                        }
                        if(add) {
                            data.reflectionClasses.add(name);
                        }
                    }
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void build(TeaCompilerData data) {
        boolean isSuccess = false;
        try {
            long timeStart = new Date().getTime();
            tool.setTargetType(targetType);
            tool.generate();
            long timeEnd = new Date().getTime();
            float seconds = (timeEnd - timeStart) / 1000f;
            ProblemProvider problemProvider = tool.getProblemProvider();
            Collection<String> classes = tool.getClasses();
            List<Problem> problems = problemProvider.getProblems();
            if(problems.size() > 0) {
                TeaLogHelper.logHeader("Compiler problems");

                DefaultProblemTextConsumer p = new DefaultProblemTextConsumer();

                for(int i = 0; i < problems.size(); i++) {
                    Problem problem = problems.get(i);
                    CallLocation location = problem.getLocation();
                    MethodReference method = location != null ? location.getMethod() : null;

                    if(i > 0) {
                        TeaLogHelper.log("");
                        TeaLogHelper.log("----");
                        TeaLogHelper.log("");
                    }
                    TeaLogHelper.log(problem.getSeverity().toString() + "[" + i + "]");
                    var sb = new StringBuilder();
                    TeaVMProblemRenderer.renderCallStack(tool.getDependencyInfo().getCallGraph(),
                            problem.getLocation(), sb);
                    var locationString = sb.toString();
                    locationString.lines().forEach(TeaLogHelper::log);
                    p.clear();
                    problem.render(p);
                    String text = p.getText();
                    TeaLogHelper.log("Text: " + text);
                }
                TeaLogHelper.logEnd();
            }
            else {
                isSuccess = true;
                TeaLogHelper.logHeader("Build complete in " + seconds + " seconds. Total Classes: " + classes.size());
            }

            if(logClassNames) {
                Stream<String> sorted = classes.stream().sorted();
                Iterator<String> iterator = sorted.iterator();
                while(iterator.hasNext()) {
                    String clazz = iterator.next();
                    TeaLogHelper.log(clazz);
                }
            }
        }
        catch(Throwable e) {
            throw new RuntimeException(e);
        }

        if(!isSuccess) {
            throw new RuntimeException("Build Failed");
        }
    }

    private void setupProgressListener() {
        tool.setProgressListener(new TeaVMProgressListener() {
            TeaVMPhase phase = null;

            @Override
            public TeaVMProgressFeedback phaseStarted(TeaVMPhase teaVMPhase, int i) {
                if(teaVMPhase == TeaVMPhase.DEPENDENCY_ANALYSIS) {
                    TeaLogHelper.logHeader("DEPENDENCY_ANALYSIS");
                }
                else if(teaVMPhase == TeaVMPhase.COMPILING) {
                    TeaLogHelper.logInternalNewLine("");
                    TeaLogHelper.logHeader("COMPILING");
                }
                phase = teaVMPhase;
                return TeaVMProgressFeedback.CONTINUE;
            }

            @Override
            public TeaVMProgressFeedback progressReached(int i) {
                if(phase == TeaVMPhase.DEPENDENCY_ANALYSIS) {
                    TeaLogHelper.logInternal("|");
                }
                return TeaVMProgressFeedback.CONTINUE;
            }
        });
    }

    private void setupSources(TeaCompilerData data) {
        HashSet<String> set = new HashSet<>();
        for(int i = 0; i < acceptedURL.size(); i++) {
            URL url = acceptedURL.get(i);
            try {
                URI uri = url.toURI();

                String path = uri.getPath();
                File file = new File(uri);

                if(file.isFile() && path.endsWith("-sources.jar")) {
                    String absolutePath = file.getAbsolutePath();
                    if(!set.contains(absolutePath)) {
                        set.add(absolutePath);
                        data.sourceFileProviders.add(new JarSourceFileProvider(file));
                    }
                }
                else {
                    File sourceFile = getSourceDirectory(file);
                    if(sourceFile != null) {
                        String absolutePath = sourceFile.getAbsolutePath();
                        if(!set.contains(absolutePath)) {
                            set.add(absolutePath);
                            data.sourceFileProviders.add(new DirectorySourceFileProvider(sourceFile));
                        }
                    }
                }
            } catch(URISyntaxException e) {
            }
        }
    }

    private static File getSourceDirectory(File file) {
        File buildRoot = file;
        while(buildRoot != null && !buildRoot.getName().equals("build")) {
            buildRoot = buildRoot.getParentFile();
        }
        if(buildRoot != null) {
            Path moduleRoot = buildRoot.getParentFile().toPath();
            Path sourceDir = moduleRoot.resolve("src/main/java");
            if(Files.exists(sourceDir) && Files.isDirectory(sourceDir)) {
                return sourceDir.toFile();
            }
        }
        return null;
    }

    public void configAssets(TeaCompilerData data) {
        TeaLogHelper.logHeader("COPYING ASSETS");
        FileHandle outputFolder = new FileHandle(data.output);
        FileHandle releaseFolder = new FileHandle(releasePath);
        FileHandle assetsFolder = releaseFolder.child("assets");
        FileHandle scriptsFolder = releaseFolder.child("scripts");
        FileHandle assetFile = assetsFolder.child("assets.txt");

        ArrayList<AssetsCopy.Asset> alLAssets = new ArrayList<>();
        // Copy Assets files
        ArrayList<AssetFileHandle> assetsPaths = data.assets;
        for(int i = 0; i < assetsPaths.size(); i++) {
            AssetFileHandle assetFileHandle = assetsPaths.get(i);
            ArrayList<AssetsCopy.Asset> assets = AssetsCopy.copyAssets(assetFileHandle, null, assetsFolder);
            alLAssets.addAll(assets);
        }

        if(assetFile.exists()) {
            // Delete assets.txt before adding the updated list.
            assetFile.delete();
        }

        AssetsCopy.generateAssetsFile(alLAssets, assetsFolder, assetFile);

        // Copy assets from resources
        List<String> resources = TeaVMResourceProperties.getResources(acceptedURL);

        List<String> scripts = new ArrayList<>();
        List<String> cppFiles = new ArrayList<>();
        // Filter out javascript
        for(int i = 0; i < resources.size(); i++) {
            String asset = resources.get(i);
            if(asset.endsWith(".js") || asset.endsWith(".wasm")) {
                resources.remove(i);
                scripts.add(asset);
                i--;
            }
            else if(asset.startsWith("/external_cpp/")) {
                if(asset.endsWith(".lib") || asset.endsWith(".a") || asset.endsWith(".h")) {
                    cppFiles.add(asset);
                }
                resources.remove(i);
                i--;
            }
        }
        // Copy additional classpath files
//        ArrayList<String> classPathAssetsFiles = configuration.assetsClasspath;
//        ArrayList<AssetsCopy.Asset> classpathAssets = AssetsCopy.copyResources(classLoader, classPathAssetsFiles, filter, assetsFolder);
//        AssetsCopy.generateAssetsFile(classpathAssets, assetsFolder, assetFile);

        // Copy resources
        ArrayList<AssetsCopy.Asset> resourceAssets = AssetsCopy.copyResources(classLoader, resources, null, assetsFolder);
        AssetsCopy.generateAssetsFile(resourceAssets, assetsFolder, assetFile);

        // Copy scripts

        if(targetType == TeaVMTargetType.C) {
            AssetsCopy.copyResources(classLoader, cppFiles, null, outputFolder);
        }
        else {
            AssetsCopy.copyScripts(classLoader, scripts, scriptsFolder);
        }

        TeaLogHelper.log("");
    }
}