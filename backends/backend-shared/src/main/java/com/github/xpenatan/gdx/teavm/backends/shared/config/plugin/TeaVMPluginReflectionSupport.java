package com.github.xpenatan.gdx.teavm.backends.shared.config.plugin;

import com.github.xpenatan.gdx.teavm.backends.shared.config.reflection.DefaultReflectionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.teavm.dependency.AbstractDependencyListener;
import org.teavm.dependency.DependencyAgent;
import org.teavm.vm.spi.TeaVMHost;

public class TeaVMPluginReflectionSupport {

    public static void install(TeaVMHost host, GdxTeaVMPluginConfig config, ArrayList<URL> classPathURLs) {
        if(!config.reflectionEnabled) {
            return;
        }

        DefaultReflectionListener listener = new PluginReflectionListener(config.reflectionDefaults);
        for(String pattern : config.reflectionPatterns) {
            listener.addClassOrPackage(pattern);
        }

        LinkedHashSet<String> classes = new LinkedHashSet<>();
        if(config.reflectionScan) {
            classes.addAll(scan(classPathURLs, listener));
        }
        else {
            classes.addAll(config.reflectionPatterns);
        }

        for(String className : classes) {
            TeaReflectionSupplier.addReflectionClass(className);
        }
        TeaReflectionSupplier.printDebugLogs = config.reflectionDebug;

        if(!classes.isEmpty()) {
            host.add(new ReflectionPreserveDependencyListener(new ArrayList<>(classes)));
        }
    }

    private static List<String> scan(ArrayList<URL> classPathURLs, DefaultReflectionListener listener) {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        for(URL url : classPathURLs) {
            File file = toFile(url);
            if(file == null || !file.exists()) {
                continue;
            }
            if(file.isDirectory()) {
                scanDirectory(file.toPath(), listener, result);
            }
            else if(file.isFile() && file.getName().endsWith(".jar")) {
                scanJar(file, listener, result);
            }
        }
        return new ArrayList<>(result);
    }

    private static void scanDirectory(Path root, DefaultReflectionListener listener, LinkedHashSet<String> out) {
        try(var stream = Files.walk(root)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".class"))
                    .forEach(path -> {
                        String className = root.relativize(path).toString().replace('\\', '.').replace('/', '.');
                        addClassName(className, listener, out);
                    });
        } catch(IOException ignored) {
        }
    }

    private static void scanJar(File file, DefaultReflectionListener listener, LinkedHashSet<String> out) {
        try(ZipFile zipFile = new ZipFile(file)) {
            var entries = zipFile.entries();
            while(entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if(!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    addClassName(entry.getName().replace('/', '.'), listener, out);
                }
            }
        } catch(IOException ignored) {
        }
    }

    private static void addClassName(String classFileName, DefaultReflectionListener listener, LinkedHashSet<String> out) {
        String className = classFileName.substring(0, classFileName.length() - ".class".length());
        if(className.equals("module-info") || className.endsWith(".package-info")) {
            return;
        }
        if(listener.shouldEnableReflection(className)) {
            out.add(className);
        }
    }

    private static File toFile(URL url) {
        try {
            if("file".equals(url.getProtocol())) {
                return new File(url.toURI());
            }
        } catch(URISyntaxException ignored) {
        }
        return null;
    }

    private static class PluginReflectionListener extends DefaultReflectionListener {
        PluginReflectionListener(boolean includeDefaults) {
            if(!includeDefaults) {
                classpathPattern.clear();
            }
        }
    }

    private static class ReflectionPreserveDependencyListener extends AbstractDependencyListener {
        private final ArrayList<String> classes;

        ReflectionPreserveDependencyListener(ArrayList<String> classes) {
            this.classes = classes;
        }

        @Override
        public void started(DependencyAgent agent) {
            for(String className : classes) {
                if(agent.getClassSource().get(className) != null) {
                    agent.linkClass(className).initClass(null);
                }
            }
        }
    }
}
