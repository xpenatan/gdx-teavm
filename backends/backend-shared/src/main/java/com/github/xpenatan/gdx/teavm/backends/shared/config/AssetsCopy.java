package com.github.xpenatan.gdx.teavm.backends.shared.config;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.teavm.backends.web.assetloader.AssetType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public static class AssetPlan {
        public final ArrayList<PlannedAsset> assets = new ArrayList<>();
        public final ArrayList<String> assetOnlyClasspathResources = new ArrayList<>();
        public final ArrayList<String> scripts = new ArrayList<>();
        public final ArrayList<String> cppFiles = new ArrayList<>();
    }

    public static class PlannedAsset {
        enum SourceType {
            Disk, Classpath
        }

        final SourceType sourceType;
        final File diskFile;
        final String classpathPath;
        final String outputPath;
        final FileType runtimeType;
        final AssetType assetType;
        final AssetFilterOption op;
        long length;

        private PlannedAsset(SourceType sourceType, File diskFile, String classpathPath, String outputPath,
                             FileType runtimeType, AssetType assetType, AssetFilterOption op) {
            this.sourceType = sourceType;
            this.diskFile = diskFile;
            this.classpathPath = classpathPath;
            this.outputPath = normalizeRelative(outputPath);
            this.runtimeType = runtimeType;
            this.assetType = assetType;
            this.op = op != null ? op : new AssetFilterOption();
        }
    }

    public static AssetPlan createAssetPlan(ClassLoader classLoader,
                                            ArrayList<URL> classPathURLs,
                                            List<AssetFileHandle> configuredAssets,
                                            AssetFilter filter) {
        AssetPlan plan = new AssetPlan();
        AssetFilter defaultAssetFilter = filter != null ? filter : new DefaultAssetFilter();

        if(configuredAssets != null) {
            for(AssetFileHandle assetFileHandle : configuredAssets) {
                if(assetFileHandle.isClasspathResource()) {
                    AssetFilter f = assetFileHandle.filter != null ? assetFileHandle.filter : defaultAssetFilter;
                    planClasspathEntry(classLoader, assetFileHandle.getClasspathResource(), f, FileType.Classpath, plan);
                }
                else {
                    planDiskAssets(assetFileHandle, defaultAssetFilter, plan);
                }
            }
        }

        if(classPathURLs != null) {
            TeaVMResourceProperties.CollectedResources collected = TeaVMResourceProperties.collect(classPathURLs);
            List<String> internalResources = partitionResources(collected.internalResources, plan);
            planClasspathResources(classLoader, internalResources, defaultAssetFilter, FileType.Classpath, plan);
            for(String resourcePath : collected.classpathResourcePaths) {
                planClasspathEntry(classLoader, resourcePath, defaultAssetFilter, FileType.Classpath, plan);
            }
        }
        return plan;
    }

    public static void copyPlanAssets(ClassLoader classLoader,
                                      AssetPlan plan,
                                      AssetOutput output,
                                      String assetsFolderName) throws IOException {
        if(plan == null) {
            return;
        }
        if(plan.assets.isEmpty() && plan.assetOnlyClasspathResources.isEmpty()) {
            TeaLogHelper.log("No assets to copy");
            return;
        }
        TeaLogHelper.log("Asset destination:");
        TeaLogHelper.log(output.describePath(assetsFolderName));
        for(PlannedAsset asset : plan.assets) {
            String targetPath = joinPath(assetsFolderName, asset.outputPath);
            if(asset.sourceType == PlannedAsset.SourceType.Disk) {
                try(InputStream input = Files.newInputStream(asset.diskFile.toPath())) {
                    asset.length = copy(input, output, targetPath);
                    logCopied(asset.runtimeType, asset.diskFile.getAbsolutePath(), output, targetPath, asset.length);
                }
            }
            else {
                try(InputStream input = classLoader.getResourceAsStream(asset.classpathPath)) {
                    if(input != null) {
                        asset.length = copy(input, output, targetPath);
                        logCopied(asset.runtimeType, asset.classpathPath, output, targetPath, asset.length);
                    }
                }
            }
        }
        copyClasspathResources(classLoader, plan.assetOnlyClasspathResources, null, output, assetsFolderName);
    }

    public static void copyClasspathResources(ClassLoader classLoader,
                                              List<String> classPathAssetsFiles,
                                              AssetFilter filter,
                                              AssetOutput output,
                                              String targetFolder) throws IOException {
        if(classLoader == null || classPathAssetsFiles == null || classPathAssetsFiles.isEmpty()) {
            return;
        }
        AssetFilter defaultAssetFilter = filter != null ? filter : new DefaultAssetFilter();
        TeaLogHelper.log("Classpath resource destination:");
        TeaLogHelper.log(output.describePath(targetFolder));
        for(String entry : classPathAssetsFiles) {
            String lookup = normalizeClasspath(entry);
            String filterArg = "/" + lookup;
            if(!defaultAssetFilter.accept(filterArg)) continue;
            try(InputStream input = classLoader.getResourceAsStream(lookup)) {
                if(input != null) {
                    String targetPath = joinPath(targetFolder, lookup);
                    long length = copy(input, output, targetPath);
                    logCopied("Classpath resource", lookup, output, targetPath, length);
                }
            }
        }
    }

    public static String generateManifest(AssetPlan plan) {
        StringBuilder buffer = new StringBuilder();
        for(String entry : generateManifestEntries(plan)) {
            buffer.append(entry).append("\n");
        }
        return buffer.toString();
    }

    public static String[] generateManifestEntries(AssetPlan plan) {
        if(plan == null || plan.assets.isEmpty()) {
            return new String[0];
        }
        String[] entries = new String[plan.assets.size()];
        for(int i = 0; i < plan.assets.size(); i++) {
            PlannedAsset asset = plan.assets.get(i);
            entries[i] = formatManifestEntry(asset.runtimeType, asset.assetType, "/" + asset.outputPath,
                    asset.length, asset.op);
        }
        return entries;
    }

    public static void measureAssetLengths(ClassLoader classLoader, AssetPlan plan) throws IOException {
        if(plan == null) {
            return;
        }
        for(PlannedAsset asset : plan.assets) {
            if(asset.sourceType == PlannedAsset.SourceType.Disk) {
                asset.length = Files.size(asset.diskFile.toPath());
            }
            else {
                try(InputStream input = classLoader.getResourceAsStream(asset.classpathPath)) {
                    if(input != null) {
                        asset.length = measure(input);
                    }
                }
            }
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
     * {@link Asset} entries are tagged with the supplied {@code destinationType}.
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
        TeaLogHelper.log("Classpath resource destination:");
        TeaLogHelper.log(assetsOutputPath.path());
        for(String entry : classPathAssetsFiles) {
            String classpathPath = entry;
            String displayPath = classpathPath;
            // The classloader expects paths without a leading slash.
            String lookup = classpathPath.startsWith("/") ? classpathPath.substring(1) : classpathPath;
            String filterArg = classpathPath.startsWith("/") ? classpathPath : "/" + classpathPath;
            if(!defaultAssetFilter.accept(filterArg)) continue;

            try(InputStream is = classLoader.getResourceAsStream(lookup)) {
                if(is == null) continue;
                FileHandle dest = assetsOutputPath.child(lookup);
                dest.write(is, false);
                logCopied(destinationType, displayPath, dest.path(), dest.length());
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

        TeaLogHelper.log("Asset source:");
        TeaLogHelper.log(assetsPath.path());
        FileHandle destRoot = target;
        if(!assetsPath.assetsChildDir.isEmpty()) {
            destRoot = destRoot.child(assetsPath.assetsChildDir);
        }
        copyDirectory(assetsPath, destRoot, combined, assets);
        TeaLogHelper.log("Asset destination:");
        TeaLogHelper.log(target.path());
        return assets;
    }

    private static String formatManifestEntry(FileType type, AssetType assetType, String path, long length,
                                              AssetFilterOption op) {
        String fileTypeStr = "i";
        if(type == FileType.Local) fileTypeStr = "l";
        else if(type == FileType.Classpath) fileTypeStr = "c";

        return fileTypeStr
                + ":" + assetType.code
                + ":" + path
                + ":" + length
                + ":" + (op.shouldOverwriteLocalData ? 1 : 0);
    }

    private static void planDiskAssets(AssetFileHandle assetsPath, AssetFilter filter, AssetPlan plan) {
        if(assetsPath == null || !assetsPath.exists()) {
            return;
        }
        AssetFilter defaultAssetFilter = filter != null ? filter : new DefaultAssetFilter();
        AssetFilter combined = file -> {
            if(assetsPath.filter != null && !assetsPath.filter.accept(file)) return false;
            return defaultAssetFilter.accept(file);
        };

        FileHandle destRoot = new FileHandle("");
        if(!assetsPath.assetsChildDir.isEmpty()) {
            destRoot = destRoot.child(assetsPath.assetsChildDir);
        }

        if(assetsPath.isDirectory()) {
            planDiskDirectory(assetsPath.file().toPath(), assetsPath.file().toPath(), destRoot.path(), combined,
                    assetsPath.type(), plan);
        }
        else if(!assetsPath.isDirectory()) {
            String outputPath = destRoot.child(assetsPath.name()).path();
            addDiskAsset(assetsPath.file().toPath(), outputPath, combined, assetsPath.type(), plan);
        }
    }

    private static void planDiskDirectory(Path root, Path sourceDir, String destRoot, AssetFilter filter,
                                          FileType runtimeType, AssetPlan plan) {
        try(var stream = Files.walk(sourceDir)) {
            var iterator = stream.iterator();
            while(iterator.hasNext()) {
                Path path = iterator.next();
                if(!Files.isRegularFile(path)) {
                    continue;
                }
                String relative = root.relativize(path).toString().replace('\\', '/');
                String outputPath = destRoot == null || destRoot.isEmpty() ? relative : destRoot + "/" + relative;
                addDiskAsset(path, outputPath, filter, runtimeType, plan);
            }
        } catch(IOException e) {
            throw new GdxRuntimeException("Error planning assets from: " + sourceDir, e);
        }
    }

    private static void addDiskAsset(Path source, String outputPath, AssetFilter filter, FileType runtimeType,
                                     AssetPlan plan) {
        String normalizedOutput = normalizeRelative(outputPath);
        if(!filter.accept("/" + normalizedOutput)) {
            return;
        }
        plan.assets.add(new PlannedAsset(PlannedAsset.SourceType.Disk, source.toFile(), null, normalizedOutput,
                runtimeType, AssetType.Binary, new AssetFilterOption()));
    }

    private static void planClasspathEntry(ClassLoader classLoader, String resourcePath, AssetFilter filter,
                                           FileType runtimeType, AssetPlan plan) {
        List<String> paths = ClasspathResourceWalker
                .listResources(classLoader, resourcePath, ClasspathResourceWalker.DEFAULT_FILTER);
        if(paths.isEmpty()) {
            TeaLogHelper.log("Classpath assets: no resources found for '" + resourcePath + "'");
            return;
        }
        planClasspathResources(classLoader, paths, filter, runtimeType, plan);
    }

    private static void planClasspathResources(ClassLoader classLoader, List<String> paths, AssetFilter filter,
                                               FileType runtimeType, AssetPlan plan) {
        if(classLoader == null || paths == null || paths.isEmpty()) {
            return;
        }
        AssetFilter defaultAssetFilter = filter != null ? filter : new DefaultAssetFilter();
        for(String path : paths) {
            String lookup = normalizeClasspath(path);
            String filterArg = "/" + lookup;
            if(!defaultAssetFilter.accept(filterArg)) continue;
            if(lookup.endsWith(".js") || lookup.endsWith(".wasm")) {
                plan.assetOnlyClasspathResources.add(lookup);
                continue;
            }
            plan.assets.add(new PlannedAsset(PlannedAsset.SourceType.Classpath, null, lookup, lookup,
                    runtimeType, AssetType.Binary, new AssetFilterOption()));
        }
    }

    private static List<String> partitionResources(List<String> resources, AssetPlan plan) {
        ArrayList<String> remaining = new ArrayList<>(resources.size());
        for(String asset : resources) {
            if(asset.endsWith(".js") || asset.endsWith(".wasm")) {
                plan.scripts.add(asset);
            }
            else if(asset.startsWith("/external_cpp/")) {
                if(asset.endsWith(".lib") || asset.endsWith(".a") || asset.endsWith(".h")
                        || asset.endsWith(".c") || asset.endsWith(".cpp") || asset.endsWith(".cmake")) {
                    plan.cppFiles.add(asset);
                }
            }
            else {
                remaining.add(asset);
            }
        }
        return remaining;
    }

    private static long copy(InputStream input, AssetOutput output, String targetPath) throws IOException {
        try(OutputStream out = output.create(targetPath)) {
            byte[] buffer = new byte[8192];
            long total = 0;
            int read;
            while((read = input.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                total += read;
            }
            return total;
        }
    }

    private static long measure(InputStream input) throws IOException {
        byte[] buffer = new byte[8192];
        long total = 0;
        int read;
        while((read = input.read(buffer)) != -1) {
            total += read;
        }
        return total;
    }

    private static String joinPath(String left, String right) {
        String l = normalizeRelative(left);
        String r = normalizeRelative(right);
        if(l.isEmpty()) return r;
        if(r.isEmpty()) return l;
        return l + "/" + r;
    }

    private static String normalizeClasspath(String path) {
        String normalized = path.replace('\\', '/');
        while(normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    private static String normalizeRelative(String path) {
        if(path == null) {
            return "";
        }
        String normalized = path.replace('\\', '/');
        while(normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        while(normalized.contains("//")) {
            normalized = normalized.replace("//", "/");
        }
        return normalized;
    }

    private static void copyFile(FileHandle source, FileHandle dest, AssetFilter filter, ArrayList<Asset> assets) {
        if(!filter.accept(dest.path())) return;
        try {
            assets.add(new Asset(dest, AssetType.Binary, new AssetFilterOption()));
            try(InputStream read = source.read()) {
                dest.write(read, false);
            }
            logCopied(srcFileType(source), source.path(), dest.path(), source.length());
        }
        catch(Exception ex) {
            throw new GdxRuntimeException("Error copying source file: " + source + "\nTo destination: " + dest, ex);
        }
    }

    private static void logCopied(FileType runtimeType, String sourcePath, AssetOutput output, String targetPath,
                                  long length) {
        logCopied(typeName(runtimeType), sourcePath, length);
    }

    private static void logCopied(FileType runtimeType, String sourcePath, String targetPath, long length) {
        logCopied(typeName(runtimeType), sourcePath, length);
    }

    private static void logCopied(String typeName, String sourcePath, AssetOutput output, String targetPath,
                                  long length) {
        logCopied(typeName, sourcePath, length);
    }

    private static void logCopied(String typeName, String sourcePath, String targetPath, long length) {
        logCopied(typeName, sourcePath, length);
    }

    private static void logCopied(String typeName, String sourcePath, long length) {
        TeaLogHelper.log("Copied [" + typeName + "] " + normalizeDisplay(sourcePath) + " (" + length + " bytes)");
    }

    private static String typeName(FileType type) {
        if(type == FileType.Classpath) return "Classpath";
        if(type == FileType.Local) return "Local";
        return "Internal";
    }

    private static FileType srcFileType(FileHandle source) {
        if(source instanceof AssetFileHandle) {
            return source.type();
        }
        return FileType.Internal;
    }

    private static String normalizeDisplay(String path) {
        return path == null ? "" : path.replace('\\', '/');
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
