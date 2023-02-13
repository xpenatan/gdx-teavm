package com.github.xpenatan.gdx.backends.teavm.preloader;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.github.xpenatan.gdx.backends.teavm.AssetLoaderListener;
import com.github.xpenatan.gdx.backends.teavm.TeaFileHandle;
import com.github.xpenatan.gdx.backends.teavm.dom.HTMLImageElementWrapper;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author xpenatan
 */
public class Preloader {
    public ObjectMap<String, Void> directories = new ObjectMap<String, Void>();
    public ObjectMap<String, HTMLImageElementWrapper> images = new ObjectMap<String, HTMLImageElementWrapper>();
    public ObjectMap<String, Blob> audio = new ObjectMap<String, Blob>();
    public ObjectMap<String, String> texts = new ObjectMap<String, String>();
    public ObjectMap<String, Blob> binaries = new ObjectMap<String, Blob>();

    private static final String ASSET_FOLDER = "assets/";

    public static class Asset {
        public Asset(String url, AssetType type, long size, String mimeType) {
            this.url = url;
            this.type = type;
            this.size = size;
            this.mimeType = mimeType;
        }

        public boolean succeed;
        public boolean failed;
        public long loaded;
        public final String url;
        public final AssetType type;
        public final long size;
        public final String mimeType;
    }

    public final String baseUrl;

    public Preloader(String newBaseURL) {
        baseUrl = newBaseURL;
    }

    public String getAssetUrl() {
        return baseUrl + ASSET_FOLDER;
    }

    public void preload(final String assetFileUrl) {
        AssetDownloader.getInstance().loadText(true, getAssetUrl() + assetFileUrl, new AssetLoaderListener<String>() {
            @Override
            public void onProgress(double amount) {
            }

            @Override
            public void onFailure(String url) {
                System.out.println("ErrorLoading: " + assetFileUrl);
            }

            @Override
            public boolean onSuccess(String url, String result) {
                String[] lines = result.split("\n");
                Array<Asset> assets = new Array<Asset>(lines.length);
                for(String line : lines) {
                    String[] tokens = line.split(":");
                    if(tokens.length != 4) {
                        throw new GdxRuntimeException("Invalid assets description file.");
                    }
                    AssetType type = AssetType.Text;
                    if(tokens[0].equals("i")) type = AssetType.Image;
                    if(tokens[0].equals("b")) type = AssetType.Binary;
                    if(tokens[0].equals("a")) type = AssetType.Audio;
                    if(tokens[0].equals("d")) type = AssetType.Directory;
                    long size = Long.parseLong(tokens[2]);
                    if(type == AssetType.Audio && !AssetDownloader.getInstance().isUseBrowserCache()) {
                        size = 0;
                    }
                    assets.add(new Asset(tokens[1].trim(), type, size, tokens[3]));
                }

                for(int i = 0; i < assets.size; i++) {
                    final Asset asset = assets.get(i);

                    if(contains(asset.url)) {
                        asset.loaded = asset.size;
                        asset.succeed = true;
                        continue;
                    }

                    loadAsset(true, asset.url, asset.type, asset.mimeType, new AssetLoaderListener<Object>() {

                        @Override
                        public void onProgress(double amount) {
                        }

                        @Override
                        public void onFailure(String url) {
                        }

                        @Override
                        public boolean onSuccess(String url, Object result) {
                            return false;
                        }
                    });
                }
                return false;
            }
        });
    }

    public void loadBinaryAsset(boolean async, final String url, AssetLoaderListener<Blob> listener) {
        AssetDownloader.getInstance().load(async, getAssetUrl() + url, AssetType.Binary, null, new AssetLoaderListener<Blob>() {
            @Override
            public void onProgress(double amount) {
                listener.onProgress(amount);
            }

            @Override
            public void onFailure(String urll) {
                listener.onFailure(urll);
            }

            @Override
            public boolean onSuccess(String urll, Blob result) {
                putAssetInMap(AssetType.Binary, url, result);
                listener.onSuccess(urll, result);
                return false;
            }
        });
    }

    public void loadAsset(boolean async, final String url, final AssetType type, final String mimeType, final AssetLoaderListener<Object> listener) {
        AssetDownloader.getInstance().load(async, getAssetUrl() + url, type, mimeType, new AssetLoaderListener<Object>() {
            @Override
            public void onProgress(double amount) {
                listener.onProgress(amount);
            }

            @Override
            public void onFailure(String urll) {
                listener.onFailure(urll);
            }

            @Override
            public boolean onSuccess(String urll, Object result) {
                putAssetInMap(type, url, result);
                listener.onSuccess(urll, result);
                return false;
            }
        });
    }

    public void loadScript(boolean async, final String url, final AssetLoaderListener<Object> listener) {
        AssetDownloader.getInstance().loadScript(async, baseUrl + url, listener);
    }

