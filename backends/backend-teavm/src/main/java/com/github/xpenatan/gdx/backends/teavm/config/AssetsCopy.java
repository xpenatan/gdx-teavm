package com.github.xpenatan.gdx.backends.teavm.config;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.teavm.TeaClassLoader;
import com.github.xpenatan.gdx.backends.teavm.preloader.AssetFilter;
import com.github.xpenatan.gdx.backends.teavm.preloader.AssetType;
import com.github.xpenatan.gdx.backends.teavm.preloader.DefaultAssetFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;
import com.badlogic.gdx.Files.FileType;

/**
 * @author xpenatan
 */
public class AssetsCopy {

    private static class Asset {
        FileHandle file;
        AssetType type;

        public Asset(FileHandle file, AssetType type) {
            this.file = file;
            this.type = type;
        }
    }

    public static void copyResources(TeaClassLoader classLoader, List<String> classPathAssetsFiles, String assetsOutputPath, boolean generateTextFile, boolean append) {
        copy(classLoader, classPathAssetsFiles, true, null, null, assetsOutputPath, generateTextFile, append);
    }

    public static void copy(TeaClassLoader classLoader, List<String> classPathAssetsFiles, ArrayList<AssetFileHandle> assetsPaths, AssetFilter filter, String assetsOutputPath, boolean generateTextFile, boolean append) {
        copy(classLoader, classPathAssetsFiles, false, assetsPaths, filter, assetsOutputPath, generateTextFile, append);
    }

