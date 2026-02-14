package com.github.xpenatan.gdx.teavm.backends.web.config;

import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFilter;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetsCopy;
import com.github.xpenatan.gdx.teavm.backends.shared.config.TeaAssets;
import com.github.xpenatan.gdx.teavm.backends.shared.config.TeaClassLoader;
import com.github.xpenatan.gdx.teavm.backends.shared.config.TeaLogHelper;
import com.github.xpenatan.gdx.teavm.backends.shared.config.TeaVMResourceProperties;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.TeaReflectionSupplier;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

/**
 * @author xpenatan
 */
@Deprecated
public class TeaBuilder {

    enum ACCEPT_STATE {
        ACCEPT, NOT_ACCEPT, NO_MATCH
    }

    private static final String EXTENSION_FREETYPE = "gdx-freetype-teavm";
    private static final String EXTENSION_BOX2D = "gdx-box2d-teavm";
    private static final String EXTENSION_BOX2D_GWT = "gdx-box2d-gwt";

    private static String webappName = "webapp";
    private static TeaBuildConfiguration configuration;
    private static File setTargetDirectory;
    private static TeaClassLoader classLoader;
    private static ArrayList<URL> acceptedURL;

    public static void config(TeaBuildConfiguration configuration) {
        TeaBuilder.configuration = configuration;
        acceptedURL = new ArrayList<>();
        String webappDirectory = configuration.webappPath;

        configClasspath(configuration, acceptedURL);

        TeaLogHelper.log("");
        TeaLogHelper.log("targetDirectory: " + webappDirectory);
        TeaLogHelper.log("");

        URL[] classPaths = acceptedURL.toArray(new URL[acceptedURL.size()]);
        classLoader = new TeaClassLoader(classPaths, TeaBuilder.class.getClassLoader());

        setTargetDirectory = new File(webappDirectory + File.separator + webappName);

        configAssets();
    }

    public static boolean build(TeaVMTool tool) {
        return build(tool, false);
    }

