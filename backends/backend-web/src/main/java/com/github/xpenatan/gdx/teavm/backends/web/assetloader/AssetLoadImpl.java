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

    public final String baseUrl;

    private Array<QueueAsset> assetInQueue;
    private HashSet<String> assetDownloading;

    private AssetDownloader assetDownloader;

    private int maxMultiDownloadCount = 5;

    public AssetLoadImpl(String newBaseURL, WebApplication teaApplication, AssetDownloader assetDownloader) {
        this.assetDownloader = assetDownloader;
        baseUrl = newBaseURL;
        assetInQueue = new Array<>();
        assetDownloading = new HashSet<>();
    }

    public void setupFileDrop(HTMLCanvasElement canvas, WebApplication teaApplication) {
        WebApplicationConfiguration config = teaApplication.getConfig();
        if(config.windowListener != null) {
            HTMLDocument document = canvas.getOwnerDocument();
            document.addEventListener("dragenter", new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    evt.preventDefault();
                }
            }, false);
            document.addEventListener("dragover", new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    evt.preventDefault();
                }
            }, false);
            document.addEventListener("drop", new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    evt.preventDefault();
                    DragEventWrapper event = (DragEventWrapper)evt;
                    DataTransferWrapper dataTransfer = event.getDataTransfer();
                    FileList files = dataTransfer.getFiles();
                    downloadDroppedFile(config, files);
                }
            });
        }
    }

    private JSPromise<FileData> getFile(String name, File fileWrapper) {
        JSPromise<FileData> success = new JSPromise<>((resolve, reject) -> {
            FileReaderWrapper fileReader = FileReaderWrapper.create();
            fileReader.readAsArrayBuffer(fileWrapper);

            fileReader.addEventListener("load", new EventListener<Event>() {
                @Override
                public void handleEvent(Event evt) {
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
        int totalDraggedFiles = files.getLength();
        if(totalDraggedFiles > 0) {
            Array<String> droppedFiles = new Array<>();
            var promises = new JSArray<JSPromise<FileData>>();
            for(int i = 0; i < totalDraggedFiles; i++) {
                File fileWrapper = files.get(i);
                String name = fileWrapper.getName();

                if(config.windowListener.acceptFileDropped(name)) {
                    JSPromise<FileData> promiss = getFile(name, fileWrapper);
                    promises.push(promiss);
                }
            }

            JSPromise<JSArrayReader<FileData>> all = JSPromise.all(promises);
            all.then(array -> {
                int length = array.getLength();
                FileData [] arr = new FileData[length];
                for(int i = 0; i < length; i++) {
                    FileData fileData = array.get(i);
                    arr[i] = fileData;
                }
                config.windowListener.filesDropped(arr);
                return "success";
            }, reason -> {
                return "failure";
            }).onSettled(() -> {
                return null;
            });
        }
    }

    @Override
    public String getAssetUrl() {
        return baseUrl + ASSET_FOLDER;
    }

    @Override
    public String getScriptUrl() {
        return baseUrl + SCRIPTS_FOLDER;
    }

    @Override
    public void preload(final String assetFileUrl, AssetLoaderListener<Void> preloadListener) {
        AssetLoaderListener<WebBlob> listener = new AssetLoaderListener<>() {
            @Override
            public void onSuccess(String url, WebBlob result) {
                assetDownloading.remove(assetFileUrl);
                Int8Array data = result.getData();
                byte[] byteArray = TypedArrays.toByteArray(data);
                String assets = new String(byteArray);
                String[] lines = assets.split("\n");

                assetTotal = lines.length;

                for(String line : lines) {
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
                preloadListener.onSuccess(assetFileUrl, null);
                downloadMultiAssets();
            }

            @Override
            public void onFailure(String url) {
                assetDownloading.remove(assetFileUrl);
                System.out.println("ErrorLoading: " + assetFileUrl);
                preloadListener.onFailure(assetFileUrl);
            }
        };

        assetDownloading.add(assetFileUrl);
        assetDownloader.load(true, getAssetUrl() + assetFileUrl, AssetType.Binary, listener);
    }

    @Override
    public boolean isAssetInQueueOrDownloading(String path) {
        String path1 = fixPath(path);
        return assetInQueue(path1) || assetDownloading.contains(path1);
    }

    @Override
    public boolean isAssetLoaded(FileType fileType, String path) {
        String path1 = fixPath(path);
        FileHandle fileHandle = Gdx.files.getFileHandle(path1, fileType);
        return fileHandle.exists();
    }

    @Override
    public void loadAsset(String path, AssetType assetType, FileType fileType) {
        loadAssetInternal(path, assetType, fileType, null, false);
    }

    @Override
    public void loadAsset(String path, AssetType assetType, FileType fileType, AssetLoaderListener<WebBlob> listener) {
        loadAssetInternal(path, assetType, fileType, listener, false);
    }

    @Override
    public void loadAsset(String path, AssetType assetType, FileType fileType, AssetLoaderListener<WebBlob> listener, boolean overwrite) {
        loadAssetInternal(path, assetType, fileType, listener, overwrite);
    }

    @Override
    public void loadScript(String path) {
        assetDownloader.loadScript(true, getScriptUrl() + path, null);
    }

    @Override
    public void loadScript(String path, AssetLoaderListener<String> listener) {
        assetDownloader.loadScript(true, getScriptUrl() + path, listener);
    }

    @Override
    public int getQueue() {
        return assetInQueue.size;
    }

    @Override
    public int getDownloadingCount() {
        return assetDownloading.size();
    }

    @Override
    public boolean isDownloading() {
        return getQueue() > 0 || getDownloadingCount() > 0;
    }

    private void loadAssetInternal(String path, AssetType assetType, FileType fileType, AssetLoaderListener<WebBlob> listener, boolean overwrite) {
        addAssetToQueue(path, assetType, fileType, listener, overwrite);
        downloadQueueAssets();
    }

    private void addAssetToQueue(String path, AssetType assetType, FileType fileType, AssetLoaderListener<WebBlob> listener, boolean overwrite) {
        String path1 = fixPath(path);

        if(path1.isEmpty()) {
            return;
        }

        if(assetInQueue(path1)) {
            return;
        }

        WebFileHandle fileHandle = (WebFileHandle)Gdx.files.getFileHandle(path1, fileType);
        boolean exists = fileHandle.exists();
        if(!overwrite && exists) {
            return;
        }

        if(assetType == AssetType.Directory) {
            if(!exists) {
                fileHandle.mkdirsInternal();
            }
            return;
        }

        QueueAsset queueAsset = new QueueAsset();
        queueAsset.assetUrl = path1;
        queueAsset.fileHandle = fileHandle;
        queueAsset.listener = listener;
        assetInQueue.add(queueAsset);
    }

    private void downloadMultiAssets() {
        for(int i = 0; i < maxMultiDownloadCount; i++) {
            downloadQueueAssets();
        }
    }

    private void downloadQueueAssets() {
        if(assetInQueue.size == 0 || assetDownloading.size() >= maxMultiDownloadCount) {
            return;
        }
        QueueAsset queueAsset = assetInQueue.removeIndex(0);
        final String assetPath = queueAsset.assetUrl;
        final FileHandle fileHandle = queueAsset.fileHandle;
        final AssetLoaderListener<WebBlob> listener = queueAsset.listener;
        assetDownloading.add(assetPath);

        createAndStartDownload(assetPath, fileHandle, listener);
    }

    private void createAndStartDownload(final String assetPath, final FileHandle fileHandle, final AssetLoaderListener<WebBlob> listener) {
        // Create an independent wrapper listener to ensure proper closure capture
        AssetLoaderListener<WebBlob> downloadListener = createDownloadListener(assetPath, fileHandle, listener);
        assetDownloader.load(true, getAssetUrl() + assetPath, AssetType.Binary, downloadListener);
    }

    private AssetLoaderListener<WebBlob> createDownloadListener(final String assetPath, final FileHandle fileHandle, final AssetLoaderListener<WebBlob> listener) {
        return new AssetLoaderListener<>() {
            @Override
            public void onFailure(String url) {
                assetDownloading.remove(assetPath);
                if(listener != null) {
                    listener.onFailure(assetPath);
                }
                downloadMultiAssets();
            }

            @Override
            public void onSuccess(String url, WebBlob result) {
                assetDownloading.remove(assetPath);
                Int8Array data = result.getData();
                byte[] byteArray = TypedArrays.toByteArray(data);
                OutputStream output = fileHandle.write(false, 4096);
                try {
                    output.write(byteArray);
                }
                catch(IOException ex) {
                    throw new GdxRuntimeException("Error writing file: " + fileHandle + " (" + fileHandle.type() + ")", ex);
                }
                finally {
                    StreamUtils.closeQuietly(output);
                }
                if(listener != null) {
                    listener.onSuccess(assetPath, result);
                }
                downloadMultiAssets();
            }
        };
    }

    private boolean assetInQueue(String path) {
        for(int i = 0; i < assetInQueue.size; i++) {
            QueueAsset queueAsset = assetInQueue.get(i);
            if(queueAsset.assetUrl.equals(path)) {
                return true;
            }
        }
        return false;
    }

    private String fixPath(String path1) {
        path1 = path1.trim().replace("\\", "/");
        if(path1.startsWith("/")) {
            path1 = path1.substring(1);
        }
        return path1;
    }
}