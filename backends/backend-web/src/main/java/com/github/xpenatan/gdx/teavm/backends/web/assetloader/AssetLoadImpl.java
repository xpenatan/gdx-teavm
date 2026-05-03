package com.github.xpenatan.gdx.teavm.backends.web.assetloader;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.backends.web.WebFileHandle;
import com.github.xpenatan.gdx.teavm.backends.web.dom.DataTransferWrapper;
import com.github.xpenatan.gdx.teavm.backends.web.dom.DragEventWrapper;
import com.github.xpenatan.gdx.teavm.backends.web.dom.FileReaderWrapper;
import com.github.xpenatan.gdx.teavm.backends.web.dom.typedarray.TypedArrays;
import com.github.xpenatan.gdx.teavm.backends.web.filesystem.FileData;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.core.JSArrayReader;
import org.teavm.jso.core.JSPromise;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.file.File;
import org.teavm.jso.file.FileList;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Int8Array;

/**
 * @author xpenatan
 */
public class AssetLoadImpl implements AssetLoader {
    public int assetTotal = -1;

    private static final String ASSET_FOLDER = "assets/";
    private static final String SCRIPTS_FOLDER = "scripts/";
    private static final boolean DEBUG_ASSET_LOAD = false;

    public final String baseUrl;

    private Array<QueueAsset> assetInQueue;
    private HashSet<String> assetDownloading;
    private HashSet<String> assetFailed;

    private AssetDownloader assetDownloader;

    private int maxMultiDownloadCount = 5;

    public AssetLoadImpl(String newBaseURL, WebApplication teaApplication, AssetDownloader assetDownloader) {
        this.assetDownloader = assetDownloader;
        baseUrl = newBaseURL;
        assetInQueue = new Array<>();
        assetDownloading = new HashSet<>();
        assetFailed = new HashSet<>();
        log("<init>", "baseUrl=" + baseUrl + ", maxMultiDownloadCount=" + maxMultiDownloadCount);
    }

