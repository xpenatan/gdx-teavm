package com.github.xpenatan.gdx.backends.teavm;

import com.github.xpenatan.gdx.backends.web.WebBuildConfiguration;
import com.github.xpenatan.gdx.backends.web.WebClassLoader;
import com.github.xpenatan.gdx.backends.web.preloader.AssetsCopy;
import com.github.xpenatan.gdx.backends.teavm.plugins.TeaClassTransformer;
import com.github.xpenatan.gdx.backends.teavm.plugins.TeaReflectionSupplier;
import org.teavm.diagnostics.DefaultProblemTextConsumer;
import org.teavm.diagnostics.Problem;
import org.teavm.diagnostics.ProblemProvider;
import org.teavm.model.CallLocation;
import org.teavm.model.MethodReference;
import org.teavm.model.TextLocation;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.vm.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * @author xpenatan
 */
public class TeaBuilder {

    private static final String EXTENSION_FREETYPE = "gdx-freetype-teavm";
    private static final String EXTENSION_BULLET = "gdx-bullet-teavm";
    private static final String EXTENSION_BOX2D = "gdx-box2d-teavm";
    private static final String EXTENSION_BOX2D_GWT = "gdx-box2d-gwt";

    public static void build(WebBuildConfiguration configuration) {
        build(configuration, null);
    }

