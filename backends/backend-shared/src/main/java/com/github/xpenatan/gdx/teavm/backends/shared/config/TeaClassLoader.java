package com.github.xpenatan.gdx.teavm.backends.shared.config;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author xpenatan
 */
public class TeaClassLoader extends URLClassLoader {

    public TeaClassLoader(URL[] classPaths, ClassLoader parent) {
        super(classPaths, parent);
    }

}
