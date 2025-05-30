package com.github.xpenatan.gdx.backends.teavm.config;

import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.backends.teavm.TeaClassLoader;
import com.github.xpenatan.gdx.backends.teavm.config.plugins.TeaClassTransformer;
import com.github.xpenatan.gdx.backends.teavm.config.plugins.TeaReflectionSupplier;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import org.teavm.vm.TeaVMOptimizationLevel;
import org.teavm.vm.TeaVMPhase;
import org.teavm.vm.TeaVMProgressFeedback;
import org.teavm.vm.TeaVMProgressListener;

/**
 * @author xpenatan
 */
public class TeaBuilder {

    public static void log(String msg) {
        String text = "| " + msg;
        logInternalNewLine(text);
    }

    public static void logInternal(String msg) {
        System.err.print(msg);
    }

    public static void logInternalNewLine(String msg) {
        logInternal(msg + "\n");
    }

    public static void logHeader(String text) {
        String msg = "";
        msg += "#################################################################\n";
        msg += "|\n| " + text + "\n|";
        msg += "\n" + "#################################################################";

        logInternalNewLine(msg);
    }

    public static void logEnd() {
        String msg = "\n#################################################################";
        logInternalNewLine(msg);
    }

    enum ACCEPT_STATE {
        ACCEPT, NOT_ACCEPT, NO_MATCH
    }

    public interface TeaProgressListener {
        void onProgress(float progress);
    }

    private static final String EXTENSION_FREETYPE = "gdx-freetype-teavm";
    private static final String EXTENSION_BOX2D = "gdx-box2d-teavm";
    private static final String EXTENSION_BOX2D_GWT = "gdx-box2d-gwt";

    private static String webappName = "webapp";
    private static TeaBuildConfiguration configuration;
    private static File setTargetDirectory;
    private static TeaClassLoader classLoader;
    private static ArrayList<URL> acceptedURL;

    public static TeaVMTool config(TeaBuildConfiguration configuration) {
        TeaVMTool tool = new TeaVMTool();
        return config(tool, configuration, null);
    }

    public static TeaVMTool config(TeaBuildConfiguration configuration, TeaProgressListener progressListener) {
        TeaVMTool tool = new TeaVMTool();
        return config(tool, configuration, progressListener);
    }

    public static TeaVMTool config(TeaVMTool tool, TeaBuildConfiguration configuration, TeaProgressListener progressListener) {
        TeaBuilder.configuration = configuration;
        acceptedURL = new ArrayList<>();
        String webappDirectory = configuration.webappPath;

        addDefaultReflectionClasses();
        automaticReflection(configuration);
        configClasspath(configuration, acceptedURL);

        TeaBuilder.log("");
        TeaBuilder.log("targetDirectory: " + webappDirectory);
        TeaBuilder.log("");

        URL[] classPaths = acceptedURL.toArray(new URL[acceptedURL.size()]);
        classLoader = new TeaClassLoader(classPaths, TeaBuilder.class.getClassLoader());

        setTargetDirectory = new File(webappDirectory + File.separator + webappName + File.separator + "teavm");

        configTool(tool, progressListener);
        return tool;
    }

    public static boolean build(TeaVMTool tool) {
        return build(tool, false);
    }

