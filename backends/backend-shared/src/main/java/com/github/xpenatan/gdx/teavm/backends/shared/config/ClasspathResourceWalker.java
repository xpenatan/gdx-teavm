package com.github.xpenatan.gdx.teavm.backends.shared.config;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Utility for enumerating files that live under a given classpath resource path.
 * <p>
 * Works uniformly across:
 * <ul>
 *     <li>{@code file:} URLs (exploded build output, project resources).</li>
 *     <li>{@code jar:} URLs (dependency jars).</li>
 * </ul>
 * Multiple classloader entries that contribute to the same resource path are merged.
 *
 * <p>Returned paths are normalized to a forward-slash, leading-slash form
 * (e.g. {@code /com/foo/uiskin.json}) so they can be used directly as
 * {@code Gdx.files.classpath(...)} keys and as on-disk relative destinations
 * under the {@code assets/} folder.</p>
 */
public class ClasspathResourceWalker {

    public interface PathFilter {
        boolean accept(String classpathPath);
    }

    /** Default filter: drop class/source files and metadata directories. */
    public static final PathFilter DEFAULT_FILTER = new PathFilter() {
        @Override
        public boolean accept(String p) {
            if(p.endsWith(".class")) return false;
            if(p.endsWith(".java")) return false;
            if(p.endsWith(".html")) return false;
            if(p.endsWith(".gwt.xml")) return false;
            if(p.endsWith(".rl")) return false;
            if(p.startsWith("/META-INF/") || p.equals("/META-INF")) return false;
            if(p.startsWith("/WEB-INF/") || p.equals("/WEB-INF")) return false;
            return true;
        }
    };

    /**
     * List every file reachable through the given classpath resource path.
     *
     * @param classLoader  classloader used to look up the resource.
     * @param resourcePath e.g. {@code "com/kotcrab/vis/ui/skin/x1"} or a single
     *                     file like {@code "foo/bar.json"}. May start with {@code /}.
     * @param filter       optional filter; {@link #DEFAULT_FILTER} is applied when {@code null}.
     * @return classpath paths in {@code /com/foo/bar.ext} form. Empty if nothing is found.
     */
    public static List<String> listResources(ClassLoader classLoader, String resourcePath, PathFilter filter) {
        if(filter == null) filter = DEFAULT_FILTER;
        String normalized = normalize(resourcePath);
        String lookup = normalized.startsWith("/") ? normalized.substring(1) : normalized;

        LinkedHashSet<String> result = new LinkedHashSet<>();
        Enumeration<URL> urls;
        try {
            urls = classLoader.getResources(lookup);
        } catch(IOException e) {
            return new ArrayList<>(result);
        }
        while(urls.hasMoreElements()) {
            URL url = urls.nextElement();
            collect(url, lookup, filter, result);
        }
        return new ArrayList<>(result);
    }

    /**
     * List every file in the given jar (or directory URL), filtered.
     * Returned paths use the same {@code /com/foo/bar.ext} form.
     */
    public static List<String> listAll(URL url, PathFilter filter) {
        if(filter == null) filter = DEFAULT_FILTER;
        LinkedHashSet<String> result = new LinkedHashSet<>();
        collect(url, "", filter, result);
        return new ArrayList<>(result);
    }

    private static void collect(URL url, String lookup, PathFilter filter, LinkedHashSet<String> out) {
        String protocol = url.getProtocol();
        if("jar".equals(protocol)) {
            collectFromJar(url, lookup, filter, out);
        }
        else if("file".equals(protocol)) {
            // A file: URL may point either to an exploded directory on the classpath
            // or directly to a .jar archive. The latter must be opened as a jar FS.
            String urlPath = url.getPath();
            if(urlPath != null && urlPath.toLowerCase().endsWith(".jar")) {
                collectFromJarFile(url, lookup, filter, out);
            }
            else {
                collectFromFile(url, lookup, filter, out);
            }
        }
    }

    private static void collectFromJarFile(URL fileUrl, String lookup, PathFilter filter, LinkedHashSet<String> out) {
        URI jarUri;
        try {
            // Build a jar:file:... URI from the underlying file URI to be path-safe (spaces, unicode).
            URI fileUri = fileUrl.toURI();
            jarUri = URI.create("jar:" + fileUri.toString());
        } catch(Exception e) {
            return;
        }
        try(FileSystem fs = openJarFileSystem(jarUri)) {
            Path root = lookup.isEmpty() ? fs.getPath("/") : fs.getPath("/" + lookup);
            walk(root, filter, out);
        } catch(IOException e) {
            // Jar can't be opened; skip silently.
        }
    }

    private static void collectFromJar(URL url, String lookup, PathFilter filter, LinkedHashSet<String> out) {
        URI uri;
        try {
            uri = url.toURI();
        } catch(Exception e) {
            return;
        }
        try(FileSystem fs = openJarFileSystem(uri)) {
            Path root = lookup.isEmpty() ? fs.getPath("/") : fs.getPath("/" + lookup);
            walk(root, filter, out);
        } catch(IOException e) {
            // Jar can't be opened; skip silently.
        }
    }

    private static FileSystem openJarFileSystem(URI uri) throws IOException {
        try {
            return FileSystems.getFileSystem(uri);
        } catch(Exception ignored) {
            return FileSystems.newFileSystem(uri, Collections.emptyMap());
        }
    }

    private static void collectFromFile(URL url, String lookup, PathFilter filter, LinkedHashSet<String> out) {
        Path root;
        try {
            root = Paths.get(url.toURI());
        } catch(Exception e) {
            return;
        }
        // For file:// classpath roots, lookup is already inside the URL path.
        // We still want to record entries with the full classpath form ("/" + lookup + "/...").
        final String prefix = lookup.isEmpty() ? "" : "/" + lookup;
        try {
            if(Files.isRegularFile(root)) {
                String full = prefix.isEmpty() ? "/" + root.getFileName().toString() : prefix;
                if(filter.accept(full)) out.add(full);
                return;
            }
            if(!Files.isDirectory(root)) return;
            final Path base = root;
            Files.walkFileTree(base, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    String rel = base.relativize(file).toString().replace('\\', '/');
                    String full = prefix.isEmpty() ? "/" + rel : prefix + "/" + rel;
                    if(filter.accept(full)) out.add(full);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch(IOException ignored) {
        }
    }

    private static void walk(Path root, PathFilter filter, LinkedHashSet<String> out) throws IOException {
        if(Files.isRegularFile(root)) {
            String full = normalize(root.toString());
            if(filter.accept(full)) out.add(full);
            return;
        }
        if(!Files.isDirectory(root)) return;
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                String full = normalize(file.toString());
                if(filter.accept(full)) out.add(full);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static String normalize(String path) {
        String s = path.replace('\\', '/');
        if(s.startsWith("./")) s = s.substring(1);
        if(!s.startsWith("/")) s = "/" + s;
        return s;
    }
}