    protected void putAssetInMap(AssetType type, String url, Object result) {
        switch(type) {
            case Text:
                texts.put(url, (String)result);
                break;
            case Image:
                images.put(url, (HTMLImageElementWrapper)result);
                break;
            case Binary:
                binaries.put(url, (Blob)result);
                break;
            case Audio:
                audio.put(url, (Blob)result);
                break;
            case Directory:
                directories.put(url, null);
                break;
        }
    }

    public InputStream read(String url) {
        if(texts.containsKey(url)) {
            try {
                return new ByteArrayInputStream(texts.get(url).getBytes("UTF-8"));
            }
            catch(UnsupportedEncodingException e) {
                return null;
            }
        }
        if(images.containsKey(url)) {
            return new ByteArrayInputStream(new byte[1]); // FIXME, sensible?
        }
        if(binaries.containsKey(url)) {
            return binaries.get(url).read();
        }
        if(audio.containsKey(url)) {
            return new ByteArrayInputStream(new byte[1]); // FIXME, sensible?
        }
        return null;
    }

    public boolean contains(String file) {
        return texts.containsKey(file) || images.containsKey(file) || binaries.containsKey(file) || audio.containsKey(file)
                || directories.containsKey(file);
    }

    public boolean isText(String url) {
        return texts.containsKey(url);
    }

    public boolean isImage(String url) {
        return images.containsKey(url);
    }

    public boolean isBinary(String url) {
        return binaries.containsKey(url);
    }

    public boolean isAudio(String url) {
        return audio.containsKey(url);
    }

    public boolean isDirectory(String url) {
        return directories.containsKey(url);
    }

    private boolean isChild(String filePath, String directory) {
        return filePath.startsWith(directory + "/");
    }

    public FileHandle[] list(final String file) {
        return getMatchedAssetFiles(new FilePathFilter() {
            @Override
            public boolean accept(String path) {
                return isChild(path, file);
            }
        });
    }

    public FileHandle[] list(final String file, final FileFilter filter) {
        return getMatchedAssetFiles(new FilePathFilter() {
            @Override
            public boolean accept(String path) {
                return isChild(path, file) && filter.accept(new File(path));
            }
        });
    }

    public FileHandle[] list(final String file, final FilenameFilter filter) {
        return getMatchedAssetFiles(new FilePathFilter() {
            @Override
            public boolean accept(String path) {
                return isChild(path, file) && filter.accept(new File(file), path.substring(file.length() + 1));
            }
        });
    }

    public FileHandle[] list(final String file, final String suffix) {
        return getMatchedAssetFiles(new FilePathFilter() {
            @Override
            public boolean accept(String path) {
                return isChild(path, file) && path.endsWith(suffix);
            }
        });
    }

    public long length(String file) {
        if(texts.containsKey(file)) {
            return texts.get(file).getBytes(StandardCharsets.UTF_8).length;
        }
        if(images.containsKey(file)) {
            return 1; // FIXME, sensible?
        }
        if(binaries.containsKey(file)) {
            return binaries.get(file).length();
        }
        if(audio.containsKey(file)) {
            return audio.get(file).length();
        }
        return 0;
    }

    private interface FilePathFilter {
        boolean accept(String path);
    }

    private FileHandle[] getMatchedAssetFiles(FilePathFilter filter) {
        Array<FileHandle> files = new Array<>();
        for(String file : texts.keys()) {
            if(filter.accept(file)) {
                files.add(new TeaFileHandle(this, file, FileType.Internal));
            }
        }
        for(String file : images.keys()) {
            if(filter.accept(file)) {
                files.add(new TeaFileHandle(this, file, FileType.Internal));
            }
        }
        for(String file : binaries.keys()) {
            if(filter.accept(file)) {
                files.add(new TeaFileHandle(this, file, FileType.Internal));
            }
        }
        for(String file : audio.keys()) {
            if(filter.accept(file)) {
                files.add(new TeaFileHandle(this, file, FileType.Internal));
            }
        }
        FileHandle[] filesArray = new FileHandle[files.size];
        System.arraycopy(files.items, 0, filesArray, 0, filesArray.length);
        return filesArray;
    }

    public void printLoadedAssets() {
        Entries<String, HTMLImageElementWrapper> iterator = images.iterator();
        System.out.println("### Text Assets: ");
        printKeys(texts);
        System.out.println("##########");
        System.out.println("### Image Assets: ");
        printKeys(images);
        System.out.println("##########");
    }

    private void printKeys(ObjectMap<String, ?> map) {
        Entries<String, ?> iterator = map.iterator();

        while(iterator.hasNext) {
            Entry<String, ?> next = iterator.next();
            String key = next.key;
            System.out.println("Key: " + key);
        }
    }
}
