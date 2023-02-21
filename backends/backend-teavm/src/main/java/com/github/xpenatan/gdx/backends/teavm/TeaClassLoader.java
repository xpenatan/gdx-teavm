package com.github.xpenatan.gdx.backends.teavm;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author xpenatan
 */
public class TeaClassLoader extends URLClassLoader {

    private URL[] jarFiles;
    private HashMap<String, ArrayList<String>> fileMap = new HashMap<>();

    private final ArrayList<String> skipClasses = new ArrayList<>();

    public TeaClassLoader(URL[] classPaths, ClassLoader parent, ArrayList<String> skipClasses) {
        super(classPaths, parent);
        this.jarFiles = classPaths;
        this.skipClasses.addAll(skipClasses);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        String fixName = name.replace(";.class", ".class");
        fixName = fixName.replace("[L", "");
        fixName = fixName.replace("[", "");

        String toPackage = fixName.replace("/", ".");
        if(containsSkipClass(toPackage)) {
            return null;
        }

        for(int i = 0; i < jarFiles.length; i++) {
            URL url = jarFiles[i];
            String path = url.getPath();
            File file = new File(path);
            if(file.exists()) {
                if(!file.isDirectory()) {
                    try {
                        JarFile jarFile = new JarFile(file);
                        ZipEntry entry = jarFile.getEntry(fixName);
                        if(entry != null) {
                            return jarFile.getInputStream(entry);
                        }
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    ArrayList<String> allClasses = getAllFiles(path);
                    String resName = fixName.replace("\\", "/");
                    for(int j = 0; j < allClasses.size(); j++) {
                        String className = allClasses.get(j);
                        if(className.contains(resName)) {
                            return super.getResourceAsStream(name);
                        }
                    }
                }
            }
        }

        return super.getResourceAsStream(name);
    }

    private boolean containsSkipClass(String clazz) {
        for(int i = 0; i < skipClasses.size(); i++) {
            String name = skipClasses.get(i);
            if(clazz.contains(name)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<String> getAllFiles(String path) {
        ArrayList<String> paths = fileMap.get(path);
        if(paths == null) {
            File rootFile = new File(path);
            paths = new ArrayList<>();
            getAllFiles(rootFile, paths, "");
            fileMap.put(path, paths);
        }
        return paths;
    }

    private void getAllFiles(File rootFile, ArrayList<String> out, String packageName) {
        String[] list = rootFile.list();
        if(list != null) {
            for(int i = 0; i < list.length; i++) {
                String subFileStr = list[i];
                File subFile = new File(rootFile, subFileStr);
                if(subFile.isDirectory()) {
                    String newPackage = packageName + subFileStr + "/";
                    getAllFiles(subFile, out, newPackage);
                }
                else {
                    String path = packageName + subFileStr;
                    path = path.replace("\\", "/");
                    out.add(path);
                }
            }
        }
    }
}