    private static void copy(TeaClassLoader classloader, List<String> classPathAssetsFiles, boolean isResources, ArrayList<AssetFileHandle> assetsPaths, AssetFilter filter, String assetsOutputPath, boolean generateTextFile, boolean append) {
        FileHandle target = new FileHandle(assetsOutputPath);
        assetsOutputPath = target.path();
        ArrayList<Asset> assets = new ArrayList<Asset>();
        AssetFilter defaultAssetFilter = filter != null ? filter : new DefaultAssetFilter();
        if(assetsPaths != null && assetsPaths.size() > 0) {
            TeaBuilder.log("Copying assets from:");
            for(int i = 0; i < assetsPaths.size(); i++) {
                FileHandle source = assetsPaths.get(i);
                String path = source.path();
                TeaBuilder.log(path);
                copyDirectory(source, target, defaultAssetFilter, assets);
            }

            TeaBuilder.log("to:");
            TeaBuilder.log(assetsOutputPath);
        }

        if(classloader != null && classPathAssetsFiles != null) {
            // Copy assets from class package directory

            addDirectoryClassPathFiles(classPathAssetsFiles);
            TeaBuilder.log("");
            TeaBuilder.log("Copying assets from:");
            for(String classpathFile : classPathAssetsFiles) {
                String path = classpathFile;
                if(path.startsWith("/") == false) {
                    path = "/" + path;
                }
                else {
                    classpathFile = classpathFile.replaceFirst("/", "");
                }
                if(defaultAssetFilter.accept(path, false)) {
                    try {
                        TeaBuilder.log(classpathFile);
                        InputStream is = classloader.getResourceAsStream(classpathFile);
                        if(is != null) {
                            FileHandle dest = target.child(classpathFile);
                            dest.write(is, false);
                            String destPath = dest.path();
                            if(!destPath.endsWith(".js") && !destPath.endsWith(".wasm")) {
                                AssetFileHandle dest2 = AssetFileHandle.createCopyHandle(dest.file(), FileType.Classpath);
                                assets.add(new Asset(dest2, AssetFilter.getType(destPath)));
                            }
                            is.close();
                        }
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        TeaBuilder.log("to:");
        TeaBuilder.log(assetsOutputPath);
        if(generateTextFile == false) return;

        HashMap<String, ArrayList<Asset>> bundles = new HashMap<String, ArrayList<Asset>>();
        for(Asset asset : assets) {
            String bundleName = defaultAssetFilter.getBundleName(asset.file.path());
            if(bundleName == null) {
                bundleName = "assets";
            }
            ArrayList<Asset> bundleAssets = bundles.get(bundleName);
            if(bundleAssets == null) {
                bundleAssets = new ArrayList<Asset>();
                bundles.put(bundleName, bundleAssets);
            }
            bundleAssets.add(asset);
        }

        for(Entry<String, ArrayList<Asset>> bundle : bundles.entrySet()) {
            StringBuffer buffer = new StringBuffer();
            for(Asset asset : bundle.getValue()) {
                setupPreloadAssetFileFormat(asset, buffer, assetsOutputPath);
            }
            target.child(bundle.getKey() + ".txt").writeString(buffer.toString(), append);
        }
    }

    private static void setupPreloadAssetFileFormat(Asset asset, StringBuffer buffer, String assetsOutputPath) {
        FileHandle fileHandle = asset.file;
        FileType type = fileHandle.type();
        String path = fileHandle.path();
        path = path.replace(assetsOutputPath, "");
        String fileTypeStr = "i";
        if(type == FileType.Local) {
            fileTypeStr = "l";
        }
        else if(type == FileType.Classpath) {
            fileTypeStr = "c";
        }

        buffer.append(fileTypeStr);
        buffer.append(":");
        buffer.append(asset.type.code);
        buffer.append(":");
        buffer.append(path);
        buffer.append(":");
        buffer.append(asset.file.isDirectory() ? 0 : asset.file.length());
        buffer.append("\n");
    }

    private static void addDirectoryClassPathFiles(List<String> classPathFiles) {
        ArrayList<String> folderFilePaths = new ArrayList<>();
        for(int k = 0; k < classPathFiles.size(); k++) {
            String classpathFile = classPathFiles.get(k);
            classpathFile = classpathFile.replace("\\", "/");
            if(classpathFile.startsWith("/") == false)
                classpathFile = "/" + classpathFile;
            URL resource = AssetsCopy.class.getResource(classpathFile);
            if(resource != null) {
                URI uri = null;
                try {
                    uri = resource.toURI();
                }
                catch(URISyntaxException e1) {
                    e1.printStackTrace();
                }
                if(uri == null)
                    continue;

                // solution to get files inside jar/folder
                Path myPath = null;
                String scheme = uri.getScheme();
                FileSystem fileSystem = null;
                if(scheme.equals("jar")) {
                    try {
                        fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                        continue;
                    }
                    myPath = fileSystem.getPath(classpathFile);
                }
                else {
                    myPath = Paths.get(uri);
                }
                Stream<Path> walk = null;
                try {
                    walk = Files.walk(myPath, 1);
                }
                catch(IOException e) {
                    e.printStackTrace();
                    continue;
                }
                boolean first = true;
                for(Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
                    Path next = it.next();
                    String path = next.toString();
                    boolean directory = Files.isDirectory(next);
                    path = path.replace("\\", "/");
                    int i = path.lastIndexOf(classpathFile); // remove absolute path
                    path = path.substring(i + 1);
                    if(path.startsWith("/") == false)
                        path = "/" + path;
                    if(path.contains(".class") || path.contains(".java"))
                        continue;
                    if(directory) {
                        if(first) {
                            first = false;
                            classPathFiles.remove(k);
                            k--;
                            continue;
                        }
                        else {
                            classPathFiles.add(path);
                            continue;
                        }
                    }
                    folderFilePaths.add(path);
                }
                walk.close();
                if(fileSystem != null) {
                    try {
                        fileSystem.close();
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {// remove if it dont exist
                classPathFiles.remove(k);
                k--;
            }
        }
        classPathFiles.addAll(folderFilePaths);
        //Hack to remove duplicates.
        // Fixme fix/improve asset copy
        HashSet<String> set = new HashSet<>(folderFilePaths);
        classPathFiles.clear();
        classPathFiles.addAll(set);
    }

    private static void copyFile(FileHandle source, FileHandle dest, AssetFilter filter, ArrayList<Asset> assets) {
        if(!filter.accept(dest.path(), false)) return;
        try {
            assets.add(new Asset(dest, AssetFilter.getType(dest.path())));
            InputStream read = source.read();
            dest.write(read, false);
            read.close();
        }
        catch(Exception ex) {
            throw new GdxRuntimeException("Error copying source file: " + source + "\n" //
                    + "To destination: " + dest, ex);
        }
    }

    private static void copyDirectory(FileHandle sourceDir, FileHandle destDir, AssetFilter filter, ArrayList<Asset> assets) {
        String destPath = destDir.path();
        if(!filter.accept(destPath, true)) return;
        destDir.mkdirs();
        FileHandle[] files = sourceDir.list();
        for(int i = 0, n = files.length; i < n; i++) {
            FileHandle srcFile = files[i];
            FileHandle destFile1 = destDir.child(srcFile.name());
            // Destination type is copied from source type
            FileHandle destFile = AssetFileHandle.createCopyHandle(destFile1.file(), srcFile.type());
            if(srcFile.isDirectory()) {
                assets.add(new Asset(destFile, AssetType.Directory));
                copyDirectory(srcFile, destFile, filter, assets);
            }
            else
                copyFile(srcFile, destFile, filter, assets);
        }
    }
}
