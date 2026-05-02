package com.github.xpenatan.gdx.teavm.backends.shared.config.reflection;

import com.badlogic.gdx.utils.ObjectMap;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DefaultReflectionListener implements TeaReflectionListener {

    protected ArrayList<String> classpathPattern = new ArrayList();

    public DefaultReflectionListener() {
        setupDefaultPatterns();
    }

    protected void setupDefaultPatterns() {
        classpathPattern.add("com.badlogic.gdx.scenes.scene2d.**");
        classpathPattern.add("net.mgsx.gltf.data.**");
        classpathPattern.add(ObjectMap.class.getName());
    }

    public void addClassOrPackage(String classOrPackage) {
        classpathPattern.add(classOrPackage);
    }

    @Override
    public boolean shouldEnableReflection(String fullClassName) {
        for(int i = 0; i < classpathPattern.size(); i++) {
            String pattern = classpathPattern.get(i);
            if(matchesPattern(fullClassName, pattern))
                return true;
        }
        return false;
    }

    protected boolean matchesPattern(String className, String pattern) {
        String path = className.replace(".", "/");
        String globPattern = "glob:" + pattern.replace(".", "/");
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(globPattern);
        return matcher.matches(Paths.get(path));
    }
}
