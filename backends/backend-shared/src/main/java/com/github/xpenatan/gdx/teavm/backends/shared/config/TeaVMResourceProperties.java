package com.github.xpenatan.gdx.teavm.backends.shared.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
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
 *       Equivalent to the user calling
 *       {@code TeaCompiler#addClasspathAssets(String)}.</li>
 * </ul>
 */
public class TeaVMResourceProperties {
    private static final String OPTION_ADDITIONAL_RESOURCES = "resources";
    private static final String OPTION_IGNORE_RESOURCES = "ignore-resources";
    private static final String OPTION_CLASSPATH_RESOURCES = "classpath-resources";

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
        for(String additional : p.additionalPath) {
            if(urlPath.contains(additional)) return true;
        }
        return false;
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
            if(!urlPath.endsWith(".jar")) continue;
            if(urlPath.contains("org.teavm")) continue;
            TeaVMResourceProperties properties = readProperties(urlPath);
            if(properties != null) result.add(properties);
        }
        return result;
    }

    private static String decodedPath(URL url) {
        return URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
    }

    private static TeaVMResourceProperties readProperties(String path) {
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

    public static String readString(InputStream in, String charset) {
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