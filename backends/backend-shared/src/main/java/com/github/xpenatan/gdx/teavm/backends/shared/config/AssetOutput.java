package com.github.xpenatan.gdx.teavm.backends.shared.config;

import com.badlogic.gdx.files.FileHandle;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import org.teavm.vm.BuildTarget;

public class AssetOutput {
    public interface Sink {
        OutputStream create(String relativePath) throws IOException;
    }

    private final Sink sink;
    private final String rootDescription;

    private AssetOutput(Sink sink, String rootDescription) {
        this.sink = sink;
        this.rootDescription = rootDescription;
    }

    public static AssetOutput fileHandle(FileHandle root) {
        return new AssetOutput(relativePath -> root.child(relativePath).write(false), root.path());
    }

    public static AssetOutput buildTarget(BuildTarget buildTarget) {
        return new AssetOutput(buildTarget::createResource, describeBuildTarget(buildTarget));
    }

    public OutputStream create(String relativePath) throws IOException {
        return sink.create(normalize(relativePath));
    }

    public String describeRoot() {
        return normalizeDisplay(rootDescription);
    }

    public String describePath(String relativePath) {
        return joinDisplay(rootDescription, normalize(relativePath));
    }

    private static String normalize(String path) {
        String normalized = path.replace('\\', '/');
        while(normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        while(normalized.contains("//")) {
            normalized = normalized.replace("//", "/");
        }
        return normalized;
    }

    private static String describeBuildTarget(BuildTarget buildTarget) {
        File directory = findDirectoryField(buildTarget);
        if(directory != null) {
            return directory.getAbsolutePath();
        }
        return "TeaVM build target (" + buildTarget.getClass().getName() + ")";
    }

    private static File findDirectoryField(Object object) {
        Class<?> type = object.getClass();
        while(type != null) {
            try {
                Field field = type.getDeclaredField("directory");
                field.setAccessible(true);
                Object value = field.get(object);
                if(value instanceof File) {
                    return (File)value;
                }
            }
            catch(NoSuchFieldException ignored) {
            }
            catch(ReflectiveOperationException | RuntimeException ignored) {
                return null;
            }
            type = type.getSuperclass();
        }
        return null;
    }

    private static String joinDisplay(String root, String relativePath) {
        String normalizedRoot = normalizeDisplay(root);
        if(normalizedRoot.isEmpty()) {
            return relativePath;
        }
        if(relativePath.isEmpty()) {
            return normalizedRoot;
        }
        while(normalizedRoot.endsWith("/")) {
            normalizedRoot = normalizedRoot.substring(0, normalizedRoot.length() - 1);
        }
        return normalizedRoot + "/" + relativePath;
    }

    private static String normalizeDisplay(String path) {
        if(path == null) {
            return "";
        }
        String normalized = path.replace('\\', '/');
        while(normalized.contains("//")) {
            normalized = normalized.replace("//", "/");
        }
        return normalized;
    }
}
