package com.github.xpenatan.gdx.backends.teavm.config;

import java.io.File;
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
import org.teavm.diagnostics.DefaultProblemTextConsumer;
import org.teavm.diagnostics.Problem;
import org.teavm.diagnostics.ProblemProvider;
import org.teavm.model.CallLocation;
import org.teavm.model.MethodReference;
import org.teavm.tooling.TeaVMProblemRenderer;
import org.teavm.tooling.TeaVMTool;
import org.teavm.tooling.sources.DirectorySourceFileProvider;
import org.teavm.tooling.sources.JarSourceFileProvider;
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

    private static final String EXTENSION_FREETYPE = "gdx-freetype-teavm";
    private static final String EXTENSION_BOX2D = "gdx-box2d-teavm";
    private static final String EXTENSION_BOX2D_GWT = "gdx-box2d-gwt";

    private static String webappName = "c";
    private static File setTargetDirectory;
    private static TeaClassLoader classLoader;
    private static ArrayList<URL> acceptedURL;

    public static void config(String outputDirectory) {
        acceptedURL = new ArrayList<>();
        configClasspath(acceptedURL);
        TeaBuilder.log("");
        TeaBuilder.log("targetDirectory: " + outputDirectory);
        TeaBuilder.log("");
        URL[] classPaths = acceptedURL.toArray(new URL[acceptedURL.size()]);
        classLoader = new TeaClassLoader(classPaths, TeaBuilder.class.getClassLoader());
        setTargetDirectory = new File(outputDirectory + File.separator + webappName);
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

    private static void configClasspath(ArrayList<URL> acceptedURL) {
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

        TeaBuilder.logHeader("ACCEPTED CLASSPATH");
        for(int i = 0; i < acceptedURL.size(); i++) {
            TeaBuilder.log(i + " true: " + acceptedURL.get(i).getPath());
        }
    }

    private static void configTool(TeaVMTool tool) {
        String tmpdir = System.getProperty("java.io.tmpdir");
        File setCacheDirectory = new File(tmpdir + File.separator + "TeaVMCache");

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

//        tool.setClassLoader(classLoader);
        tool.setTargetDirectory(setTargetDirectory);
        tool.setCacheDirectory(setCacheDirectory);
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
                return TeaVMProgressFeedback.CONTINUE;
            }
        });
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