    public static boolean build(TeaVMTool tool, boolean logClassNames) {
        boolean isSuccess = false;
        try {
            configTool(tool);

            long timeStart = new Date().getTime();
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
        return isSuccess;
    }

    private static void preserveClasses(TeaVMTool tool, TeaBuildConfiguration configuration, TeaClassLoader classLoader) {
        //Keep reflection classes
        List<String> classesToPreserve = tool.getClassesToPreserve();
        ArrayList<String> configClassesToPreserve = configuration.classesToPreserve;
        List<String> reflectionClasses = TeaReflectionSupplier.getReflectionClasses();
        configClassesToPreserve.addAll(reflectionClasses);
        // Get classes or packages from reflection. When path is a package, get all classes from it.
        ArrayList<String> preserveClasses = classLoader.getAllClasses(configClassesToPreserve);
        classesToPreserve.addAll(preserveClasses);
    }

    private static void sortAcceptedClassPath(ArrayList<URL> acceptedURL) {
        // The idea here is to replace native java classes with the emulated java class.
        // 0 - TeaVM backend - Contains all teavm api stuff to make it work.
        // 1 - Extensions - Emulate native extension classes

        // TODO make a better sort. Lazy to do it now
        // Move extensions to be first so native classes are replaced by the emulated classes
        makeClassPathFirst(acceptedURL, EXTENSION_FREETYPE);
        makeClassPathFirst(acceptedURL, EXTENSION_BOX2D);
        makeClassPathFirst(acceptedURL, EXTENSION_BOX2D_GWT);
        // Move generic backend to be first
        makeClassPathFirst(acceptedURL, "backend-web");
    }

    private static void makeClassPathFirst(ArrayList<URL> acceptedURL, String module) {
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

    private static void automaticReflection(ArrayList<URL> acceptedURL) {
        for(URL classPath : acceptedURL) {
            try {
                ZipInputStream zip = new ZipInputStream(classPath.openStream());
                for(ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                    if(!entry.isDirectory() && entry.getName().endsWith(".class")) {
                        // This ZipEntry represents a class. Now, what class does it represent?
                        String className = entry.getName().replace('/', '.'); // including ".class"
                        String name = className.substring(0, className.length() - ".class".length());
                        boolean add = false;
                        if(configuration.reflectionListener != null) {
                            add = configuration.reflectionListener.shouldEnableReflection(name);
                        }
                        if(add) {
                            TeaReflectionSupplier.addReflectionClass(name);
                        }
                    }
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void configClasspath(TeaBuildConfiguration configuration, ArrayList<URL> acceptedURL) {
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

        acceptedURL.addAll(configuration.additionalClasspath);

        sortAcceptedClassPath(acceptedURL);

        automaticReflection(acceptedURL);

        TeaLogHelper.logHeader("ACCEPTED CLASSPATH");
        for(int i = 0; i < acceptedURL.size(); i++) {
            TeaLogHelper.log(i + " true: " + acceptedURL.get(i).getPath());
        }
    }

    private static void configTool(TeaVMTool tool) {
        String tmpdir = System.getProperty("java.io.tmpdir");
        File setCacheDirectory = new File(tmpdir + File.separator + "TeaVMCache");
        tool.setTargetType(configuration.targetType);
        if(configuration.targetType == TeaVMTargetType.WEBASSEMBLY_GC) {
            tool.setTargetFileName(configuration.targetFileName + ".wasm");
        }
        else if(configuration.targetType == TeaVMTargetType.JAVASCRIPT) {
            tool.setTargetFileName(configuration.targetFileName + ".js");
        }

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
                        tool.addSourceFileProvider(new JarSourceFileProvider(file));
                    }
                }
                else {
                    File sourceFile = getSourceDirectory(file);
                    if(sourceFile != null) {
                        String absolutePath = sourceFile.getAbsolutePath();
                        if(!set.contains(absolutePath)) {
                            set.add(absolutePath);
                            tool.addSourceFileProvider(new DirectorySourceFileProvider(sourceFile));
                        }
                    }
                }
            } catch(URISyntaxException e) {
            }
        }

        tool.setClassLoader(classLoader);
        tool.setTargetDirectory(setTargetDirectory);
        tool.setCacheDirectory(setCacheDirectory);
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
        preserveClasses(tool, configuration, classLoader);
    }

    public static void copyRuntime(File setTargetDirectory) {
        try {
            var name = new StringBuilder("wasm-gc-runtime.min");
            setTargetDirectory.mkdirs();
            var resourceName = "org/teavm/backend/wasm/" + name + ".js";
            var classLoader = TeaBuilder.class.getClassLoader();
            try (var input = classLoader.getResourceAsStream(resourceName)) {
                Files.copy(input, setTargetDirectory.toPath().resolve(name + ".js"), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch(Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static void configAssets() {
        TeaLogHelper.logHeader("COPYING ASSETS");
        String webappDirectory = configuration.webappPath;;
        FileHandle distFolder = new FileHandle(webappDirectory);
        FileHandle webappFolder = distFolder.child(webappName);
        FileHandle assetsFolder = webappFolder.child("assets");
        FileHandle scriptsFolder = webappFolder.child("scripts");
        FileHandle assetFile = assetsFolder.child(TeaAssets.ASSETS_FILE_NAME);

        AssetFilter filter = configuration.assetFilter;

        if(configuration.useDefaultHtmlIndex) {
            BaseWebApp webApp = configuration.webApp;
            if(webApp == null) {
                webApp = new DefaultWebApp();
            }
            webApp.setup(classLoader, configuration, webappFolder);
        }
        if(configuration.targetType == TeaVMTargetType.WEBASSEMBLY_GC) {
            copyRuntime(setTargetDirectory);
        }

        boolean generateAssetPaths = configuration.shouldGenerateAssetFile;

        ArrayList<AssetsCopy.Asset> alLAssets = new ArrayList<>();
        // Copy Assets files
        ArrayList<AssetFileHandle> assetsPaths = configuration.assetsPath;
        for(int i = 0; i < assetsPaths.size(); i++) {
            AssetFileHandle assetFileHandle = assetsPaths.get(i);
            ArrayList<AssetsCopy.Asset> assets = AssetsCopy.copyAssets(assetFileHandle, filter, assetsFolder);
            alLAssets.addAll(assets);
        }

        if(assetFile.exists()) {
            // Delete assets.txt before adding the updated list.
            assetFile.delete();
        }

        if(generateAssetPaths) {
            AssetsCopy.generateAssetsFile(alLAssets, assetsFolder, assetFile);
        }

        // Copy assets from resources
        List<String> resources = TeaVMResourceProperties.getResources(acceptedURL);

        List<String> scripts = new ArrayList<>();
        // Filter out javascript
        for(int i = 0; i < resources.size(); i++) {
            String asset = resources.get(i);
            if(asset.endsWith(".js") || asset.endsWith(".wasm")) {
                resources.remove(i);
                scripts.add(asset);
                i--;
            }
        }
        // Copy additional classpath files
        ArrayList<String> classPathAssetsFiles = configuration.assetsClasspath;
        ArrayList<AssetsCopy.Asset> classpathAssets = AssetsCopy.copyResources(classLoader, classPathAssetsFiles, filter, assetsFolder);

        // Copy resources
        ArrayList<AssetsCopy.Asset> resourceAssets = AssetsCopy.copyResources(classLoader, resources, filter, assetsFolder);

        // Copy scripts
        ArrayList<AssetsCopy.Asset> scriptsAssets = AssetsCopy.copyScripts(classLoader, scripts, scriptsFolder);

        AssetsCopy.generateAssetsFile(classpathAssets, assetsFolder, assetFile);
        AssetsCopy.generateAssetsFile(resourceAssets, assetsFolder, assetFile);

        TeaLogHelper.log("");
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
}
