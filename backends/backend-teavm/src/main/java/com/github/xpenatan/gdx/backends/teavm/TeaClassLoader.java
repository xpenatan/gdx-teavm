package com.github.xpenatan.gdx.backends.teavm;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author xpenatan
 */
public class TeaClassLoader extends URLClassLoader {

    public TeaClassLoader(URL[] classPaths, ClassLoader parent) {
        super(classPaths, parent);
    }

    public ArrayList<String> getAllClasses(List<String> classOrPackageNames) {
        ArrayList<String> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        for(String name : classOrPackageNames) {
            // Check if it's a full class name (contains a class at the end)
            if(name.matches(".*\\.[A-Z].*")) {
                try {
                    Class<?> clazz = Class.forName(name);
                    classes.add(clazz.getName());
                } catch(ClassNotFoundException e) {
                    // If it's not a valid class, treat it as a package
                    addClassesFromPackage(name, classes, classLoader);
                }
            }
            else {
                // Treat as package name
                addClassesFromPackage(name, classes, classLoader);
            }
        }

        return classes;
    }

    private static void addClassesFromPackage(String packageName, ArrayList<String> classes, ClassLoader classLoader) {
        String path = packageName.replace('.', '/');
        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            while(resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());

                if(directory.exists() && directory.isDirectory()) {
                    File[] files = directory.listFiles();
                    if(files != null) {
                        for(File file : files) {
                            if(file.isFile() && file.getName().endsWith(".class")) {
                                String className = packageName + '.' +
                                        file.getName().substring(0, file.getName().length() - 6);
                                classes.add(Class.forName(className).getName());
                            }
                        }
                    }
                }
            }
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }
}