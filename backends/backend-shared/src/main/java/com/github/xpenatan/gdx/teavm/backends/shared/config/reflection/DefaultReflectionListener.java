package com.github.xpenatan.gdx.teavm.backends.shared.config.reflection;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.ObjectFloatMap;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Queue;
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
        classpathPattern.add(Array.class.getName());
        classpathPattern.add(ArrayMap.class.getName());
        classpathPattern.add(IntIntMap.class.getName());
        classpathPattern.add(IntMap.class.getName());
        classpathPattern.add(IntSet.class.getName());
        classpathPattern.add(LongMap.class.getName());
        classpathPattern.add(ObjectFloatMap.class.getName());
        classpathPattern.add(ObjectIntMap.class.getName());
        classpathPattern.add(ObjectMap.class.getName());
        classpathPattern.add(ObjectSet.class.getName());
        classpathPattern.add(Queue.class.getName());
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
