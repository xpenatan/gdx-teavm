package com.github.xpenatan.gdx.teavm.backends.shared.config.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class GdxTeaVMPluginConfig {
    public static final String WEBAPP_ENABLED = "gdx.teavm.webapp.enabled";
    public static final String WEBAPP_INDEX_PATH = "gdx.teavm.webapp.indexPath";
    public static final String ENTRY_POINT_NAME = "gdx.teavm.entryPointName";
    public static final String MAIN_CLASS_ARGS = "gdx.teavm.mainClassArgs";
    public static final String HTML_TITLE = "gdx.teavm.html.title";
    public static final String HTML_WIDTH = "gdx.teavm.html.width";
    public static final String HTML_HEIGHT = "gdx.teavm.html.height";
    public static final String LOGO_PATH = "gdx.teavm.logoPath";
    public static final String COPY_LOADING_ASSET = "gdx.teavm.copyLoadingAsset";
    public static final String CLASSPATH = "gdx.teavm.classpath";
    public static final String ASSETS = "gdx.teavm.assets";
    public static final String CLASSPATH_ASSETS = "gdx.teavm.classpathAssets";
    public static final String ASSET_MANIFEST = "gdx.teavm.assets.manifest";
    public static final String REFLECTION_ENABLED = "gdx.teavm.reflection.enabled";
    public static final String REFLECTION_DEFAULTS = "gdx.teavm.reflection.defaults";
    public static final String REFLECTION_SCAN = "gdx.teavm.reflection.scan";
    public static final String REFLECTION = "gdx.teavm.reflection";
    public static final String REFLECTION_CLASSES = "gdx.teavm.reflection.classes";
    public static final String REFLECTION_DEBUG = "gdx.teavm.reflection.debug";
    public static final String NATIVE_BACKEND = "gdx.teavm.native.backend";
    public static final String NATIVE_OUTPUT_ROOT = "gdx.teavm.native.outputRoot";
    public static final String NATIVE_RELEASE_PATH = "gdx.teavm.native.releasePath";
    public static final String NATIVE_GENERATED_SOURCES = "gdx.teavm.native.generatedSources";
    public static final String NATIVE_BUILD_TYPE = "gdx.teavm.native.buildType";
    public static final String NATIVE_BUILD_EXECUTABLE = "gdx.teavm.native.buildExecutable";
    public static final String NATIVE_RUN_EXECUTABLE = "gdx.teavm.native.runExecutable";
    public static final String NATIVE_CONSOLE_LOG = "gdx.teavm.native.consoleLog";
    public static final String NATIVE_CMAKE_DEFINITIONS = "gdx.teavm.native.cmakeDefinitions";
    public static final String IOS_XCODE_PROJECT_DIR = "gdx.teavm.ios.xcode.projectDir";

    public final boolean webappEnabled;
    public final String webappIndexPath;
    public final String entryPointName;
    public final String mainClassArgs;
    public final String htmlTitle;
    public final int htmlWidth;
    public final int htmlHeight;
    public final String logoPath;
    public final boolean copyLoadingAsset;
    public final List<String> classpath;
    public final List<String> assets;
    public final List<String> classpathAssets;
    public final boolean reflectionEnabled;
    public final boolean reflectionDefaults;
    public final boolean reflectionScan;
    public final List<String> reflectionPatterns;
    public final List<String> reflectionClasses;
    public final boolean reflectionDebug;
    public final String nativeBackend;
    public final String nativeOutputRoot;
    public final String nativeReleasePath;
    public final String nativeGeneratedSources;
    public final String nativeBuildType;
    public final boolean nativeBuildExecutable;
    public final boolean nativeRunExecutable;
    public final boolean nativeConsoleLog;
    public final Map<String, String> nativeCMakeDefinitions;
    public final String iosXcodeProjectDir;

    private GdxTeaVMPluginConfig(Properties properties) {
        webappEnabled = getBoolean(properties, WEBAPP_ENABLED, false);
        webappIndexPath = getString(properties, WEBAPP_INDEX_PATH, "index.html");
        entryPointName = getString(properties, ENTRY_POINT_NAME, "main");
        mainClassArgs = getString(properties, MAIN_CLASS_ARGS, "");
        htmlTitle = getString(properties, HTML_TITLE, "gdx-teavm");
        htmlWidth = getInt(properties, HTML_WIDTH, 800);
        htmlHeight = getInt(properties, HTML_HEIGHT, 600);
        logoPath = getString(properties, LOGO_PATH, "startup-logo.png");
        copyLoadingAsset = getBoolean(properties, COPY_LOADING_ASSET, true);
        classpath = Collections.unmodifiableList(readPathList(properties, CLASSPATH));
        assets = Collections.unmodifiableList(readPathList(properties, ASSETS));
        classpathAssets = Collections.unmodifiableList(readTokenList(properties, CLASSPATH_ASSETS));
        reflectionEnabled = getBoolean(properties, REFLECTION_ENABLED, true);
        reflectionDefaults = getBoolean(properties, REFLECTION_DEFAULTS, true);
        reflectionScan = getBoolean(properties, REFLECTION_SCAN, true);
        reflectionPatterns = Collections.unmodifiableList(readTokenList(properties, REFLECTION));
        reflectionClasses = Collections.unmodifiableList(readTokenList(properties, REFLECTION_CLASSES));
        reflectionDebug = getBoolean(properties, REFLECTION_DEBUG, false);
        nativeBackend = getString(properties, NATIVE_BACKEND, "");
        nativeOutputRoot = getString(properties, NATIVE_OUTPUT_ROOT, "");
        nativeReleasePath = getString(properties, NATIVE_RELEASE_PATH, "");
        nativeGeneratedSources = getString(properties, NATIVE_GENERATED_SOURCES, "");
        nativeBuildType = getString(properties, NATIVE_BUILD_TYPE, "Debug");
        nativeBuildExecutable = getBoolean(properties, NATIVE_BUILD_EXECUTABLE, false);
        nativeRunExecutable = getBoolean(properties, NATIVE_RUN_EXECUTABLE, false);
        nativeConsoleLog = getBoolean(properties, NATIVE_CONSOLE_LOG, false);
        nativeCMakeDefinitions = Collections.unmodifiableMap(readIndexedMap(properties, NATIVE_CMAKE_DEFINITIONS));
        iosXcodeProjectDir = getString(properties, IOS_XCODE_PROJECT_DIR, "");
    }

    public static GdxTeaVMPluginConfig from(Properties properties) {
        return new GdxTeaVMPluginConfig(properties != null ? properties : new Properties());
    }

    private static String getString(Properties properties, String key, String defaultValue) {
        String value = properties.getProperty(key);
        if(value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    private static boolean getBoolean(Properties properties, String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if(value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    private static int getInt(Properties properties, String key, int defaultValue) {
        String value = properties.getProperty(key);
        if(value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch(NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private static ArrayList<String> readPathList(Properties properties, String key) {
        ArrayList<String> result = new ArrayList<>();
        addPathValues(result, properties.getProperty(key));
        for(String childKey : sortedChildKeys(properties, key)) {
            addPathValues(result, properties.getProperty(childKey));
        }
        return result;
    }

    private static void addPathValues(ArrayList<String> out, String value) {
        if(value == null) {
            return;
        }
        String separator = File.pathSeparator;
        String normalized = value.replace("\r\n", "\n").replace('\r', '\n');
        normalized = normalized.replace('\n', ';');
        if(!";".equals(separator)) {
            normalized = normalized.replace(separator, ";");
        }
        for(String token : normalized.split(";")) {
            addTrimmed(out, token);
        }
    }

    private static ArrayList<String> readTokenList(Properties properties, String key) {
        ArrayList<String> result = new ArrayList<>();
        addTokenValues(result, properties.getProperty(key));
        for(String childKey : sortedChildKeys(properties, key)) {
            addTokenValues(result, properties.getProperty(childKey));
        }
        return result;
    }

    private static void addTokenValues(ArrayList<String> out, String value) {
        if(value == null) {
            return;
        }
        String normalized = value.replace("\r\n", "\n").replace('\r', '\n');
        normalized = normalized.replace('\n', ',').replace(';', ',');
        for(String token : normalized.split(",")) {
            addTrimmed(out, token);
        }
    }

    private static void addTrimmed(ArrayList<String> out, String value) {
        String trimmed = value == null ? "" : value.trim();
        if(!trimmed.isEmpty()) {
            out.add(trimmed);
        }
    }

    private static Set<String> sortedChildKeys(Properties properties, String key) {
        TreeSet<String> result = new TreeSet<>();
        String prefix = key + ".";
        for(String propertyKey : properties.stringPropertyNames()) {
            if(propertyKey.startsWith(prefix)) {
                result.add(propertyKey);
            }
        }
        return result;
    }

    private static LinkedHashMap<String, String> readIndexedMap(Properties properties, String key) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        String prefix = key + ".";
        TreeSet<String> indexes = new TreeSet<>();
        for(String propertyKey : properties.stringPropertyNames()) {
            if(!propertyKey.startsWith(prefix)) {
                continue;
            }
            String childKey = propertyKey.substring(prefix.length());
            int separatorIndex = childKey.indexOf('.');
            if(separatorIndex > 0 && childKey.substring(separatorIndex + 1).equals("name")) {
                indexes.add(childKey.substring(0, separatorIndex));
            }
        }
        for(String index : indexes) {
            String entryPrefix = prefix + index;
            String name = properties.getProperty(entryPrefix + ".name");
            String value = properties.getProperty(entryPrefix + ".value");
            if(name != null && value != null) {
                result.put(name, value);
            }
        }
        return result;
    }
}