    public static void build(WebBuildConfiguration configuration, TeaProgressListener progressListener) {
        addDefaultReflectionClasses();
        for (URL classPath : configuration.getAdditionalClasspath()) {
            try {
                ZipInputStream zip = new ZipInputStream(classPath.openStream());
                WebBuildConfiguration.logHeader("Automatic Reflection Include");
                for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                    if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                        // This ZipEntry represents a class. Now, what class does it represent?
                        String className = entry.getName().replace('/', '.'); // including ".class"
                        String name = className.substring(0, className.length() - ".class".length());
                        boolean add = false;
                        for (String toInclude : configuration.getReflectionInclude()) {
                            if (name.startsWith(toInclude)) add = true;
                        }
                        for (String toExclude : configuration.getReflectionExclude()) {
                            if (name.startsWith(toExclude)) add = false;
                        }

                        if (add) {
                            WebBuildConfiguration.log("Include class: " + name);
                            TeaReflectionSupplier.addReflectionClass(name);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String pathSeparator = System.getProperty("path.separator");
        String[] classPathEntries = System.getProperty("java.class.path") .split(pathSeparator);
        
        ArrayList<URL> acceptedURL = new ArrayList<>();
        ArrayList<URL> notAcceptedURL = new ArrayList<>();

        for (int i = 0; i < classPathEntries.length; i++) {
            String path = classPathEntries[i];
            File file = new File(path);
            path = path.replace("\\", "/");
            URL url = null;
            try {
                url = file.toURI().toURL();
            } catch(MalformedURLException e) {
                e.printStackTrace();
            }
            ACCEPT_STATE acceptState = acceptPath(path);
            boolean accept = acceptState == ACCEPT_STATE.ACCEPT;
            if (accept || acceptState == ACCEPT_STATE.NO_MATCH)
                accept = configuration.acceptClasspath(url);

            if (accept)
                acceptedURL.add(url);
            else
                notAcceptedURL.add(url);
        }

        acceptedURL.addAll(configuration.getAdditionalClasspath());

        sortAcceptedClassPath(acceptedURL);

        WebBuildConfiguration.logHeader("Accepted Libs ClassPath Order");

        for (int i = 0; i < acceptedURL.size(); i++) {
            WebBuildConfiguration.log(i + " true: " + acceptedURL.get(i).getPath());
        }

        WebBuildConfiguration.logHeader("Not Accepted Libs ClassPath");

        for (int i = 0; i < notAcceptedURL.size(); i++) {
            WebBuildConfiguration.log(i + " false: " + notAcceptedURL.get(i).getPath());
        }

        int size = acceptedURL.size();

        if (size <= 0) {
            System.err.println("No urls found");
            return;
        }

        URL[] classPaths = acceptedURL.toArray(new URL[acceptedURL.size()]);
        WebClassLoader classLoader = new WebClassLoader(classPaths, TeaBuilder.class.getClassLoader());

        CustomTeaVMTool tool = new CustomTeaVMTool();

        boolean setDebugInformationGenerated = false;
        boolean setSourceMapsFileGenerated = false;
        boolean setSourceFilesCopied = false;

        String webappDirectory = configuration.getWebAppPath();

        String webappName = "webapp";

        System.err.println("targetDirectory: " + webappDirectory);

        File setTargetDirectory = new File(webappDirectory + File.separator + webappName + File.separator + "teavm");
        String setTargetFileName = "app.js";
        boolean setMinifying = configuration.minifying();
        String mainClass = configuration.getMainClass();
        TeaClassTransformer.applicationListener = configuration.getApplicationListenerClass();

        String tmpdir = System.getProperty("java.io.tmpdir");
        File setCacheDirectory = new File(tmpdir + File.separator + "TeaVMCache");
        boolean setIncremental = false;

        tool.setClassLoader(classLoader);
        tool.setDebugInformationGenerated(setDebugInformationGenerated);
        tool.setSourceMapsFileGenerated(setSourceMapsFileGenerated);
        tool.setSourceFilesCopied(setSourceFilesCopied);
        tool.setTargetDirectory(setTargetDirectory);
        tool.setTargetFileName(setTargetFileName);
        tool.setObfuscated(setMinifying);
        tool.setFastDependencyAnalysis(false);
        tool.setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE);
//		tool.setRuntime(mapRuntime(configuration.getRuntime()));
        tool.setMainClass(mainClass);
        //		tool.getProperties().putAll(profile.getProperties());
        tool.setIncremental(setIncremental);
        tool.setCacheDirectory(setCacheDirectory);
        tool.setStrict(false);
        tool.setTargetType(TeaVMTargetType.JAVASCRIPT);

        List<String> classesToPreserve = tool.getClassesToPreserve();

        ArrayList<String> configClassesToPreserve = configuration.getClassesToPreserve();
        List<String> reflectionClasses = TeaReflectionSupplier.getReflectionClasses();
        configClassesToPreserve.addAll(reflectionClasses);
        ArrayList<String> preserveClasses = classLoader.getPreserveClasses(configClassesToPreserve);
        classesToPreserve.addAll(preserveClasses);

        Properties properties = tool.getProperties();

        properties.put("teavm.libgdx.fsJsonPath", webappDirectory + File.separator + webappName + File.separator + "filesystem.json");
        properties.put("teavm.libgdx.warAssetsDirectory", webappDirectory + File.separator + webappName + File.separator + "assets");

        ArrayList<String> webappAssetsFiles = new ArrayList<>();
        webappAssetsFiles.add(webappName);
        AssetsCopy.copy(classLoader, webappAssetsFiles, new ArrayList<>(), webappDirectory, false);

        WebBuildConfiguration.logHeader("Copying Assets");

        String assetsOutputPath = webappDirectory + File.separator + webappName + File.separator + "assets";
        ArrayList<File> assetsPaths = new ArrayList<>();
        ArrayList<String> classPathAssetsFiles = new ArrayList<>();
        assetsDefaultClasspath(classPathAssetsFiles);
        ArrayList<String> additionalAssetClasspath = configuration.getAdditionalAssetClasspath();
        classPathAssetsFiles.addAll(additionalAssetClasspath);
        boolean generateAssetPaths = configuration.assetsPath(assetsPaths);
        AssetsCopy.copy(classLoader, classPathAssetsFiles, assetsPaths, assetsOutputPath, generateAssetPaths);
        tool.setProgressListener(new TeaVMProgressListener() {
            TeaVMPhase phase = null;
            @Override
            public TeaVMProgressFeedback phaseStarted(TeaVMPhase teaVMPhase, int i) {
                if(teaVMPhase == TeaVMPhase.DEPENDENCY_ANALYSIS) {
                    WebBuildConfiguration.logHeader("DEPENDENCY_ANALYSIS");
                }
                else if(teaVMPhase == TeaVMPhase.COMPILING) {
                    WebBuildConfiguration.logInternalNewLine("");
                    WebBuildConfiguration.logHeader("COMPILING");
                }
                phase = teaVMPhase;
                return TeaVMProgressFeedback.CONTINUE;
            }

            @Override
            public TeaVMProgressFeedback progressReached(int i) {
                if(phase == TeaVMPhase.DEPENDENCY_ANALYSIS) {
                    WebBuildConfiguration.logInternal("|");
                }
                if(phase == TeaVMPhase.COMPILING) {
                    if(progressListener!= null) {
                        float progress = i / 1000f;
                        progressListener.onProgress(progress);
                    }
                }
                return TeaVMProgressFeedback.CONTINUE;
            }
        });

        try {
            tool.generate();
            ProblemProvider problemProvider = tool.getProblemProvider();
            List<Problem> problems = problemProvider.getProblems();

            if (problems.size() > 0) {
                if(progressListener != null) {
                    progressListener.onSuccess(false);
                }
                WebBuildConfiguration.logHeader("Compiler problems");

                DefaultProblemTextConsumer p = new DefaultProblemTextConsumer();

                for (int i = 0; i < problems.size(); i++) {
                    Problem problem = problems.get(i);
                    CallLocation location = problem.getLocation();
                    MethodReference method = location != null ? location.getMethod() : null;
                    String classSource = "-";
                    String methodName = "-";

                    if (location != null) {
                        TextLocation sourceLocation = location.getSourceLocation();
                        if (sourceLocation != null)
                            classSource = sourceLocation.toString();
                        else {
                            //TODO
                        }
                        if (method != null) {
                            methodName = method.toString();
                        }
                    }

                    if (i > 0) {
                        WebBuildConfiguration.log("");
                        WebBuildConfiguration.log("----");
                        WebBuildConfiguration.log("");
                    }
                    WebBuildConfiguration.log(problem.getSeverity().toString() + "[" + i + "]");
                    WebBuildConfiguration.log("Class: " + classSource);
                    WebBuildConfiguration.log("Method: " + methodName);
                    p.clear();
                    problem.render(p);
                    String text = p.getText();
                    WebBuildConfiguration.log("Text: " + text);
                }
                WebBuildConfiguration.logEnd();
            } else {
                if(progressListener != null) {
                    progressListener.onSuccess(true);
                }
                Collection<String> classes = tool.getClasses();
                WebBuildConfiguration.logHeader("Build Complete. Total Classes: " + classes.size());

                boolean classLog = configuration.logClasses();

                if(classLog) {
                    Stream<String> sorted = classes.stream().sorted();
                    Iterator<String> iterator = sorted.iterator();
                    while(iterator.hasNext()) {
                        String clazz = iterator.next();
                        WebBuildConfiguration.log(clazz);
                    }
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void sortAcceptedClassPath(ArrayList<URL> acceptedURL) {
        // The idea here is to replace native java classes with the emulated java class.
        // 0 - TeaVM backend - Contains all teavm api stuff to make it work.
        // 1 - Backend-web - Emulate generic java classes. Some classes may be implemented by the teavm backend.
        // 2 - Extensions - Emulate native extension classes

        // TODO make a better sort. Lazy to do it now
        // Move extensions to be first so native classes are replaced by the emulated classes
        makeClassPathFirst(acceptedURL, EXTENSION_FREETYPE);
        makeClassPathFirst(acceptedURL, EXTENSION_BULLET);
        makeClassPathFirst(acceptedURL, EXTENSION_BOX2D);
        // Move generic backend to be first
        makeClassPathFirst(acceptedURL, "backend-web");
        makeClassPathFirst(acceptedURL, "backend-teavm");
    }

    private static void makeClassPathFirst(ArrayList<URL> acceptedURL, String module) {
        for (int i = 0; i < acceptedURL.size(); i++) {
            URL url = acceptedURL.get(i);
            String string = url.toString();
            if (string.contains(module)) {
                acceptedURL.remove(i);
                acceptedURL.add(0, url);
                break;
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

    private static void assetsDefaultClasspath(ArrayList<String> filePath) {
        filePath.add("com/badlogic/gdx/graphics/g3d/particles/");
        filePath.add("com/badlogic/gdx/graphics/g3d/shaders/");
        filePath.add("com/badlogic/gdx/utils/arial-15.fnt"); // Cannot be utils folder for now because its trying to copy from emu folder and not core gdx classpath
        filePath.add("com/badlogic/gdx/utils/arial-15.png");

        filePath.add("com/badlogic/gdx/utils/lsans-15.fnt");
        filePath.add("com/badlogic/gdx/utils/lsans-15.png");

        filePath.add("scripts/soundmanager2-jsmin.js");
        filePath.add("scripts/freetype.js");
        filePath.add("scripts/bullet.js");
        filePath.add("scripts/bullet.wasm.js");
        filePath.add("scripts/bullet.wasm.wasm");
    }

    private static ACCEPT_STATE acceptPath(String path) {
        ACCEPT_STATE isValid = ACCEPT_STATE.NO_MATCH;
        if (path.contains("teavm-") && path.contains(".jar"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("junit"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("hamcrest"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("jackson-"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("Java/jdk"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("commons-io"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("org/ow2"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("carrotsearch"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("google/code"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("jcraft"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("joda-time"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("mozilla"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("jutils"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("jinput-"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("lwjgl"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("jlayer-"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("/classes"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("/resources"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("javax"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("jsinterop-annotations"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("gwt-user-"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("sac-"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("gdx-box2d"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("gdx-jnigen"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("gdx-platform"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("imgui-"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("imgui-gdx"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("generator/core/"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("gdx-bullet/"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;
        else if (path.contains("gdx-bullet-platform"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;

        if (path.contains("backend-teavm-"))
            isValid = ACCEPT_STATE.ACCEPT;
        else if (path.contains(EXTENSION_BOX2D_GWT))
            isValid = ACCEPT_STATE.ACCEPT;
        else if (path.contains(EXTENSION_FREETYPE))
            isValid = ACCEPT_STATE.ACCEPT;
        else if (path.contains(EXTENSION_BULLET))
            isValid = ACCEPT_STATE.ACCEPT;
        else if (path.contains(EXTENSION_BOX2D))
            isValid = ACCEPT_STATE.ACCEPT;

        if (path.contains("backend-teavm-native"))
            isValid = ACCEPT_STATE.NOT_ACCEPT;

        return isValid;
    }

    enum ACCEPT_STATE {
        ACCEPT, NOT_ACCEPT, NO_MATCH
    }

    public interface TeaProgressListener {
        void onSuccess(boolean success);
        void onProgress(float progress);
    }
}
