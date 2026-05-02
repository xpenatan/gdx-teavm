package com.github.xpenatan.gdx.teavm.backends.shared.config.compiler;

import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFilter;

/**
 * Declares a classpath resource path whose contents should be copied into the
 * web app's {@code assets/} folder and registered as
 * {@link com.badlogic.gdx.Files.FileType#Classpath} assets so that
 * {@code Gdx.files.classpath(...)} resolves them at runtime.
 *
 * <p>The resource is looked up via the build classpath (so any jar that exposes
 * the path participates). Files are written under
 * {@code assets/<resourcePath>/...} preserving the package layout. This means
 * {@code Gdx.files.classpath("com/foo/bar.json")} works out of the box.</p>
 */
public class ClasspathAssetEntry {
    /** Classpath resource path (e.g. {@code com/kotcrab/vis/ui/skin/x1}). May be a directory or a single file. */
    public final String resourcePath;
    /** Optional filter overriding the global {@code assetFilter}. May be {@code null}. */
    public final AssetFilter filter;

    public ClasspathAssetEntry(String resourcePath, AssetFilter filter) {
        if(resourcePath == null) throw new IllegalArgumentException("resourcePath must not be null");
        // Always store without leading slash for classloader lookups.
        this.resourcePath = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
        this.filter = filter;
    }

    public ClasspathAssetEntry(String resourcePath) {
        this(resourcePath, null);
    }
}

