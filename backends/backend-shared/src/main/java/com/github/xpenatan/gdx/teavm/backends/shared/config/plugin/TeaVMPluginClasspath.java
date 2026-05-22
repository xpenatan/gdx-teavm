package com.github.xpenatan.gdx.teavm.backends.shared.config.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

public class TeaVMPluginClasspath {

    public static ArrayList<URL> getURLs(ClassLoader classLoader) {
        return getURLs(classLoader, null);
    }

    public static ArrayList<URL> getURLs(ClassLoader classLoader, List<String> classPathEntries) {
        LinkedHashSet<URL> urls = new LinkedHashSet<>();
        collectConfiguredClasspath(urls, classPathEntries);
        if(urls.isEmpty()) {
            collectURLClassLoader(urls, classLoader, true);
        }
        if(urls.isEmpty()) {
            collectJavaClassPath(urls);
        }
        return new ArrayList<>(urls);
    }

    private static void collectConfiguredClasspath(LinkedHashSet<URL> urls, List<String> classPathEntries) {
        if(classPathEntries == null || classPathEntries.isEmpty()) {
            return;
        }
        for(String entry : classPathEntries) {
            if(entry == null || entry.trim().isEmpty()) {
                continue;
            }
            addFileURL(urls, entry);
        }
    }

    private static void collectURLClassLoader(LinkedHashSet<URL> urls, ClassLoader classLoader, boolean includeParents) {
        ClassLoader current = classLoader;
        while(current != null) {
            if(current instanceof URLClassLoader) {
                URLClassLoader urlClassLoader = (URLClassLoader)current;
                for(URL url : urlClassLoader.getURLs()) {
                    urls.add(url);
                }
                if(!includeParents) {
                    return;
                }
            }
            current = current.getParent();
        }
    }

    private static void collectJavaClassPath(LinkedHashSet<URL> urls) {
        String classPath = System.getProperty("java.class.path");
        if(classPath == null || classPath.trim().isEmpty()) {
            return;
        }
        String[] entries = classPath.split(Pattern.quote(File.pathSeparator));
        for(String entry : entries) {
            if(entry == null || entry.trim().isEmpty()) {
                continue;
            }
            addFileURL(urls, entry);
        }
    }

    private static void addFileURL(LinkedHashSet<URL> urls, String entry) {
        try {
            urls.add(new File(entry).toURI().toURL());
        } catch(MalformedURLException e) {
            throw new RuntimeException("Invalid classpath entry: " + entry, e);
        }
    }
}
