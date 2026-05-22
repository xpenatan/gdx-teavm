package com.github.xpenatan.gdx.teavm.backends.shared.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Reads {@code META-INF/gdx-teavm.properties} from libraries on the classpath
 * to opt-in resources that should be copied to the {@code assets/} folder.
 *
 * <p>Supported keys (one per line, repeatable):</p>
 * <ul>
 *   <li>{@code resources=<substring>} – additional jar paths whose contents should be copied.</li>
 *   <li>{@code ignore-resources=<substring>} – substrings to drop from copied resources.</li>
 *   <li>{@code classpath-resources=<resource-path>} – classpath paths whose contents should
 *       be copied <i>and registered as {@code FileType.Classpath}</i> in the preload manifest.
 *       Equivalent to adding an {@link AssetFileHandle} with {@code FileType.Classpath}
 *       through {@code TeaBuilder#addAssets(AssetFileHandle)}.</li>
 * </ul>
 */
public class TeaVMResourceProperties {
    private static final String OPTION_ADDITIONAL_RESOURCES = "resources";
    private static final String OPTION_IGNORE_RESOURCES = "ignore-resources";
    private static final String OPTION_CLASSPATH_RESOURCES = "classpath-resources";
    private static final Pattern VERSIONED_STEM_PATTERN = Pattern.compile("^(.*)-(\\d[0-9a-zA-Z._-]*)$");

    public final String path;
    public final ArrayList<String> additionalPath = new ArrayList<>();
    public final ArrayList<String> ignorePath = new ArrayList<>();
    public final ArrayList<String> classpathResources = new ArrayList<>();

    public TeaVMResourceProperties(String path, String content) {
        this.path = path;
        try(Scanner scanner = new Scanner(content)) {
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if(line.isEmpty() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if(eq <= 0) continue;
                String option = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();
                if(option.isEmpty() || value.isEmpty()) continue;
                setupOption(option, value);
            }
        }
    }

    private void setupOption(String option, String value) {
        switch(option) {
            case OPTION_ADDITIONAL_RESOURCES:
                additionalPath.add(value);
                break;
            case OPTION_IGNORE_RESOURCES:
                ignorePath.add(value);
                break;
            case OPTION_CLASSPATH_RESOURCES:
                classpathResources.add(value);
                break;
        }
    }

    /** Aggregates everything declared by all {@code gdx-teavm.properties} files on the classpath. */
    public static class CollectedResources {
        /** Files to copy as plain {@code FileType.Internal} assets. */
        public final ArrayList<String> internalResources = new ArrayList<>();
        /** Resource paths to copy as {@code FileType.Classpath} assets (auto-discovered). */
        public final ArrayList<String> classpathResourcePaths = new ArrayList<>();
    }

    /**
     * Walk all jars looking for {@code META-INF/gdx-teavm.properties}, then resolve
     * the declared additional jars / ignore filters / classpath resource paths.
     */
    public static CollectedResources collect(ArrayList<URL> acceptedURL) {
        ArrayList<TeaVMResourceProperties> propertiesList = getAllProperties(acceptedURL);

        // Pass 1: aggregate every ignore filter once, regardless of order.
        HashSet<String> ignoreResources = new HashSet<>();
        for(TeaVMResourceProperties p : propertiesList) {
            ignoreResources.addAll(p.ignorePath);
        }

        // Pass 2: determine which classpath URLs we should walk for plain resources.
        LinkedHashSet<URL> filteredUrls = new LinkedHashSet<>();
        for(URL url : acceptedURL) {
            String urlPath = decodedPath(url);
            for(TeaVMResourceProperties p : propertiesList) {
                if(matches(urlPath, p)) {
                    filteredUrls.add(url);
                    break;
                }
            }
        }

        CollectedResources out = new CollectedResources();
        for(URL url : filteredUrls) {
            List<String> list = ClasspathResourceWalker.listAll(url, ClasspathResourceWalker.DEFAULT_FILTER);
            for(String res : list) {
                if(!containsResource(res, ignoreResources)) {
                    out.internalResources.add(res);
                }
            }
        }

        // Aggregate explicit classpath-resources= declarations.
        for(TeaVMResourceProperties p : propertiesList) {
            out.classpathResourcePaths.addAll(p.classpathResources);
        }
        return out;
    }

    private static boolean matches(String urlPath, TeaVMResourceProperties p) {
        if(urlPath.contains(p.path)) return true;
        if(matchesMainJarArtifact(urlPath, p.path)) return true;
        for(String additional : p.additionalPath) {
            if(matchesSelector(urlPath, additional)) return true;
        }
        return false;
    }

    /**
     * If a jar contains gdx-teavm.properties, allow sibling classifier jars
     * from the same artifact/version (e.g. runtime-web-1.0.jar -> runtime-web-1.0-wasm.jar).
     */
    static boolean matchesMainJarArtifact(String urlPath, String mainJarPath) {
        String candidateStem = fileStem(urlPath);
        String mainStem = fileStem(mainJarPath);
        if(mainStem.isEmpty() || candidateStem.isEmpty()) return false;

        // Standard case: artifact-version -> artifact-version-classifier
        if(candidateStem.startsWith(mainStem + "-") || candidateStem.startsWith(mainStem + "_")) return true;

        VersionBoundary boundary = extractVersionBoundary(mainStem);
        if(boundary == null) return false;

        // Also support artifact-classifier-version naming (runtime-web-wasm-1.2.3).
        return startsWithBaseClassifier(candidateStem, boundary.base)
                && candidateStem.endsWith(boundary.suffix)
                && !candidateStem.equals(mainStem);
    }

    private static boolean startsWithBaseClassifier(String candidateStem, String base) {
        return candidateStem.startsWith(base + "-") || candidateStem.startsWith(base + "_");
    }

    private static VersionBoundary extractVersionBoundary(String mainStem) {
        Matcher matcher = VERSIONED_STEM_PATTERN.matcher(mainStem);
        if(matcher.matches()) {
            String base = matcher.group(1);
            String version = matcher.group(2);
            return new VersionBoundary(base, "-" + version);
        }

        // Snapshot-like stems may encode version boundary with "--" (artifact--SNAPSHOT).
        // In this case classifier jars look like artifact-classifier--SNAPSHOT.
        int versionBoundary = mainStem.indexOf("--");
        if(versionBoundary <= 0) return null;

        String base = mainStem.substring(0, versionBoundary);
        String versionSuffix = mainStem.substring(versionBoundary);
        return new VersionBoundary(base, versionSuffix);
    }

    private static class VersionBoundary {
        final String base;
        final String suffix;

        VersionBoundary(String base, String suffix) {
            this.base = base;
            this.suffix = suffix;
        }
    }

    private static String fileStem(String path) {
        String normalized = normalize(path);
        int slash = normalized.lastIndexOf('/');
        String name = slash >= 0 ? normalized.substring(slash + 1) : normalized;
        if(!name.endsWith(".jar")) return "";
        return name.substring(0, name.length() - 4);
    }

    private static boolean matchesSelector(String urlPath, String selector) {
        if(urlPath.contains(selector)) return true;

        String path = normalize(urlPath);
        String value = selector == null ? "" : selector.trim();
        if(value.isEmpty()) return false;

        // Accept maven-ish selectors used in gdx-teavm.properties:
        // artifact, artifact:classifier, group:artifact, group:artifact:classifier
        String[] split = value.split(":");
        if(split.length == 1) {
            return path.contains(normalize(split[0]));
        }

        if(split.length == 2) {
            if(split[0].contains(".")) {
                return matchesGroupArtifact(path, split[0], split[1]);
            }
            return matchesArtifactClassifier(path, split[0], split[1]);
        }

        if(split[0].contains(".")) {
            return matchesGroupArtifact(path, split[0], split[1])
                    && matchesClassifier(path, split[split.length - 1]);
        }

        return matchesArtifactClassifier(path, split[0], split[split.length - 1]);
    }

    private static boolean matchesGroupArtifact(String normalizedPath, String group, String artifact) {
        String groupPath = "/" + normalize(group.replace('.', '/')) + "/";
        String artifactPath = "/" + normalize(artifact) + "/";
        return normalizedPath.contains(groupPath + normalize(artifact) + "/")
                || normalizedPath.contains(groupPath) && normalizedPath.contains(artifactPath)
                || normalizedPath.contains(normalize(group + ":" + artifact));
    }

    private static boolean matchesArtifactClassifier(String normalizedPath, String artifact, String classifier) {
        String artifactToken = normalize(artifact);
        String classifierToken = normalize(classifier);
        if(artifactToken.isEmpty() || classifierToken.isEmpty()) return false;
        return matchesArtifact(normalizedPath, artifactToken) && matchesClassifier(normalizedPath, classifierToken);
    }

    private static boolean matchesArtifact(String normalizedPath, String artifactToken) {
        return normalizedPath.contains("/" + artifactToken + "/")
                || normalizedPath.contains("/" + artifactToken + "-")
                || normalizedPath.endsWith("/" + artifactToken + ".jar");
    }

    private static boolean matchesClassifier(String normalizedPath, String classifierToken) {
        return normalizedPath.contains("-" + classifierToken + ".jar")
                || normalizedPath.contains("-" + classifierToken + "-");
    }

    private static String normalize(String value) {
        return value.toLowerCase(Locale.ROOT).replace('\\', '/');
    }

    private static boolean containsResource(String resource, HashSet<String> ignoreResources) {
        for(String ignore : ignoreResources) {
            if(resource.contains(ignore)) return true;
        }
        return false;
    }

    private static ArrayList<TeaVMResourceProperties> getAllProperties(ArrayList<URL> acceptedURL) {
        ArrayList<TeaVMResourceProperties> result = new ArrayList<>();
        for(URL url : acceptedURL) {
            String urlPath = decodedPath(url);
            if(urlPath.contains("org.teavm")) continue;
            TeaVMResourceProperties properties = readProperties(urlPath);
            if(properties != null) result.add(properties);
        }
        return result;
    }

    private static String decodedPath(URL url) {
        try {
            if("file".equals(url.getProtocol())) {
                return new File(url.toURI()).getAbsolutePath();
            }
        } catch(URISyntaxException ignored) {
        }
        return URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
    }

    private static TeaVMResourceProperties readProperties(String path) {
        File file = new File(path);
        if(file.isDirectory()) {
            Path propertiesPath = file.toPath().resolve("META-INF").resolve("gdx-teavm.properties");
            if(!Files.exists(propertiesPath)) return null;
            try(InputStream in = Files.newInputStream(propertiesPath)) {
                return new TeaVMResourceProperties(path, readString(in, null));
            } catch(IOException e) {
                return null;
            }
        }
        if(!path.endsWith(".jar")) {
            return null;
        }
        try(ZipFile zipFile = new ZipFile(path)) {
            ZipEntry entry = zipFile.getEntry("META-INF/gdx-teavm.properties");
            if(entry == null) return null;
            try(InputStream in = zipFile.getInputStream(entry)) {
                return new TeaVMResourceProperties(path, readString(in, null));
            }
        } catch(IOException e) {
            return null;
        }
    }

    private static String readString(InputStream in, String charset) {
        StringBuilder out = new StringBuilder(512);
        try(InputStreamReader reader = (charset == null)
                ? new InputStreamReader(in, StandardCharsets.UTF_8)
                : new InputStreamReader(in, charset)) {
            char[] buf = new char[2048];
            int n;
            while((n = reader.read(buf)) != -1) {
                out.append(buf, 0, n);
            }
        } catch(IOException e) {
            throw new RuntimeException("Error reading resource", e);
        }
        return out.toString();
    }
}
