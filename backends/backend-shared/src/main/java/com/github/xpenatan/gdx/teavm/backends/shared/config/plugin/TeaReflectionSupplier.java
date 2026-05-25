package com.github.xpenatan.gdx.teavm.backends.shared.config.plugin;

import com.github.xpenatan.gdx.teavm.backends.shared.config.TeaLogHelper;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.teavm.classlib.ReflectionContext;
import org.teavm.classlib.ReflectionSupplier;
import org.teavm.dependency.AbstractDependencyListener;
import org.teavm.dependency.DependencyAgent;
import org.teavm.model.ClassReader;
import org.teavm.model.FieldReader;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodReader;
import org.teavm.vm.spi.TeaVMHost;

public class TeaReflectionSupplier implements ReflectionSupplier {

    public static boolean printDebugLogs = false;

    private static final List<String> DEFAULT_REFLECTION_PATTERNS = Collections.unmodifiableList(Arrays.asList(
            "com.badlogic.gdx.scenes.scene2d.**",
            "net.mgsx.gltf.data.**",
            "com.badlogic.gdx.utils.Array",
            "com.badlogic.gdx.utils.ArrayMap",
            "com.badlogic.gdx.utils.IntIntMap",
            "com.badlogic.gdx.utils.IntMap",
            "com.badlogic.gdx.utils.IntSet",
            "com.badlogic.gdx.utils.LongMap",
            "com.badlogic.gdx.utils.ObjectFloatMap",
            "com.badlogic.gdx.utils.ObjectIntMap",
            "com.badlogic.gdx.utils.ObjectMap",
            "com.badlogic.gdx.utils.ObjectSet",
            "com.badlogic.gdx.utils.Queue"
    ));

    private static ArrayList<String> clazzList = new ArrayList();

    public static List<String> getDefaultReflectionPatterns() {
        return DEFAULT_REFLECTION_PATTERNS;
    }

    public static void addReflectionClass(Class<?> type) {
        addReflectionClass(type.getName());
    }

    public static void addReflectionClass(ArrayList<String> classes) {
        addReflectionClass((Collection<String>)classes);
    }

    public static void addReflectionClass(Collection<String> classes) {
        if(printDebugLogs) {
            TeaLogHelper.logHeader("ADD REFLECTION CLASSES: " + classes.size());
        }
        for(String className : classes) {
            addReflectionClass(className);
        }
        if(printDebugLogs) {
            TeaLogHelper.logEnd();
        }
    }

    public static List<String> getReflectionClasses() {
        return clazzList;
    }

    public static void addDefaultReflectionClasses(Collection<URL> classPathURLs) {
        addReflectionClasses(classPathURLs, DEFAULT_REFLECTION_PATTERNS);
    }

    public static void addReflectionClasses(Collection<URL> classPathURLs, Collection<String> patterns) {
        List<PathMatcher> matchers = reflectionMatchers(patterns);
        if(matchers.isEmpty() || classPathURLs == null || classPathURLs.isEmpty()) {
            return;
        }
        if(printDebugLogs) {
            TeaLogHelper.logHeader("SCAN REFLECTION PATTERNS: " + matchers.size());
        }
        for(URL classPathURL : classPathURLs) {
            File classPathFile = toFile(classPathURL);
            if(classPathFile == null || !classPathFile.exists()) {
                continue;
            }
            if(classPathFile.isDirectory()) {
                scanReflectionDirectory(classPathFile.toPath(), matchers);
            }
            else if(classPathFile.isFile() && classPathFile.getName().endsWith(".jar")) {
                scanReflectionJar(classPathFile, matchers);
            }
        }
        if(printDebugLogs) {
            TeaLogHelper.logEnd();
        }
    }

    public static void installReflectionDependencySupport(TeaVMHost host) {
        host.add(new AbstractDependencyListener() {
            @Override
            public void started(DependencyAgent agent) {
                for(String className : new ArrayList<>(clazzList)) {
                    agent.linkClass(className).initClass(null);
                }
            }
        });
    }

    public static boolean containsReflection(String className) {
        for(int i = 0; i < clazzList.size(); i++) {
            String reflectionClass = clazzList.get(i);
            if(className.contains(reflectionClass))
                return true;
        }
        return false;
    }

    /**
     * Full class name. Use config reflectionListener for more control.
     */
    public static void addReflectionClass(String className) {
        if(!clazzList.contains(className)) {
            clazzList.add(className);
            if(printDebugLogs) {
                TeaLogHelper.log("Added [Reflection] " + className);
            }
        }
        else if(printDebugLogs) {
            TeaLogHelper.log("Skipped duplicate [Reflection] " + className);
        }
    }

