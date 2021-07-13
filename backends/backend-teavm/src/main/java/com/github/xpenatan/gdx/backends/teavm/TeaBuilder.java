package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.utils.Array;
import com.github.xpenatan.gdx.backend.web.WebBuildConfiguration;
import com.github.xpenatan.gdx.backend.web.WebClassLoader;
import com.github.xpenatan.gdx.backend.web.preloader.AssetsCopy;
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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Properties;


/**
 * @author xpenatan
 */
public class TeaBuilder {
    public static void build(WebBuildConfiguration configuration) {
        build(configuration, null);
    }

    public static void build(WebBuildConfiguration configuration, TeaProgressListener progressListener) {
        addDefaultReflectionClasses();

        URL[] urLs = ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs();

        Array<URL> acceptedURL = new Array<>();
        Array<URL> notAcceptedURL = new Array<>();

        for (int i = 0; i < urLs.length; i++) {
            URL url = urLs[i];
            String path = url.getPath();
            ACCEPT_STATE acceptState = acceptPath(path);
            boolean accept = acceptState == ACCEPT_STATE.ACCEPT;
            if (acceptState == ACCEPT_STATE.NO_MATCH)
                accept = configuration.acceptClasspath(url);

            if (accept)
                acceptedURL.add(url);
            else
                notAcceptedURL.add(url);
        }

        for (int i = 0; i < acceptedURL.size; i++) {
            URL url = acceptedURL.get(i);
            String string = url.toString();
            if (string.contains("backend-web")) {
                acceptedURL.removeIndex(i);
                acceptedURL.insert(0, url);
                break;
            }
        }

        // Make backend-teavm first so some classes are replaced by emulated classes
        for (int i = 0; i < acceptedURL.size; i++) {
            URL url = acceptedURL.get(i);
            String string = url.toString();
            if (string.contains("backend-teavm")) {
                acceptedURL.removeIndex(i);
                acceptedURL.insert(0, url);
                break;
            }
        }

        acceptedURL.addAll(configuration.getAdditionalClasspath());

        WebBuildConfiguration.logHeader("Accepted Libs ClassPath Order");

        for (int i = 0; i < acceptedURL.size; i++) {
            WebBuildConfiguration.log(i + " true: " + acceptedURL.get(i).getPath());
        }

        WebBuildConfiguration.logHeader("Not Accepted Libs ClassPath");

        for (int i = 0; i < notAcceptedURL.size; i++) {
            WebBuildConfiguration.log(i + " false: " + notAcceptedURL.get(i).getPath());
        }

        int size = acceptedURL.size;

        if (size <= 0) {
            System.out.println("No urls found");
            return;
        }

        URL[] classPaths = acceptedURL.toArray(URL.class);
        WebClassLoader classLoader = new WebClassLoader(classPaths, TeaBuilder.class.getClassLoader());

        CustomTeaVMTool tool = new CustomTeaVMTool();

        boolean setDebugInformationGenerated = false;
        boolean setSourceMapsFileGenerated = false;
        boolean setSourceFilesCopied = false;

        String webappDirectory = configuration.getWebAppPath();

        String webappName = "webapp";

        System.out.println("targetDirectory: " + webappDirectory);

        File setTargetDirectory = new File(webappDirectory + "\\" + webappName + "\\" + "teavm");
        String setTargetFileName = "app.js";
        boolean setMinifying = configuration.minifying();
        String mainClass = configuration.getMainClass();
        TeaClassTransformer.applicationListener = configuration.getApplicationListenerClass();

        File setCacheDirectory = new File("C:\\TeaVMCache");
        boolean setIncremental = false;

        tool.setClassLoader(classLoader);
        tool.setDebugInformationGenerated(setDebugInformationGenerated);
        tool.setSourceMapsFileGenerated(setSourceMapsFileGenerated);
        tool.setSourceFilesCopied(setSourceFilesCopied);
        tool.setTargetDirectory(setTargetDirectory);
        tool.setTargetFileName(setTargetFileName);
        tool.setObfuscated(setMinifying);
        tool.setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE);
//		tool.setRuntime(mapRuntime(configuration.getRuntime()));
        tool.setMainClass(mainClass);
        //		tool.getProperties().putAll(profile.getProperties());
        tool.setIncremental(setIncremental);
        tool.setCacheDirectory(setCacheDirectory);
        tool.setStrict(false);
        tool.setTargetType(TeaVMTargetType.JAVASCRIPT);
        Properties properties = tool.getProperties();

        properties.put("teavm.libgdx.fsJsonPath", webappDirectory + "\\" + webappName + "\\" + "filesystem.json");
        properties.put("teavm.libgdx.warAssetsDirectory", webappDirectory + "\\" + webappName + "\\" + "assets");

        Array<String> webappAssetsFiles = new Array<>();
        webappAssetsFiles.add(webappName);
        AssetsCopy.copy(classLoader, webappAssetsFiles, new Array<>(), webappDirectory, false);

        WebBuildConfiguration.logHeader("Copying Assets");

        String assetsOutputPath = webappDirectory + "\\" + webappName + "\\assets";
        Array<File> assetsPaths = new Array<>();
        Array<String> classPathAssetsFiles = new Array<>();
        assetsDefaultClasspath(classPathAssetsFiles);
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
                WebBuildConfiguration.logHeader("Build Complete");
            }

        } catch (Throwable e) {
            e.printStackTrace();
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

    private static void assetsDefaultClasspath(Array<String> filePath) {
        filePath.add("com/badlogic/gdx/graphics/g3d/particles/");
        filePath.add("com/badlogic/gdx/graphics/g3d/shaders/");
        filePath.add("com/badlogic/gdx/utils/arial-15.fnt"); // Cannot be utils folder for now because its trying to copy from emu folder and not core gdx classpath
        filePath.add("com/badlogic/gdx/utils/arial-15.png");
        filePath.add("scripts/soundmanager2-jsmin.js");
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


        if (path.contains("backend-teavm-"))
            isValid = ACCEPT_STATE.ACCEPT;
        else if (path.contains("gdx-box2d-gwt"))
            isValid = ACCEPT_STATE.ACCEPT;

        return isValid;
    }

    enum ACCEPT_STATE {
        ACCEPT, NOT_ACCEPT, NO_MATCH
    }

    public interface TeaProgressListener {
        public void onSuccess(boolean success);
        public void onProgress(float progress);
    }
}