    public void setupFileDrop(HTMLCanvasElement canvas, WebApplication teaApplication) {
        log("setupFileDrop", "windowListener=" + (teaApplication.getConfig().windowListener != null));
        WebApplicationConfiguration config = teaApplication.getConfig();
        if(config.windowListener != null) {
            HTMLDocument document = canvas.getOwnerDocument();
            document.addEventListener("dragenter", new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    log("setupFileDrop.dragenter", "event");
                    evt.preventDefault();
                }
            }, false);
            document.addEventListener("dragover", new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    log("setupFileDrop.dragover", "event");
                    evt.preventDefault();
                }
            }, false);
            document.addEventListener("drop", new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    log("setupFileDrop.drop", "event");
                    evt.preventDefault();
                    DragEventWrapper event = (DragEventWrapper)evt;
                    DataTransferWrapper dataTransfer = event.getDataTransfer();
                    FileList files = dataTransfer.getFiles();
                    log("setupFileDrop.drop", "filesLength=" + files.getLength());
                    downloadDroppedFile(config, files);
                }
            });
        }
    }

    private JSPromise<FileData> getFile(String name, File fileWrapper) {
        log("getFile", "name=" + name);
        JSPromise<FileData> success = new JSPromise<>((resolve, reject) -> {
            FileReaderWrapper fileReader = FileReaderWrapper.create();
            fileReader.readAsArrayBuffer(fileWrapper);

            fileReader.addEventListener("load", new EventListener<Event>() {
                @Override
                public void handleEvent(Event evt) {
                    log("getFile.load", "name=" + name);
                    FileReaderWrapper target = (FileReaderWrapper)evt.getTarget();
                    ArrayBuffer arrayBuffer = target.getResultAsArrayBuffer();
                    Int8Array data = new Int8Array(arrayBuffer);
                    byte[] bytes = TypedArrays.toByteArray(data);
                    FileData fielData = new FileData(name, bytes);
                    resolve.accept(fielData);
                }
            });
        });

        return success;
    }

    private void downloadDroppedFile(WebApplicationConfiguration config, FileList files) {
        log("downloadDroppedFile", "totalDraggedFiles=" + files.getLength());
        int totalDraggedFiles = files.getLength();
        if(totalDraggedFiles > 0) {
            Array<String> droppedFiles = new Array<>();
            var promises = new JSArray<JSPromise<FileData>>();
            for(int i = 0; i < totalDraggedFiles; i++) {
                File fileWrapper = files.get(i);
                String name = fileWrapper.getName();
                log("downloadDroppedFile", "candidate=" + name);

                if(config.windowListener.acceptFileDropped(name)) {
                    log("downloadDroppedFile", "accepted=" + name);
                    JSPromise<FileData> promiss = getFile(name, fileWrapper);
                    promises.push(promiss);
                }
                else {
                    log("downloadDroppedFile", "rejected=" + name);
                }
            }

            JSPromise<JSArrayReader<FileData>> all = JSPromise.all(promises);
            all.then(array -> {
                log("downloadDroppedFile.then", "length=" + array.getLength());
                int length = array.getLength();
                FileData [] arr = new FileData[length];
                for(int i = 0; i < length; i++) {
                    FileData fileData = array.get(i);
                    arr[i] = fileData;
                }
                config.windowListener.filesDropped(arr);
                return "success";
            }, reason -> {
                log("downloadDroppedFile.then", "failure reason=" + reason);
                return "failure";
            }).onSettled(() -> {
                log("downloadDroppedFile.onSettled", "completed");
                return null;
            });
        }
    }

    @Override
    public String getAssetUrl() {
        log("getAssetUrl", "result=" + (baseUrl + ASSET_FOLDER));
        return baseUrl + ASSET_FOLDER;
    }

    @Override
    public String getScriptUrl() {
        log("getScriptUrl", "result=" + (baseUrl + SCRIPTS_FOLDER));
        return baseUrl + SCRIPTS_FOLDER;
    }

    @Override
    public void preload(final String assetFileUrl, AssetLoaderListener<Void> preloadListener) {
        log("preload", "assetFileUrl=" + assetFileUrl);
        AssetLoaderListener<WebBlob> listener = new AssetLoaderListener<>() {
            @Override
            public void onSuccess(String url, WebBlob result) {
                log("preload.onSuccess", "assetFileUrl=" + assetFileUrl + ", bytes=" + (result != null ? result.getData().getLength() : -1));
                assetDownloading.remove(assetFileUrl);
                Int8Array data = result.getData();
                byte[] byteArray = TypedArrays.toByteArray(data);
                String assets = new String(byteArray);
                String[] lines = assets.split("\n");

                assetTotal = lines.length;

                for(String line : lines) {
                    log("preload.onSuccess", "assets.txt line=" + line);
                    String[] tokens = line.split(":");
                    if(tokens.length != 5) {
                        throw new GdxRuntimeException("Invalid assets description file. " + tokens.length + " " + line);
                    }
                    String fileTypeStr = tokens[0];
                    String assetTypeStr = tokens[1];
                    String assetUrl = tokens[2].trim();
                    String fileLength = tokens[3];
                    boolean shouldOverwriteLocalData = tokens[4].equals("1");
                    assetUrl = assetUrl.trim();
                    if(assetUrl.isEmpty()) {
                        continue;
                    }

                    FileType fileType = FileType.Internal;
                    if(fileTypeStr.equals("c")) {
                        fileType = FileType.Classpath;
                    }
                    else if(fileTypeStr.equals("l")) {
                        fileType = FileType.Local;
                    }
                    AssetType assetType = AssetType.Binary;
                    if(assetTypeStr.equals("d")) assetType = AssetType.Directory;

                    addAssetToQueue(assetUrl, assetType, fileType, null, shouldOverwriteLocalData);
                }
                logState("preload.onSuccess.afterQueuePopulate");
                preloadListener.onSuccess(assetFileUrl, null);
                downloadMultiAssets();
            }

            @Override
            public void onFailure(String url) {
                assetDownloading.remove(assetFileUrl);
                log("preload.onFailure", "assetFileUrl=" + assetFileUrl);
                preloadListener.onFailure(assetFileUrl);
            }
        };

        assetDownloading.add(assetFileUrl);
        logState("preload.beforeLoad");
        assetDownloader.load(true, getAssetUrl() + assetFileUrl, AssetType.Binary, listener);
    }

    @Override
    public boolean isAssetInQueueOrDownloading(String path) {
        String path1 = fixPath(path);
        boolean assetInQueue = assetInQueue(path1);
        boolean isDownloading = assetDownloading.contains(path1);
        log("isAssetInQueueOrDownloading", "path=" + path + ", fixed=" + path1 + ", inQueue=" + assetInQueue + ", isDownloading=" + isDownloading);
        return assetInQueue || isDownloading;
    }

    @Override
    public boolean isAssetLoaded(FileType fileType, String path) {
        String path1 = fixPath(path);
        FileHandle fileHandle = Gdx.files.getFileHandle(path1, fileType);
        boolean exists = fileHandle.exists();
        log("isAssetLoaded", "path=" + path + ", fixed=" + path1 + ", fileType=" + fileType + ", exists=" + exists);
        return exists;
    }

    @Override
    public boolean isAssetFailed(FileType fileType, String path) {
        String path1 = fixPath(path);
        boolean failed = assetFailed.contains(path1);
        log("isAssetFailed", "path=" + path + ", fixed=" + path1 + ", failed=" + failed);
        return failed;
    }

    @Override
    public void loadAsset(String path, AssetType assetType, FileType fileType) {
        log("loadAsset", "path=" + path + ", assetType=" + assetType + ", fileType=" + fileType + ", listener=false, overwrite=false");
        loadAssetInternal(path, assetType, fileType, null, false);
    }

    @Override
    public void loadAsset(String path, AssetType assetType, FileType fileType, AssetLoaderListener<WebBlob> listener) {
        log("loadAsset", "path=" + path + ", assetType=" + assetType + ", fileType=" + fileType + ", listener=" + (listener != null) + ", overwrite=false");
        loadAssetInternal(path, assetType, fileType, listener, false);
    }

    @Override
    public void loadAsset(String path, AssetType assetType, FileType fileType, AssetLoaderListener<WebBlob> listener, boolean overwrite) {
        log("loadAsset", "path=" + path + ", assetType=" + assetType + ", fileType=" + fileType + ", listener=" + (listener != null) + ", overwrite=" + overwrite);
        loadAssetInternal(path, assetType, fileType, listener, overwrite);
    }

    @Override
    public void loadScript(String path) {
        log("loadScript", "path=" + path + ", listener=false");
        assetDownloader.loadScript(true, getScriptUrl() + path, null);
    }

    @Override
    public void loadScript(String path, AssetLoaderListener<String> listener) {
        log("loadScript", "path=" + path + ", listener=" + (listener != null));
        assetDownloader.loadScript(true, getScriptUrl() + path, listener);
    }

    @Override
    public int getQueue() {
        int size = assetInQueue.size;
        log("getQueue", "size=" + size);
        return size;
    }

    @Override
    public int getDownloadingCount() {
        int size = assetDownloading.size();
        log("getDownloadingCount", "size=" + size);
        return size;
    }

    @Override
    public boolean isDownloading() {
        boolean result = getQueue() > 0 || getDownloadingCount() > 0;
        log("isDownloading", "result=" + result);
        return result;
    }

    private void loadAssetInternal(String path, AssetType assetType, FileType fileType, AssetLoaderListener<WebBlob> listener, boolean overwrite) {
        log("loadAssetInternal", "path=" + path + ", assetType=" + assetType + ", fileType=" + fileType + ", listener=" + (listener != null) + ", overwrite=" + overwrite);
        addAssetToQueue(path, assetType, fileType, listener, overwrite);
        logState("loadAssetInternal.afterAddAssetToQueue");
        downloadQueueAssets();
    }

    private void addAssetToQueue(String path, AssetType assetType, FileType fileType, AssetLoaderListener<WebBlob> listener, boolean overwrite) {
        String path1 = fixPath(path);
        log("addAssetToQueue", "path=" + path + ", fixed=" + path1 + ", assetType=" + assetType + ", fileType=" + fileType + ", listener=" + (listener != null) + ", overwrite=" + overwrite);

        if(path1.isEmpty()) {
            log("addAssetToQueue", "skip empty path");
            return;
        }

        if(assetInQueue(path1)) {
            log("addAssetToQueue", "skip already in queue path=" + path1);
            return;
        }
        if(assetDownloading.contains(path1)) {
            log("addAssetToQueue", "skip already downloading path=" + path1);
            return;
        }

        WebFileHandle fileHandle = (WebFileHandle)Gdx.files.getFileHandle(path1, fileType);
        boolean exists = fileHandle.exists();
        if(!overwrite && exists) {
            log("addAssetToQueue", "skip existing path=" + path1 + ", overwrite=false");
            return;
        }

        if(assetType == AssetType.Directory) {
            if(!exists) {
                fileHandle.mkdirsInternal();
                log("addAssetToQueue", "created directory path=" + path1);
            }
            log("addAssetToQueue", "directory path processed path=" + path1);
            return;
        }

        QueueAsset queueAsset = new QueueAsset();
        queueAsset.assetUrl = path1;
        queueAsset.fileHandle = fileHandle;
        queueAsset.listener = listener;
        assetInQueue.add(queueAsset);
        // Clear any prior failure marker so retries can be attempted.
        assetFailed.remove(path1);
        logState("addAssetToQueue.added path=" + path1);
    }

    private void downloadMultiAssets() {
        log("downloadMultiAssets", "start maxMultiDownloadCount=" + maxMultiDownloadCount);
        for(int i = 0; i < maxMultiDownloadCount; i++) {
            log("downloadMultiAssets", "iteration=" + i);
            downloadQueueAssets();
        }
        logState("downloadMultiAssets.end");
    }

    private void downloadQueueAssets() {
        logState("downloadQueueAssets.start");
        if(assetInQueue.size == 0 || assetDownloading.size() >= maxMultiDownloadCount) {
            log("downloadQueueAssets", "skip queueEmpty=" + (assetInQueue.size == 0) + ", downloadingFull=" + (assetDownloading.size() >= maxMultiDownloadCount));
            return;
        }

        while(assetInQueue.size > 0 && assetDownloading.size() < maxMultiDownloadCount) {
            QueueAsset queueAsset = assetInQueue.removeIndex(0);
            final String assetPath = queueAsset.assetUrl;
            final FileHandle fileHandle = queueAsset.fileHandle;
            final AssetLoaderListener<WebBlob> listener = queueAsset.listener;
            log("downloadQueueAssets", "dequeue path=" + assetPath + ", listener=" + (listener != null));
            if(assetDownloading.contains(assetPath)) {
                log("downloadQueueAssets", "skip duplicate in downloading path=" + assetPath);
                continue;
            }
            assetDownloading.add(assetPath);
            logState("downloadQueueAssets.beforeStart path=" + assetPath);
            createAndStartDownload(assetPath, fileHandle, listener);
            return;
        }
        logState("downloadQueueAssets.end");
    }

    private void createAndStartDownload(final String assetPath, final FileHandle fileHandle, final AssetLoaderListener<WebBlob> listener) {
        // Create an independent wrapper listener to ensure proper closure capture
        log("createAndStartDownload", "assetPath=" + assetPath + ", fileType=" + fileHandle.type() + ", listener=" + (listener != null));
        AssetLoaderListener<WebBlob> downloadListener = createDownloadListener(assetPath, fileHandle, listener);
        try {
            assetDownloader.load(true, getAssetUrl() + assetPath, AssetType.Binary, downloadListener);
            log("createAndStartDownload", "load started assetPath=" + assetPath);
        }
        catch(Throwable t) {
            assetDownloading.remove(assetPath);
            assetFailed.add(assetPath);
            log("createAndStartDownload", "load threw assetPath=" + assetPath + ", error=" + t);
            logState("createAndStartDownload.catch path=" + assetPath);
            if(listener != null) {
                listener.onFailure(assetPath);
            }
            downloadMultiAssets();
        }
    }

    private AssetLoaderListener<WebBlob> createDownloadListener(final String assetPath, final FileHandle fileHandle, final AssetLoaderListener<WebBlob> listener) {
        log("createDownloadListener", "assetPath=" + assetPath + ", listener=" + (listener != null));
        return new AssetLoaderListener<>() {
            @Override
            public void onFailure(String url) {
                assetDownloading.remove(assetPath);
                assetFailed.add(assetPath);
                log("createDownloadListener.onFailure", "assetPath=" + assetPath + ", url=" + url);
                logState("createDownloadListener.onFailure.afterUpdate path=" + assetPath);
                if(listener != null) {
                    listener.onFailure(assetPath);
                }
                downloadMultiAssets();
            }

            @Override
            public void onSuccess(String url, WebBlob result) {
                assetDownloading.remove(assetPath);
                assetFailed.remove(assetPath);
                log("createDownloadListener.onSuccess", "assetPath=" + assetPath + ", url=" + url + ", bytes=" + (result != null ? result.getData().getLength() : -1));
                logState("createDownloadListener.onSuccess.afterUpdate path=" + assetPath);
                Int8Array data = result.getData();
                byte[] byteArray = TypedArrays.toByteArray(data);
                OutputStream output = fileHandle.write(false, 4096);
                try {
                    log("createDownloadListener.onSuccess", "writing file path=" + assetPath + ", bytes=" + byteArray.length);
                    output.write(byteArray);
                }
                catch(IOException ex) {
                    log("createDownloadListener.onSuccess", "write error path=" + assetPath + ", error=" + ex);
                    throw new GdxRuntimeException("Error writing file: " + fileHandle + " (" + fileHandle.type() + ")", ex);
                }
                finally {
                    StreamUtils.closeQuietly(output);
                    log("createDownloadListener.onSuccess", "write stream closed path=" + assetPath);
                }
                if(listener != null) {
                    listener.onSuccess(assetPath, result);
                }
                downloadMultiAssets();
            }
        };
    }

    private boolean assetInQueue(String path) {
        log("assetInQueue", "path=" + path + ", queueSize=" + assetInQueue.size);
        for(int i = 0; i < assetInQueue.size; i++) {
            QueueAsset queueAsset = assetInQueue.get(i);
            if(queueAsset.assetUrl.equals(path)) {
                log("assetInQueue", "found path=" + path + ", index=" + i);
                return true;
            }
        }
        log("assetInQueue", "not found path=" + path);
        return false;
    }

    private String fixPath(String path1) {
        String original = path1;
        path1 = path1.trim().replace("\\", "/");
        if(path1.startsWith("/")) {
            path1 = path1.substring(1);
        }
        log("fixPath", "original=" + original + ", fixed=" + path1);
        return path1;
    }

    private void logState(String method) {
        if(DEBUG_ASSET_LOAD) {
            System.out.println("[AssetLoadImpl] " + method + " | queue=" + assetInQueue.size + ", downloading=" + assetDownloading.size() + ", failed=" + assetFailed.size());
        }
    }

    private void log(String method, String message) {
        if(DEBUG_ASSET_LOAD) {
            System.out.println("[AssetLoadImpl] " + method + " | " + message);
        }
    }
}