    /**
     * Must be called after {@code TeaBuilder.build}.
     */
    public static void printReflectionClasses() {
        TeaLogHelper.logHeader("REFLECTION CLASSES: " + clazzList.size());
        for(String reflectionClass : clazzList) {
            TeaLogHelper.log(reflectionClass);
        }
        TeaLogHelper.logEnd();
    }

    private static List<PathMatcher> reflectionMatchers(Collection<String> patterns) {
        ArrayList<PathMatcher> matchers = new ArrayList<>();
        if(patterns == null) {
            return matchers;
        }
        for(String pattern : patterns) {
            String trimmed = pattern == null ? "" : pattern.trim();
            if(!trimmed.isEmpty()) {
                matchers.add(FileSystems.getDefault().getPathMatcher("glob:" + trimmed.replace('.', '/')));
            }
        }
        return matchers;
    }

    private static File toFile(URL url) {
        try {
            return new File(url.toURI());
        } catch(URISyntaxException e) {
            return new File(url.getPath());
        }
    }

    private static void scanReflectionDirectory(Path root, List<PathMatcher> matchers) {
        try(Stream<Path> stream = Files.walk(root)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".class"))
                    .forEach(path -> addReflectionClass(root.relativize(path).toString(), matchers));
        } catch(IOException ignored) {
        }
    }

    private static void scanReflectionJar(File file, List<PathMatcher> matchers) {
        try(ZipFile zipFile = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while(entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if(!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    addReflectionClass(entry.getName(), matchers);
                }
            }
        } catch(IOException ignored) {
        }
    }

    private static void addReflectionClass(String classFileName, List<PathMatcher> matchers) {
        String className = classFileName
                .replace('\\', '.')
                .replace('/', '.');
        className = className.substring(0, className.length() - ".class".length());
        if(className.equals("module-info") || className.endsWith(".package-info")) {
            return;
        }
        if(matchesReflectionPattern(className, matchers)) {
            addReflectionClass(className);
        }
    }

    private static boolean matchesReflectionPattern(String className, List<PathMatcher> matchers) {
        String currentClassName = className;
        while(true) {
            Path path = Paths.get(currentClassName.replace('.', '/'));
            for(PathMatcher matcher : matchers) {
                if(matcher.matches(path)) {
                    return true;
                }
            }
            int nestedIndex = currentClassName.lastIndexOf('$');
            if(nestedIndex < 0) {
                return false;
            }
            currentClassName = currentClassName.substring(0, nestedIndex);
        }
    }

    public TeaReflectionSupplier() {
    }

    @Override
    public Collection<String> getAccessibleFields(ReflectionContext context, String className) {
        ClassReader cls = context.getClassSource().get(className);
        if(cls == null) {
            return Collections.emptyList();
        }
        Set<String> fields = new HashSet<>();

        if(cls != null) {
            if(canHaveReflection(className)) {
                for(FieldReader field : cls.getFields()) {
                    String name = field.getName();
                    fields.add(name);
                }
            }
        }
        if(printDebugLogs) {
            System.out.println("getAccessibleFields: " + className + " = " + fields);
        }
        return fields;
    }

    @Override
    public Collection<MethodDescriptor> getAccessibleMethods(ReflectionContext context, String className) {
        ClassReader cls = context.getClassSource().get(className);
        if(cls == null) {
            return Collections.emptyList();
        }
        Set<MethodDescriptor> methods = new HashSet<>();
        if(canHaveReflection(className)) {
            Collection<? extends MethodReader> methods2 = cls.getMethods();
            for(MethodReader method : methods2) {
                MethodDescriptor descriptor = method.getDescriptor();
                methods.add(descriptor);
            }
        }
        if(printDebugLogs) {
            System.out.println("getAccessibleMethods: " + className + " = " + methods);
        }
        return methods;
    }

    @Override
    public boolean isClassFoundByName(ReflectionContext context, String name) {
        boolean b = canHaveReflection(name);
        if(printDebugLogs) {
            System.out.println("isClassFoundByName: " + name + " = " + b);
        }
        return b;
    }

    private boolean canHaveReflection(String className) {
        boolean flag = false;
        for(int i = 0; i < clazzList.size(); i++) {
            String name = clazzList.get(i);
            if(className.contains(name)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
