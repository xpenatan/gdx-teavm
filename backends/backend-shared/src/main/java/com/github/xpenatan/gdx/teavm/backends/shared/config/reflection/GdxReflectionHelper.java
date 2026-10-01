package com.github.xpenatan.gdx.teavm.backends.shared.config.reflection;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import org.teavm.extension.introspect.IntrospectClass;
import org.teavm.extension.spi.reflection.SimpleReflectionPolicy.ClassPolicy;

/** Optional compiler-side reflection rules for the classes covered by the former backend defaults. */
public final class GdxReflectionHelper {
    public static final List<String> DEFAULT_REFLECTION_PATTERNS = List.of(
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
    );

    private GdxReflectionHelper() {
    }

    /**
     * Grants name lookup and all fields, methods and constructors, including non-public members,
     * for the default patterns. Exact class entries also include their nested classes.
     *
     * <p>Call from your {@code SimpleReflectionPolicy.setup()}:
     * <pre>GdxReflectionHelper.applyDefaults(this::selectClasses);</pre>
     * The selector callback uses TeaVM's protected selection method in the caller's policy.
     * Register that policy normally. This helper does not register itself, scan the classpath,
     * preserve classes, or force class initialization. Grants combine with other TeaVM policies.
     */
    public static void applyDefaults(Function<Predicate<IntrospectClass<?>>, ClassPolicy> selectClasses) {
        Objects.requireNonNull(selectClasses, "selectClasses")
                .apply(GdxReflectionHelper::matchesDefaults)
                .foundByName()
                .reflectableMembers(member -> true);
    }

    private static boolean matchesDefaults(IntrospectClass<?> type) {
        String name = type.name();
        for(String pattern : DEFAULT_REFLECTION_PATTERNS) {
            if(pattern.endsWith(".**")) {
                if(name.startsWith(pattern.substring(0, pattern.length() - 2))) return true;
            } else if(name.equals(pattern) || name.startsWith(pattern + "$")) {
                return true;
            }
        }
        return false;
    }
}