    public static boolean build(TeaVMTool tool, boolean logClassNames) {
        boolean isSuccess = false;
        try {
            configAssets(tool);
            long timeStart = new Date().getTime();
            tool.generate();
            long timeEnd = new Date().getTime();
            float seconds = (timeEnd - timeStart) / 1000f;
            ProblemProvider problemProvider = tool.getProblemProvider();
            Collection<String> classes = tool.getClasses();
            List<Problem> problems = problemProvider.getProblems();
            if(problems.size() > 0) {
                TeaBuilder.logHeader("Compiler problems");

                DefaultProblemTextConsumer p = new DefaultProblemTextConsumer();

                for(int i = 0; i < problems.size(); i++) {
                    Problem problem = problems.get(i);
                    CallLocation location = problem.getLocation();
                    MethodReference method = location != null ? location.getMethod() : null;

                    if(i > 0) {
                        TeaBuilder.log("");
                        TeaBuilder.log("----");
                        TeaBuilder.log("");
                    }
                    TeaBuilder.log(problem.getSeverity().toString() + "[" + i + "]");
                    var sb = new StringBuilder();
                    TeaVMProblemRenderer.renderCallStack(tool.getDependencyInfo().getCallGraph(),
                            problem.getLocation(), sb);
                    var locationString = sb.toString();
                    locationString.lines().forEach(TeaBuilder::log);
                    p.clear();
                    problem.render(p);
                    String text = p.getText();
                    TeaBuilder.log("Text: " + text);
                }
                TeaBuilder.logEnd();
            }
            else {
                isSuccess = true;
                TeaBuilder.logHeader("Build complete in " + seconds + " seconds. Total Classes: " + classes.size());
            }

            if(logClassNames) {
                Stream<String> sorted = classes.stream().sorted();
                Iterator<String> iterator = sorted.iterator();
                while(iterator.hasNext()) {
                    String clazz = iterator.next();
                    TeaBuilder.log(clazz);
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
        makeClassPathFirst(acceptedURL, "backend-teavm");
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

    private static void automaticReflection(TeaBuildConfiguration configuration) {
        for(URL classPath : configuration.additionalClasspath) {
            try {
                ZipInputStream zip = new ZipInputStream(classPath.openStream());
                TeaBuilder.logHeader("Automatic Reflection Include");
                for(ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                    if(!entry.isDirectory() && entry.getName().endsWith(".class")) {
                        // This ZipEntry represents a class. Now, what class does it represent?
                        String className = entry.getName().replace('/', '.'); // including ".class"
                        String name = className.substring(0, className.length() - ".class".length());
                        boolean add = false;
                        for(String toInclude : configuration.reflectionInclude) {
                            if(name.startsWith(toInclude)) add = true;
                        }
                        for(String toExclude : configuration.reflectionExclude) {
                            if(name.startsWith(toExclude)) add = false;
                        }

                        if(add) {
                            TeaBuilder.log("Include class: " + name);
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

    private static void addDefaultReflectionClasses() {
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.scenes.scene2d");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.math");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g2d.GlyphLayout");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g2d.TextureRegion");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g2d.Sprite");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g2d.BitmapFont");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g2d.NinePatch");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g3d.materials.MaterialAttribute");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.Color");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.Texture");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.utils.Array");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.utils.Disposable");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.utils.Json");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.utils.ObjectMap");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.utils.OrderedMap");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.utils.Queue");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.utils.Predicate");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.VertexAttribute");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g3d.model");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.Net");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.maps.MapObject");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.maps.objects");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.maps.tiled.objects");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g3d.particles.ParticleEffect");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g3d.particles.ParticleController");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g3d.particles.ResourceData");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g3d.particles.ResourceData.SaveData");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g3d.particles.ResourceData.AssetData");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g3d.particles.ParallelArray");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g3d.particles.values");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g3d.particles.emitters");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g3d.particles.influencers");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.g3d.particles.renderers");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.math.Interpolation");
        TeaReflectionSupplier.addReflectionClass("net.mgsx.gltf.data");
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

        TeaBuilder.logHeader("ACCEPTED CLASSPATH");
        for(int i = 0; i < acceptedURL.size(); i++) {
            TeaBuilder.log(i + " true: " + acceptedURL.get(i).getPath());
        }
    }

    private static void configTool(TeaVMTool tool, TeaProgressListener progressListener) {
        boolean setDebugInformationGenerated = false;
        boolean setSourceMapsFileGenerated = false;
        boolean setSourceFilesCopied = false;

        String setTargetFileName = "app.js";
        TeaVMTargetType targetType = TeaVMTargetType.JAVASCRIPT;
        String tmpdir = System.getProperty("java.io.tmpdir");
        File setCacheDirectory = new File(tmpdir + File.separator + "TeaVMCache");
        boolean setIncremental = false;

        tool.setClassLoader(classLoader);
        tool.setDebugInformationGenerated(setDebugInformationGenerated);
        tool.setSourceMapsFileGenerated(setSourceMapsFileGenerated);
        tool.setSourceFilesCopied(setSourceFilesCopied);
        tool.setTargetDirectory(setTargetDirectory);
        tool.setTargetFileName(setTargetFileName);
        tool.setFastDependencyAnalysis(false);
        tool.setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE);
        String applicationListenerClass = configuration.mainApplicationClass;
        String mainClass = configuration.mainClass;
        if(applicationListenerClass != null) {
            TeaClassTransformer.applicationListener = applicationListenerClass;
            TeaClassTransformer.mainClass = mainClass;
        }
        tool.setMainClass(mainClass);
        tool.setIncremental(setIncremental);
        tool.setCacheDirectory(setCacheDirectory);
        tool.setStrict(false);
        tool.setTargetType(targetType);
        tool.setProgressListener(new TeaVMProgressListener() {
            TeaVMPhase phase = null;

            @Override
            public TeaVMProgressFeedback phaseStarted(TeaVMPhase teaVMPhase, int i) {
                if(teaVMPhase == TeaVMPhase.DEPENDENCY_ANALYSIS) {
                    TeaBuilder.logHeader("DEPENDENCY_ANALYSIS");
                }
                else if(teaVMPhase == TeaVMPhase.COMPILING) {
                    TeaBuilder.logInternalNewLine("");
                    TeaBuilder.logHeader("COMPILING");
                }
                phase = teaVMPhase;
                return TeaVMProgressFeedback.CONTINUE;
            }

            @Override
            public TeaVMProgressFeedback progressReached(int i) {
                if(phase == TeaVMPhase.DEPENDENCY_ANALYSIS) {
                    TeaBuilder.logInternal("|");
                }
                if(phase == TeaVMPhase.COMPILING) {
                    if(progressListener != null) {
                        float progress = i / 1000f;
                        progressListener.onProgress(progress);
                    }
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

    public static void configAssets(TeaVMTool tool) {
        TeaBuilder.logHeader("COPYING ASSETS");
        String webappDirectory = configuration.webappPath;;
        FileHandle webappDistFolder = new FileHandle(webappDirectory);
        FileHandle webappFolder = webappDistFolder.child(webappName);
        FileHandle assetsFolder = webappFolder.child("assets");
        FileHandle scriptsFolder = webappFolder.child("scripts");
        FileHandle assetFile = assetsFolder.child("assets.txt");

        AssetFilter filter = configuration.assetFilter;

        boolean shouldUseDefaultHtmlIndex = configuration.useDefaultHtmlIndex;
        if(shouldUseDefaultHtmlIndex) {
            useDefaultHTMLIndexFile(tool, webappDistFolder, webappFolder);
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
        ArrayList<String> classPathAssetsFiles = configuration.additionalAssetsClasspathFiles;
        ArrayList<AssetsCopy.Asset> classpathAssets = AssetsCopy.copyResources(classLoader, classPathAssetsFiles, filter, assetsFolder);

        // Copy resources
        ArrayList<AssetsCopy.Asset> resourceAssets = AssetsCopy.copyResources(classLoader, resources, filter, assetsFolder);

        // Copy scripts
        ArrayList<AssetsCopy.Asset> scriptsAssets = AssetsCopy.copyScripts(classLoader, scripts, scriptsFolder);

        AssetsCopy.generateAssetsFile(classpathAssets, assetsFolder, assetFile);
        AssetsCopy.generateAssetsFile(resourceAssets, assetsFolder, assetFile);

        TeaBuilder.log("");
    }

    private static void useDefaultHTMLIndexFile(TeaVMTool tool, FileHandle webappDistFolder, FileHandle webappFolder) {
        ArrayList<String> webappAssetsFiles = new ArrayList<>();
        webappAssetsFiles.add(webappName);
        // Copy webapp folder from resources to destination
        AssetsCopy.copyResources(classLoader, webappAssetsFiles, null, webappDistFolder);
        TeaBuilder.log("");

        FileHandle handler = webappFolder.child("index.html");
        String indexHtmlStr = handler.readString();

        String logo = configuration.logoPath;
        String htmlLogo = logo;
        boolean showLoadingLogo = configuration.showLoadingLogo;

        String mode = "main(%ARGS%)";
        String jsScript = "<script type=\"text/javascript\" charset=\"utf-8\" src=\"teavm/app.js\"></script>";
        if(tool.getTargetType() == TeaVMTargetType.WEBASSEMBLY_GC) {
            copyRuntime(setTargetDirectory);
            mode = "let teavm = await TeaVM.wasmGC.load(\"teavm/app.js.wasm\"); teavm.exports.main([%ARGS%]);";
            String jsName = "wasm-gc-runtime.min";
            jsScript = "<script type=\"text/javascript\" charset=\"utf-8\" src=\"teavm/" + jsName + ".js\"></script>";
        }

        indexHtmlStr = indexHtmlStr.replace("%MODE%", mode);
        indexHtmlStr = indexHtmlStr.replace("%JS_SCRIPT%", jsScript);
        indexHtmlStr = indexHtmlStr.replace("%TITLE%", configuration.htmlTitle);
        indexHtmlStr = indexHtmlStr.replace("%WIDTH%", String.valueOf(configuration.htmlWidth));
        indexHtmlStr = indexHtmlStr.replace("%HEIGHT%", String.valueOf(configuration.htmlHeight));
        indexHtmlStr = indexHtmlStr.replace("%ARGS%", configuration.mainClassArgs);
        indexHtmlStr = indexHtmlStr.replace(
                "%LOGO%", showLoadingLogo ? "<img id=\"progress-img\" src=\"" + htmlLogo + "\">" : ""
        );

        handler.writeString(indexHtmlStr, false);

        if(showLoadingLogo) {
            ArrayList<String> logoAsset = new ArrayList<>();
            logoAsset.add(logo);
            AssetsCopy.copyResources(classLoader, logoAsset, null, webappFolder);
        }
    }
}
