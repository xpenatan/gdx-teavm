package com.github.xpenatan.gdx.teavm.backends.shared.config.reflection;

import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.TeaReflectionSupplier;
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
        classpathPattern.addAll(TeaReflectionSupplier.getDefaultReflectionPatterns());
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
        String globPattern = "glob:" + pattern.replace(".", "/");
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(globPattern);
        String currentClassName = className;
        while(true) {
            String path = currentClassName.replace(".", "/");
            if(matcher.matches(Paths.get(path))) {
                return true;
            }
            int nestedIndex = currentClassName.lastIndexOf('$');
            if(nestedIndex < 0) {
                return false;
            }
            currentClassName = currentClassName.substring(0, nestedIndex);
        }
    }
}
