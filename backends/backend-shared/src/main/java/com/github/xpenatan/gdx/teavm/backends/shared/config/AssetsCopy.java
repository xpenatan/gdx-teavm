package com.github.xpenatan.gdx.teavm.backends.shared.config;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.teavm.backends.web.assetloader.AssetType;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xpenatan
 */
public class AssetsCopy {

    public static class Asset {
        FileHandle file;
        AssetType type;
        AssetFilterOption op;

        public Asset(FileHandle file, AssetType type, AssetFilterOption op) {
            this.file = file;
            this.type = type;
            this.op = op;
        }
    }

    /**
     * Copy a folder of assets (already on disk) to {@code assetsOutputPath}.
     * Returned assets keep their source {@link FileType} (typically Internal).
     */
    public static ArrayList<Asset> copyAssets(AssetFileHandle fileHandle, AssetFilter filter, FileHandle assetsOutputPath) {
        return copyDirectory(fileHandle, filter, assetsOutputPath);
    }

    /**
     * Copy a list of classpath resource paths (e.g. {@code /com/foo/bar.json})
     * into {@code assetsOutputPath}, preserving the package layout. The returned
     * {@link Asset} entries are tagged with the supplied {@code destinationType}
     * so {@link #generateAssetsFile} writes them into the preload manifest with
     * the matching {@code i:}/{@code c:}/{@code l:} marker.
     */
    public static ArrayList<Asset> copyResources(ClassLoader classLoader,
                                                 List<String> classPathAssetsFiles,
                                                 AssetFilter filter,
                                                 FileHandle assetsOutputPath,
                                                 FileType destinationType) {
        ArrayList<Asset> assets = new ArrayList<>();
        if(classLoader == null || classPathAssetsFiles == null || classPathAssetsFiles.isEmpty()) {
            return assets;
        }
        AssetFilter defaultAssetFilter = filter != null ? filter : new DefaultAssetFilter();

        TeaLogHelper.log("");
        TeaLogHelper.log("Copying classpath resources (" + destinationType + ") from:");
        for(String entry : classPathAssetsFiles) {
            String classpathPath = entry;
            String displayPath = classpathPath;
            // The classloader expects paths without a leading slash.
            String lookup = classpathPath.startsWith("/") ? classpathPath.substring(1) : classpathPath;
            String filterArg = classpathPath.startsWith("/") ? classpathPath : "/" + classpathPath;
            if(!defaultAssetFilter.accept(filterArg)) continue;

            try(InputStream is = classLoader.getResourceAsStream(lookup)) {
                if(is == null) continue;
                TeaLogHelper.log(displayPath);
                FileHandle dest = assetsOutputPath.child(lookup);
                dest.write(is, false);
                String destPath = dest.path();
                if(!destPath.endsWith(".js") && !destPath.endsWith(".wasm")) {
                    AssetFileHandle tagged = new AssetFileHandle(dest.file(), FileType.Absolute, destinationType);
                    assets.add(new Asset(tagged, AssetType.Binary, new AssetFilterOption()));
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        TeaLogHelper.log("to:");
        TeaLogHelper.log(assetsOutputPath.path());
        return assets;
    }

    /**
     * Copy a directory of files into {@code target} respecting the optional
     * {@code assetsChildDir} on the {@link AssetFileHandle}.
     */
    private static ArrayList<Asset> copyDirectory(AssetFileHandle assetsPath, AssetFilter filter, FileHandle target) {
        ArrayList<Asset> assets = new ArrayList<>();
        if(assetsPath == null || !assetsPath.exists() || !assetsPath.isDirectory()) {
            return assets;
        }
        AssetFilter defaultAssetFilter = filter != null ? filter : new DefaultAssetFilter();
        AssetFilter combined = file -> {
            if(assetsPath.filter != null && !assetsPath.filter.accept(file)) return false;
            return defaultAssetFilter.accept(file);
        };

        TeaLogHelper.log("Copying assets from:");
        TeaLogHelper.log(assetsPath.path());
        FileHandle destRoot = target;
        if(!assetsPath.assetsChildDir.isEmpty()) {
            destRoot = destRoot.child(assetsPath.assetsChildDir);
        }
        copyDirectory(assetsPath, destRoot, combined, assets);
        TeaLogHelper.log("to:");
        TeaLogHelper.log(target.path());
        return assets;
    }

    public static void generateAssetsFile(ArrayList<Asset> assets, FileHandle location, FileHandle assetFile) {
        StringBuilder buffer = new StringBuilder();
        String assetsOutputPath = location.path();
        for(int i = 0; i < assets.size(); i++) {
            setupPreloadAssetFileFormat(assets.get(i), buffer, assetsOutputPath);
        }
        assetFile.writeString(buffer.toString(), true);
    }

    private static void setupPreloadAssetFileFormat(Asset asset, StringBuilder buffer, String assetsOutputPath) {
        FileHandle fileHandle = asset.file;
        FileType type = fileHandle.type();
        String path = fileHandle.path();
        path = path.replace(assetsOutputPath, "");
        String fileTypeStr = "i";
        if(type == FileType.Local) fileTypeStr = "l";
        else if(type == FileType.Classpath) fileTypeStr = "c";

        buffer.append(fileTypeStr)
                .append(":").append(asset.type.code)
                .append(":").append(path)
                .append(":").append(asset.file.isDirectory() ? 0 : asset.file.length())
                .append(":").append(asset.op.shouldOverwriteLocalData ? 1 : 0)
                .append("\n");
    }

    private static void copyFile(FileHandle source, FileHandle dest, AssetFilter filter, ArrayList<Asset> assets) {
        if(!filter.accept(dest.path())) return;
        try {
            assets.add(new Asset(dest, AssetType.Binary, new AssetFilterOption()));
            try(InputStream read = source.read()) {
                dest.write(read, false);
            }
        }
        catch(Exception ex) {
            throw new GdxRuntimeException("Error copying source file: " + source + "\nTo destination: " + dest, ex);
        }
    }

    private static void copyDirectory(FileHandle sourceDir, FileHandle destDir, AssetFilter filter, ArrayList<Asset> assets) {
        destDir.mkdirs();
        FileHandle[] files = sourceDir.list();
        for(FileHandle srcFile : files) {
            FileHandle destChild = destDir.child(srcFile.name());
            // Destination type is copied from source type

            FileHandle destFile = new AssetFileHandle(destChild.file(), FileType.Absolute, srcFile.type());
            if(srcFile.isDirectory()) {
                copyDirectory(srcFile, destFile, filter, assets);
            }
            else {
                copyFile(srcFile, destFile, filter, assets);
            }
        }
    }
}